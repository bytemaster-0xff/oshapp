package com.softwarelogistics.oshgeo.poc.repos;

import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.attributes.AttributesColumn;
import mil.nga.geopackage.core.contents.Contents;
import mil.nga.geopackage.core.contents.ContentsDao;
import mil.nga.geopackage.core.contents.ContentsDataType;
import mil.nga.geopackage.core.srs.SpatialReferenceSystem;
import mil.nga.geopackage.core.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.columns.GeometryColumnsDao;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.features.user.FeatureCursor;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.features.user.FeatureTable;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.map.geom.GoogleMapShape;
import mil.nga.geopackage.map.geom.GoogleMapShapeConverter;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryType;
import mil.nga.sf.Point;
import mil.nga.sf.proj.ProjectionConstants;

/**
 * Will contain access for settings for available OSH installations
 */
public class OSHDataContext {
    GeoPackage mGeoPackage;
    static final String COL_ID = "id";
    static final String COL_NAME = "name";
    static final String COL_GEOMETRY = "geometry";
    static final String COL_TIMESTAMP = "timestamp";

    static final String HUB_TABLE_NAME = "oshhubs";
    static final String HUB_COL_SSID = "ssid";
    static final String HUB_COL_PWD = "pwd";
    static final String HUB_COL_IP = "ipaddress";
    static final String HUB_COL_IMAGE = "image";


    static final String SNSR_TABLE_NAME = "sensors";
    static final String SNSR_COL_SNSR_ID = "sensor_id";
    static final String SNSR_COL_SNSR_TYPE = "sensor_type";
    static final String SNSR_COL_LAST_CONTACT = "last_contact";

    static final String READING_TABLE_NAME = "sensor_readings";
    static final String READING_COL_SENSOR_ID = "sensor_id";

    static final String VALUE_TABLE_NAME = "sensor_values";
    static final String VALUE_COL_READING_ID = "reading_id";
    static final String VALUE_COL_SENSOR_ID = "sensor_id";
    static final String VALUE_COL_LABEL = "label";
    static final String VALUE_COL_DATA_TYPE = "data_type";
    static final String VALUE_COL_INT_VALUE = "int_value";
    static final String VALUE_COL_STR_VALUE = "str_value";
    static final String VALUE_COL_NUMB_VALUE = "numb_value";
    static final String VALUE_COL_UNITS = "units";


    public OSHDataContext(GeoPackage geoPackage) {
        mGeoPackage = geoPackage;
    }

    public List<OpenSensorHub> getHubs() {
        List<OpenSensorHub> hubs = new ArrayList<>();

        FeatureDao hubDao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
        FeatureCursor featureCursor = hubDao.queryForAll();

        try{
            while(featureCursor.moveToNext()){
                OpenSensorHub hub = new OpenSensorHub();

                hub.Name = (String)featureCursor.getValue(featureCursor.getColumnIndex(COL_NAME), GeoPackageDataType.TEXT);
                hubs.add(hub);
            }
        }finally{
            featureCursor.close();
        }

        return hubs;
    }


    public boolean createTable() {
        try {
            mGeoPackage.createGeometryColumnsTable();
            createHubsTable();
            createSensorsTable();
            createReadingsTable();
            createValuesTable();
            return true;
        }
        catch(SQLException ex) {
            return false;
        }
    }

    private SpatialReferenceSystem getSrs() throws SQLException{
        SpatialReferenceSystemDao srsDao = mGeoPackage.getSpatialReferenceSystemDao();
        return srsDao.getOrCreateCode(ProjectionConstants.AUTHORITY_EPSG, (long) ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);
    }

