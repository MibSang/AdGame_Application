package com.example.adgame;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int CARD_NUM = 5;
    ImageView[] myCardViews = new ImageView[CARD_NUM];
    ImageView[] opCardViews = new ImageView[CARD_NUM];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCardViews[0] = (ImageView)findViewById(R.id.myCard1);
        myCardViews[1] = (ImageView)findViewById(R.id.myCard2);
        myCardViews[2] = (ImageView)findViewById(R.id.myCard3);
        myCardViews[3] = (ImageView)findViewById(R.id.myCard4);
        myCardViews[4] = (ImageView)findViewById(R.id.myCard5);
        opCardViews[0] = (ImageView)findViewById(R.id.opCard1); // 추후 상대 card가 shuffle된 경우 대비 view할당
        opCardViews[1] = (ImageView)findViewById(R.id.opCard2);
        opCardViews[2] = (ImageView)findViewById(R.id.opCard3);
        opCardViews[3] = (ImageView)findViewById(R.id.opCard4);
        opCardViews[4] = (ImageView)findViewById(R.id.opCard5);
        for (int i = 0; i < CARD_NUM; i++)
            myCardViews[i].setOnClickListener(this);
    }

    public void onClick(View v) {
        for (int i = 0; i < CARD_NUM; i++) {
            if (v == myCardViews[i]){
                myCardViews[i].setColorFilter(Color.parseColor("#55ff0000"));
                //Toast.makeText(getApplication(), "selected "+i, Toast.LENGTH_SHORT).show();
            }
            else {
                myCardViews[i].setColorFilter(null);
            }
        }
    }
}