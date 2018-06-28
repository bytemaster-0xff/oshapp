package com.softwarelogistics.oshgeo.poc.repos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.softwarelogistics.oshgeo.poc.models.GeoLocation;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.models.Sensor;
import com.softwarelogistics.oshgeo.poc.models.SensorReading;
import com.softwarelogistics.oshgeo.poc.models.SensorValue;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.extension.related.dublin.DublinCoreType;
import mil.nga.geopackage.attributes.AttributesColumn;
import mil.nga.geopackage.attributes.AttributesCursor;
import mil.nga.geopackage.attributes.AttributesDao;
import mil.nga.geopackage.attributes.AttributesRow;
import mil.nga.geopackage.core.contents.Contents;
import mil.nga.geopackage.core.contents.ContentsDao;
import mil.nga.geopackage.core.contents.ContentsDataType;
import mil.nga.geopackage.core.srs.SpatialReferenceSystem;
import mil.nga.geopackage.core.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.extension.related.RelatedTablesExtension;
import mil.nga.geopackage.factory.GeoPackageFactory;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.columns.GeometryColumnsDao;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.extension.related.ExtendedRelation;
import mil.nga.geopackage.user.custom.UserCustomColumn;
import mil.nga.geopackage.extension.related.UserMappingDao;
import mil.nga.geopackage.extension.related.UserMappingRow;
import mil.nga.geopackage.extension.related.UserMappingTable;
import mil.nga.geopackage.extension.related.dublin.DublinCoreMetadata;
import mil.nga.geopackage.extension.related.dublin.DublinCoreType;
import mil.nga.geopackage.features.user.FeatureCursor;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.features.user.FeatureTable;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.map.geom.GoogleMapShapeConverter;
import mil.nga.geopackage.user.custom.UserCustomCursor;
import mil.nga.geopackage.user.custom.UserCustomRow;
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
    static final String COL_DESCRIPTION = "description";
    static final String COL_GEOMETRY = "geometry";
    static final String COL_TIMESTAMP = "timestamp";

    public static final String HUB_TABLE_NAME = "oshhubs";
    static final String HUB_COL_LOCAL_WIFI = "local_wifi";
    static final String HUB_COL_SSID = "ssid";
    static final String HUB_COL_SSID_PASSWORD = "pwd";
    static final String HUB_COL_HUB_AUTH_TYPE = "hub_auth_type";
    static final String HUB_COL_HUB_LOGIN_USERID= "hub_login_user_id";
    static final String HUB_COL_HUB_LOGIN_PASSWORD = "hub_login_password";
    static final String HUB_COL_SECURE_CONNECTION = "secure_connection";
    static final String HUB_COL_IP = "ipaddress";
    static final String HUB_COL_IMAGE = "image";
    static final String HUB_COL_PORT = "port";

    public static final String SNSR_TABLE_NAME = "sensors";
    static final String SNSR_COL_SNSR_ID = "sensor_id";
    static final String SNSR_HUB_ID = "sensor_id";
    static final String SNSR_COL_SNSR_TYPE = "sensor_type";
    static final String SNSR_COL_LAST_CONTACT = "last_contact";

    static final String READING_TABLE_NAME = "sensor_readings";
    static final String READING_COL_SENSOR_ID = "sensor_id";

    static final String VALUE_CURRENT_TABLE_NAME = "current_sensor_values";
    static final String VALUE_TABLE_NAME = "sensor_values";
    static final String VALUE_COL_READING_ID = "reading_id";
    static final String VALUE_COL_SENSOR_ID = "sensor_id";
    static final String VALUE_COL_LABEL = "label";
    static final String VALUE_COL_DATA_TYPE = "data_type";
    static final String VALUE_COL_STR_VALUE = "str_value";
    static final String VALUE_COL_DATE_VALUE = "date_value";
    static final String VALUE_COL_MEDIA_VALUE = "media_value";
    static final String VALUE_COL_NUMB_VALUE = "numb_value";
    static final String VALUE_COL_UNITS = "units";

    private String mGeoPackageName;

    public OSHDataContext(GeoPackage geoPackage, String packageName) {
        mGeoPackage = geoPackage;
        mGeoPackageName = packageName;
    }

    public List<OpenSensorHub> getHubs() {
        List<OpenSensorHub> hubs = new ArrayList<>();

        FeatureDao hubDao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
        FeatureCursor featureCursor = hubDao.queryForAll();

        try{
            while(featureCursor.moveToNext()){
                OpenSensorHub hub = new OpenSensorHub();
                hub.Id = (long)featureCursor.getValue(featureCursor.getColumnIndex( COL_ID), GeoPackageDataType.INTEGER);
                hub.Name = (String)featureCursor.getValue(featureCursor.getColumnIndex(COL_NAME), GeoPackageDataType.TEXT);
                hubs.add(hub);
            }
        }finally{
            featureCursor.close();
        }

        return hubs;
    }

    public boolean createTables(LatLng northWest, LatLng southEast) {
        try {
            mGeoPackage.createGeometryColumnsTable();

            createHubsTable(northWest, southEast);
            createSensorsTable(northWest, southEast);
            createReadingsTable();
            createValuesTable();
            createCurrentValuesTable();
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

    private void addContentTables(String tableName, String description, LatLng northWest, LatLng southEast) throws SQLException
    {
        /* Now add maker int the package contents to show where the hubs are */
        Contents contents = new Contents();
        contents.setTableName(tableName);
        contents.setDataType(ContentsDataType.FEATURES);
        contents.setIdentifier(tableName);
        contents.setMaxY(northWest.latitude);
        contents.setMaxX(southEast.longitude);
        contents.setMinY(southEast.latitude);
        contents.setMinX(northWest.longitude);
        contents.setDescription(description);
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


    private void addContentTables(String tableName, String description) throws SQLException
    {
        /* Now add maker int the package contents to show where the hubs are */
        Contents contents = new Contents();
        contents.setTableName(tableName);
        contents.setDataType(ContentsDataType.FEATURES);
        contents.setIdentifier(tableName);
        contents.setDescription(description);
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

    private void createHubsTable(LatLng northWest, LatLng southEast) throws SQLException {
        List<FeatureColumn> columns = new ArrayList<>();
        int idx = 0;
        columns.add(FeatureColumn.createPrimaryKeyColumn(idx++, COL_ID));
        columns.add(FeatureColumn.createGeometryColumn(idx++, COL_GEOMETRY, GeometryType.POINT, true, null));
        columns.add(FeatureColumn.createColumn(idx++, COL_NAME, GeoPackageDataType.TEXT,true, ""));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_LOCAL_WIFI, GeoPackageDataType.BOOLEAN,true, false));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_SSID, GeoPackageDataType.TEXT,false, null));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_SSID_PASSWORD, GeoPackageDataType.TEXT,false, null));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_HUB_AUTH_TYPE, GeoPackageDataType.TEXT,true, "anonymous"));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_HUB_LOGIN_USERID, GeoPackageDataType.TEXT,false, null));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_HUB_LOGIN_PASSWORD, GeoPackageDataType.TEXT,false, null));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_SECURE_CONNECTION, GeoPackageDataType.BOOLEAN,true, false));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_IP, GeoPackageDataType.TEXT,true, ""));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_PORT, GeoPackageDataType.INT,true, 8181));
        columns.add(FeatureColumn.createColumn(idx++, HUB_COL_IMAGE, GeoPackageDataType.BLOB,false, null));
        FeatureTable tbl = new FeatureTable(HUB_TABLE_NAME, columns);
        mGeoPackage.createFeatureTable(tbl);

        addContentTables(HUB_TABLE_NAME, "List of sensors for a particular sensor hub.", northWest, southEast);
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
    private void createSensorsTable(LatLng northWest, LatLng southEast) throws SQLException {
        List<FeatureColumn> columns = new ArrayList<>();
        int idx = 0;
        columns.add(FeatureColumn.createPrimaryKeyColumn(idx++, COL_ID));
        columns.add(FeatureColumn.createColumn(idx++, SNSR_HUB_ID, GeoPackageDataType.INT, true, 0));
        columns.add(FeatureColumn.createGeometryColumn(idx++, COL_GEOMETRY, GeometryType.POINT, true, null));
        columns.add(FeatureColumn.createColumn(idx++, COL_NAME, GeoPackageDataType.TEXT,true, ""));
        columns.add(FeatureColumn.createColumn(idx++, SNSR_COL_SNSR_TYPE, GeoPackageDataType.TEXT, true, null));
        columns.add(FeatureColumn.createColumn(idx++, SNSR_COL_LAST_CONTACT, GeoPackageDataType.TEXT, false, null));
        FeatureTable tbl = new FeatureTable(SNSR_TABLE_NAME, columns);
        mGeoPackage.createFeatureTable(tbl);

        addContentTables(SNSR_TABLE_NAME, "List of sensors for all hubs.", northWest, southEast);
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
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_DATE_VALUE, GeoPackageDataType.DATETIME, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_MEDIA_VALUE, GeoPackageDataType.BLOB, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_NUMB_VALUE, GeoPackageDataType.DOUBLE, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_UNITS, GeoPackageDataType.TEXT, false, null));
        mGeoPackage.createAttributesTable(VALUE_TABLE_NAME, columns);
    }

    /**
     * The most recent values for the sensor
     */
    private void createCurrentValuesTable() {
        List<AttributesColumn> columns = new ArrayList<>();

        int columnNumber = 0;
        columns.add(AttributesColumn.createPrimaryKeyColumn(columnNumber++, COL_ID));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_SENSOR_ID, GeoPackageDataType.INT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, COL_TIMESTAMP, GeoPackageDataType.INT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_LABEL, GeoPackageDataType.TEXT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_DATA_TYPE, GeoPackageDataType.TEXT, true, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_STR_VALUE, GeoPackageDataType.TEXT, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_DATE_VALUE, GeoPackageDataType.DATETIME, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_MEDIA_VALUE, GeoPackageDataType.BLOB, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_NUMB_VALUE, GeoPackageDataType.DOUBLE, false, null));
        columns.add(AttributesColumn.createColumn(columnNumber++, VALUE_COL_UNITS, GeoPackageDataType.TEXT, false, null));

        mGeoPackage.createAttributesTable(VALUE_CURRENT_TABLE_NAME, columns);
    }

    public List<String> getFeatureTables(){
        List<String> featureTableNames = new ArrayList<>();
        try {
            ContentsDao contentDao = mGeoPackage.getContentsDao();
            List<Contents> contents = contentDao.queryForAll();
            for (Contents content : contents) {
                if (content.getDataType() == ContentsDataType.FEATURES &&
                        !content.getTableName().equalsIgnoreCase(OSHDataContext.HUB_TABLE_NAME) &&
                        !content.getTableName().equalsIgnoreCase(OSHDataContext.SNSR_TABLE_NAME)) {
                    featureTableNames.add(content.getTableName());
                }
            }
        }
        catch (SQLException ex){ }

        return featureTableNames;
    }

    public boolean featureTableNameInUse(String tableName){
        List<String> featureTableNames = new ArrayList<>();
        try {
            ContentsDao contentDao = mGeoPackage.getContentsDao();
            List<Contents> contents = contentDao.queryForAll();

            for (Contents content : contents) {
                if(tableName.equalsIgnoreCase(content.getTableName())){
                    return true;
                }
            }
        }
        catch(SQLException ex){

        }

        return false;
    }

    public void createFeatureTable(String tableName, String description) {
        List<FeatureColumn> columns = new ArrayList<>();
        int idx= 0;
        columns.add(FeatureColumn.createPrimaryKeyColumn(idx++, COL_ID));
        columns.add(FeatureColumn.createGeometryColumn(idx++, COL_GEOMETRY, GeometryType.POINT, true, null));
        columns.add(FeatureColumn.createColumn(idx++, COL_NAME, GeoPackageDataType.TEXT, true, null));
        columns.add(FeatureColumn.createColumn(idx++, COL_DESCRIPTION, GeoPackageDataType.TEXT, true, null));
        FeatureTable tbl = new FeatureTable(tableName, columns);
        mGeoPackage.createFeatureTable(tbl);

        try {
            addContentTables(tableName, description);
        }
        catch(SQLException ex){

        }
    }

    private MapFeature featureFromFeatureRow(FeatureRow row){
        MapFeature feature = new MapFeature();
        feature.Id = row.getId();
        feature.Name = row.getValue(COL_NAME).toString();
        feature.Description = row.getValue(COL_DESCRIPTION).toString();

        GeoPackageGeometryData geometryData = row.getGeometry();
        Point point = (Point)geometryData.getGeometry();
        feature.Location = new LatLng(point.getY(), point.getX());

        return  feature;
    }

    private FeatureRow featureRowFromFeature(MapFeature feature, FeatureRow row){
        try {
            row.setValue(COL_NAME, feature.Name);
            row.setValue(COL_DESCRIPTION, feature.Description);
            Point pt = new Point(feature.Location.longitude, feature.Location.latitude);
            GeoPackageGeometryData geometryData = new GeoPackageGeometryData(getSrs().getSrsId());
            geometryData.setGeometry(pt);
            row.setGeometry(geometryData);
        }
        catch (SQLException ex){

        }

        return row;
    }

    private SensorReading readingFromAttrRow(AttributesRow row) {
        SensorReading reading = new SensorReading();
        reading.Id = row.getId();
        reading.Timestamp = (java.sql.Date)row.getValue(COL_TIMESTAMP);
        reading.SensorId = (long)row.getValue(COL_ID);
        return reading;
    }

    private AttributesRow valueToAttrRow(SensorValue value, AttributesRow row ){
        row.setValue(VALUE_COL_DATA_TYPE, value.DataType);
        row.setValue(COL_NAME, value.Name);
        row.setValue(COL_TIMESTAMP, value.Timestamp);
        row.setValue(VALUE_COL_LABEL, value.Label);
        row.setValue(VALUE_COL_NUMB_VALUE, value.NumbValue);
        row.setValue(VALUE_COL_DATE_VALUE, value.DateValue);
        row.setValue(VALUE_COL_MEDIA_VALUE, value.MediaValue);
        row.setValue(VALUE_COL_STR_VALUE, value.StrValue);
        row.setValue(VALUE_COL_UNITS, value.Units);

        return row;
    }

    private SensorValue valueFromAttrRow(AttributesRow row) {
        SensorValue sensorValue = new SensorValue();
        sensorValue.Id = (long)row.getValue(COL_ID);
        sensorValue.SensorId = (long)row.getValue(VALUE_COL_SENSOR_ID);
        sensorValue.Timestamp = (java.sql.Date)row.getValue(COL_TIMESTAMP);
        sensorValue.Name = row.getValue(COL_NAME).toString();
        sensorValue.Label = row.getValue(VALUE_COL_LABEL).toString();
        sensorValue.DataType = row.getValue(VALUE_COL_DATA_TYPE).toString();

        Object readingValue = row.getValue(VALUE_COL_READING_ID);
        if(readingValue != null){
            sensorValue.ReadingId = (long)readingValue;
        }

        Object dateValue = row.getValue(VALUE_COL_DATE_VALUE);
        if(dateValue != null) {
            sensorValue.DateValue = (java.sql.Date)dateValue;
        }

        Object strValue = row.getValue(VALUE_COL_STR_VALUE);
        if(strValue != null){
            sensorValue.StrValue = strValue.toString();
        }

        Object numbValue = row.getValue(VALUE_COL_NUMB_VALUE);
        if(numbValue != null){
            sensorValue.NumbValue = (double)numbValue;
        }

        Object mediaValue = row.getValue(VALUE_COL_MEDIA_VALUE);
        if(mediaValue != null){
            sensorValue.MediaValue = (byte[])mediaValue;
        }

        Object unitsValue = row.getValue(VALUE_COL_UNITS);
        if(unitsValue != null) {
            sensorValue.Units = unitsValue.toString();
        }

        return sensorValue;
    }


    private Sensor sensorFromFeatureRow(FeatureRow row) {
        Sensor sensor = new Sensor();
        sensor.HubId = (long)row.getValue(SNSR_HUB_ID);
        sensor.Name = row.getValue(COL_NAME).toString();
        sensor.SensorType = row.getValue(SNSR_COL_SNSR_TYPE).toString();
        sensor.SensorId = row.getValue(SNSR_COL_SNSR_TYPE).toString();
        Object lastContact = row.getValue(SNSR_COL_LAST_CONTACT);
        if(lastContact != null) {
            sensor.LastContact = (java.sql.Date)lastContact;
        }
        sensor.Id = row.getId();
        return sensor;
    }

    private FeatureRow sensorToFeatureRow(Sensor sensor, FeatureRow row){
        row.setValue(COL_NAME, sensor.Name);
        row.setValue(SNSR_HUB_ID, sensor.HubId);
        row.setValue(SNSR_COL_SNSR_ID, sensor.SensorId);
        row.setValue(SNSR_COL_SNSR_TYPE, sensor.SensorType);
        if (sensor.LastContact != null) {
            row.setValue(SNSR_COL_LAST_CONTACT, sensor.LastContact);
        }

        return row;
    }


    private OpenSensorHub hubFromFeatureRow(FeatureRow row) {
        OpenSensorHub hub = new OpenSensorHub();
        hub.Id = row.getId();
        hub.LocalWiFi = (boolean)row.getValue(HUB_COL_LOCAL_WIFI);

        hub.SecureConnection = (boolean)row.getValue(HUB_COL_SECURE_CONNECTION);
        hub.URI = row.getValue(HUB_COL_IP).toString();
        hub.Port = (long)row.getValue(HUB_COL_PORT);

        hub.Name = row.getValue(COL_NAME).toString();

        Object ssid = row.getValue(HUB_COL_SSID);
        if(ssid != null){
            hub.SSID = ssid.toString();
        }

        Object ssidPwd = row.getValue(HUB_COL_SSID_PASSWORD);
        if(ssidPwd != null) {
            hub.SSIDPassword = ssidPwd.toString();
        }

        hub.HubAuthType = row.getValue(HUB_COL_HUB_AUTH_TYPE).toString();

        Object uid = row.getValue(HUB_COL_HUB_LOGIN_USERID);
        if(uid != null) {
            hub.HubUserId = uid.toString();
        }

        Object pwd = row.getValue(HUB_COL_HUB_LOGIN_USERID);
        if(pwd != null) {
            hub.HubPassword = pwd.toString();
        }

        GeoPackageGeometryData geometryData = row.getGeometry();
        Point point = (Point)geometryData.getGeometry();
        hub.Location = new LatLng(point.getY(), point.getX());

        return hub;
    }

    private FeatureRow hubToFeatureRow(OpenSensorHub hub, FeatureRow row)  {
        try {
            GeoPackageGeometryData geometryData = new GeoPackageGeometryData(getSrs().getSrsId());
            Point pt = new Point(hub.Location.longitude, hub.Location.latitude);
            geometryData.setGeometry(pt);
            row.setGeometry(geometryData);
            row.setValue(COL_NAME, hub.Name);
            row.setValue(HUB_COL_SSID, hub.SSID);
            row.setValue(HUB_COL_SSID_PASSWORD, hub.SSIDPassword);
            row.setValue(HUB_COL_LOCAL_WIFI, hub.LocalWiFi);
            row.setValue(HUB_COL_SECURE_CONNECTION, hub.SecureConnection);
            row.setValue(HUB_COL_HUB_AUTH_TYPE, hub.HubAuthType);
            row.setValue(HUB_COL_HUB_LOGIN_USERID, hub.HubUserId);
            row.setValue(HUB_COL_HUB_LOGIN_PASSWORD, hub.HubPassword);
            row.setValue(HUB_COL_IP, hub.URI);
            row.setValue(HUB_COL_PORT, hub.Port);
            return row;
        }
        catch(SQLException ex){
            return null;
        }
    }


    public OpenSensorHub addHub(OpenSensorHub hub) {
        FeatureDao dao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
        FeatureRow newRow = dao.newRow();
        dao.create(hubToFeatureRow(hub, newRow));
        hub.Id = newRow.getId();
        return hub;
    }

    public OpenSensorHub getHub(long hubId) {
        FeatureDao hubDao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
        FeatureCursor hubCursor = hubDao.queryForId(hubId);
        try {

            if (hubCursor.moveToNext()) {
                return hubFromFeatureRow(hubCursor.getRow());
            }
        }
        finally {
            hubCursor.close();
        }

        return new OpenSensorHub();
    }

    public void updateHub(OpenSensorHub updatedHub) {
        FeatureDao hubDao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
        FeatureCursor hubCursor = hubDao.queryForId(updatedHub.Id);
        try {

            if (hubCursor.moveToNext()) {
                FeatureRow row = hubCursor.getRow();
                hubToFeatureRow(updatedHub, row);
                hubDao.update(row);
            }
        }
        finally {
            hubCursor.close();
        }
    }

    public void removeFeatureTable(String featureTable) {
        mGeoPackage.deleteTable(featureTable);
    }

    public MapFeature addFeature(String featureTable, MapFeature feature){
        FeatureDao featureDao = mGeoPackage.getFeatureDao(featureTable);
        FeatureRow newRow = featureDao.newRow();
        featureDao.create(featureRowFromFeature(feature, newRow));
        feature.Id = newRow.getId();
        return feature;
    }

    public void removeFeature(String featureTable, long featureId){
        FeatureDao featureDao = mGeoPackage.getFeatureDao(featureTable);
        featureDao.deleteById(featureId);
    }

    public List<MapFeature> getFeatures(String featureTable){
        List<MapFeature>  features = new ArrayList<>();

        FeatureDao featureDao = mGeoPackage.getFeatureDao(featureTable);
        FeatureCursor featureCursor = featureDao.queryForAll();
        while(featureCursor.moveToNext()){
            features.add(featureFromFeatureRow(featureCursor.getRow()));
        }

        return features;
    }

    public MapFeature getMapFeature(String featureTable, long featureId){
        FeatureDao featureDao = mGeoPackage.getFeatureDao(featureTable);
        FeatureCursor featureCursor = featureDao.queryForId(featureId);
        if(featureCursor.moveToNext()){
            return featureFromFeatureRow(featureCursor.getRow());
        }

        return null;
    }


    public void removeHub(OpenSensorHub hub) {
        FeatureDao hubDao = mGeoPackage.getFeatureDao(HUB_TABLE_NAME);
        hubDao.deleteById(hub.Id);
    }

    public boolean addSensor(OpenSensorHub hub, Sensor sensor) {
        try
        {
            FeatureDao dao = mGeoPackage.getFeatureDao(SNSR_TABLE_NAME);
            FeatureRow newRow = dao.newRow();

            Point pt = new Point(hub.Location.longitude, hub.Location.latitude);
            GeoPackageGeometryData geometryData = new GeoPackageGeometryData(getSrs().getSrsId());
            geometryData.setGeometry(pt);
            newRow.setGeometry(geometryData);
            dao.create(sensorToFeatureRow(sensor, newRow));

            return true;
        }
        catch(SQLException ex){
            return false;
        }
    }

    public List<Sensor> getSensors(int hubId) {
        List<Sensor> sensors = new ArrayList<>();
        FeatureDao sensorsDao = mGeoPackage.getFeatureDao(SNSR_TABLE_NAME);
        FeatureCursor sensorsCursor = sensorsDao.queryForEq(SNSR_HUB_ID, hubId);
        try{
            while(sensorsCursor.moveToNext()){
                sensors.add(sensorFromFeatureRow(sensorsCursor.getRow()));
            }
        }
        finally {
            sensorsCursor.close();
        }

        return sensors;
    }

    public Sensor findSensor(String sensorId) {
        FeatureDao sensorsDao = mGeoPackage.getFeatureDao(SNSR_TABLE_NAME);
        FeatureCursor cursor = sensorsDao.queryForEq(SNSR_COL_SNSR_ID, sensorId);
        try {
            if (cursor.moveToNext()) {
                return sensorFromFeatureRow(cursor.getRow());
            }
        }
        finally {
            cursor.close();
        }

        return null;
    }

    public Sensor findSensor(int sensorId) {
        FeatureDao sensorsDao = mGeoPackage.getFeatureDao(SNSR_TABLE_NAME);
        FeatureCursor cursor = sensorsDao.queryForId(sensorId);
        if(cursor.moveToNext()) {
            return sensorFromFeatureRow(cursor.getRow());
        }

        return null;
    }

    public boolean addReadings(OpenSensorHub hub, Sensor sensor, List<SensorValue> values) {
        if(values.size() == 0){
            return true;
        }

        java.sql.Date timestamp = values.get(0).Timestamp;

        FeatureDao sensorDao = mGeoPackage.getFeatureDao(SNSR_TABLE_NAME);
        FeatureCursor sensorCursor = sensorDao.queryForId(sensor.Id);
        if(sensorCursor.moveToNext()){
            FeatureRow sensorRow = sensorCursor.getRow();
            sensorRow.setValue(SNSR_COL_LAST_CONTACT, timestamp);
            sensorDao.update(sensorRow);
        }

        AttributesDao readingsDao = mGeoPackage.getAttributesDao(READING_TABLE_NAME);
        AttributesRow readingRow = readingsDao.newRow();
        readingsDao.create(readingRow);

        AttributesDao valuesDao = mGeoPackage.getAttributesDao(VALUE_TABLE_NAME);

        for(SensorValue value : values) {
            AttributesRow row = valuesDao.newRow();
            row.setValue(COL_TIMESTAMP, timestamp);
            row.setValue(VALUE_COL_SENSOR_ID, sensor.Id);
            row.setValue(VALUE_COL_READING_ID, readingRow.getId());
            valuesDao.create(valueToAttrRow(value, row));
        }

        AttributesDao currentValuesDao = mGeoPackage.getAttributesDao(VALUE_CURRENT_TABLE_NAME);

        /* Likely a better way of doing this, but since all parameters are either a constant or long, we are safe from SQL Injection attacks */
        mGeoPackage.execSQL(String.format("delete from %s where %s = %i", VALUE_CURRENT_TABLE_NAME, VALUE_COL_SENSOR_ID, sensor.Id));

        for(SensorValue value : values) {
            AttributesRow row = currentValuesDao.newRow();
            row.setValue(COL_TIMESTAMP, timestamp);
            row.setValue(VALUE_COL_SENSOR_ID, sensor.Id);
            currentValuesDao.create(valueToAttrRow(value, row));
        }

        return true;
    }

    public List<SensorReading> getReadings(long sensorId) {
        List<SensorReading> readings = new ArrayList<>();

        AttributesDao attrDao = mGeoPackage.getAttributesDao(READING_TABLE_NAME);
        AttributesCursor attrCursor = attrDao.queryForEq(READING_COL_SENSOR_ID, sensorId);
        try {
            while (attrCursor.moveToNext()) {
                AttributesRow row = attrCursor.getRow();
                readings.add(readingFromAttrRow(row));
            }
        }
        finally {
            attrCursor.close();
        }

        return readings;
    }

    public List<SensorValue> getValues(long readingId) {
        List<SensorValue> values = new ArrayList<>();

        AttributesDao attrDao = mGeoPackage.getAttributesDao(VALUE_TABLE_NAME);
        AttributesCursor attrCursor = attrDao.queryForEq(VALUE_COL_READING_ID, readingId);
        try {
            while (attrCursor.moveToNext()) {
                values.add(valueFromAttrRow(attrCursor.getRow()));
            }
        }
        finally {
            attrCursor.close();
        }

        return values;
    }

    public List<SensorValue> getSensorCurrentValues(long sensorId) {
        List<SensorValue> currentValues = new ArrayList<>();

        AttributesDao attrDao = mGeoPackage.getAttributesDao(VALUE_TABLE_NAME);
        AttributesCursor attrCursor = attrDao.queryForEq(VALUE_COL_SENSOR_ID, sensorId);
        try {
            while (attrCursor.moveToNext()) {
                currentValues.add(valueFromAttrRow(attrCursor.getRow()));
            }
        }
        finally {
            attrCursor.close();
        }

        return currentValues;
    }

    private String getRelatedHubTableName(String featureTableName){
        return String.format("%s_hubs", featureTableName);
    }

    private String getRelatedSensorTableName(String featureTableName){
        return String.format("%s_sensors", featureTableName);
    }

    /*
      https://github.com/ngageoint/geopackage-android/blob/rte/geopackage-sdk/src/androidTest/java/mil/nga/geopackage/test/extension/related/RelatedTablesUtils.java
     */

    private void createHubsRelatedTables(String featureTableName) {
        RelatedTablesExtension relatedTables = new RelatedTablesExtension(mGeoPackage);

        int columnIndex = 0;
        List<UserCustomColumn> columns = new ArrayList<>();
        columns.add(UserCustomColumn.createColumn(columnIndex++, DublinCoreType.DESCRIPTION.getName(), GeoPackageDataType.TEXT, false, null));
        columns.add(UserCustomColumn.createColumn(columnIndex++, DublinCoreType.TITLE.getName(), GeoPackageDataType.TEXT, false, null));

        UserMappingTable userMappingTable = UserMappingTable.create(getRelatedHubTableName(featureTableName), columns);
        relatedTables.addFeaturesRelationship(featureTableName, HUB_TABLE_NAME , userMappingTable);
    }

    public void relateFeatureToHub(String featureTableName, long featureId, long hubId, String title, String description){
        RelatedTablesExtension relatedTables = new RelatedTablesExtension(mGeoPackage);
        UserMappingDao mappingDao = relatedTables.getMappingDao(getRelatedHubTableName(featureTableName));

        UserMappingRow row = mappingDao.newRow();
        DublinCoreMetadata.setValue(row, DublinCoreType.TITLE, title);
        DublinCoreMetadata.setValue(row, DublinCoreType.DESCRIPTION, description);
        row.setBaseId(featureId);
        row.setRelatedId(hubId);
        mappingDao.insert(row);
    }

    private void createSensorsRelatedTables(String featureTableName) {
        RelatedTablesExtension relatedTables = new RelatedTablesExtension(mGeoPackage);
        int columnIndex = 0;
        List<UserCustomColumn> columns = new ArrayList<>();
        columns.add(UserCustomColumn.createColumn(columnIndex++, DublinCoreType.DESCRIPTION.getName(), GeoPackageDataType.TEXT, false, null));
        columns.add(UserCustomColumn.createColumn(columnIndex++, DublinCoreType.TITLE.getName(), GeoPackageDataType.TEXT, false, null));

        UserMappingTable userMappingTable = UserMappingTable.create(getRelatedSensorTableName(featureTableName), columns);
        relatedTables.addFeaturesRelationship(featureTableName, SNSR_TABLE_NAME, userMappingTable);
    }

    public void relateFeatureToSensor(String featureTableName, long featureId, long sensorId, String title, String description){
        RelatedTablesExtension relatedTables = new RelatedTablesExtension(mGeoPackage);
        UserMappingDao mappingDao = relatedTables.getMappingDao(getRelatedSensorTableName(featureTableName));

        UserMappingRow row = mappingDao.newRow();
        DublinCoreMetadata.setValue(row, DublinCoreType.TITLE, title);
        DublinCoreMetadata.setValue(row, DublinCoreType.DESCRIPTION, description);
        row.setBaseId(featureId);
        row.setRelatedId(sensorId);
        mappingDao.insert(row);
    }

    public List<SensorValue> getRelatedSensorValuesForFeature(String baseFeatureTableName, long baseFeatureId){
        List<SensorValue> currentSensorValues = new ArrayList<>();

        RelatedTablesExtension relatedTables = new RelatedTablesExtension(mGeoPackage);
        UserMappingDao mappingDao = relatedTables.getMappingDao(getRelatedSensorTableName(baseFeatureTableName));

        UserCustomCursor cursor = mappingDao.queryByBaseId(baseFeatureId);
        while(cursor.moveToNext()){
            UserMappingRow row = mappingDao.getRow(cursor.getRow());
            currentSensorValues.addAll(getSensorCurrentValues(row.getRelatedId()));
        }

        return currentSensorValues;
    }
}
