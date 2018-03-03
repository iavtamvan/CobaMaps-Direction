package com.sandec.iavariav.root.cobamapslagi;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by rr on 10/21/17.
 */

public class ExtendedLatLng implements Serializable {
    private LatLng mLatLng;
    private String mInfo;
    private GooglePlaceModel mGooglePlaceModel;

    public ExtendedLatLng() {
        this.mGooglePlaceModel = new GooglePlaceModel();
        this.mLatLng = new LatLng(0,0);
    }

    public ExtendedLatLng(LatLng mLatLng, String mInfo) {
        this.mLatLng = mLatLng;
        this.mInfo = mInfo;
        this.mGooglePlaceModel = new GooglePlaceModel();
        this.mLatLng = new LatLng(0,0);
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String mInfo) {
        this.mInfo = mInfo;
    }

    public GooglePlaceModel getGooglePlaceModel() {
        return mGooglePlaceModel;
    }

    public void setGooglePlaceModel(GooglePlaceModel mGooglePlaceModel) {
        this.mGooglePlaceModel = mGooglePlaceModel;
    }
}
