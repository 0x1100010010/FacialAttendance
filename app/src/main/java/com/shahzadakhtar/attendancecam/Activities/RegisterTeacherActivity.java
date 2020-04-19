package com.shahzadakhtar.attendancecam.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shahzadakhtar.attendancecam.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterTeacherActivity extends AppCompatActivity {


    Spinner departmentSpinner;

    ArrayList<String> departments = new ArrayList<>();
    ArrayList<String> computerScienceList = new ArrayList<>();
    ArrayList<String> managementScienceList = new ArrayList<>();
    ArrayList<String> commerceList = new ArrayList<>();
    ArrayList<String> humanitiesList = new ArrayList<>();
    ArrayList<String> artFashionList = new ArrayList<>();
    ArrayList<String> lifeHealthList = new ArrayList<>();
    ArrayList<String> lawList = new ArrayList<>();

    String selectedDepartment;
    String selectedCourse;

    ArrayAdapter<String> courseAdapter;

    int selectedDepartmentId = 0;

    ProgressDialog progressDialog;

    DatabaseReference businessDatabase;

    @BindView(R.id.etFullName)
    EditText etFullName;

    @BindView(R.id.etContactNo)
    EditText etContactNo;

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etPassword)
    EditText etPassword;


    @BindView(R.id.etDegree)
    EditText etDegree;


    @BindView(R.id.etSalary)
    EditText etSalary;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;

    ArrayList<String> genderList = new ArrayList<>();

    String selectedGender = "Male";


    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);


        ButterKnife.bind(this);


        auth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        initDepartments();
        initComputerScience();
        initManagementScience();
        initCommerce();
        initHumanities();
        initArtFashion();
        initLifeHealth();
        initLaw();
        initGenders();

      /*  departmentSpinner = findViewById(R.id.departmentSpinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departments);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(arrayAdapter);*/

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = genderList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    /*    departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


    }

    private void initGenders() {

        if (genderList.size() == 0) {

            genderList.add("Male");
            genderList.add("Female");
        }

    }

    private void initLaw() {

        if (lawList.size() == 0) {
            lawList.add("Bachelor of Law(LLB)");
        }

    }

    private void initLifeHealth() {

        if (lifeHealthList.size() == 0) {
            lifeHealthList.add("Doctor of Physiotherapy");
            lifeHealthList.add("Bachelor of Food & Nutrition");
        }

    }

    private void initArtFashion() {

        if (artFashionList.size() == 0) {
            artFashionList.add("Bachelor of Architecture");
            artFashionList.add("Bachelor of Building & Construction");
            artFashionList.add("Bachelor of Fashion Design(Hons)");
            artFashionList.add("Bachelor of Interior Design");
            artFashionList.add("Bachelor of Textile Design");
        }

    }

    private void initHumanities() {

        if (humanitiesList.size() == 0) {
            humanitiesList.add("BS(Hons) English");
            humanitiesList.add("BS(Hons) English: Literature");
            humanitiesList.add("BS(Hons) English: Language/Linguistics");
            humanitiesList.add("BS(Hons) Psychology");
            humanitiesList.add("BS(Hons) Sociology");
            humanitiesList.add("Bachelor of Media Studies (Hons)");
            humanitiesList.add("Bachelor of Media Studies (Hons): Electronic Media");
            humanitiesList.add("Bachelor of Media Studies (Hons): Print Media");
            humanitiesList.add("Bachelor of Media Studies (Hons): Advertising & Public Relations");
            humanitiesList.add("BS(Hons) Education");
            humanitiesList.add("BS(Hons) Mathematics");
            humanitiesList.add("BS(Hons) Physical Education");
        }

    }

    private void initCommerce() {

        if (commerceList.size() == 0) {
            commerceList.add("Bachelor of Commerce");
            commerceList.add("B.Com(Hons): Accounting &amp; Finance");
            commerceList.add("B.Com(Hons): Marketing");
            commerceList.add("B.Com(Hons): Economics");
            commerceList.add("B.Com(Hons): Business Analytics");
            commerceList.add("BS(Hons): Accounting & Finance");
        }

    }

    private void initManagementScience() {

        if (managementScienceList.size() == 0) {
            managementScienceList.add("BBA(Hons)");
            managementScienceList.add("BBA(Hons): Human Resource Management");
            managementScienceList.add("BBA(Hons): Management");
            managementScienceList.add("BBA(Hons): Marketing");
            managementScienceList.add("BBA(Hons): Innovation & Entrepreneurship");
            managementScienceList.add("BBA(Hons): Accounting & Finance");
            managementScienceList.add("BBA(Hons): Economics");
            managementScienceList.add("BBA(Hons): Operations Management");
            managementScienceList.add("BBA(Hons): Public Administration");
        }

    }

    private void initComputerScience() {

        if (computerScienceList.size() == 0) {
            computerScienceList.add("Bachelor in Computer Science");
            computerScienceList.add("Bachelor in Software Engineering");
            computerScienceList.add("Bachelor in Information Technology");
            computerScienceList.add("BSCS(Hons): Robotics");
            computerScienceList.add("BSCS(Hons): Computer Networking");
            computerScienceList.add("BSCS(Hons): Game Development");
            computerScienceList.add("BSCS(Hons): Artificial Intelligence");
            computerScienceList.add("BSCS(Hons): Digital Media and Web Technology");
            computerScienceList.add("BSCS(Hons): Data Sciences");
        }

    }

    private void initDepartments() {

        if (departments.size() == 0) {
            departments.add("M.SC");
            departments.add("M.Phil");
            departments.add("Phd");
        }

    }

    public void addStudent() {

        if(TextUtils.isEmpty(etEmail.getText().toString())){
            etEmail.setError("Enter email");
            return;
        }
        if(!etEmail.getText().toString().endsWith("usa.edu.pk")){
            etEmail.setError("Enter valid email");
            return;
        }

        progressDialog.show();

        businessDatabase = FirebaseDatabase.getInstance().getReference().child("Teachers").push();

        HashMap<String, String> businessMap = new HashMap<>();
        businessMap.put("teacherId", businessDatabase.getKey());
        businessMap.put("teacherName", etFullName.getText().toString());
        businessMap.put("teacherEmail", etEmail.getText().toString());
        businessMap.put("birthDay", "18");
        businessMap.put("birthMonth", "2");
        businessMap.put("birthYear", "1993");
        businessMap.put("gender", selectedGender);
        businessMap.put("phoneNo", etContactNo.getText().toString());
        businessMap.put("degree", etDegree.getText().toString());
        businessMap.put("salary", etSalary.getText().toString());
        businessMap.put("address", etAddress.getText().toString());

        businessDatabase.setValue(businessMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(RegisterTeacherActivity.this, "Teacher Registered", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(RegisterTeacherActivity.this, LoginActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterTeacherActivity.this, "bExcp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @OnClick(R.id.btnRegister)
    void btnRegisterOnClick(View view) {

        auth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {

                    auth = FirebaseAuth.getInstance();

                    addStudent();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterTeacherActivity.this, "excp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }



}
