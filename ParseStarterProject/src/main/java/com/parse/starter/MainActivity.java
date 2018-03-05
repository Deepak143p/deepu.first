/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends AppCompatActivity {

  Switch riderOrdriverSwitch;

  public void GetStarted(View view){

    String riderOrdriver="fyntuner";

    if (riderOrdriverSwitch.isChecked()){

      riderOrdriver="fyntune";
    }

    ParseUser.getCurrentUser().put("riderOrdriver",riderOrdriver);
    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e==null){
         rediectUser();
        }
      }
    });
  }
  public void rediectUser(){

    if (ParseUser.getCurrentUser().get("riderOrdriver").equals("fyntune")){

      Intent i=new Intent(getApplicationContext(),MyLocation.class);

      startActivity(i);

    }else{
      Intent i=new Intent(getApplicationContext(),ViewRequest.class);

      startActivity(i);


    }

  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    ParseUser.getCurrentUser().put("riderOrdriver","Fyntuner");

    riderOrdriverSwitch =(Switch) findViewById(R.id.SwitchItem);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

    getSupportActionBar().hide();

    if (ParseUser.getCurrentUser()==null) {
  ParseAnonymousUtils.logIn(new LogInCallback() {

    @Override

    public void done(ParseUser user, ParseException e) {
      if (e != null) {
        Log.d("MyApp", "Anonymous login failed.");
      } else {
        Log.d("MyApp", "Anonymous user logged in.");
      }
    }
  });
}else{

      if (ParseUser.getCurrentUser().get("riderOrdriver")!=null){

        rediectUser();

      }
    }
  }
}
