package com.shahzadakhtar.attendancecam.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shahzadakhtar.attendancecam.MainActivity;
import com.shahzadakhtar.attendancecam.MyPrefs;
import com.shahzadakhtar.attendancecam.R;

public class SplashActivity extends Activity {

    FirebaseAuth auth;
    MyPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();

        prefs = new MyPrefs(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();

                if (auth != null) {
                    Toast.makeText(SplashActivity.this, "not null", Toast.LENGTH_SHORT).show();
                    if (prefs.getType().equals("std")) {
                        Intent intent = new Intent(SplashActivity.this, StudentActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (prefs.getType().equals("teacher")) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else{
                        startActivity(new Intent(SplashActivity.this, StartingActivity.class));
                    }
                } else {
                    Toast.makeText(SplashActivity.this, "auth null", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this, StartingActivity.class));
                }

            }
        }, 3000);

    }
}
