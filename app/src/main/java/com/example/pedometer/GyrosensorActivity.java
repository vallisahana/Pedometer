package com.example.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedometer.datalogger.DataLoggerManager;
import com.example.pedometer.gauge.GaugeBearing;
import com.example.pedometer.gauge.GaugeRotation;
import com.example.pedometer.view.VectorDrawableButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kircherelectronics.fsensor.filter.averaging.MeanFilter;
import com.kircherelectronics.fsensor.observer.SensorSubject;
import com.kircherelectronics.fsensor.sensor.FSensor;
import com.kircherelectronics.fsensor.sensor.gyroscope.ComplementaryGyroscopeSensor;
import com.kircherelectronics.fsensor.sensor.gyroscope.GyroscopeSensor;
import com.kircherelectronics.fsensor.sensor.gyroscope.KalmanGyroscopeSensor;

import java.util.Locale;

import static java.lang.String.valueOf;

public class GyrosensorActivity extends AppCompatActivity {

    private final static int WRITE_EXTERNAL_STORAGE_REQUEST = 1000;
    // Indicate if the output should be logged to a .csv file
    private boolean logData = false;
    private boolean meanFilterEnabled;
    private float[] fusedOrientation = new float[3];

    // The gauge views. Note that these are views and UI hogs since they run in
    // the UI thread, not ideal, but easy to use.
    private GaugeBearing gaugeBearingCalibrated;
    private GaugeRotation gaugeTiltCalibrated;

    // Handler for the UI plots so everything plots smoothly
    protected Handler uiHandler;
    protected Runnable uiRunnable;
    private TextView tvXAxis;
    private TextView tvYAxis;
    private TextView tvZAxis;
    private TextView info;
    private FSensor fSensor;
    private MeanFilter meanFilter;
    private DataLoggerManager dataLogger;
    private Dialog helpDialog;

    String num;

    TextView textX, textY, textZ;
    SensorManager sensorManager;
    Sensor sensor;

    private SensorSubject.SensorObserver sensorObserver = new SensorSubject.SensorObserver() {
        @Override
        public void onSensorChanged(float[] values) {
            updateValues(values);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Body is in Rest Position");
        builder.setTitle("Popup Message");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Toast.makeText(GyrosensorActivity.this, "Body is in Rest Position", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyrosensor);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);

        dataLogger = new DataLoggerManager(this);
        meanFilter = new MeanFilter();

        uiHandler = new Handler();
        uiRunnable = new Runnable() {
            @Override
            public void run() {
                uiHandler.postDelayed(this, 100);
                updateText();
                updateGauges();
            }
        };
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        sensorManager.registerListener(gyroListener, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        Mode mode = readPrefs();

        switch (mode) {
            case GYROSCOPE_ONLY:
                fSensor = new GyroscopeSensor(this);
                break;
            case COMPLIMENTARY_FILTER:
                fSensor = new ComplementaryGyroscopeSensor(this);
                ((ComplementaryGyroscopeSensor) fSensor).setFSensorComplimentaryTimeConstant(getPrefImuOCfQuaternionCoeff());
                break;
            case KALMAN_FILTER:
                fSensor = new KalmanGyroscopeSensor(this);
                break;
        }
        fSensor.register(sensorObserver);
        fSensor.start();
        uiHandler.post(uiRunnable);
    }

    @Override
    public void onPause() {
        if (helpDialog != null && helpDialog.isShowing()) {
            helpDialog.dismiss();
        }

        fSensor.unregister(sensorObserver);
        fSensor.stop();
        uiHandler.removeCallbacksAndMessages(null);

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(gyroListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                startDataLog();
            }
        }
    }

