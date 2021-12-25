package com.example.adgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.adgame.http.httpThread;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    EditText editTextName;
    EditText editTextPassword;
    EditText editTextPassword2;
    EditText editTextEmail;
    EditText editTextPhone;
    Button signup_btn;
    Button cancel_btn;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = (EditText) findViewById(R.id.editTextTextPersonName2);
        editTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        editTextPassword2 = (EditText) findViewById(R.id.editTextTextPassword2);
        editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        signup_btn = (Button) findViewById(R.id.signup_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_signup);
        progress = (ProgressBar) findViewById(R.id.progressBar_signup);

        signup_btn.setOnClickListener(v -> {
            ContentValues params = new ContentValues();
            params.put("email", editTextEmail.getText().toString());
            params.put("pw", editTextPassword.getText().toString());
            params.put("name", editTextName.getText().toString());
            params.put("phoneNum", editTextPhone.getText().toString());
            httpThread http = new httpThread();
            http.setParams("/login", params, "", "PUT");

            progress.setVisibility(View.VISIBLE);
            http.start();
            try {
                http.join();
                JSONObject jObj = new JSONObject(http.getRes());

                // shared preference 잘 작동하는지 확인 코드
                // System.out.println(pref.getString("token", "NO VALUE SAVED!!"));
                // login_progress.setVisibility(View.INVISIBLE);

                finish();
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        });

        cancel_btn.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * 키보드 다른 영역 터치시 키보드 내리기
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null
                && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE)
                && view instanceof EditText
                && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}