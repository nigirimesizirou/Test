package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class count_activity extends AppCompatActivity implements SensorEventListener {

    private static final long START_TIME=10000;

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button getmButtonReset;
    private Button  returnButton;
    private  CountDownTimer mCountDownTimer;
    private  boolean mTimerRunning;

//    private long mTimeLeftInMillis= START_TIME;

    private long data1=START_TIME;
    private long time;

//    public static class SampleGlobal{
//        public  static long data1;
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        

        System.out.println("mTimerRunningの初期値は？ " + mTimerRunning);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        // センサーオブジェクトを取得
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 近接センサーのオブジェクトを取得
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        //MAINからの値を取得
        Intent intent = getIntent();
        long gdata= intent.getLongExtra("SEND_DATA",10000);
        long gdata_2 = intent.getLongExtra("SEND_DATA_2",10000);
        time = gdata*60+gdata_2;
        data1=time*1000;


        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        getmButtonReset = findViewById(R.id.buttonreset);
        returnButton = findViewById(R.id.return_btn2);

        mButtonStartPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println(mTimerRunning);
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });


        getmButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetTimer();
            }
        });

        updateCountDownText();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
// 近接センサーを有効
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 近接センサーを無効
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // values = 5 or 0
            if (event.values[0] > 0 ) {
            } else if (mTimerRunning == true ){
                pauseTimer();
            } else{
                startTimer();
            }
        }
    }

    private void startTimer(){

        mCountDownTimer = new CountDownTimer(data1,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                data1 = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("スタート");
                getmButtonReset.setVisibility(View.INVISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("一時停止");
        getmButtonReset.setVisibility(View.INVISIBLE);
    }
    private void pauseTimer(){
        System.out.println("一時停止処理前：" + mTimerRunning);
        mCountDownTimer.cancel();
        mTimerRunning = false;
        System.out.println("一時停止処理後：" + mTimerRunning);
        mButtonStartPause.setText("スタート");
        getmButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer(){
        data1 = time*1000;
        updateCountDownText();
        mButtonStartPause.setVisibility(View.VISIBLE);
        getmButtonReset.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText(){

//        int minutes = (int)(mTimeLeftInMillis/1000)/60;
//        int seconds = (int)(mTimeLeftInMillis/1000)%60;
        int minutes = (int)(data1/1000)/60;
        int seconds = (int)(data1/1000)%60;
        String timerLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timerLeftFormatted);
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}

