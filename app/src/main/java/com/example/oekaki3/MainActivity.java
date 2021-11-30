package com.example.oekaki3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    NewView newView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newView = findViewById(R.id.view2);
    }

    public void onClearBotton(View view) {
        newView.clear();
    }

    public void onTurnBotton(View view) {newView.mPaint.setColor(Color.RED);
    }
}