    private void createHubsTable() throws SQLException {

        List<FeatureColumn> columns = new ArrayList<>();
        int idx = 0;
        columns.add(FeatureColumn.createPrimaryKeyColumn(idx++, COL_ID));
        columns.add(FeatureColumn.createGeometryColumn(idx++, COL_GEOMETRY, GeometryType.POINT, true, null));
        columns.add(FeatureColumn.createColumn(idx++, COL_NAME, GeoPackageDataType.TEXT,true, ""));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_SSID, GeoPackageDataType.TEXT,true, ""));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_PWD, GeoPackageDataType.TEXT,false, ""));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_IP, GeoPackageDataType.TEXT,true, ""));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_IMAGE, GeoPackageDataType.BLOB,false, null));
        FeatureTable tbl = new FeatureTable(HUB_TABLE_NAME, columns);
        mGeoPackage.createFeatureTable(tbl);

        Contents contents = new Contents();
        contents.setTableName(HUB_TABLE_NAME);
        contents.setDataType(ContentsDataType.FEATURES);
        contents.setIdentifier(HUB_TABLE_NAME);
        contents.setDescription("List of sensors for a particular sensor hub.");
        contents.setSrs(getSrs());

        ContentsDao contentsDao = mGeoPackage.getContentsDao();
        contentsDao.create(contents);

        GeometryColumnsDao geometryColumnsDao = mGeoPackage.getGeometryColumnsDao();

        GeometryColumns geometryColumns = new GeometryColumns();
        geometryColumns.setContents(contents);
        geometryColumns.setColumnName(COL_GEOMETRY);
        geometryColumns.setGeometryType(GeometryType.POINT);
        geometryColumns.setSrs(getSrs());
        geometryColumns.setZ((byte) 0);
        geometryColumns.setM((byte) 0);

        geometryColumnsDao.create(geometryColumns);
    }

    /**
     * The sensors table will be features will contain a list of sensors for a sensor hub installation.
     * It will contain
     *  Sensor Name
     *  Sensor Id
     *  Sensor Type
     *  Last Contact
     *
     */
    private void createSensorsTable() {
        List<FeatureColumn> columns = new ArrayList<>();
        int idx = 0;
        columns.add(FeatureColumn.createPrimaryKeyColumn(idx++, COL_ID));
        columns.add(FeatureColumn.createGeometryColumn(idx++, COL_GEOMETRY, GeometryType.POINT, false, null));
        columns.add(FeatureColumn.createColumn(idx++, COL_NAME, GeoPackageDataType.TEXT,false, ""));
        columns.add(FeatureColumn.createColumn(idx++, SNSR_COL_SNSR_ID, GeoPackageDataType.TEXT, false, ""));
        columns.add(FeatureColumn.createColumn(idx++, SNSR_COL_SNSR_TYPE, GeoPackageDataType.TEXT, false, null));
        FeatureTable tbl = new FeatureTable(SNSR_TABLE_NAME, columns);
        mGeoPackage.createFeatureTable(tbl);
    }

    /**
     * A reading is a collection of sensor values that have a specific date stamp for a sensor hub.
     * It will contain:
     *  Date Stamp
     *  Location
     *
     * It will have a foreign key into the hubs table.
     */
    private void createReadingsTable() {
        List<AttributesColumn> columns = new ArrayList<>();

        int columnNumber = 0;
        columns.add(AttributesColumn.createPrimaryKeyColumn(columnNumber++, COL_ID));
        columns.add(AttributesColumn.createColumn(columnNumber++, READING_COL_SENSOR_ID, GeoPackageDataType.INT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, COL_TIMESTAMP, GeoPackageDataType.INT, true, null));
        mGeoPackage.createAttributesTable(READING_TABLE_NAME, columns);
    }

    /**
     * A value is a specific data point from a sensor.
     * It will contain:
     *  Name
     *  Label
     *  Value
     *  Units Label
     *
     *  If will have a foreign key into the sensor readings table.
     */
    private void createValuesTable() {
        List<AttributesColumn> columns = new ArrayList<>();

        int columnNumber = 0;
        columns.add(AttributesColumn.createPrimaryKeyColumn(columnNumber++, COL_ID));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_READING_ID, GeoPackageDataType.INT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_SENSOR_ID, GeoPackageDataType.INT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, COL_TIMESTAMP, GeoPackageDataType.INT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_LABEL, GeoPackageDataType.TEXT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_DATA_TYPE, GeoPackageDataType.TEXT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_STR_VALUE, GeoPackageDataType.TEXT, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_INT_VALUE, GeoPackageDataType.INT, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_NUMB_VALUE, GeoPackageDataType.DOUBLE, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_UNITS, GeoPackageDataType.TEXT, false, null));
        mGeoPackage.createAttributesTable(VALUE_TABLE_NAME, columns);
    }

    public boolean addHub(OpenSensorHub hub) {
        try
        {
            FeatureDao dao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
            FeatureRow newRow = dao.newRow();

            GeoPackageGeometryData geometryData = new GeoPackageGeometryData(getSrs().getSrsId());
            Point pt = new Point(hub.Location.Longitude, hub.Location.Longitude);
            geometryData.setGeometry(pt);
            newRow.setGeometry(geometryData);
            newRow.setValue(COL_NAME, hub.Name);
            newRow.setValue(HUB_COL_IP, hub.IPAddress);
            newRow.setValue(HUB_COL_SSID, hub.SSID);
            newRow.setValue(HUB_COL_PWD, hub.SSIDPassword);

            dao.create(newRow);

            return true;
        }
        catch(SQLException ex){
            return false;
        }
    }

    public void editHub() {

    }

    public OpenSensorHub getHub() {
        return new OpenSensorHub();
    }
}
