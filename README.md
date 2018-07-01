
# Open Sensor Hub - Android Proof of Concept

Open Sensor Hub Android Proof of Concept developed by [Software Logistics, LLC](https://www.software-logistics.com)

Created for [TEAMWERX Mobile Data Challenge](https://www.teamwerx.org/mobile)

### Initial Release Notes
* Application is fairly complete
* Application still needs considerable QA
* There are a number of areas where the user interface could use some tuning
* This version does not include importing and exporting Geopackages
* Application was tested with default [Simulated GPS Sensor] and [Simulated Weather Sensor] as well as custom arduino sensor
* Application will connect and pull data from the [Sensia Soft OSH Server](http://sensiasoft.net:8181/demo.html) however the server doesn't seem to handle getting the latest data properly.  I've contacted developer
* Compass/Navigation to OSH Hub view needs a little more tuning

### Application Videos
[Application Overview](https://www.youtube.com/watch?v=xEC-0g7DYxo)
This video will demonstrate the core feature of using the application after hubs and features have been added.

[Offline Features](https://www.youtube.com/watch?v=BhRo8GbL5HU)
This video will demonstrate how the application can be used to connect to local data sources with preconfigured and stored WiFi settings.

When you add your hub, you can specify "Local Wifi" if you do, it will allow you to specify a SSID and Password for the hub.  When you use the acquire feature you can connect to the hub by clicking the WiFi symbol.  If Local WiFi is selected you won't be able to synchronize with the hub unless you are connected to a matching SSID.

[Configuration](https://www.youtube.com/watch?v=1bTbUfA2L-M)
This video demonstrates how to configure the key components of the application
1. Set up a new hub (you can add osh-dev.nuviot.com to pull test data)
2. You should connect to your sensor hub to download the available sensors
3. Add Feature Tables - you can have many features tables each with unique features
4. Add Features - Add a feature by name and select a location
5. Associate sensors with a feature.

## Table Structure
### Hub Table - Open Sensor Hub Connection Data
```
Table Name: [oshhubs]
id - long (primary key)
geometry - point
name - text
local_wifi - boolean
ssid - text
ssid_password - text NOTE: This is not encrypted
hub_auth_type - text (anonymous, basic) - Currently Basic is not supported
hub_login_user_id - text 
hub_login_password - text NOTE: This is not encrypted
secure_connection - boolean NOTE: HTTP or HTTPS
ipaddress - text NOTE: IP Address or DNS name for server
image - blob NOTE: Not currently supported
port - int NOTE: Port to connect to server
last_contact - DateTime 
```
### Sensor Table - Sensors from the Sensor Hub
```
Table Name: [sensors]
id - long (primary key)
hub_id - long (foreign key to hub table)
geometry - point
name - text
description - text
sensor_unique_id - text NOTE: This is the ID given from the OSH
sensor_type - text NOTE: For future expansion
last_contact - DateTime
```

### Readings Table - Collection of Sensor Values as part of one call to the server
```
Table Name: [sensor_readings]
id - long (primary key)
hub_id - long (foreign key to hub table)
sensor_id - long (foreign key to sensor table)
timestamp - DateTime
```
### Current Values Table - Most recent values from Sensor
```
Table Name: [current_sensor_values]
id - long (primary key)
reading_id - long (foreign key to readings table)
hub_id - long (foreign key to hub table)
sensor_id - long (foreign key to sensor table)
timestamp - DateTime
name - text
label - text
data_type - text
str_value - text
date_value - DateTime (for future expansion)
media_value - blob (for future expansion)
numb_value - double (for future expansion)
units - text
```

###  Values Table - Historic values from Sensor
```
Table Name: [current_sensor_values]
id - long (primary key)
hub_id - long (foreign key to hub table)
sensor_id - long (foreign key to sensor table)
timestamp - DateTime
name - text
label - text
data_type - text
str_value - text
date_value - DateTime (for future expansion)
media_value - blob (for future expansion)
numb_value - double (for future expansion)
units - text
```
### Features  - Stored in custom feature tables created as part of application
```
id - long (primary key)
geometry - Point
name - text
description - text
```
### Feature Related Attributes
```
Base Table - Feature
Base Table Id - feature_id
Related Table - Sensors
Related Table Id - sensor_id
``` 


