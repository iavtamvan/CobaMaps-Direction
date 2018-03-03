package com.sandec.iavariav.root.cobamapslagi;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rr on 10/16/17.
 */

public class GooglePlaceModel {
    private String description;
    private String placeId;
    private String name;
    private String address;
    private LatLng coordinate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }
}
