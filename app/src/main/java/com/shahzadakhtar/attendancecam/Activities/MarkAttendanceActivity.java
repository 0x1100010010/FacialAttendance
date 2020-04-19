package com.shahzadakhtar.attendancecam.Activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.shahzadakhtar.attendancecam.Constants;
import com.shahzadakhtar.attendancecam.FaceDetectHelper.ImagePicker;
import com.shahzadakhtar.attendancecam.Listeners.ClassFragListener;
import com.shahzadakhtar.attendancecam.Model.Class;
import com.shahzadakhtar.attendancecam.Model.Student;
import com.shahzadakhtar.attendancecam.R;
import com.shahzadakhtar.attendancecam.SampleApp;
import com.shahzadakhtar.attendancecam.Utils.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MarkAttendanceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_ID = 200;
    private static final int PICK_IMAGE_ID2 = 300;
    ImageView ivCamera;
    boolean isFirstAttendance = true;
    List<Student> identifiedStudents;
    boolean imageSelected = false;
    List<UUID> faceIds;
    String personGroupId;
    String personId;
    ProgressDialog progressDialog;
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
    String selectedCourse;
    ArrayAdapter<String> courseAdapter;
    int selectedDepartmentId = 0;
    String attendanceStatus = "absent";
    ArrayList<String> studentIds;
    private Bitmap mBitmap2;
    private DatabaseReference mDatabase;

    Class newClass;
    @BindView(R.id.tvClass)
    TextView tvClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        ButterKnife.bind(this);

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
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, computerScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 1:
                        selectedDepartmentId = 1;
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, managementScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 2:
                        selectedDepartmentId = 2;
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, commerceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 3:
                        selectedDepartmentId = 3;
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, humanitiesList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 4:
                        selectedDepartmentId = 4;
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, artFashionList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 5:
                        selectedDepartmentId = 5;
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, lifeHealthList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 6:
                        selectedDepartmentId = 6;
                        courseAdapter = new ArrayAdapter<String>(MarkAttendanceActivity.this, android.R.layout.simple_spinner_item, lawList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                }

                courseSpinner.setAdapter(courseAdapter);
                selectedDepartment = departmentSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedCourse = courseSpinner.getSelectedItem().toString();
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


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        ivCamera = findViewById(R.id.ivCamera);

        ivCamera.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivCamera:
//                startActivity(new Intent(MarkAttendanceActivity.this, AttendanceCamActivity.class));

//                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext(), getString(R.string.pick_image_intent_text));
                Intent chooseImageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID2);

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:
                final Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);


                if (bitmap != null) {
                    imageSelected = true;

                    if (isFirstAttendance)
                        identifiedStudents = new ArrayList<>();

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());


                    ivCamera.setImageBitmap(bitmap);

                    // new DetectionTask().execute(inputStream);

                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    detectFaces(image);

                    isFirstAttendance = false;
                } else {
                    imageSelected = false;


                }
                break;
            case PICK_IMAGE_ID2:


                // If image is selected successfully, set the image URI and bitmap.
                Uri imageUri2 = data.getData();
                mBitmap2 = ImageHelper.loadSizeLimitedBitmapFromUri(
                        imageUri2, getContentResolver());

                mBitmap2 = (Bitmap) data.getExtras().get("data");
                ivCamera.setImageBitmap(mBitmap2);

                if (mBitmap2 != null) {
                    ivCamera.setImageBitmap(mBitmap2);

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    mBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

                    new DetectionTask().execute(inputStream);

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void detectFaces(FirebaseVisionImage image) {
        // [START set_detector_options]
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();
        // [END set_detector_options]

        // [START get_detector]
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
        // [END get_detector]

        // [START run_detector]
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // [START_EXCLUDE]
                                        // [START get_face_info]
                                        for (FirebaseVisionFace face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                            // nose available):
                                            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                            Log.e("leftEar", "" + leftEar.toString());

                                            if (leftEar != null) {
                                                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                Log.e("leftEarPos", "" + leftEarPos.toString());

                                            }

                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float smileProb = face.getSmilingProbability();
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            }

                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int id = face.getTrackingId();
                                                Log.e("tId", "" + face.toString());
                                            }
                                        }
                                        // [END get_face_info]
                                        // [END_EXCLUDE]
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }



