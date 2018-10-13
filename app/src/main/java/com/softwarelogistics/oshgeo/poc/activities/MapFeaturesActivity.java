package com.softwarelogistics.oshgeo.poc.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softwarelogistics.oshgeo.poc.R;
import com.softwarelogistics.oshgeo.poc.adapters.MapFeatureAdapter;
import com.softwarelogistics.oshgeo.poc.adapters.MapFeatureHandler;
import com.softwarelogistics.oshgeo.poc.models.MapFeature;
import com.softwarelogistics.oshgeo.poc.models.OpenSensorHub;
import com.softwarelogistics.oshgeo.poc.repos.GeoDataContext;
import com.softwarelogistics.oshgeo.poc.repos.OSHDataContext;

import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.core.contents.ContentsDao;
import mil.nga.geopackage.core.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.extension.ExtensionsDao;
import mil.nga.geopackage.factory.GeoPackageFactory;
import mil.nga.geopackage.features.columns.GeometryColumnsDao;
import mil.nga.geopackage.features.index.FeatureIndexManager;
import mil.nga.geopackage.features.index.FeatureIndexType;
import mil.nga.geopackage.features.user.FeatureCursor;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.map.geom.GoogleMapShape;
import mil.nga.geopackage.map.geom.GoogleMapShapeConverter;
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay;
import mil.nga.geopackage.map.tiles.overlay.GeoPackageOverlayFactory;
import mil.nga.geopackage.metadata.MetadataDao;
import mil.nga.geopackage.metadata.reference.MetadataReferenceDao;
import mil.nga.geopackage.schema.columns.DataColumnsDao;
import mil.nga.geopackage.schema.constraints.DataColumnConstraintsDao;
import mil.nga.geopackage.tiles.TileGenerator;
import mil.nga.geopackage.tiles.UrlTileGenerator;
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles;
import mil.nga.geopackage.tiles.features.FeatureTileGenerator;
import mil.nga.geopackage.tiles.features.FeatureTiles;
import mil.nga.geopackage.tiles.features.custom.NumberFeaturesTile;
import mil.nga.geopackage.tiles.matrix.TileMatrixDao;
import mil.nga.geopackage.tiles.matrixset.TileMatrixSetDao;
import mil.nga.geopackage.tiles.user.TileCursor;
import mil.nga.geopackage.tiles.user.TileDao;
import mil.nga.geopackage.tiles.user.TileRow;
import mil.nga.sf.Geometry;
import mil.nga.sf.proj.ProjectionConstants;
import mil.nga.sf.proj.ProjectionFactory;
import mil.nga.sf.proj.ProjectionTransform;

