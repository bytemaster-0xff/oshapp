package com.softwarelogistics.oshgeo.poc.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultTemplate {
    public ResultTemplate()
    {
        Fields = new ArrayList<>();
    }

    public List<ObservationDescriptor> Fields;

    public String Type;
    public String Definition;
    public String Desription;

    public static ResultTemplate create(JSONObject json)
    {
          ResultTemplate template = new ResultTemplate();

          return template;
    }
}
