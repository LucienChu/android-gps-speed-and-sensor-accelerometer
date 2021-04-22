package com.example.geospeedandaccelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorHandler {
    public static final String TAG = "SensorHandler";
    private Context context;
    private SensorManager sensorManager;
    private OnSensorUpdateListener sensorListener;
    private Sensor accelerometerSensor;
    private Sensor ambientTemperatureSensor;
    private Sensor linearAccelerometerSensor;
//    private Sensor magnetoSensor;

    private SensorEventListener localSensorListener;


    /*===================================================================== variables for getting orientation START =====================================================================*/
    // based on the info about accelerometer and magnetometer
    // the device rotation, in degree or radian could be obtained
    // https://www.youtube.com/watch?v=IzzGVLnZBfQ
//    private float[] lastAccelerometerInfo = new float[3];
//    private float[] lastMagnetoInfo = new float[3];
//    private float[] orientationInfo = new float[3];
//    private float[] rotationalMatrix = new float[9];
//    public static final int ORIENTATION_UPDATE_INTERVAL = 10000;
//    private long lastOrientationUpdatedTimestamp = 0;
//    private boolean isAccelerometerInfoCopied = false;
//    private boolean isMagnetoInfoCopied = false;
    /*===================================================================== variables for getting orientation END =====================================================================*/


    public SensorHandler(Context context, OnSensorUpdateListener sensorListener) {
        this.context = context;
        this.sensorListener = sensorListener;
        instantiateSensors();
    }

    private void instantiateSensors() {
        this.sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        this.ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        this.accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.linearAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        this.magnetoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void registerSensors() {
        this.localSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor eventSensor = event.sensor;
                // ambient sensor
                if (eventSensor == ambientTemperatureSensor && sensorListener != null) {
                    sensorListener.onAmbientTemperatureChanged(event.values[0]);
                }
                // accelerometer info
                else if (eventSensor == accelerometerSensor && sensorListener != null) {
//                    System.arraycopy(event.values, 0, lastAccelerometerInfo, 0, event.values.length);
//                    isAccelerometerInfoCopied = true;
                    float[] values = event.values;
                    sensorListener.onAccelerometerUpdated(values);
                }
                // linear accelerometer info
                else if (eventSensor == linearAccelerometerSensor && sensorListener != null) {
                    sensorListener.onLinearAccelerometerUpdated(event.values);

                }
                // magnetometer info
//                else if (eventSensor == magnetoSensor) {
//                    System.arraycopy(event.values, 0, lastMagnetoInfo, 0, event.values.length);
//                    isMagnetoInfoCopied = true;
////                    checkOrientation();
//                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        if (this.accelerometerSensor != null) {
            this.sensorManager.registerListener(localSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
            Log.i(TAG, "registerSensors: accelerometer sensor is registered");
        }

        if (this.linearAccelerometerSensor != null) {
            this.sensorManager.registerListener(localSensorListener, linearAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
            Log.i(TAG, "registerSensors: linear accelerometer sensor is registered");

        }

        if (this.ambientTemperatureSensor != null) {
            this.sensorManager.registerListener(localSensorListener, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);

            Log.i(TAG, "registerSensors: ambient sensor is registered");

        }
//        if (this.magnetoSensor != null) {
//            this.sensorManager.registerListener(localSensorListener, magnetoSensor, SensorManager.SENSOR_DELAY_NORMAL);
//            Log.i(TAG, "registerSensors: magneto sensor is registered");
//
//        }

    }

    public void unregisterSensors() {
        if (this.accelerometerSensor != null) {
            this.sensorManager.unregisterListener(localSensorListener);
            Log.i(TAG, "unregisterSensors: accelerometer sensor for: " + context.toString());
//            Log.i(TAG, "unregisterSensors: accelerometer sensor should have been UNREGISTERED for " + this.activity.toString());
        }
        if (this.ambientTemperatureSensor != null) {
            this.sensorManager.unregisterListener(localSensorListener);
            Log.i(TAG, "unregisterSensors: ambient sensor for: " + context.toString());
//            Log.i(TAG, "unregisterSensors: ambient temperature sensor should have been UNREGISTERED for " + this.activity.toString());
        }

        if (this.linearAccelerometerSensor != null) {
            this.sensorManager.unregisterListener(localSensorListener);
            Log.i(TAG, "unregisterSensors: linear accelerometer sensor for " + context.toString());
        }
//        if (this.magnetoSensor != null) {
//            this.sensorManager.unregisterListener(localSensorListener);
//            Log.i(TAG, "unregisterSensors: magneto sensor for " + context.toString());
//        }
    }

    /**
     * get device's rotation and dispatch it to the listener
     * https://www.youtube.com/watch?v=IzzGVLnZBfQ
     */
    private void checkOrientation() {
//        float degree = 0;
//        if (this.isAccelerometerInfoCopied && this.isMagnetoInfoCopied && System.currentTimeMillis() - this.lastOrientationUpdatedTimestamp > ORIENTATION_UPDATE_INTERVAL) {
//            Log.i(TAG, "notifyOrientation: checking orientation");
//            SensorManager.getRotationMatrix(rotationalMatrix, null, this.lastAccelerometerInfo, this.lastMagnetoInfo);
//            SensorManager.getOrientation(rotationalMatrix, orientationInfo);
//            float radian = orientationInfo[0];
//            degree = (float) Math.toDegrees(radian);
//            this.lastOrientationUpdatedTimestamp = System.currentTimeMillis();
//            if (this.sensorListener != null) {
//                sensorListener.onDeviceOrientationChanged(degree);
//            }
//        }

    }

    public interface OnSensorUpdateListener {
        void onAmbientTemperatureChanged(float newTemp);

        void onAccelerometerUpdated(float[] values);

        void onLinearAccelerometerUpdated(float[] values);

//        void onDeviceOrientationChanged(float degree);
    }
}
