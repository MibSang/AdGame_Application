package com.example.adgame;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int CARD_NUM = 5;
    ImageView[] myCardViews = new ImageView[CARD_NUM];
    ImageView[] opCardViews = new ImageView[CARD_NUM];
    WebSocketClient mWebSocketClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        myCardViews[0] = (ImageView) findViewById(R.id.myCard1);
        myCardViews[1] = (ImageView) findViewById(R.id.myCard2);
        myCardViews[2] = (ImageView) findViewById(R.id.myCard3);
        myCardViews[3] = (ImageView) findViewById(R.id.myCard4);
        myCardViews[4] = (ImageView) findViewById(R.id.myCard5);
        opCardViews[0] = (ImageView) findViewById(R.id.opCard1);
        opCardViews[1] = (ImageView) findViewById(R.id.opCard2);
        opCardViews[2] = (ImageView) findViewById(R.id.opCard3);
        opCardViews[3] = (ImageView) findViewById(R.id.opCard4);
        opCardViews[4] = (ImageView) findViewById(R.id.opCard5);
        for (int i = 0; i < CARD_NUM; i++)
            myCardViews[i].setOnClickListener(this);
        connectWebSocket();
        JSONObject json = new JSONObject();
        SharedPreferences pref = getSharedPreferences("jwt", MODE_PRIVATE);
        String token = pref.getString("token", "");
        try {
            json.put("r", "single");
            json.put("token", "Bearer " + token);
            while (!mWebSocketClient.isOpen()) {
                Thread.sleep(1000);
            }
            sendMessage(json.toString());
            Thread.sleep(1000);

            json.put("r", "ready");
            sendMessage(json.toString());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        for (int i = 0; i < CARD_NUM; i++) {
            if (v == myCardViews[i]) {
                JSONObject json = new JSONObject();
                try {
                    json.put("r", "card");
                    json.put("choice", Integer.toString(i+1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendMessage(json.toString());
                myCardViews[i].setColorFilter(Color.parseColor("#55ff0000"));
            }
            else {
                myCardViews[i].setColorFilter(null);
            }
        }
    }

    private void connectWebSocket() {
        System.out.println("Websocket connect");
        URI uri;
        JSONObject json = new JSONObject();
        try {
            uri = new URI("ws://ec2-13-124-160-65.ap-northeast-2.compute.amazonaws.com:8080/ws/game");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("Websocket Opened");
                try {
                    json.put("r", "hello");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mWebSocketClient.send(json.toString());
            }

            @Override
            public void onMessage(String s) {
                System.out.println("server: " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Websocket Closed" + s);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Websocket Error" + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(String payload) {
        System.out.println("client: "+payload);
        mWebSocketClient.send(payload);
    }
}
