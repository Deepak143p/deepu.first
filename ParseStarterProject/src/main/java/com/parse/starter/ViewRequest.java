package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRequest extends AppCompatActivity implements LocationListener {

    ListView listView;

    ArrayList<String> ListViewContent;

    ArrayList<String> username;

     ArrayList<String> requests;

    ArrayList<Double> latitudes;

    ArrayList<Double> longitudes;

    ArrayAdapter arrayAdapter;

    Location location;

    private GoogleMap mMap;

    Boolean requestActive=false;

    LocationManager locationManager;
    String provider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        listView = (ListView) findViewById(R.id.listView);

        ListViewContent = new ArrayList<String>();

        username =new ArrayList<String>();
        latitudes=new ArrayList<Double>();
        longitudes=new ArrayList<Double>();

        ListViewContent.add("Loading...");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListViewContent);

        listView.setAdapter(arrayAdapter);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);

        locationManager.requestLocationUpdates(provider, 400, 15, this);

        final Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {

            updateLocation(location);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               Intent intent= new Intent(getApplicationContext(),RiderViewactivity.class);

               intent.putExtra("position",username.get(position));

                intent.putExtra("latitude", latitudes.get(position));

                intent.putExtra("longitude",longitudes.get(position));

                intent.putExtra("UserLatitude",location.getLatitude());

                intent.putExtra("UserLongitude",location.getLongitude());


                startActivity(intent);

            }
        });

    }

    public void updateLocation(Location location){

        final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

      //  ParseUser.getCurrentUser().put("location",userLocation);
       // ParseUser.getCurrentUser().saveInBackground();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        query.whereNear("requesterLocation", userLocation);
        query.setLimit(100);
        query.findInBackground(new FindCallback<ParseObject>() {


            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e==null){

                    if (objects.size()>0){

                        ListViewContent.clear();
                        username.clear();
                        latitudes.clear();
                        longitudes.clear();


                        for (ParseObject object:objects){

                            if (object.get("driverUsername")==null) {
                                Double distanceInKm=userLocation.distanceInKilometersTo((ParseGeoPoint) object.get("requesterLocation"));

                                Double DistanceDP= (double) Math.round(distanceInKm * 10) / 10;

                                ListViewContent.add(DistanceDP.toString() + "Km");

                                username.add(object.getString("requesterUsername"));
                                latitudes.add(object.getParseGeoPoint("requesterLocation").getLatitude());
                                longitudes.add(object.getParseGeoPoint("requesterLocation").getLongitude());



                            }

                        }
                        arrayAdapter.notifyDataSetChanged();

                    }
                }

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
       locationManager.removeUpdates(this);

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

}
