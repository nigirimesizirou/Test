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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//audio
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.content.res.AssetFileDescriptor;
import java.io.IOException;

import java.util.Locale;

public class count_activity extends AppCompatActivity implements SensorEventListener {

    private static final long START_TIME=10000;

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button getmButtonReset;
    private Button  returnButton;
    private MediaPlayer mediaPlayer;
    private CountDownTimer mCountDownTimer;

    private  boolean mTimerRunning;

    private  boolean States = false;

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
                States = false;

            } else{
                if(States == false){
                    startTimer();
                    States = true;
                }
                else if(States == true){
                    resetTimer();
                    States = false;
                }
                }
        }
        }




    private void startTimer(){

        mCountDownTimer = new CountDownTimer(data1,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                data1 = millisUntilFinished;
                updateCountDownText();
                States = false;
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("スタート");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                getmButtonReset.setVisibility(View.VISIBLE);
                audioPlay();
                States = true;

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
        States = false;
        System.out.println("一時停止処理後：" + mTimerRunning);
        mButtonStartPause.setText("スタート");
        getmButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer(){
        data1 = time*1000;
        updateCountDownText();
        mButtonStartPause.setVisibility(View.VISIBLE);
        getmButtonReset.setVisibility(View.INVISIBLE);
        if(States == true) {
            audioStop();
        }
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

//}

// audio
    private boolean audioSetup(){
        // インタンスを生成
        mediaPlayer = new MediaPlayer();

        //音楽ファイル名, あるいはパス
        String filePath = "music.mp3";

        boolean fileCheck = false;

        // assetsから mp3 ファイルを読み込み
        try(AssetFileDescriptor afdescripter = getAssets().openFd(filePath))
        {
            // MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            // 音量調整を端末のボタンに任せる
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            fileCheck = true;
            mediaPlayer.setLooping(true);//    ループ設定
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return fileCheck;
    }

    private void audioPlay() {

        if (mediaPlayer == null) {
            // audio ファイルを読出し
            if (audioSetup()) {
                Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
        }

        // 再生する
        mediaPlayer.start();

         //終了を検知するリスナー
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                audioStop();
            }
        });
//        lambda
        mediaPlayer.setOnCompletionListener( mp -> {
            Log.d("debug","end of audio");
            audioStop();
        });

    }

    private void audioStop() {
        // 再生終了
        mediaPlayer.stop();
        // リセット
        mediaPlayer.reset();
        // リソースの解放
        mediaPlayer.release();

        mediaPlayer = null;
    }
}

//    }
//}