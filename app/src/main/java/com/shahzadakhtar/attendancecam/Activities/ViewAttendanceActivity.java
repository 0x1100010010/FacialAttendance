package com.shahzadakhtar.attendancecam.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shahzadakhtar.attendancecam.Adapters.ViewAttendanceAdapter;
import com.shahzadakhtar.attendancecam.Model.Attendance;
import com.shahzadakhtar.attendancecam.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewAttendanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    RecyclerView recyclerAttendance;

    @BindView(R.id.btnSelectDate)
    Button btnSelectDate;
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
    String selectedCourse;
    ArrayAdapter<String> courseAdapter;
    ProgressDialog progressDialog;
    int selectedDepartmentId = 0;
    int dayC = 0, monthC = 0, yearC = 0;
    private DatabaseReference mDatabase;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Showing attendance...");
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

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = departmentSpinner.getSelectedItem().toString();
                switch (position) {
                    case 0:
                        selectedDepartmentId = 0;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, computerScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 1:
                        selectedDepartmentId = 1;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, managementScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 2:
                        selectedDepartmentId = 2;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, commerceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 3:
                        selectedDepartmentId = 3;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, humanitiesList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 4:
                        selectedDepartmentId = 4;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, artFashionList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 5:
                        selectedDepartmentId = 5;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, lifeHealthList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 6:
                        selectedDepartmentId = 6;
                        courseAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, lawList);
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

                selectedCourse = courseSpinner.getSelectedItem().toString();

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


        recyclerAttendance = findViewById(R.id.recyclerAttendance);
        recyclerAttendance.setHasFixedSize(true);
        recyclerAttendance.setLayoutManager(new LinearLayoutManager(this));

        ViewAttendanceAdapter attendanceAdapter = new ViewAttendanceAdapter(this, new ArrayList<Attendance>());
        recyclerAttendance.setAdapter(attendanceAdapter);

        selectedCourse = courseSpinner.getSelectedItem().toString();
        selectedDepartment = departmentSpinner.getSelectedItem().toString();

    }

    @OnClick(R.id.btnSelectDate)
    void btnSelectDateOnClick(View view) {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ViewAttendanceActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
// If you're calling this from a support Fragment
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
// If you're calling this from an AppCompatActivity
// dpd.show(getSupportFragmentManager(), "Datepickerdialog");

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int m = monthOfYear + 1;

        dayC = dayOfMonth;
        monthC = m;
        yearC = year;

        btnSelectDate.setText(dayOfMonth + "/" + m + "/" + year);
    }


    @OnClick(R.id.btnSearch)
    void btnSearchOnClick(View view) {
        loadStudents();
    }


    private void loadStudents() {
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Attendance");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> titleList = new ArrayList<String>();
                ArrayList<Attendance> attendances = new ArrayList<>();
                progressDialog.dismiss();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String attendanceId = dataSnapshot1.child("attendanceId").getValue(String.class);
                    String classId = dataSnapshot1.child("classId").getValue(String.class);
                    String className = dataSnapshot1.child("className").getValue(String.class);
                    String studentId = dataSnapshot1.child("studentId").getValue(String.class);
                    String studentName = dataSnapshot1.child("studentName").getValue(String.class);
                    String courseId = dataSnapshot1.child("courseId").getValue(String.class);
                    String courseName = dataSnapshot1.child("courseName").getValue(String.class);
                    String day = dataSnapshot1.child("day").getValue(String.class);
                    String month = dataSnapshot1.child("month").getValue(String.class);
                    String year = dataSnapshot1.child("year").getValue(String.class);
                    String attendanceStatus = dataSnapshot1.child("attendanceStatus").getValue(String.class);
                    String department = dataSnapshot1.child("department").getValue(String.class);
                    String session = dataSnapshot1.child("session").getValue(String.class);
                    String semester = dataSnapshot1.child("semester").getValue(String.class);
                    String roomNo = dataSnapshot1.child("roomNo").getValue(String.class);

                    Log.e("dept", ">" + department);
                    Log.e("cour", ">" + courseName);

                    if (department == null) {
                        return;
                    }
                    if (courseName == null) {
                        return;
                    }

                    if (dayC != 0) {

                        if(className == null){
                            return;
                        }
                        if(semester == null){
                            return;
                        }
                        if(session == null){
                            return;
                        }
                        if(roomNo == null){
                            return;
                        }

                        if (department.equals(selectedDepartment)
                                && className.equals(selectedCourse)
                                && dayC == Integer.parseInt(day)
                                && monthC == Integer.parseInt(month)
                                && yearC == Integer.parseInt(year)) {

                            titleList.add(studentName);
                            Attendance attendance = new Attendance();
                            attendance.setDepartment(department);
                            attendance.setAttendanceStatus(attendanceStatus);
                            attendance.setYear(Integer.parseInt(year));
                            attendance.setMonth(Integer.parseInt(month));
                            attendance.setDay(Integer.parseInt(day));
                            attendance.setCourseName(courseName);
                            attendance.setCourseId(courseId);
                            attendance.setStudentName(studentName);
                            attendance.setStudentId(studentId);
                            attendance.setClassName(className);
                            attendance.setClassId(classId);
                            attendance.setAttendanceId(attendanceId);
                            attendance.setSession(session);
                            attendance.setSemester(semester);
                            attendance.setRoomNo(roomNo);

                            attendances.add(attendance);
                        }else{
                            Toast.makeText(ViewAttendanceActivity.this, "nothing found", Toast.LENGTH_SHORT).show();
                        }


                    } else {

                        if (department.equals(selectedDepartment) && courseName.equals(selectedCourse)) {
                            titleList.add(studentName);
                            Attendance attendance = new Attendance();
                            attendance.setDepartment(department);
                            attendance.setAttendanceStatus(attendanceStatus);
                            attendance.setYear(Integer.parseInt(year));
                            attendance.setMonth(Integer.parseInt(month));
                            attendance.setDay(Integer.parseInt(day));
                            attendance.setCourseName(courseName);
                            attendance.setCourseId(courseId);
                            attendance.setStudentName(studentName);
                            attendance.setStudentId(studentId);
                            attendance.setClassName(className);
                            attendance.setClassId(classId);
                            attendance.setAttendanceId(attendanceId);

                            attendances.add(attendance);
                        }

                    }

                }
                /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewAttendanceActivity.this, android.R.layout.simple_spinner_item, titleList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                studentSpinner.setAdapter(arrayAdapter);
*/

                ViewAttendanceAdapter attendanceAdapter = new ViewAttendanceAdapter(ViewAttendanceActivity.this, attendances);
                recyclerAttendance.setAdapter(attendanceAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewAttendanceActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
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


}
