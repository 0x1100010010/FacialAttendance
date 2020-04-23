package com.shahzadakhtar.attendancecam.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shahzadakhtar.attendancecam.R;
import com.transitionseverywhere.TransitionManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartingActivity extends AppCompatActivity implements View.OnClickListener {

    CardView btnRegStd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        ButterKnife.bind(this);

    /*    btnRegStd = findViewById(R.id.btnStdLogin);
        btnRegStd.setOnClickListener(this);*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStdLogin:

                break;
        }
    }

    @OnClick(R.id.btnTeacherLogin)
    void btnManualOnClick(View view){
        //TransitionManager.beginDelayedTransition(transitionsContainer);
        startActivity(new Intent(this, LoginActivity.class).putExtra("type","teacher"));
    }
    @OnClick(R.id.btnStdLogin)
    void btnStdLoginOnClick(View view){
        startActivity(new Intent(this, LoginActivity.class).putExtra("type","std"));
    }
    @OnClick(R.id.btnRegTeacher)
    void btnRegTeacherOnClick(View view){

        startActivity(new Intent(this, RegisterTeacherActivity.class));
    }
    @OnClick(R.id.btnRegStd)
    void btnRegStdOnClick(View view){
        startActivity(new Intent(this, RegisterStudentActivity.class));
    }




}
