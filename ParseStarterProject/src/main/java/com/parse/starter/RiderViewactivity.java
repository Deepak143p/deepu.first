package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

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
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RiderViewactivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Intent intent;

   public void back(View view){

        Intent i=new Intent(getApplicationContext(),ViewRequest.class);
        startActivity(i);

    }

   public void acceptRequest(View view){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        query.whereEqualTo("requesterUsername",intent.getStringExtra("username"));
        query.findInBackground(new FindCallback<ParseObject>() {


            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e==null){

                    if (objects.size()>0){

                        for (ParseObject object :objects){

                            object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null) {

                                        Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?daddr=" + intent.getDoubleExtra("latitude", 0) + "," + intent.getDoubleExtra("longitude", 0)));
                                        startActivity(i);
                                    }

                                }
                            });

                        }



                    }
                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_viewactivity);

        intent=getIntent();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }

 @Override
    public void onMapReady(GoogleMap googleMap) {
     mMap = googleMap;


     RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
     mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
         @Override
         public void onGlobalLayout() {
             //and write code, which you can see in answer above

             LatLngBounds.Builder builder = new LatLngBounds.Builder();

             ArrayList<Marker> markers = new ArrayList<Marker>();

             markers.add(  mMap.addMarker(new MarkerOptions()
                     .position(new LatLng(intent.getDoubleExtra("latitude",0),intent.getDoubleExtra("longitude",0)))
                     .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                     .title("Location")));

             markers.add(  mMap.addMarker(new MarkerOptions().position(new LatLng(intent.getDoubleExtra("UserLatitude",0),intent.getDoubleExtra("UserLongitude",0))).title("your Location")));

             for (Marker marker : markers) {
                 builder.include(marker.getPosition());
             }
             LatLngBounds bounds = builder.build();

             int padding = 50; // offset from edges of the map in pixels

             CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

             mMap.animateCamera(cu);


         }
     });



 }



}

