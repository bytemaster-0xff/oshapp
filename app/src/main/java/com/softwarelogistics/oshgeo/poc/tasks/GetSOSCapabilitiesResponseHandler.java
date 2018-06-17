package com.softwarelogistics.oshgeo.poc.tasks;

import com.softwarelogistics.oshgeo.poc.models.Capabilities;

public interface GetSOSCapabilitiesResponseHandler {
    void gotCapabilities(Capabilities capabilities);
}
