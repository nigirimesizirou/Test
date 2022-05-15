package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity_subレイアウトをセット
        setContentView(R.layout.activity_sub);

        //MAINからの値を取得
        Intent intent = getIntent();
        String gdata= intent.getStringExtra("SEND_DATA");
        String gdata_2 = intent.getStringExtra("SEND_DATA_2");

        //値をセット
        TextView textView = findViewById(R.id.gettext);
        textView.setText(gdata);
        TextView textView_2 = findViewById(R.id.gettext_2);
        textView_2.setText(gdata_2);

        Button returnButton = findViewById(R.id.return_btn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}