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
        try {
            ResultTemplate template = new ResultTemplate();

            template.Type = json.getString("type");
            template.Definition = json.getString("definition");
            template.Desription = json.getString("description");

            JSONArray fields =  json.getJSONArray("field");
            for(int idx = 0; idx < fields.length(); ++idx){
                template.Fields.add(ObservationDescriptor.create(fields.getJSONObject(idx)));
            }

            return template;
        }
        catch (JSONException ex)
        {
            return null;
        }
    }

}