    private boolean getPrefMeanFilterEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(ConfigActivity.MEAN_FILTER_SMOOTHING_ENABLED_KEY, false);
    }

    private float getPrefMeanFilterTimeConstant() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return Float.parseFloat(prefs.getString(ConfigActivity.MEAN_FILTER_SMOOTHING_TIME_CONSTANT_KEY, "0.5"));
    }

    private boolean getPrefKalmanEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(ConfigActivity.KALMAN_QUATERNION_ENABLED_KEY, false);
    }

    private boolean getPrefComplimentaryEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(ConfigActivity.COMPLIMENTARY_QUATERNION_ENABLED_KEY, false);
    }

    private float getPrefImuOCfQuaternionCoeff() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return Float.parseFloat(prefs.getString(ConfigActivity.COMPLIMENTARY_QUATERNION_COEFF_KEY, "0.5"));
    }

    private void initStartButton() {
        final VectorDrawableButton button = findViewById(R.id.button_start);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!logData) {
                    button.setText(getString(R.string.action_stop));
                    startDataLog();
                } else {
                    button.setText(getString(R.string.action_start));
                    stopDataLog();
                }
            }
        });
    }

    /**
     * Initialize the UI.
     */
    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize the calibrated text views
        tvXAxis = this.findViewById(R.id.value_x_axis_calibrated);
        tvYAxis = this.findViewById(R.id.value_y_axis_calibrated);
        tvZAxis = this.findViewById(R.id.value_z_axis_calibrated);

        // Initialize the calibrated gauges views
        gaugeBearingCalibrated = findViewById(R.id.gauge_bearing_calibrated);
        gaugeTiltCalibrated = findViewById(R.id.gauge_tilt_calibrated);

        initStartButton();
    }

    private Mode readPrefs() {
        meanFilterEnabled = getPrefMeanFilterEnabled();
        boolean complimentaryFilterEnabled = getPrefComplimentaryEnabled();
        boolean kalmanFilterEnabled = getPrefKalmanEnabled();

        if (meanFilterEnabled) {
            meanFilter.setTimeConstant(getPrefMeanFilterTimeConstant());
        }

        Mode mode;

        if (!complimentaryFilterEnabled && !kalmanFilterEnabled) {
            mode = Mode.GYROSCOPE_ONLY;
        } else if (complimentaryFilterEnabled) {
            mode = Mode.COMPLIMENTARY_FILTER;
        } else {
            mode = Mode.KALMAN_FILTER;
        }

        return mode;
    }

   /* private void showHelpDialog() {
        helpDialog = new Dialog(this);
        helpDialog.setCancelable(true);
        helpDialog.setCanceledOnTouchOutside(true);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.layout_help_home, (ViewGroup) findViewById(android.R.id.content), false);
        helpDialog.setContentView(view);
        helpDialog.show();
    }*/

    private void startDataLog() {
        if (!logData && requestPermissions()) {
            logData = true;
            dataLogger.startDataLog();
        }
    }

    private void stopDataLog() {

        if (logData) {
            logData = false;
            String path = dataLogger.stopDataLog();
            Toast.makeText(this, "File Written to: " + path, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateText() {
        tvXAxis.setText(String.format(Locale.getDefault(), "%.1f", (Math.toDegrees(fusedOrientation[1]) + 360) % 360));
        tvYAxis.setText(String.format(Locale.getDefault(), "%.1f", (Math.toDegrees(fusedOrientation[2]) + 360) % 360));
        tvZAxis.setText(String.format(Locale.getDefault(), "%.1f", (Math.toDegrees(fusedOrientation[0]) + 360) % 360));

       /* if (fusedOrientation[1] <= 300.5) {
            info.setText("1");

        } else if (fusedOrientation[1] <= 200.4) {
            info.setText("2");
        }*/
    }

    private void updateGauges() {
        gaugeBearingCalibrated.updateBearing(fusedOrientation[0]);
        gaugeTiltCalibrated.updateRotation(fusedOrientation[1], fusedOrientation[2]);


    }

    private boolean requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
            return false;
        }

        return true;
    }

    private void updateValues(float[] values) {
        fusedOrientation = values;
        if(meanFilterEnabled) {
            fusedOrientation = meanFilter.filter(fusedOrientation);
        }

        if(logData) {
            dataLogger.setRotation(fusedOrientation);
        }

    }
    private enum Mode {
        GYROSCOPE_ONLY,
        COMPLIMENTARY_FILTER,
        KALMAN_FILTER
    }

    public void up(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Your are Up");
        builder.setTitle("Popup Message");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GyrosensorActivity.this, "Upwards", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            textX.setText("X : " + (int)x + " rad/s");
            textY.setText("Y : " + (int)y + " rad/s");
            textZ.setText("Z : " + (int)z + " rad/s");

            if(x>1){
                Toast.makeText(GyrosensorActivity.this, "Moving Upward", Toast.LENGTH_SHORT).show();
            }
            if(x<0){
                Toast.makeText(GyrosensorActivity.this, "Moving downward", Toast.LENGTH_SHORT).show();
            }

        }
    };
}
