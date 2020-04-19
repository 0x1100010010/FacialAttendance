package com.shahzadakhtar.attendancecam.Activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shahzadakhtar.attendancecam.Model.Class;
import com.shahzadakhtar.attendancecam.Model.Student;
import com.shahzadakhtar.attendancecam.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManualAttendenceActivity extends AppCompatActivity {


    DatabaseReference businessDatabase;

    Spinner departmentSpinner, courseSpinner;

    ArrayList<String> departments = new ArrayList<>();
    ArrayList<String> computerScienceList = new ArrayList<>();
    ArrayList<String> managementScienceList = new ArrayList<>();
    ArrayList<String> commerceList = new ArrayList<>();
    ArrayList<String> humanitiesList = new ArrayList<>();
    ArrayList<String> artFashionList = new ArrayList<>();
    ArrayList<String> lifeHealthList = new ArrayList<>();
    ArrayList<String> lawList = new ArrayList<>();

    String selectedDepartment;
    String selectedClass;
    ArrayAdapter<String> courseAdapter;

    int selectedDepartmentId = 0;

    @BindView(R.id.studentSpinner)
    Spinner studentSpinner;
    String selectedStudent;
    ProgressDialog progressDialog;
    String attendanceStatus = "absent";
    @BindView(R.id.rbAbsent)
    RadioButton rbAbsent;
    @BindView(R.id.rbPresent)
    RadioButton rbPresent;
    ArrayList<Student> students;

    Class newClass;
    @BindView(R.id.tvClass)
    TextView tvClass;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_attendence);

        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Marking attendance...");
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

        courseSpinner = findViewById(R.id.courseSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departments);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(arrayAdapter);

        courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, computerScienceList);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);

//        loadStudents();

        studentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStudent = studentSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = departmentSpinner.getSelectedItem().toString();
                switch (position) {
                    case 0:
                        selectedDepartmentId = 0;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, computerScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 1:
                        selectedDepartmentId = 1;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, managementScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 2:
                        selectedDepartmentId = 2;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, commerceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 3:
                        selectedDepartmentId = 3;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, humanitiesList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 4:
                        selectedDepartmentId = 4;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, artFashionList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 5:
                        selectedDepartmentId = 5;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, lifeHealthList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 6:
                        selectedDepartmentId = 6;
                        courseAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, lawList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                }

                courseSpinner.setAdapter(courseAdapter);
                selectedDepartment = departmentSpinner.getSelectedItem().toString();
//                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedClass = courseSpinner.getSelectedItem().toString();

//                loadStudents();
                /*switch (position) {
                    case 0:
                        selectedClass = computerScienceList.get(position);
                        break;
                    case 1:

                        selectedClass = managementScienceList.get(position);
                        break;
                    case 2:

                        selectedClass = commerceList.get(position);
                        break;
                    case 3:

                        selectedClass = humanitiesList.get(position);
                        break;
                    case 4:
                        selectedClass = artFashionList.get(position);
                        break;
                    case 5:
                        selectedClass = lifeHealthList.get(position);
                        break;
                    case 6:

                        selectedClass = lawList.get(position);
                        break;
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadStudents() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Students");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> titleList = new ArrayList<String>();
                students = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String department = dataSnapshot1.child("department").getValue(String.class);
                    String courseName = dataSnapshot1.child("courseName").getValue(String.class);
                    String studentName = dataSnapshot1.child("studentName").getValue(String.class);
                    String parentName = dataSnapshot1.child("parentName").getValue(String.class);
                    String parentNumber = dataSnapshot1.child("parentNumber").getValue(String.class);
                    String studentId = dataSnapshot1.child("studentId").getValue(String.class);

                    Log.e("dept", ">" + department);
                    Log.e("cour", ">" + courseName);

                    if (department == null) {
                        return;
                    }
                    if (courseName == null) {
                        return;
                    }
                    if (department.equals(selectedDepartment) && courseName.equals(selectedClass)) {

                        Student student = new Student();
                        student.setParentName(parentName);
                        student.setParentNumber(parentNumber);
                        student.setStudentId(studentId);
                        students.add(student);

                        titleList.add(studentName);
                    }

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManualAttendenceActivity.this, android.R.layout.simple_spinner_item, titleList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                studentSpinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManualAttendenceActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            departments.add("Faculty of Computer Science");
            departments.add("Faculty of Management Sciences");
            departments.add("Faculty of Commerce");
            departments.add("Faculty of Humanities and Social Sciences");
            departments.add("Faculty of Art and Fashion Design");
            departments.add("Faculty of Life and Health Sciences");
            departments.add("Faculty of Law");
        }

    }


    @OnClick(R.id.btnMark)
    void btnMarkOnClick(View view) {

        if (rbPresent.isChecked()) {
            attendanceStatus = "present";
        } else if (rbAbsent.isChecked()) {
            attendanceStatus = "absent";
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        progressDialog.show();
        businessDatabase = FirebaseDatabase.getInstance().getReference().child("Attendance").push();

        HashMap<String, String> businessMap = new HashMap<>();
        businessMap.put("attendanceId", businessDatabase.getKey());
        businessMap.put("studentId", students.get(studentSpinner.getSelectedItemPosition()).getStudentId());
        businessMap.put("studentName", selectedStudent);
        businessMap.put("classId", newClass.getClassId());
        businessMap.put("className", newClass.getClassName());
        businessMap.put("courseName", newClass.getCourseName());
        businessMap.put("semester", newClass.getClassSemester() + "");
        businessMap.put("teacherId", newClass.getTeacherId() + "");
        businessMap.put("roomNo", newClass.getRoomNo() + "");
        businessMap.put("session", newClass.getClassSession() + "");
        businessMap.put("day", day + "");
        businessMap.put("month", "" + month);
        businessMap.put("year", "" + year);
        businessMap.put("department", newClass.getDepartment());
        businessMap.put("attendanceStatus", attendanceStatus);

        businessDatabase.setValue(businessMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                sendText(students.get(studentSpinner.getSelectedItemPosition()).getParentNumber(), "Dear " + students.get(studentSpinner.getSelectedItemPosition()).getParentName() + ", your son/daughter " + studentSpinner.getSelectedItem().toString() + " is " + attendanceStatus + " today.");
                Toast.makeText(ManualAttendenceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ManualAttendenceActivity.this, "bExcp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void sendText(String number, String sms) {
        String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        SmsManager.getDefault().sendTextMessage(number, null, sms, sentPI, deliveredPI);
    }


}
