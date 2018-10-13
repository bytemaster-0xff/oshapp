# Phase Two Notes

1. GeoPackages created with V1.0 of the app will not be compatible with V2.0 
1. After opening a GeoPackage it will attempt to find the bounds of the package by looking at the BaseMap tile layer, this will also set the center of the package.
1. A new field is added to the hub that will let you set the path of the SOS, for NOAA service this will be /sos/server.php
1. OSH Uses SOS V2.0 http://sensiasoft.net:8181/sensorhub/sos?service=SOS&acceptVersions=2.0.0&request=GetCapabilities
1. NOAA Buoy Server Uses SOS V1.0 https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=2.0&request=GetCapabilities
1. Have to do something for setting parameters for the service, XML is currently required as a parameter https://sdf.ndbc.noaa.gov/sos/server.php?service=SOS&version=1.0.0&request=DescribeSensor&procedure=urn:ioos:station:wmo:0y2w3&outputFormat=text%2Fxml%3Bsubtype%3D%22sensorML%2F1.0.1%22

Additional work/questions:
1. Should we include SOS Version?  Right now it just tries to pick this out of the XML.

Assumptions:
1. Currently always appending outputFormat of XML, I don't think we want to hard code this.
1. 

ToDo: 
1. Should likely add the server SOS Version into the database