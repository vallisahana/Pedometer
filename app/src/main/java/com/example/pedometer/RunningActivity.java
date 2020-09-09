package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RunningActivity extends AppCompatActivity implements AccelerometerListener, LocationListener {

    SharedPreferences share;
    private static final int Req_Location = 1;
    private Button addBtn, addNumbtn;
    private EditText editNumber, editSpeed;
    LocationManager locationManager;
    double latitude, langitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        share = getSharedPreferences("acce", Context.MODE_PRIVATE);
        editNumber = (EditText) findViewById(R.id.editTextNum);
        editSpeed = (EditText) findViewById(R.id.editTextSpeed);
        addBtn = (Button) findViewById(R.id.setSpeed);
        addNumbtn = (Button) findViewById(R.id.setnumbe);
        addNumbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSpeed();
            }
        });
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RunningActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Req_Location);
            return;
        }else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        getLocation();
    }

    private void addNumber(){
        String numbers = editNumber.getText().toString().trim();
        if (TextUtils.isEmpty(numbers)) {
            Snackbar.make(addNumbtn, "Number Field is Empty", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }
        if (numbers.length() != 10) {
            Snackbar.make(addNumbtn, "Invalid phone number", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        SharedPreferences.Editor editor=share.edit();
        editor.putString("number",numbers);
        editor.commit();
        buildAlertMessage("Number added successfully");

    }

    private void addSpeed(){
        String speed = editSpeed.getText().toString().trim();
        if (TextUtils.isEmpty(speed)) {
            Snackbar.make(addNumbtn, "Speed Field is Empty", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        SharedPreferences.Editor editor=share.edit();
        editor.putString("speed",speed);
        editor.commit();
        buildAlertMessage("Speed added successfully");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
            getLocation();
        }
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force) {
        if (share.getString("speed","") != null) {
            float speed = Float.valueOf(share.getString("speed",""));
            if ( force > speed){
                //getLocation();
                String address = getCompleteAddressString(latitude,langitude);
                Toast.makeText(this, "Current Speed:"+force, Toast.LENGTH_SHORT).show();
                sendMessage(share.getString("number",""), speed,address, share.getString("carNumber",""));
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();

//Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

//Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onStop Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onDestroy Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }
    private void buildAlertMessage(String msg) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("Alert!")
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void sendMessage(String number, float speed, String address, String carNumber)
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            String dateTime = DateFormat.getDateTimeInstance().format(new Date());
            String msg="Alert! "+ "Dear User" +" exceeded the Running speed limit of "+speed+ " km/hr on "+dateTime+" at "+ address ;
            try
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, msg, null, null);
            }
            catch (Exception ErrVar)
            {
                buildAlertMessage(ErrVar.getMessage().toString());
                ErrVar.printStackTrace();
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 10);
            }
        }

    }
//    private void getLocation() {
//        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Req_Location);
//
//        } else {
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location != null) {
//                latitude = location.getLatitude();
//                langitude = location.getLongitude();
//            } else {
//                Snackbar.make(findViewById(R.id.setSpeed), "Unable find your location", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//            }
//        }
//    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(RunningActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(RunningActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RunningActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Req_Location);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                langitude = location.getLongitude();
                Toast.makeText(this,"Lat"+latitude+"lang"+langitude,Toast.LENGTH_LONG).show();

            } else {
                Snackbar.make(findViewById(R.id.setSpeed), "Unable find your location", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                //Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                strAdd = city + ", "+ state;
                Log.w("My", strAdd.toString());
            } else {
                Log.w("My", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        langitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(RunningActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_LONG).show();
    }
}