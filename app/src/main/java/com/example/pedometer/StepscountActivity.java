package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class StepscountActivity extends AppCompatActivity implements SensorEventListener, SensorListener {

    SensorManager sensorManager;
    TextView textView;
    Sensor mstepcount;
    boolean running;
    int stepcount=0;
    TextView cal,km;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepscount);

        cal=findViewById(R.id.cal);
        km=findViewById(R.id.km);

        textView=findViewById(R.id.count);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){

            mstepcount=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            running=true;
        }else{
            Toast.makeText(this, "sensor not detected", Toast.LENGTH_SHORT).show();
            running=false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null) {

            sensorManager.registerListener(this, mstepcount, SensorManager.SENSOR_DELAY_NORMAL);

        }

       /* running=true;

        Sensor countsensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countsensor != null)
        {
            sensorManager.registerListener(this,countsensor, SensorManager.SENSOR_DELAY_UI);

        }else {

        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){

            sensorManager.registerListener(this,mstepcount,SensorManager.SENSOR_DELAY_NORMAL);

        }

       // running = false;
       // sensorManager.unregisterListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == mstepcount) {
            stepcount = (int) event.values[0];
            textView.setText(String.valueOf(stepcount));

        }
        if (stepcount < 50) {
            km.setText("0.185km");
        } else if (stepcount < 100) {
            km.setText("0.285km");
        } else if (stepcount < 150) {
            km.setText("0.350km");
        } else if (stepcount < 200) {
            km.setText("0.385km");
        } else if (stepcount < 250) {
            km.setText("0.485km");
        } else if (stepcount < 300) {
            km.setText("0.585km");
        } else if (stepcount < 350) {
            km.setText("0.600km");
        } else if (stepcount < 400) {
            km.setText("0.680km");
        } else if (stepcount < 500) {
            km.setText("0.720km");
        } else if (stepcount < 600) {
            km.setText("0.750km");
        } else if (stepcount < 700) {
            km.setText("0.796km");
        } else if (stepcount < 800) {
            km.setText("0.800km");
        } else if (stepcount < 900) {
            km.setText("0.850km");
        } else if (stepcount < 950) {
            km.setText("0.896km");
        } else if (stepcount < 990) {
            km.setText("0.996km");
        } else if (stepcount < 1000) {
            km.setText("1.00km");
        } else if (stepcount < 1500) {
            km.setText("1.300km");
        } else if (stepcount < 2000) {
            km.setText("1.500km");
        }
            if (stepcount < 50) {
                cal.setText("2 cal");
            } else if (stepcount < 100) {
                cal.setText("4 cal ");
            } else if (stepcount < 150) {
                cal.setText("5 cal");
            } else if (stepcount < 200) {
                cal.setText("6 cal");
            } else if (stepcount < 250) {
                cal.setText("10 cal");
            } else if (stepcount < 300) {
                cal.setText("15 cal");
            } else if (stepcount < 350) {
                cal.setText("20 cal");
            } else if (stepcount < 400) {
                cal.setText("35 cal");
            } else if (stepcount < 500) {
                cal.setText("40 cal");
            } else if (stepcount < 600) {
                cal.setText("45 cal");
            } else if (stepcount < 700) {
                cal.setText("50 cal");
            } else if (stepcount < 800) {
                cal.setText("60 cal");
            } else if (stepcount < 900) {
                cal.setText("65 cal");
            } else if (stepcount < 950) {
                cal.setText("70 cal");
            } else if (stepcount < 990) {
                cal.setText("75 cal");
            } else if (stepcount <= 1000) {
                cal.setText("80 cal");
                Toast.makeText(this, "Dear User, You Have Reached ur Goal, Drink Water", Toast.LENGTH_LONG).show();
            } else if (stepcount < 2000) {
                cal.setText("100 cal");
            }

       /* if(running){
            textView.setText(String.valueOf(event.values[0]));
        }*/
        }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {

    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
