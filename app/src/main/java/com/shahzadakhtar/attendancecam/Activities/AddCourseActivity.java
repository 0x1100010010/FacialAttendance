package com.shahzadakhtar.attendancecam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shahzadakhtar.attendancecam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddCourseActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinnerClasses;
    EditText etCourseName, etCourseCode, etTotalStudents;
    Button btnAdd;

    DatabaseReference businessDatabase;
    ProgressDialog progressDialog;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding course");

        spinnerClasses = findViewById(R.id.spinnerClasses);

        etCourseName = findViewById(R.id.etCourseName);
        etCourseCode = findViewById(R.id.etCourseCode);
        etTotalStudents = findViewById(R.id.etTotalStudents);

        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(this);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Classes");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> titleList = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String titlename = dataSnapshot1.child("className").getValue(String.class);
                    titleList.add(titlename);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_spinner_item, titleList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClasses.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddCourseActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd:

                if(TextUtils.isEmpty(etCourseName.getText().toString())){
                    etCourseName.setError("Enter course name");
                    return;
                }
                if(TextUtils.isEmpty(etCourseCode.getText().toString())){
                    etCourseCode.setError("Enter course code");
                    return;
                }
                if(TextUtils.isEmpty(etTotalStudents.getText().toString())){
                    etTotalStudents.setError("Enter total students");
                    return;
                }

                progressDialog.show();

                businessDatabase = FirebaseDatabase.getInstance().getReference().child("Courses").push();

                String className = (String) spinnerClasses.getSelectedItem();

                HashMap<String, String> businessMap = new HashMap<>();
                businessMap.put("courseId", businessDatabase.getKey());
                businessMap.put("courseName", etCourseName.getText().toString());
                businessMap.put("courseCode", etCourseCode.getText().toString());
                businessMap.put("className", className);
                businessMap.put("classId", "classId");
                businessMap.put("totalStudents", ""+etTotalStudents.getText().toString());
                businessMap.put("presentStudents", "0");
                businessMap.put("absentStudents", "0");

                businessDatabase.setValue(businessMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCourseActivity.this, "Class added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCourseActivity.this, "bExcp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                break;
        }
    }
}
