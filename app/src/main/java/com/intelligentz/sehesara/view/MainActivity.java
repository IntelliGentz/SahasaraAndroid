package com.intelligentz.sehesara.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.intelligentz.sehesara.R;
import com.intelligentz.sehesara.constants.Data;
import com.intelligentz.sehesara.constants.Tags;
import com.intelligentz.sehesara.constants.URL;
import com.intelligentz.sehesara.model.Bus;
import com.intelligentz.sehesara.model.Route;
import com.intelligentz.sehesara.parser.JSONParser;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    private SelectiveSpiner routeSpinner;
    private Spinner headingSpinner;
    private AppCompatButton searchBtn;
    private String route;
    private String heading;
    private LinearLayout headingLayout;
    private LinearLayout routeLayout;
    private boolean viewsHidden = false;
    private SweetAlertDialog progressDialog;
    private boolean searched = false;

    final SweetAlertDialog.OnSweetClickListener successListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismissWithAnimation();
            if (viewsHidden) {
                showViews();
            }
        }
    };
    final SweetAlertDialog.OnSweetClickListener errorListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
           finish();
        }
    };
    private ArrayList<Bus> busList;
    private ArrayList<Route> routeList;
    private ArrayList<Marker> markerList;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    MarkerOptions currentLocationMarkerOption;
    private boolean wentToGPSactivstion = false;
    View mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        configureRouteSpinner();
        configureHeadingSpinner();
        configureSearchBtn();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mFragment.getView();
        mFragment.getMapAsync(this);
        new LoadRoutes().execute();
    }

    private void configureHeadingSpinner() {
        headingSpinner = (Spinner) findViewById(R.id.heading_spinner);
        ArrayAdapter headingAdaptor = new ArrayAdapter(this,
                R.layout.layout_spinner, Data.heading);
        headingSpinner.setAdapter(headingAdaptor);

    }

    public void configureRouteSpinner() {
        final TextView routeTxt = (TextView) findViewById(R.id.route_txt);
        searchBtn = (AppCompatButton) findViewById(R.id.btn_search);
        headingLayout = (LinearLayout) findViewById(R.id.headingLayout);
        routeLayout = (LinearLayout) findViewById(R.id.route_layout);
        routeSpinner = (SelectiveSpiner) findViewById(R.id.search_spinner);
        ArrayAdapter routeAdaptor = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, Data.routes);
        routeSpinner.setAdapter(routeAdaptor);
        routeSpinner.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] heading = new String[2];
                heading[0] = routeList.get(i).getStartCity();
                heading[1] = routeList.get(i).getEndCity();
                ArrayAdapter headingAdaptor = new ArrayAdapter(context,
                        R.layout.layout_spinner, heading);
                headingSpinner.setAdapter(headingAdaptor);
                final Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                final Animation slideUp2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                if (headingLayout.getVisibility() == View.INVISIBLE) {
                    headingLayout.startAnimation(slideUp);
                    headingLayout.setVisibility(View.VISIBLE);
                }
                if (routeTxt.getVisibility() == View.GONE) {
                    routeTxt.setVisibility(View.VISIBLE);
                }
                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        searchBtn.startAnimation(slideUp2);
                        searchBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void configureSearchBtn(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideViews();
                checkNetworkAndLocation();
            }
        });
    }
    private void hideViews(){
        viewsHidden = true;
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setAlpha(1.0f);
// Start the animation
        searchBtn.animate()
                .translationY(-searchBtn.getHeight())
                .alpha(0.0f);
        headingLayout.setVisibility(View.VISIBLE);
        headingLayout.setAlpha(1.0f);
// Start the animation
        headingLayout.animate()
                .translationY(-headingLayout.getHeight())
                .alpha(0.0f);
        routeLayout.setVisibility(View.VISIBLE);
        routeLayout.setAlpha(1.0f);
// Start the animation
        routeLayout.animate()
                .translationY(-routeLayout.getHeight())
                .alpha(0.0f);
    }
    private void showViews(){
        viewsHidden = false;
// Start the animation
        routeLayout.animate()
                .translationY(0)
                .alpha(1.0f);
// Start the animation
        searchBtn.animate()
                .translationY(0).alpha(1.0f);
        searchBtn.setVisibility(View.INVISIBLE);
// Start the animation
        headingLayout.animate()
                .translationY(0).alpha(1.0f);
        headingLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 120, 60);
        }

        mGoogleMap.setMyLocationEnabled(true);
        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("You Are Here");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            currentLocationMarkerOption = markerOptions;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You Are Here");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);
        currentLocationMarkerOption = markerOptions;
        //zoom to current position:
        if (!searched) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        }

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    class PerformSearch extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitleText("Searching...");
            progressDialog.setContentText("This may take a few seconds");
            progressDialog.getProgressHelper().setRimColor(R.color.green_progress);
            progressDialog.show();
            route = (String) routeSpinner.getSelectedItem();
            heading = (String) headingSpinner.getSelectedItem();
            int routeIndex = routeSpinner.getSelectedItemPosition();
            if (routeList.get(routeIndex).getStartCity().equals(heading)){
                heading = routeList.get(routeIndex).getEndCity();
            } else if (routeList.get(routeIndex).getEndCity().equals(heading)){
                heading = routeList.get(routeIndex).getStartCity();
            }
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                JsonObject body = new JsonObject();
                body.addProperty("route", route);
                body.addProperty("heading", heading);
                body.addProperty("lat", String.valueOf(latLng.latitude));
                body.addProperty("lon", String.valueOf(latLng.longitude));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONParser jsonParser = new JSONParser();
                JSONObject json = jsonParser.makeHttpRequest(
                        URL.SEARCH_URL, "POST", body);

                if (json == null) {
                    return null;
                }
                // check your log for json response
                Log.d("Search attempt", json.toString());

                // json success tag
                success = json.getInt(Tags.TAG_SUCCESS);
                JSONArray buses = json.getJSONArray("buses");
                Bus bus = null;
                busList = new ArrayList<>();
                for (int i = 0; i< buses.length();i++){
                    String name = ((JSONObject)(buses.get(i))).getString("name");
                    double lat = ((JSONObject)(buses.get(i))).getDouble("lat");
                    double lon = ((JSONObject)(buses.get(i))).getDouble("lon");
                    String duration = ((JSONObject)(buses.get(i))).getString("duration");
                    bus = new Bus(name,lat,lon,duration);

                    busList.add(bus);
                }
                if (success == 1) {
                    Log.d("Search Successful!", json.toString());

                    return json.getString(Tags.TAG_MESSAGE);
                }else{

                    Log.d("Login Failure!", json.getString(Tags.TAG_MESSAGE));
                    //Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_LONG).show();
                    return json.getString(Tags.TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }



        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            if (file_url != null){
                if (busList.isEmpty()){
                    progressDialog.setTitleText("No Buses!")
                            .setContentText("Couldn't find any bus near by.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(successListener)
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                }else {
                    progressDialog.dismissWithAnimation();
                }
                showBusesOnMap();
            }else {
                progressDialog.setTitleText("Failed!")
                        .setContentText("Couldn't complete the search. Please check your internet connection.")
                        .setConfirmText("OK")
                        .setConfirmClickListener(successListener)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }

        }
    }

    class LoadRoutes extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitleText("Locating...");
            progressDialog.setContentText("This may take a few seconds");
            progressDialog.getProgressHelper().setRimColor(R.color.green_progress);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // getting product details by making HTTP request
                JSONParser jsonParser = new JSONParser();
                JSONObject json = jsonParser.makeHttpRequest(
                        URL.ROUTE_RETRIEVAL_URL, "POST", params);

                // check your log for json response
                if (json == null) {
                    return null;
                }
                Log.d("Route Load attempt", json.toString());

                // json success tag
                success = json.getInt(Tags.TAG_SUCCESS);
                JSONArray buses = json.getJSONArray("routes");
                Route route = null;
                routeList = new ArrayList<>();
                for (int i = 0; i< buses.length();i++){
                    String name = ((JSONObject)(buses.get(i))).getString("name");
                    String start = ((JSONObject)(buses.get(i))).getString("start");
                    String end = ((JSONObject)(buses.get(i))).getString("end");
                    route = new Route(name,start,end);
                    routeList.add(route);
                }
                if (success == 1) {
                    Log.d("Search Successful!", json.toString());

                    return json.getString(Tags.TAG_MESSAGE);
                }else{

                    Log.d("Login Failure!", json.getString(Tags.TAG_MESSAGE));
                    //Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_LONG).show();
                    return json.getString(Tags.TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }



        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted

            if (file_url != null){
                //Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            } else {
                progressDialog.setTitleText("Failed!")
                        .setContentText("Couldn't connect to the server")
                        .setConfirmText("OK")
                        .setConfirmClickListener(errorListener)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                return;
            }
            progressDialog.dismissWithAnimation();
            String[] routes = new String[routeList.size()];
            for (int i = 0 ; i < routeList.size() ; i++) {
                routes[i] = routeList.get(i).getRouteName();
            }

            ArrayAdapter routeAdaptor = new ArrayAdapter(context,
                    android.R.layout.simple_spinner_dropdown_item, routes);
            routeSpinner.setAdapter(routeAdaptor);
        }
    }

    private void showBusesOnMap() {
        if (markerList != null) {
            for (Marker marker : markerList){
                marker.remove();
            }
        }
        markerList = new ArrayList<>();
        LatLng buslatlng;
        for(Bus bus : busList) {
            buslatlng = new LatLng(bus.getLatitude(), bus.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(buslatlng);
            String busName = bus.getName().split(",")[0].trim();
            markerOptions.snippet("Expected arrival: "+bus.getDuration());
            markerOptions.title("Bus: "+busName);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon_small));
            markerList.add(mGoogleMap.addMarker(markerOptions));
        }
        Marker user_marker = mGoogleMap.addMarker(currentLocationMarkerOption);
        if (currLocationMarker !=  null) {
            currLocationMarker.remove();
        }
        currLocationMarker = user_marker;
        markerList.add(user_marker);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        if (markerList.isEmpty()) {
            return;
        }
        searched = true;
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.animateCamera(cu);
        if (viewsHidden) {
            showViews();
        }
    }
    @Override
    public void onBackPressed() {
        if (viewsHidden){
            showViews();
        }else {
            super.onBackPressed();
        }
    }

    private void checkNetworkAndLocation(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled || !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    wentToGPSactivstion = true;
                    //get gps
                    //checkNetworkAndLocation();
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    if (viewsHidden) {
                        showViews();
                    }
                }
            });
            dialog.show();
        }
        else {
            new PerformSearch().execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wentToGPSactivstion) {
            wentToGPSactivstion = false;
            checkNetworkAndLocation();
        }
    }
}
