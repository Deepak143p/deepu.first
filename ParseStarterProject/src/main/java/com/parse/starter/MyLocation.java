package com.parse.starter;

import android.app.DownloadManager;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Request;

import static com.parse.starter.R.string.request;

public class MyLocation extends FragmentActivity implements OnMapReadyCallback,LocationListener{

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    Location location;
    TextView infoText;
    Button RqstFyntunerButton;
    TextView addressTV;
    Boolean requestActive=false;
    String driverUsername="";

    public void requestFyntuner(View view) {

       if (requestActive==false) {

           Log.i("My App","Fyntune Requested");
           ParseObject request=new ParseObject("Requests");
           com.parse.ParseUser.getCurrentUser().put("Requests",request);
           com.parse.ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
               @Override
               public void done(ParseException e) {

                   if (e==null){

                       infoText.setText("Finding Fyntuner....");
                       RqstFyntunerButton.setText("Cancel Request");
                       requestActive = true;

                   }
               }
           });
       }else {
           infoText.setText("Request Cancelled");
           RqstFyntunerButton.setText("Request Again");
           requestActive = false;
           ParseQuery<ParseObject> query=ParseQuery.getQuery("Requests");
           query.findInBackground(new FindCallback<ParseObject>() {
               @Override
               public void done(List<ParseObject> parseObjects, ParseException e) {
                   if(e==null) {


                       for (ParseObject delete : parseObjects) {
                           delete.deleteInBackground();
                           Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                       }
                   }else{
                       Toast.makeText(getApplicationContext(), "error in deleting", Toast.LENGTH_SHORT).show();
                   }
               }
           });


       }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        addressTV=(TextView) findViewById(R.id.Adresses);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        infoText =(TextView) findViewById(R.id.infoText);

        RqstFyntunerButton=(Button) findViewById(R.id.RqstFyntuner);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider=locationManager.getBestProvider(new Criteria(),false);

        locationManager.requestLocationUpdates(provider,400,15,this);

        Location location=locationManager.getLastKnownLocation(provider);



    }
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider,400,15,this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

*/

    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat=location.getLatitude();
        Double lng=location.getLongitude();
        mMap.clear();

      /*  if (requestActive==false){

            ParseQuery<ParseObject> query= ParseQuery.getQuery("Riquests");
            query.whereEqualTo("requesterUsername",ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e==null){

                        if (objects.size()>0){

                            for (ParseObject object:objects){

                                requestActive=true;

                                infoText.setText("Wait For Driver...");
                                RqstFyntunerButton.setText("Cancel Request");


                                if (object.get("driverUsername")!=null){

                                    driverUsername=object.getString("driverUsername");
                                    infoText.setText("Your Driver Is On The Way");
                                    RqstFyntunerButton.setVisibility(View.INVISIBLE);
                                    Log.i("AppInfo",driverUsername);
                                }
                            }
                        }


                    }

                }
            });

        }*/



        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()))
                .title("Your Location"));
        if (requestActive==true) {

            if (!driverUsername.equals("")) {

          //       ParseQuery<com.parse.ParseUser> userquery= com.parse.ParseUser.getQuery();
          //      userquery

            }

            final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            ParseQuery<ParseObject> query=ParseQuery.getQuery("Requests");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e==null) {


                        for (ParseObject delete : parseObjects) {
                            delete.put("requesterLocation",userLocation);
                            delete.saveInBackground();
                            Toast.makeText(getApplicationContext(), "Get Location", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "error in Get Location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


        if (requestActive==true) {

            final ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {


                        for (ParseObject delete : parseObjects) {
                            delete.put("requesterLocation",point);
                            delete.saveInBackground();
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "error in Saving", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }




        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> ListAddersses=geocoder.getFromLocation(lat,lng,15);
            if (ListAddersses!=null&&ListAddersses.size()>0){

                Log.i("Place Info",ListAddersses.get(0).toString());
                String addressHolder="";

                for (int i=0;i< ListAddersses.get(0).getMaxAddressLineIndex();i++){

                    addressHolder += ListAddersses.get(0).getAddressLine(i);

                }
                addressTV.setText("Adderess:\n  "+addressHolder);


            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private static class ParseUser {
        private static com.parse.ParseUser currentUser;

        public static com.parse.ParseUser getCurrentUser() {
            return currentUser;
        }
    }


}

