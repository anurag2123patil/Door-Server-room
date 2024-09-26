package com.project.server.room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import logicBox.SharedSpace;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    SharedSpace sharedSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedSpace = new SharedSpace(Splash.this);
        final String loginStatus = sharedSpace.getString("login");
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {

                if (loginStatus != null) {
                    startActivity(new Intent(getApplication(), Home.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplication(), Login.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }
}
