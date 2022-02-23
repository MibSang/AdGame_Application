package com.example.adgame;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int CARD_NUM = 5;
    final ImageView[] myCardViews = new ImageView[CARD_NUM];
    final ImageView[] opCardViews = new ImageView[CARD_NUM];
    TextView opScore;
    TextView myScore;
    TextView result;
    TextView timeleft;
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
        opScore = (TextView) findViewById(R.id.opScore);
        myScore = (TextView) findViewById(R.id.myScore);
        result = (TextView) findViewById(R.id.result);
        timeleft = (TextView) findViewById(R.id.timeleft);

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
            }

            @Override
            public void onMessage(String s) {
                System.out.println("server: " + s);
                final JSONObject json;
                String r;
                try {
                    json = new JSONObject(s);
                    r = json.getString("r");

                    switch (r) {
                        case "stage":
                            final int op = json.getInt("opponent");
                            final int me = json.getInt("player");

                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    opCardViews[op-1].setVisibility(View.INVISIBLE);
                                    myCardViews[me-1].setVisibility(View.INVISIBLE);
                                }
                            };
                            timer.schedule(task, 3000);

                            opCardViews[op-1].setColorFilter(Color.parseColor("#55ff0000"));
                            myCardViews[me-1].setColorFilter(Color.parseColor("#55ff0000"));

                            if (op < me)
                                myScore.setText(String.format("%d", Integer.parseInt((String) myScore.getText()) + 1));
                            else if (me < op)
                                opScore.setText(String.format("%d", Integer.parseInt((String) opScore.getText()) + 1));


                            break;
                        case "final":
                            if (json.getString("result").equals("win"))
                                result.setText("승리");
                            else if (json.getString("result").equals("lose"))
                                result.setText("패배");
                            else
                                result.setText("무승부");
                            break;
                        case "timeleft":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        timeleft.setText(String.format("%d", Integer.parseInt(json.getString("timeleft"))));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Websocket Closed" + s);
                finish();
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
