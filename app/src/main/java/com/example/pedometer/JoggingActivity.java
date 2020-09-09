package com.example.pedometer;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static java.lang.String.valueOf;

public class JoggingActivity extends AppCompatActivity {

EditText meditinput;
TextView mtextcountdown;
Button startpause,reset,set;

CountDownTimer countDownTimer;
private boolean mtimerrunning;

private long mstarttimeinmills;
private long mtimeleftinmilles;
private long mendtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        meditinput=findViewById(R.id.editminute);
        mtextcountdown=findViewById(R.id.count);
        startpause=findViewById(R.id.buttonstart);
        reset=findViewById(R.id.btnreset);
        set=findViewById(R.id.set);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input=meditinput.getText().toString();
                if(input.length()==0){
                    Toast.makeText(JoggingActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisinput= Long.parseLong(input)*60000;
                if(millisinput==0){
                    Toast.makeText(JoggingActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisinput);
                meditinput.setText("");
            }
        });

        startpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mtimerrunning){
                    pauseTimer();
                }else{
                    startTimer();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void startTimer() {

        mendtime = System.currentTimeMillis() + mtimeleftinmilles;
        countDownTimer = new CountDownTimer(mtimeleftinmilles, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mtimeleftinmilles = millisUntilFinished;
               updateCountdowntext();
            }
            @Override
            public void onFinish() {
                mtimerrunning = false;
                UpdateWatchInterface();
            }
        }.start();
        mtimerrunning = true;
       UpdateWatchInterface();
    }

    private void updateCountdowntext() {

        int hours =(int) (mtimeleftinmilles/1000)/3600;
        int minutes=(int) ((mtimeleftinmilles /1000)%3600)/60;
        int seconds=(int)(mtimeleftinmilles/1000) %60;

        String timeleftformatted;
        if(hours > 0){
            timeleftformatted = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        }else{
            timeleftformatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

        }
        mtextcountdown.setText(timeleftformatted);

        if(seconds == 0 && minutes == 0 && hours == 0 ){
            Uri notification= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer r=MediaPlayer.create(getApplicationContext(),notification);
            r.start();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(5000);
        }
    }


    private void setTime(long milliseconds){

        mstarttimeinmills=milliseconds;
        resetTimer();
        closeKeyboard();

    }

    private void closeKeyboard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void pauseTimer() {
        countDownTimer.cancel();
        mtimerrunning = false;
       UpdateWatchInterface();

    }
    private void resetTimer() {
        mtimeleftinmilles = mstarttimeinmills;
       updateCountdowntext();
      UpdateWatchInterface();

    }
    private void UpdateWatchInterface(){

        if(mtimerrunning){
            meditinput.setVisibility(View.VISIBLE);
            set.setVisibility(View.VISIBLE);
           reset.setVisibility(View.VISIBLE);
            startpause.setText("pause");
        }else{
            meditinput.setVisibility(View.VISIBLE);
            set.setVisibility(View.VISIBLE);
            startpause.setText("Start");
            if(mtimeleftinmilles<1000){
                startpause.setVisibility(View.VISIBLE);

            }else{
                startpause.setVisibility(View.VISIBLE);
            }
            if(mtimeleftinmilles<mstarttimeinmills){
                startpause.setVisibility(View.VISIBLE);
            }else
            {
                startpause.setVisibility(View.VISIBLE);
            }
        }
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", mstarttimeinmills);
        editor.putLong("millisLeft", mtimeleftinmilles);
        editor.putBoolean("timerRunning", mtimerrunning);
        editor.putLong("endTime",mendtime );
        editor.apply();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mstarttimeinmills = prefs.getLong("startTimeInMillis", 600000);
        mtimeleftinmilles = prefs.getLong("millisLeft", mstarttimeinmills);
        mtimerrunning = prefs.getBoolean("timerRunning", false);
       updateCountdowntext();
        UpdateWatchInterface();
        if (mtimerrunning) {
            mendtime = prefs.getLong("endTime", 0);
            mtimeleftinmilles = mendtime - System.currentTimeMillis();
            if (mtimeleftinmilles < 0) {
               mtimeleftinmilles = 0;
                mtimerrunning = false;
                updateCountdowntext();
                UpdateWatchInterface();
            } else {
                startTimer();
            }
        }
    }*/
}
