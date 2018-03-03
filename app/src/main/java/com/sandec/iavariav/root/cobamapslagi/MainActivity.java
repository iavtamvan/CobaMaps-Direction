package com.sandec.iavariav.root.cobamapslagi;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback
        , View.OnClickListener
        , DirectionCallback
    ,GoogleApiClient.OnConnectionFailedListener
    ,GoogleApiClient.ConnectionCallbacks
{
    private Button btnRequestDirection;
    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private String serverKey = "AIzaSyDN5pcqSWXSqraABPp1Nf3pPMr00qJDfCg";
    private LatLng origin = new LatLng(-7.0051450, 110.4381250);
    private LatLng destination = new LatLng(-6.1751100, 106.8650390);
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};
    private Double latHome, longHome;
    private GPSTracker gpsTracker;

    private PlaceArrayAdapter placeArrayAdapter;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private AutoCompleteTextView edtDestination;
    private String TAG;

    private Place place;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    double latitudeDestination;
    double longitudeDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRequestDirection = findViewById(R.id.btn_request_direction);
        btnRequestDirection.setOnClickListener(this);
//        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        edtDestination = findViewById(R.id.edtDestination);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        gpsTracker = new GPSTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()){
            latHome = gpsTracker.getLatitude();
            longHome = gpsTracker.getLongitude();
            LatLng sydney = new LatLng(latHome, longHome);
            Toast.makeText(MainActivity.this, "Loc " + sydney, Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "latitudeDestination : " + latHome + "longHome :  " + longHome, Toast.LENGTH_SHORT).show();
        } else {
            gpsTracker.showSettingsAlert();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        edtDestination.setOnItemClickListener(mAutocompleteClickListener);
        placeArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        edtDestination.setAdapter(placeArrayAdapter);

    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Toast.makeText(MainActivity.this, "" + item.placeId, Toast.LENGTH_SHORT).show();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Toast.makeText(MainActivity.this, "" + "Place query did not complete. Error: " +
                        places.getStatus().toString(), Toast.LENGTH_SHORT).show();
                return;
            }
            // Selecting the first object buffer.
            place = places.get(0);
            CharSequence attributions = places.getAttributions();

            Toast.makeText(MainActivity.this, "" + Html.fromHtml(String.valueOf(place.getLatLng().toString().trim())), Toast.LENGTH_SHORT).show();
//            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
//            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
//            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
//            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
//            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
                Toast.makeText(MainActivity.this, "" + attributions.toString(), Toast.LENGTH_SHORT).show();
//                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };



    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_request_direction) {
            requestDirection();
        }
    }

    public void requestDirection() {
        latitudeDestination = place.getLatLng().latitude;
        longitudeDestination = place.getLatLng().longitude;
        Toast.makeText(this, "Direction Requesting", Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "" + latitudeDestination + longitudeDestination, Toast.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(serverKey)
                .from(new LatLng(latHome, longHome))
                .to(new LatLng(latitudeDestination, longitudeDestination))
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        Toast.makeText(this, "Succes", Toast.LENGTH_SHORT).show();
        if (direction.isOK()) {
            gMap.addMarker(new MarkerOptions().position(new LatLng(latHome, longHome)));
            gMap.addMarker(new MarkerOptions().position(new LatLng(latitudeDestination, longitudeDestination)));

            for (int i = 0; i < direction.getRouteList().size(); i++) {
                Route route = direction.getRouteList().get(i);
                String color = colors[i % colors.length];
                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                gMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
            }
            setCameraWithCoordinationBounds(direction.getRouteList().get(0));

//            btnRequestDirection.setVisibility(View.GONE);
        }
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        this.gMap = googleMap;
        gMap = googleMap;
        LatLng sydney = new LatLng(latHome, longHome);
        gMap.addMarker(new MarkerOptions().position(sydney).title("Posisi" + latHome + longHome));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placeArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Toast.makeText(MainActivity.this, "Api Places Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        placeArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
}
