package com.heymeowcat.codefestadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heymeowcat.codefestadmin.Gps.GPSHelper;
import com.heymeowcat.codefestadmin.Model.CustomLatLng;
import com.heymeowcat.codefestadmin.directionsLib.FetchURL;


import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MapFragment";
    FusedLocationProviderClient fusedLocationProviderClient;
    public  GoogleMap currentMap;
    public Marker customerMarkerwithIcon,destinationMarkerwithIcon;
    LatLng dropLocation;
    LatLng customerLocation;
    public Polyline currntPolyline;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            currentMap = googleMap;
            LatLng sl = new LatLng(7.8731, 80.7718);


            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsFragment.super.getContext());


            MarkerOptions iconcustomer = new MarkerOptions().position(sl).title("Customer").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).draggable(false);

            MarkerOptions icondestination = new MarkerOptions().position(sl).title("Customer want to go to...").icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).draggable(true);




            customerMarkerwithIcon = currentMap.addMarker(iconcustomer);
            destinationMarkerwithIcon = currentMap.addMarker(icondestination);
            UpdateCustomerLocation(currentMap);
        }
    };



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                boolean ACCESS_FINE_LOCATION =grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean ACCESS_COARSE_LOCATION =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(ACCESS_FINE_LOCATION&&ACCESS_COARSE_LOCATION){
                    UpdateCustomerLocation(currentMap);
                    GPSHelper gps = new GPSHelper(this);
                    gps.getCurrentLocationListner(getContext());
                }

                break;
            }

        }
    }



    private void UpdateCustomerLocation(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else {

            Task<Location> task = fusedLocationProviderClient.getLastLocation();

            task.addOnSuccessListener(location -> {
                if (location != null) {


                    customerLocation= new LatLng(location.getLatitude(), location.getLongitude());

                    dropLocation = new LatLng(location.getLatitude(), location.getLongitude());



                    customerMarkerwithIcon.setPosition(customerLocation);
                    destinationMarkerwithIcon.setPosition(customerLocation);

                    List<Marker> markers = new ArrayList<>();
                    markers.add(customerMarkerwithIcon);
                    markers.add(destinationMarkerwithIcon);


                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();

                    int padding =10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    currentMap.animateCamera(cu);
//                    currentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 15));




                    currentMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @SuppressLint("StaticFieldLeak")
                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            dropLocation =marker.getPosition();

//                            currentMap.addPolyline(new PolylineOptions().add(customerLocation,dropLocation));


                            new FetchURL() {
                                @Override
                                public void onTaskDone(Object... values) {
                                    if(currntPolyline!=null){
                                        currntPolyline.remove();

                                    }
                                    currntPolyline=currentMap.addPolyline((PolylineOptions) values[0]);
                                    ((NewsEvents)getActivity()).setEventLocation(new CustomLatLng(customerMarkerwithIcon.getPosition()));                                }


                            }.execute(getUrl(customerMarkerwithIcon.getPosition(),dropLocation,"driving"),"driving");

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }
                    });

                }


            }).addOnFailureListener(e -> {
                Toast.makeText(MapsFragment.super.getContext(), "Error + " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }


    }


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
        Log.d(TAG,"URL:"+url);
        return url;
    }

    public void showlastJobDestinationLocation(double lat,double lon){
        if(currentMap!=null){
            if (destinationMarkerwithIcon!=null){
                destinationMarkerwithIcon.setDraggable(false);
                destinationMarkerwithIcon.setPosition(new LatLng(lat,lon));
            }
        }

    }


    //for vectors
    private BitmapDescriptor getBitmapDesc(FragmentActivity activity, int ic_tracking) {
        Drawable LAYER_1 = ContextCompat.getDrawable(activity,ic_tracking);
        LAYER_1.setBounds(0, 0, LAYER_1.getIntrinsicWidth(), LAYER_1.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(LAYER_1.getIntrinsicWidth(), LAYER_1.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        LAYER_1.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public String getPrice(int DistanceInMeters){
        double startPrice =50;
        double additionalPriceperKm=40;
        double additionam = DistanceInMeters-1000;
        double additionalPrice = (additionam/1000)*additionalPriceperKm;
        double estimated_Price =startPrice+additionalPrice;
        return String.format("%.2f", estimated_Price);

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
    }
}