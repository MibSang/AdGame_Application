package com.example.adgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.adgame.http.HttpThread;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText email_text;
    EditText password_text;
    Button login_button;
    Button register_button;
    ProgressBar login_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_text = (EditText) findViewById(R.id.emailText);
        password_text = (EditText) findViewById(R.id.passwordText);
        login_button = (Button) findViewById(R.id.loginBtn);
        register_button = (Button) findViewById(R.id.registerBtn);
        login_progress = (ProgressBar) findViewById(R.id.progressBar);

        /**
         * login button 클릭시 람다함수 실행
         */
        login_button.setOnClickListener(v -> {
            ContentValues params = new ContentValues();
            params.put("email", email_text.getText().toString());
            params.put("pw", password_text.getText().toString());
            SharedPreferences pref = getSharedPreferences("jwt", MODE_PRIVATE);
            HttpThread http = new HttpThread();
            http.setParams("/login", params, "", "GET");
            SharedPreferences.Editor edit = pref.edit();

            login_progress.setVisibility(View.VISIBLE);
            http.start();
            try {
                http.join();
                login_progress.setVisibility(View.INVISIBLE);
                JSONObject jObj = new JSONObject(http.getRes());
                String token;

                if (http.getRes().equals("") || http.getRes() == null || http.getRes().equals("null"))
                    return;
                token = jObj.getString("token");
                if (token.equals("") || token.equals("null")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("오류").setMessage("잘못된 로그인 정보입니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }

                edit.putString("token", token);
                edit.apply();

                // shared preference 잘 작동하는지 확인 코드
                // System.out.println(pref.getString("token", "NO VALUE SAVED!!"));
                // login_progress.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        });

        register_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
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