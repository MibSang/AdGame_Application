package com.example.adgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.adgame.http.HttpThread;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("jwt", MODE_PRIVATE);
        String token = pref.getString("token", "");
        System.out.println(token);
        HttpThread http = new HttpThread();

        if (!token.equals("")) {
            http.setParams("/", null, token, "GET");
            http.start();
            try {
                http.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent intent;
        System.out.println("response");
        System.out.println("");
        if (!http.getRes().equals(""))
            intent = new Intent(getApplicationContext(), MainActivity.class);
        else
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}