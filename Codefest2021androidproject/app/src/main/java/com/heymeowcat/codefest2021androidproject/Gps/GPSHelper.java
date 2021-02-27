package com.heymeowcat.codefest2021androidproject.Gps;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heymeowcat.codefest2021androidproject.MapsFragment;
import com.heymeowcat.codefest2021androidproject.ViewMoreDetails;
import com.heymeowcat.codefest2021androidproject.directionsLib.FetchURL;
import com.heymeowcat.codefest2021androidproject.pojo.MapDistanceObj;
import com.heymeowcat.codefest2021androidproject.pojo.MapTimeObj;


import java.util.Date;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class GPSHelper implements LocationListener {

    private static final String TAG = "GPSHelper";
    MapsFragment mapFragment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public GPSHelper(MapsFragment mapFragment) {
        this.mapFragment = mapFragment;

    }

    public Location getCurrentLocationListner(Context mContext) {
        int MIN_TIME_BW_UPDATES = 200;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
        Location loc = null;
        Double latitude, longitude;

        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        Boolean checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        Boolean checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!checkGPS && !checkNetwork) {
            Toast.makeText(mContext, "No Service Provider Available", Toast.LENGTH_SHORT).show();
        } else {
            //this.canGetLocation = true;
            // First get location from Network Provider
//            if (checkNetwork) {
////                Toast.makeText(mContext, "Network", Toast.LENGTH_SHORT).show();
//                try {
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                    Log.d("Network", "Network");
//                    if (locationManager != null) {
//                        loc = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    }
//
//                    if (loc != null) {
//                        if (mapFragment.riderMarkerwithIcon != null) {
//                            LatLng currentLocationnetwork = new LatLng(loc.getLatitude(), loc.getLongitude());
//                            mapFragment.riderMarkerwithIcon.setPosition(currentLocationnetwork);
//                        }
//
//                    }
//                } catch (SecurityException e) {
//
//                }
//            }

            if (checkGPS) {
//                Toast.makeText(mContext, "GPS", Toast.LENGTH_SHORT).show();
                if (loc == null) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                            }
                        }
                    } catch (SecurityException e) {

                    }
                }
            }

        }
        // if GPS Enabled get lat/long using GPS Services

        Location locErr = null;
        return locErr;
    }


    @Override
    public void onLocationChanged(Location location) {

        if (mapFragment != null) {
//            Toast.makeText(mapFragment.getActivity(), "Location Changed :" + location.getLatitude() + " " +
//                    location.getLongitude(), Toast.LENGTH_SHORT).show();
            if (mapFragment.meMarker != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                mapFragment.meMarker.setPosition(currentLocation);
//                mapFragment.riderMarker.setPosition(currentLocation);
//                mapFragment.currentMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
                builder.include(mapFragment.meMarker.getPosition());
                builder.include(mapFragment.destinationMarkerwithIcon.getPosition());

                LatLngBounds bounds = builder.build();

                int width = mapFragment.getActivity().getResources().getDisplayMetrics().widthPixels;
                int height = mapFragment.getActivity().getResources().getDisplayMetrics().heightPixels;

//                mapFragment.currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 100);
//                mapFragment.currentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
//                mapFragment.currentMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                mapFragment.currentMap.animateCamera(cu);


                new FetchURL() {
                    @Override
                    public void onTaskDone(Object... values) {
                        if(mapFragment.currntPolyline!=null){
                            mapFragment.currntPolyline.remove();

                        }
                        mapFragment.currntPolyline=mapFragment.currentMap.addPolyline((PolylineOptions) values[0]);

                    }

                    @Override
                    public void onDistanceTaskDone(MapDistanceObj distance) {
                        ((ViewMoreDetails) mapFragment.getActivity()).setDistance(distance.getDistanceInText());
                    }

                    @Override
                    public void onTimeTaskDone(MapTimeObj time) {
                        ((ViewMoreDetails) mapFragment.getActivity()).setDuration(time.getTimeInText());
                    }


                }.execute(mapFragment.getUrl(currentLocation,mapFragment.destinationMarkerwithIcon.getPosition(),"driving"),"driving");



            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}