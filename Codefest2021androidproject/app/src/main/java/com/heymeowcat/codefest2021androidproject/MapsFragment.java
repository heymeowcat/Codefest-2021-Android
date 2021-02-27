package com.heymeowcat.codefest2021androidproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heymeowcat.codefest2021androidproject.Gps.GPSHelper;
import com.heymeowcat.codefest2021androidproject.Model.News;
import com.heymeowcat.codefest2021androidproject.directionsLib.FetchURL;
import com.heymeowcat.codefest2021androidproject.pojo.MapDistanceObj;
import com.heymeowcat.codefest2021androidproject.pojo.MapTimeObj;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    //    public Marker riderMarker;
    public GoogleMap currentMap;
    public Marker meMarker, destinationMarkerwithIcon;
    public News selectedNews;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Polyline currntPolyline;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            currentMap = googleMap;
            LatLng sl = new LatLng(7.8731, 80.7718);


            MarkerOptions iconrider = new MarkerOptions().position(sl).title("Me...").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).draggable(false);

            MarkerOptions icondestination = new MarkerOptions().position(sl).title("Customer want to go to...").icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).draggable(false);


            meMarker = currentMap.addMarker(iconrider);

            destinationMarkerwithIcon = currentMap.addMarker(icondestination);


            if (((ViewMoreDetails) getActivity()).newsEventId != null) {

                db.collection("NewsOrEvents").whereEqualTo("docid", ((ViewMoreDetails) getActivity()).newsEventId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<News> newsList = queryDocumentSnapshots.toObjects(News.class);
                    if (!newsList.isEmpty()) {



                        selectedNews = newsList.get(0);
                        if (selectedNews != null) {


                            destinationMarkerwithIcon.setPosition(new LatLng(selectedNews.getDirections().getLatitude(), selectedNews.getDirections().getLongitude()));


                            List<Marker> markers = new ArrayList<>();
                            markers.add(meMarker);
                            markers.add(destinationMarkerwithIcon);


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();

                            int padding = 10; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            currentMap.animateCamera(cu);



                            new FetchURL() {
                                @Override
                                public void onTaskDone(Object... values) {
                                    if(currntPolyline!=null){
                                        currntPolyline.remove();

                                    }
                                    currntPolyline=currentMap.addPolyline((PolylineOptions) values[0]);

                                }

                                @Override
                                public void onDistanceTaskDone(MapDistanceObj distance) {
                                    ((ViewMoreDetails)getActivity()).setDistance(distance.getDistanceInText());
                                }

                                @Override
                                public void onTimeTaskDone(MapTimeObj time) {
                                    ((ViewMoreDetails)getActivity()).setDuration(time.getTimeInText());
                                }


                            }.execute(getUrl(meMarker.getPosition(),destinationMarkerwithIcon.getPosition(),"driving"),"driving");


                        }
                    }

                });

            }


        }


    };


    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else {

            GPSHelper gps = new GPSHelper(this);
            gps.getCurrentLocationListner(getContext());
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                boolean ACCESS_FINE_LOCATION =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean ACCESS_COARSE_LOCATION =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(ACCESS_FINE_LOCATION&&ACCESS_COARSE_LOCATION){
                    GPSHelper gps = new GPSHelper(this);
                    gps.getCurrentLocationListner(getContext());
                }

                break;
            }

        }
    }

}