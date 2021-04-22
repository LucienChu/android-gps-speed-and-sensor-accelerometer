package com.example.geospeedandaccelerometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LocationHandler.OnLocationUpdateListener, SensorHandler.OnSensorUpdateListener {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_ID = 999;
    private SensorHandler sensorHandler;
    private LocationHandler locationHandler;
    private ArrayList<HashMap<String, String>> list0 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> list1 = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.locationHandler = new LocationHandler(this, 1000, 0);
        locationHandler.setLocationUpdateListener(this);
        this.sensorHandler = new SensorHandler(this, this);
    }

    public void requestUserLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // when permissions are NOT GRANTED
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                new AlertDialog.Builder(this)
                        .setTitle("Please grant all these permissions")
                        .setMessage("Otherwise, the application would not work properly")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ID
                                );
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ID
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            this.locationHandler.startLocationUpdate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ID) {
            if (grantResults.length >= 2 &&
                    (grantResults[0] +
                            grantResults[1]
                            == PackageManager.PERMISSION_GRANTED)) {

                this.locationHandler.startLocationUpdate();

            } else {
                // quick app since not all permission are granted
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestUserLocation();

        if (this.sensorHandler != null) {
            sensorHandler.registerSensors();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.locationHandler != null) {
            this.locationHandler.stopLocationUpdate();
            this.locationHandler.setLocationUpdateListener(null);
            locationHandler = null;
        }
        if (this.sensorHandler != null) {
            sensorHandler.unregisterSensors();
            sensorHandler = null;
        }
    }

    Location lastKnownLocation;
    @Override
    public void onLocationUpdated(Location location) {
        String value = OffsetDateTime.now().toString() + ", " + location.getLatitude() + ", " + location.getLongitude();
        float distance = 0;
        if(this.lastKnownLocation != null) {
            distance = location.distanceTo(lastKnownLocation);
        }
        lastKnownLocation = location;
        value += ", " + distance + ", " + location.getSpeed() + "\n";
        Log.i(TAG, "onLocationUpdated: " + value);
        new Thread(new WriteSpeedToFileIo(new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + "speed").toString(), value)).start();
    }

    @Override
    public void onAmbientTemperatureChanged(float newTemp) {

    }

    @Override
    public void onAccelerometerUpdated(float[] values) {

        Log.i(TAG, "onAccelerometerUpdated: x: " + values[0] + " y: " + values[1] + " z: " + values[2]);
        float x = values[0];
        float y = values[1];
        float z = values[2];
        if(count % 2 == 0) {
            if(list0.size() == 0) {
                Log.i(TAG, "onAccelerometerUpdated: start writing to list 0");
            }
            HashMap<String, String> temp = new HashMap<>();
            temp.put("time", OffsetDateTime.now().toString());
            temp.put("x", x+"");
            temp.put("y", y+"");
            temp.put("z", z +"");

            list0.add(temp);

            if(list0.size() >= 250) {
                Log.i(TAG, "onAccelerometerUpdated: writing list 0 to file");
                count ++;
                File cachedFile = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + "acc");
                Thread t = new Thread(new WriteToFileIO(cachedFile.toString(), list0));
                t.start();
            }
        }else if(count % 2 == 1) {
            if(list1.size() == 0) {
                Log.i(TAG, "onAccelerometerUpdated: start writing to list 1");
            }

            HashMap<String, String> temp = new HashMap<>();
            temp.put("time", OffsetDateTime.now().toString());
            temp.put("x", x+"");
            temp.put("y", y+"");
            temp.put("z", z +"");

            if(list1.size() >= 250) {
                Log.i(TAG, "onAccelerometerUpdated: writing list 1 to file");
                count ++;
                File cachedFile = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + "acc");
                Thread t = new Thread(new WriteToFileIO(cachedFile.toString(), list1));
                t.start();
            }
        }
    }

    @Override
    public void onLinearAccelerometerUpdated(float[] values) {

    }
}