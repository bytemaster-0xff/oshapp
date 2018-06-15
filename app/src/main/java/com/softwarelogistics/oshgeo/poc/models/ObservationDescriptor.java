package com.softwarelogistics.oshgeo.poc.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ObservationDescriptor {
    public String Name;
    public String Type;
    public String Definition;
    public String Label;
    public String AxisID;
    public String ReferenceFrame;

    public UnitOfMeasureDescriptor UnitOfMeasure;

    public static ObservationDescriptor create(JSONObject obj) throws JSONException {
        ObservationDescriptor descriptor = new ObservationDescriptor();
        descriptor.Name = obj.getString("name");
        descriptor.Type = obj.getString("type");
        descriptor.Definition = obj.getString("definition");
        descriptor.Label = obj.getString("label");
        descriptor.AxisID = obj.getString("axisID");
        return descriptor;
    }
}
