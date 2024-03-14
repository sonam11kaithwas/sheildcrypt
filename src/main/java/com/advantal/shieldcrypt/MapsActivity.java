package com.advantal.shieldcrypt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;

    String mapKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapKey = getResources().getString(R.string.google_maps_key);





        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(MapsActivity.this).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MapsActivity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


        supportMapFragment.getMapAsync(this);
    }


    private byte[] getMapThumbnail(double lati, double longi) {

        String mapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=color:red%7Clabel:G%7C&zoom=14&size=200x200&sensor=false&key=";
        mapUrl += getResources().getString(R.string.google_maps_key);

        String finalMapUrl = mapUrl;
        Log.v("ssintrg", mapUrl);
        Callable<byte[]> thumb = new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {

                try {
                    InputStream inputStream = new URL(finalMapUrl).openStream();
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    return byteArray;
                } catch (Exception e) {
                    return new byte[0];
                }
            }
        };

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    @Override
    protected void onResume() {
        super.onResume();
//        displayLocationSettingsRequest(MapsActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


    }

    public void getGoogleMapThumbnail(double lati, double longi) {
        String latLng = lati + "," + longi;
        String mapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=color:red%7Clabel:G%7C&zoom=14&size=200x200&sensor=false&key=" + mapKey;

        GetMapImageAsyncTask getMapImageAsyncTask = new GetMapImageAsyncTask(mapUrl, latLng);
        getMapImageAsyncTask.execute();
    }

    private class GetMapImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        String mapUrl = "";
        String latLng = "";
        String msg = "";
        private ProgressDialog pd;

        public GetMapImageAsyncTask(String mapUrl, String latLng) {
            this.mapUrl = mapUrl;
            this.latLng = latLng;
        }

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(MapsActivity.this, "", "Loading", true,
                    false); // Create and show Progress dialog
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bmp = null;
            InputStream inputStream;
            try {
                inputStream = new URL(mapUrl).openStream();
                bmp = BitmapFactory.decodeStream(inputStream);
                String s = "";
            } catch (IOException e) {
                e.printStackTrace();
                msg = String.valueOf(e);
                //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
            }
            return bmp;

        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(Bitmap result) {
            pd.dismiss();
            Bitmap b = result;
            Intent in = new Intent();
            in.putExtra("data", b);
            in.putExtra("latLng", latLng);
            in.putExtra("msg", msg);
            setResult(RESULT_OK, in);
            finish();
        }
    }


}