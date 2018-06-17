package com.softwarelogistics.oshgeo.poc.models;

import com.softwarelogistics.oshgeo.poc.utils.NodeUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;


public class ObservationDescriptorDataRecord {
    public ObservationDescriptorDataRecord() {
        Fields = new ArrayList<>();
    }

    public List<ObservationDescriptorDataField> Fields;

    public  static ObservationDescriptorDataRecord create(Node node){
        ObservationDescriptorDataRecord record = new ObservationDescriptorDataRecord();

        return record;
    }
}
