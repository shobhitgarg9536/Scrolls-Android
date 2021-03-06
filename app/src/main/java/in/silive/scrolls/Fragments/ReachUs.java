package in.silive.scrolls.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import in.silive.scrolls.R;


/**
 * Created by kone on 12/9/15.
 */
public class ReachUs extends Fragment implements RoutingListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private View rootView;
    private static SupportMapFragment mapFragment;
    Location lastlocation;
    private LatLng latLngAKGEC = new LatLng(28.676564, 77.500242), startLocation;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(3000)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    public static ReachUs instance;

    public static ReachUs getInstance(){
        if (instance == null) {
            instance = new ReachUs();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        if (rootView == null) {
        rootView = inflater.inflate(R.layout.fragment_reach_us, container, false);
        android.support.v4.app.FragmentManager fManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fManager.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        setUpGoogleApiClientIfNeeded();

            mGoogleApiClient.connect();
        }
        if (mMap==null){
            mapFragment.getMapAsync(this);
        }
        return rootView;
    }

    private void setUpGoogleApiClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }


    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRoutingFailure(RouteException var1) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "Internet connection not available", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        progressDialog.dismiss();

        //add route(s) to the map.
      /*  for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }*/

        //For shortest Path
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.colorPrimaryDark));
        polyOptions.width(10);
        polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
        Polyline polyline = mMap.addPolyline(polyOptions);
        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(startLocation);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pointer));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(latLngAKGEC);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_akgec));
        mMap.addMarker(options);
    }


    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        lastlocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastlocation == null)
            Toast.makeText(getActivity(), "Please enable yor GPS", Toast.LENGTH_SHORT).show();
        else {
            startLocation = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom, 3000, null);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                                "Fetching route information.", true);
                        Routing routing = new Routing.Builder()
                                .travelMode(AbstractRouting.TravelMode.WALKING)
                                .withListener(ReachUs.this)
                                .waypoints(startLocation, latLngAKGEC)
                                .build();
                        routing.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, 4000);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}