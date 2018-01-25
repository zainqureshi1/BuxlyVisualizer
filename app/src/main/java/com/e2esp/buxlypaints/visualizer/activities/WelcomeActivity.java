package com.e2esp.buxlypaints.visualizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;

import com.e2esp.buxlypaints.visualizer.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        AppCompatButton buttonGetStarted = findViewById(R.id.buttonGetStarted);
        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetStartedButtonClick();
            }
        });
    }

    private void onGetStartedButtonClick() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
