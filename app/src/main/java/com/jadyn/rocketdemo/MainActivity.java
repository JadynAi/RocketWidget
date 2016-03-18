package com.jadyn.rocketdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.main_open)
    Button mainOpen;
    @Bind(R.id.main_close)
    Button mainClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.main_open, R.id.main_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_open:
                startService(new Intent(this, RocketService.class));
                break;
            case R.id.main_close:
                stopService(new Intent(this, RocketService.class));
                break;
        }
    }
}
