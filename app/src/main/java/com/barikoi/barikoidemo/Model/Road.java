package com.barikoi.barikoidemo.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class Road {

    private String id,nameorNum,area_id,subarea_id;
    private String roadCondition;
    private int num_of_lanes;
    private ArrayList<LatLng> coordinates;

    public Road(String id, String nameorNum, String area_id, String subarea_id, String roadCondition, int num_of_lanes, ArrayList<LatLng> coordinates) {
        this.id = id;
        this.nameorNum = nameorNum;
        this.area_id = area_id;
        this.subarea_id = subarea_id;
        this.roadCondition = roadCondition;
        this.num_of_lanes = num_of_lanes;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public String getNameorNum() {
        return nameorNum;
    }

    public String getArea_id() {
        return area_id;
    }

    public String getSubarea_id() {
        return subarea_id;
    }

    public String getRoadCondition() {
        return roadCondition;
    }

    public int getNum_of_lanes() {
        return num_of_lanes;
    }

    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }
}
