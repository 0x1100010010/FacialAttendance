package com.shahzadakhtar.attendancecam;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shahzadakhtar.attendancecam.Activities.AddCourseActivity;
import com.shahzadakhtar.attendancecam.Activities.AddStudentActivity;
import com.shahzadakhtar.attendancecam.Activities.ManualAttendenceActivity;
import com.shahzadakhtar.attendancecam.Activities.MarkAttendanceActivity;
import com.shahzadakhtar.attendancecam.Activities.StartingActivity;
import com.shahzadakhtar.attendancecam.Activities.ViewAttendanceActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btnMarkAttendance, btnAddStudent, btnAddClass, btnAddCourse, btnViewAttendance;

    FirebaseAuth auth;
    MyPrefs prefs;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        prefs = new MyPrefs(this);
        auth = FirebaseAuth.getInstance();

        firebaseUser = auth.getCurrentUser();



        /*btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddClass = findViewById(R.id.btnAddClass);
        btnAddCourse = findViewById(R.id.btnAddCourse);*/
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
        btnViewAttendance = findViewById(R.id.btnViewAttendance);

        /*btnAddStudent.setOnClickListener(this);
        btnAddClass.setOnClickListener(this);
        btnAddCourse.setOnClickListener(this);*/
        btnMarkAttendance.setOnClickListener(this);
        btnViewAttendance.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMarkAttendance:
                startActivity(new Intent(this, MarkAttendanceActivity.class));

                break;
            case R.id.btnAddStudent:
                startActivity(new Intent(this, AddStudentActivity.class));

                break;
            case R.id.btnAddClass:

                break;
            case R.id.btnAddCourse:
                startActivity(new Intent(this, AddCourseActivity.class));

                break;
            case R.id.btnViewAttendance:
                startActivity(new Intent(this, ViewAttendanceActivity.class));

                break;
        }
    }



    @OnClick(R.id.btnManualAttendence)
    void btnManualAttendenceOnClick(View view) {
        startActivity(new Intent(this, ManualAttendenceActivity.class));
    }

    @OnClick(R.id.layLogout)
    void layLogoutOnClick(View view) {
        finish();
        prefs.setType("");
        auth.signOut();
        startActivity(new Intent(MainActivity.this, StartingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

}

