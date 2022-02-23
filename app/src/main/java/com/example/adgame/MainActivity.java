package com.example.adgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.adgame.http.HttpThread;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView welcome_text;
    Button startGame_btn;
    Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome_text = (TextView) findViewById(R.id.welcome_text);
        startGame_btn = (Button) findViewById(R.id.startGame_btn);
        logout_btn = (Button) findViewById(R.id.logout_btn);

        SharedPreferences pref = getSharedPreferences("jwt", MODE_PRIVATE);
        String token = pref.getString("token", "");
        HttpThread http = new HttpThread();

        http.setParams("/main", null, token, "GET");
        http.start();
        try {
            http.join();
            JSONObject jObj;
            if (http.getRes() == "") {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
            jObj = new JSONObject(http.getRes());
            welcome_text.setText(String.format("%s님 환영합니다.", jObj.getString("nickname")));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        startGame_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });
        logout_btn.setOnClickListener(v -> {
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}