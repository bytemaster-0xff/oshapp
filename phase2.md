# Phase Two Notes

1. GeoPackages created with V1.0 of the app will not be compatible with V2.0 
1. After opening a GeoPackage it will attempt to find the bounds of the package by looking at the BaseMap tile layer, this will also set the center of the package.
1. A new field is added to the hub that will let you set the path of the SOS, for NOAA service this will be /sos/server.php
1. OSH Uses SOS V2.0 http://sensiasoft.net:8181/sensorhub/sos?service=SOS&acceptVersions=2.0.0&request=GetCapabilities
1. NOAA Buoy Server Uses SOS V1.0 https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=2.0&request=GetCapabilities
1. Have to do something for setting parameters for the service, XML is currently required as a parameter https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=1.0.0&request=DescribeSensor&procedure=urn:ioos:station:wmo:0y2w3&outputFormat=text%2Fxml%3Bsubtype%3D%22sensorML%2F1.0.1%22
1. Have some thoughts on a better UI to incorporate additional features from the original data set.
1. To get sensor values: 
    https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=1.0.0&offering=urn%3Aioos%3Astation%3Awmo%3A46222&request=GetObservation&observedProperty=sea_water_temperature&temporalFilter=phenomenonTime,now&responseFormat=text%2Fxml%3Bsubtype%3D%22om%2F1.0.0%22
    https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=1.0.0&offering=urn%3Aioos%3Astation%3Awmo%3A46222&request=GetObservation&observedProperty=sea_water_temperature&responseFormat=text%2Fxml%3Bsubtype%3D%22om%2F1.0.0%22
    https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=1.0.0&offering=urn%3Aioos%3Astation%3Awmo%3A46222&request=GetObservation&observedProperty=sea_water_temperature&responseFormat=text%2Fcsv


# Notes
1. SOS Scehams http://www.opengeospatial.org/standards/sos#schemas

# Additional work/questions:
1. Should we include SOS Version?  Right now it just tries to pick this out of the XML.

# Assumptions:
1. Currently always appending outputFormat of XML, I don't think we want to hard code this.
1. If an ObservationOffering has multiple procedures we will ignore it, in the case of "network-all" it will be all the stations that are in other offerings. 
1. An ObservationOffering should have a bounding box associated with it, if it doesn't we ignore it, if it does, we make sure the center is within the bounds of the GeoPackage
1. For V1.0 SOS Services, we are using the sensor name as the ID.

# ToDo: 
1. Should likely add the server SOS Version into the database

# San Diego Geopackage notes
1. Bounds as stated in the package is 32.5x-117.5 to 33.0x-117, this is only rougly a ~30 square mile area
1. Within the original GeoPacakge bounds there are no NOAA Buoys.  If I expand it to +/- another 1 degree, we get 15 buoys within the SD area.