public class MapFeaturesActivity extends AppCompatActivity
        implements OnMapReadyCallback, MapFeatureHandler {


    private MapFeatureAdapter mMapFeatureAdapter;
    private String mGeoPackageName;
    private FusedLocationProviderClient mLocationClient;
    private ListView mFeaturesList;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private List<Marker> mHubMarkers;
    private List<MapFeature> mFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_features);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.features_map);
        mMapFragment.getMapAsync(this);

        mFeaturesList = findViewById(R.id.map_feature_features_list);

        mGeoPackageName = getIntent().getStringExtra(MainActivity.EXTRA_DB_NAME);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mHubMarkers = new ArrayList<>();

        mFeatures = new ArrayList<>();

        /*
        GeoDataContext ctx = new GeoDataContext(this);
        OSHDataContext hubsContext = ctx.getOSHDataContext(mGeoPackageName);
        List<String> featureTables = hubsContext.getFeatureTables();
        for(String featureTable : featureTables){
            List<MapFeature> features = hubsContext.getFeatures(featureTable);
            for(MapFeature feature : features){
                feature.TableName = featureTable;
                mFeatures.add(feature);
            }
        }

        mMapFeatureAdapter = new MapFeatureAdapter(this, R.layout.list_row_map_feature, mFeatures, this);
        mFeaturesList.setAdapter(mMapFeatureAdapter);
        */
    }

    OSHDataContext mHubsContext;
    GeoPackage mGeoPackage;

    protected  void loadBaseMap(){
        GeoDataContext ctx = new GeoDataContext(this);
        mHubsContext = ctx.getOSHDataContext(mGeoPackageName);

        Context context = this;
        mGeoPackage = mHubsContext.getPackage();

     /*   SpatialReferenceSystemDao srsDao = geoPackage.getSpatialReferenceSystemDao();
        ContentsDao contentsDao = geoPackage.getContentsDao();
        GeometryColumnsDao geomColumnsDao = geoPackage.getGeometryColumnsDao();
        TileMatrixSetDao tileMatrixSetDao = geoPackage.getTileMatrixSetDao();
        TileMatrixDao tileMatrixDao = geoPackage.getTileMatrixDao();
        DataColumnsDao dataColumnsDao = geoPackage.getDataColumnsDao();
        DataColumnConstraintsDao dataColumnConstraintsDao = geoPackage.getDataColumnConstraintsDao();
        MetadataDao metadataDao = geoPackage.getMetadataDao();
        MetadataReferenceDao metadataReferenceDao = geoPackage.getMetadataReferenceDao();
        ExtensionsDao extensionsDao = geoPackage.getExtensionsDao();*/

// Feature and tile tables
    //    List<String> features = geoPackage.getFeatureTables();
/*
// Query Features
        String featureTable = features.get(0);
        FeatureDao featureDao = geoPackage.getFeatureDao(featureTable);
        GoogleMapShapeConverter converter = new GoogleMapShapeConverter(
                featureDao.getProjection());
        FeatureCursor featureCursor = featureDao.queryForAll();
        try{
            while(featureCursor.moveToNext()){
                FeatureRow featureRow = featureCursor.getRow();
                GeoPackageGeometryData geometryData = featureRow.getGeometry();
                Geometry geometry = geometryData.getGeometry();
                GoogleMapShape shape = converter.toShape(geometry);
                GoogleMapShape mapShape = GoogleMapShapeConverter
                        .addShapeToMap(mMap, shape);
                // ...
            }
        }finally{
            featureCursor.close();
        }


// Query Tiles
        TileCursor tileCursor = tileDao.queryForAll();
        try{
            while(tileCursor.moveToNext()){
                TileRow tileRow = tileCursor.getRow();
                byte[] tileBytes = tileRow.getTileData();
                Bitmap tileBitmap = tileRow.getTileDataBitmap();
                // ...
            }
        }finally{
            tileCursor.close();
        }
*/
// Tile Provider (GeoPackage or Google API)
        try {
            List<String> tiles = mGeoPackage.getTileTables();
            String tileTable = tiles.get(0);
            BoundingBox bb = mHubsContext.getBoundingBox();
            TileDao tileDao = mGeoPackage.getTileDao(tileTable);
            TileProvider overlay = GeoPackageOverlayFactory.getTileProvider(tileDao);
            TileOverlayOptions overlayOptions = new TileOverlayOptions();
            overlayOptions.tileProvider(overlay);
            overlayOptions.zIndex(-1);
            mMap.addTileOverlay(overlayOptions);
        }
        catch (Exception ex){
            Log.d("OSHAPP", ex.getLocalizedMessage());
        }


        List<String> features = mGeoPackage.getFeatureTables();
        String featureTable = features.get(2);
        FeatureDao featureDao = mGeoPackage.getFeatureDao(featureTable);
  /*      GoogleMapShapeConverter converter = new GoogleMapShapeConverter(
                featureDao.getProjection());
        FeatureCursor featureCursor = featureDao.queryForAll();
        try{
            while(featureCursor.moveToNext()){
                FeatureRow featureRow = featureCursor.getRow();
                GeoPackageGeometryData geometryData = featureRow.getGeometry();
                Geometry geometry = geometryData.getGeometry();
                GoogleMapShape shape = converter.toShape(geometry);
                GoogleMapShape mapShape = GoogleMapShapeConverter
                        .addShapeToMap(mMap, shape);
                // ...
            }
        }finally{
            featureCursor.close();
        }
*/
        FeatureIndexManager indexer = new FeatureIndexManager(context, mGeoPackage, featureDao);
        indexer.setIndexLocation(FeatureIndexType.GEOPACKAGE);
        int indexedCount = indexer.index();

        // Feature Tile Provider (dynamically draw tiles from features)
        FeatureTiles featureTiles = new DefaultFeatureTiles(context, featureDao);
        featureTiles.setMaxFeaturesPerTile(1000); // Set max features to draw per tile
        NumberFeaturesTile numberFeaturesTile = new NumberFeaturesTile(context); // Custom feature tile implementation
        featureTiles.setMaxFeaturesTileDraw(numberFeaturesTile); // Draw feature count tiles when max features passed
        featureTiles.setIndexManager(indexer); // Set index manager to query feature indices
        FeatureOverlay featureOverlay = new FeatureOverlay(featureTiles);
        featureOverlay.setMinZoom(featureDao.getZoomLevel()); // Set zoom level to start showing tiles
        TileOverlayOptions featureOverlayOptions = new TileOverlayOptions();
        featureOverlayOptions.tileProvider(featureOverlay);
        featureOverlayOptions.zIndex(-1); // Draw the feature tiles behind map markers
        mMap.addTileOverlay(featureOverlayOptions);


       /*
        BoundingBox tileBounds = tileDao.getBoundingBox();
        ProjectionTransform transform = ProjectionFactory
                .getProjection((long)ProjectionConstants.EPSG_WEB_MERCATOR)
                .getTransformation((long)ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);

        BoundingBox projectedBoundingBox = tileBounds.transform(transform);
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        boundsBuilder.include(new LatLng(projectedBoundingBox.getMinLatitude(), projectedBoundingBox.getMinLongitude()));
        boundsBuilder.include(new LatLng(projectedBoundingBox.getMinLatitude(), projectedBoundingBox.getMaxLongitude()));
        boundsBuilder.include(new LatLng(projectedBoundingBox.getMaxLatitude(), projectedBoundingBox.getMinLongitude()));
        boundsBuilder.include(new LatLng(projectedBoundingBox.getMaxLatitude(), projectedBoundingBox.getMaxLongitude()));

        Log.d("OSHAPP", String.format("%.3f, %.3f - %.3f, %.3f",
                projectedBoundingBox.getMinLatitude(),projectedBoundingBox.getMaxLongitude(),
                projectedBoundingBox.getMaxLatitude(), projectedBoundingBox.getMaxLongitude()));


        Log.d("OSHAPP", String.format("%.3f, %.3f",
                boundsBuilder.build().getCenter().latitude,
                boundsBuilder.build().getCenter().longitude));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(boundsBuilder.build().getCenter(), 12.0f));
        */

        /*

// Index Features
        FeatureIndexManager indexer = new FeatureIndexManager(context, geoPackage, featureDao);
        indexer.setIndexLocation(FeatureIndexType.GEOPACKAGE);
        int indexedCount = indexer.index();

// Feature Tile Provider (dynamically draw tiles from features)
        FeatureTiles featureTiles = new DefaultFeatureTiles(context, featureDao);
        featureTiles.setMaxFeaturesPerTile(1000); // Set max features to draw per tile
        NumberFeaturesTile numberFeaturesTile = new NumberFeaturesTile(context); // Custom feature tile implementation
        featureTiles.setMaxFeaturesTileDraw(numberFeaturesTile); // Draw feature count tiles when max features passed
        featureTiles.setIndexManager(indexer); // Set index manager to query feature indices
        FeatureOverlay featureOverlay = new FeatureOverlay(featureTiles);
        featureOverlay.setMinZoom(featureDao.getZoomLevel()); // Set zoom level to start showing tiles
        TileOverlayOptions featureOverlayOptions = new TileOverlayOptions();
        featureOverlayOptions.tileProvider(featureOverlay);
        featureOverlayOptions.zIndex(-1); // Draw the feature tiles behind map markers
        mMap.addTileOverlay(featureOverlayOptions);

        BoundingBox boundingBox = new BoundingBox();
        Projection projection = ProjectionFactory.getProjection(ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);

// URL Tile Generator (generate tiles from a URL)
        TileGenerator urlTileGenerator = new UrlTileGenerator(context, geoPackage,
                "url_tile_table", "http://url/{z}/{x}/{y}.png", 2, 7, boundingBox, projection);
        int urlTileCount = urlTileGenerator.generateTiles();

// Feature Tile Generator (generate tiles from features)
        TileGenerator featureTileGenerator = new FeatureTileGenerator(context, geoPackage,
                featureTable + "_tiles", featureTiles, 10, 15, boundingBox, projection);
        int featureTileCount = featureTileGenerator.generateTiles();
*/
// Close database when done
    }



    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Sorry, you do not appear to have location permissions enabled.  Please restart the app and allow access to location.", Toast.LENGTH_LONG).show();
            return;
        }

        mMap.setMyLocationEnabled(true);

        mLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
             //   LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //
                LatLng latLng = new LatLng(32.7153, -117.1573);
               // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
            }
        });

     /*   for (MapFeature feature : mFeatures) {
            MarkerOptions newMarkerOptions = new MarkerOptions().position(feature.Location);
            newMarkerOptions.title(feature.Name);
            Marker newMarker = mMap.addMarker(newMarkerOptions);
            mHubMarkers.add(newMarker);
        }*/

        loadBaseMap();
    }

    @Override
    public void showMapFeature(MapFeature feature) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(feature.Location, 9));
    }

    @Override
    public void showMapFeatureDetails(MapFeature feature) {
        Intent intent = new Intent(this, FeatureAttributesActivity.class);
        intent.putExtra(MainActivity.EXTRA_DB_NAME, mGeoPackageName);
        intent.putExtra(FeatureAttributesActivity.FEATURE_TABLE_NAME, feature.TableName);
        intent.putExtra(FeatureAttributesActivity.FEATURE_ID, feature.Id);
        startActivity(intent);
    }
}
