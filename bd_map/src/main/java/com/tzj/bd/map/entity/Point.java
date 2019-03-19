package com.tzj.bd.map.entity;

import com.baidu.mapapi.model.LatLng;

public class Point {
    private LatLng latLng;

    public Point(double latitude, double longitude) {
        this.latLng = new LatLng(latitude,longitude);
    }
    public LatLng getLatLng(){
        return latLng;
    }

    @Override
    public String toString() {
        return latLng.toString();
    }
}