/*    private class DetectionTask extends AsyncTask<InputStream, Void, Face[]> {
        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try {

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  *//* Input stream of image to detect *//*
                        true,       *//* Whether to return face ID *//*
                        false,       *//* Whether to return face landmarks *//*
     *//* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair *//*
                        null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("LOOK", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Face[] faces) {

            if (faces != null) {
                if (faces.length == 0) {
                    Log.d("", "No faces detected!");
                    Toast.makeText(MarkAttendanceActivity.this, "No faces detected in the picture", Toast.LENGTH_SHORT).show();

                    *//*findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                    identifiedStudentsListView.setVisibility(View.VISIBLE);*//*
                   // takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
                } else {
                    faceIds = new ArrayList<>();
                    for (Face face : faces) {
                        faceIds.add(face.faceId);
                    }

                    new TrainPersonGroupTask().execute(personGroupId);
                }
            } else {
                Toast.makeText(MarkAttendanceActivity.this, "No faces detected in the picture", Toast.LENGTH_SHORT).show();

            *//*    findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);*//*
                //takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
            }
        }
    }



    class TrainPersonGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try {
                publishProgress("Training person group...");
                faceServiceClient.trainLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Train", e.toString() + " " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                *//*findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                Toast.makeText(TakeAttendance.this, "The Person Group could not be trained", Toast.LENGTH_SHORT).show();
                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));*//*
            } else {
               // new IdentificationTask().execute(faceIds.toArray(new UUID[faceIds.size()]));
            }
        }
    }*/
/*
    private class IdentificationTask extends AsyncTask<UUID, Void, IdentifyResult[]> {
        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
            Log.d("", "Request: Identifying faces ");

            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try {

                TrainingStatus trainingStatus = faceServiceClient.getLargePersonGroupTrainingStatus(personGroupId);

                if (!trainingStatus.status.toString().equals("Succeeded")) {
                    return null;
                }
                System.out.println("PERSON GROUP ID: " + personGroupId);
                return faceServiceClient.identityInLargePersonGroup(
                        personGroupId,     *//* personGroupId *//*
                        params,                  *//* faceIds *//*
                        1);                      *//* maxNumOfCandidatesReturned *//*
            } catch (Exception e) {
                Log.d("", e.getMessage());
                e.printStackTrace();
                System.out.println("Identification exception" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
            if (identifyResults != null) {
                String logString = "Response: Success. ";
                List<String> personIdsOfIdentified = new ArrayList<>();

                int numberOfUnidentifiedFaces = 0;

                for (IdentifyResult identifyResult : identifyResults) {
                    if (!identifyResult.candidates.isEmpty())
                        personIdsOfIdentified.add(identifyResult.candidates.get(0).personId.toString());

                    if (identifyResult.candidates.size() == 0) {
                        numberOfUnidentifiedFaces++;
                    }

                    logString += "Face " + identifyResult.faceId.toString() + " is identified as "
                            + (identifyResult.candidates.size() > 0
                            ? identifyResult.candidates.get(0).personId.toString()
                            : "Unknown Person")
                            + ". ";
                }

                if (numberOfUnidentifiedFaces > 0)
                    Toast.makeText(TakeAttendance.this, numberOfUnidentifiedFaces + " face(s) cannot be recognized", Toast.LENGTH_SHORT).show();

                Log.d("", logString);


                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                for (String personId : personIdsOfIdentified) {
                    identifiedStudents.add(db.studentDao().getStudentFromId(personId));
                }

                Set<Student> hs = new HashSet<>(identifiedStudents);
                identifiedStudents.clear();
                identifiedStudents.addAll(hs);

                for (Student identifiedStudent : identifiedStudents) {
                    if (identifiedStudent.studentId == null) continue;
                    if (!studentIdAttendanceIncremented.contains(identifiedStudent.studentId))
                        db.attendanceDao().incrementAttendance(identifiedStudent.courseId, identifiedStudent.regNo);
                    studentIdAttendanceIncremented.add(identifiedStudent.studentId);
                }

                studentListAdapter = new StudentListAdapter(TakeAttendance.this, R.layout.list_identified_students_row, identifiedStudents);


                identifiedStudentsListView.setAdapter(studentListAdapter);

                findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(TakeAttendance.this, "No faces found in the picture. Try Again.", Toast.LENGTH_SHORT).show();

                findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);
                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
            }

        }
    }*/

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

    public void markAttendance(String studentId) {
        progressDialog.setMessage("Marking Attendance");

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        businessDatabase = FirebaseDatabase.getInstance().getReference().child("Attendance").push();

        HashMap<String, String> businessMap = new HashMap<>();
        businessMap.put("attendanceId", businessDatabase.getKey());
        businessMap.put("studentId", studentId);
        businessMap.put("studentName", "");
        businessMap.put("classId", newClass.getClassId());
        businessMap.put("className", newClass.getClassName());
        businessMap.put("courseName", newClass.getCourseName());
        businessMap.put("semester", newClass.getClassSemester() + "");
        businessMap.put("teacherId", newClass.getTeacherId() + "");
        businessMap.put("roomNo", newClass.getRoomNo() + "");
        businessMap.put("day", day + "");
        businessMap.put("month", "" + month);
        businessMap.put("year", "" + year);
        businessMap.put("department", newClass.getDepartment());
        businessMap.put("attendanceStatus", "present");

        businessDatabase.setValue(businessMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(MarkAttendanceActivity.this, "Marked", Toast.LENGTH_SHORT).show();
                getStudentInfo(studentId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MarkAttendanceActivity.this, "bExcp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getStudentInfo(String studentId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("Students").orderByChild("studentId").equalTo(studentId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"

                        Log.e("adapterData", issue.toString());
                        String name = issue.child("studentName").getValue(String.class);
                        String rollNo = issue.child("rollNo").getValue(String.class);
                        String parentName = issue.child("parentName").getValue(String.class);
                        String parentNumber = issue.child("parentNumber").getValue(String.class);
                        String studentName = issue.child("studentName").getValue(String.class);

                        sendText(parentNumber, "Dear " + parentName + ", your son/daughter " + name + " is  present today.");


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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



    private class DetectionTask extends AsyncTask<InputStream, Void, Face[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Detecting faces");
            progressDialog.show();
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

            try {
                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("LOOK", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Face[] faces) {

            if (faces != null) {
                if (faces.length == 0) {
                    progressDialog.dismiss();
                    Log.d("", "No faces detected!");
                    Toast.makeText(MarkAttendanceActivity.this, "No faces detected in the picture", Toast.LENGTH_SHORT).show();
/*
                    findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                    identifiedStudentsListView.setVisibility(View.VISIBLE);
                    takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));*/
                } else {
                    faceIds = new ArrayList<>();
                    for (Face face : faces) {
                        faceIds.add(face.faceId);
                    }

                    new TrainPersonGroupTask().execute(Constants.GROUP_NAME);
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(MarkAttendanceActivity.this, "No faces detected in the picture", Toast.LENGTH_SHORT).show();

               /* findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);
                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));*/
            }
        }
    }

    class TrainPersonGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Almost done");
        }

        @Override
        protected String doInBackground(String... params) {

            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Training person group...");

                faceServiceClient.trainLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Train", e.toString() + " " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
//                findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                progressDialog.dismiss();
                Toast.makeText(MarkAttendanceActivity.this, "The Person Group could not be trained", Toast.LENGTH_SHORT).show();
//                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
            } else {
                new IdentificationTask().execute(faceIds.toArray(new UUID[faceIds.size()]));
            }
        }
    }

    private class IdentificationTask extends AsyncTask<UUID, Void, IdentifyResult[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Identifying faces");
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
            Log.d("", "Request: Identifying faces ");

            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {

                TrainingStatus trainingStatus = faceServiceClient.getLargePersonGroupTrainingStatus(Constants.GROUP_NAME);

                if (!trainingStatus.status.toString().equals("Succeeded")) {
                    return null;
                }
                System.out.println("PERSON GROUP ID: " + Constants.GROUP_NAME);
                return faceServiceClient.identityInLargePersonGroup(
                        Constants.GROUP_NAME,     /* personGroupId */
                        params,                  /* faceIds */
                        1);                      /* maxNumOfCandidatesReturned */
            } catch (Exception e) {
                Log.d("", e.getMessage());
                e.printStackTrace();
                System.out.println("Identification exception" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
//            takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
            studentIds = new ArrayList<>();
            studentIds.clear();

            if (identifyResults != null) {
                String logString = "Response: Success. ";
                List<String> personIdsOfIdentified = new ArrayList<>();
                int numberOfUnidentifiedFaces = 0;

                for (IdentifyResult identifyResult : identifyResults) {
                    if (!identifyResult.candidates.isEmpty())
                        personIdsOfIdentified.add(identifyResult.candidates.get(0).personId.toString());

                    if (identifyResult.candidates.size() == 0) {
                        numberOfUnidentifiedFaces++;
                    }

                    logString += "Face " + identifyResult.faceId.toString() + " is identified as "
                            + (identifyResult.candidates.size() > 0
                            ? identifyResult.candidates.get(0).personId.toString()
                            : "Unknown Person")
                            + ". ";
                }

                if (numberOfUnidentifiedFaces > 0)
                    Toast.makeText(MarkAttendanceActivity.this, numberOfUnidentifiedFaces + " face(s) cannot be recognized", Toast.LENGTH_SHORT).show();

                Log.d("taskIdentity", logString);


//                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                for (String pId : personIdsOfIdentified) {
//                    identifiedStudents.add(db.studentDao().getStudentFromId(personId));
                    personId = pId;
                    new gerPersonTask().execute();
                }


             /*   Set<Student> hs = new HashSet<>(identifiedStudents);
                identifiedStudents.clear();
                identifiedStudents.addAll(hs);

                for (Student identifiedStudent : identifiedStudents) {
                    if (identifiedStudent.studentId == null) continue;
                    if (!studentIdAttendanceIncremented.contains(identifiedStudent.studentId))
                        db.attendanceDao().incrementAttendance(identifiedStudent.courseId, identifiedStudent.regNo);
                    studentIdAttendanceIncremented.add(identifiedStudent.studentId);
                }*/

/*                studentListAdapter = new StudentListAdapter(TakeAttendance.this, R.layout.list_identified_students_row, identifiedStudents);


                identifiedStudentsListView.setAdapter(studentListAdapter);

                findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);*/
            } else {
                progressDialog.dismiss();
                Toast.makeText(MarkAttendanceActivity.this, "No faces found in the picture. Try Again.", Toast.LENGTH_SHORT).show();

                /*findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);
                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));*/
            }

        }
    }

    // Background task of adding a face to person.
    class gerPersonTask extends AsyncTask<Void, String, Boolean> {



        @Override
        protected Boolean doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Adding face...");
//                    UUID personId = UUID.fromString(personGroupId);

         /*       ByteArrayOutputStream stream = new ByteArrayOutputStream();
                detectedCroppedFace.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());*/
 /*FaceRectangle faceRect = mFaceGridViewAdapter.faceRectList.get(index);
                        addLog("Request: Adding face to person " + mPersonId);*/
                // Start the request to add face.
                Person person = faceServiceClient.getPersonInLargePersonGroup(
                        Constants.GROUP_NAME,
                        UUID.fromString(personId));

                Log.e("taskPerson", "dat>" + person.userData);

                studentIds.add(person.userData);

//                        mFaceGridViewAdapter.faceIdList.set(index, result.persistedFaceId);
             /*   for (Integer index: mFaceIndices) {

                }*/
                return true;
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("taskPerson", ">" + e.getMessage());
//                    addLog(e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            Log.e("TaskPerson", "TaskPerson started");
            progressDialog.setMessage("Getting face");
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            if (result) {

                for (String ids : studentIds) {
                    if(!progressDialog.isShowing()){
                        progressDialog.show();
                    }
                    markAttendance(ids);
                }

                Log.e("Task", "person Shown");
            } else {

                Log.e("Task", "person not Shown");
            }
        }
    }



}
