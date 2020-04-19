package com.shahzadakhtar.attendancecam.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.shahzadakhtar.attendancecam.Constants;
import com.shahzadakhtar.attendancecam.FaceDetectHelper.ImagePicker;
import com.shahzadakhtar.attendancecam.MyPrefs;
import com.shahzadakhtar.attendancecam.R;
import com.shahzadakhtar.attendancecam.SampleApp;
import com.shahzadakhtar.attendancecam.TestActivity;
import com.shahzadakhtar.attendancecam.Utils.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterStudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ID = 200;
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
    @BindView(R.id.etRollNo)
    EditText etRollNo;
    @BindView(R.id.etSession)
    EditText etSession;
    @BindView(R.id.etParentName)
    EditText etParentName;
    @BindView(R.id.etParentPhone)
    EditText etParentPhone;
    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;
    String imageUrl;
    ArrayList<String> genderList = new ArrayList<>();
    String selectedGender = "Male";
    FirebaseAuth auth;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;
    boolean imageSelected = false;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    boolean isDetected = false;
    boolean detected;
    String personId;
    Bitmap detectedCroppedFace;
    FaceRectangle faceRectangle;
    String persistedFaceResult;
    String persistedFaceId;
    MyPrefs myPrefs;
    int groupSize;
    UUID[] uuid;
    private SparseArray<Face> sparseArray;
    private Canvas canvas;
    private Paint rectPaint;
    private Bitmap myBitmap;
    private Bitmap tempBitmap;
    private Bitmap mBitmap;
    private String personGroupId;

    List<UUID> faceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        ButterKnife.bind(this);

        myPrefs = new MyPrefs(this);

        detected = false;


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

        courseSpinner = findViewById(R.id.courseSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departments);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(arrayAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, computerScienceList);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);


        rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.STROKE);


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = genderList.get(position);
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
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, computerScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 1:
                        selectedDepartmentId = 1;
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, managementScienceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 2:
                        selectedDepartmentId = 2;
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, commerceList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 3:
                        selectedDepartmentId = 3;
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, humanitiesList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 4:
                        selectedDepartmentId = 4;
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, artFashionList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 5:
                        selectedDepartmentId = 5;
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, lifeHealthList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 6:
                        selectedDepartmentId = 6;
                        courseAdapter = new ArrayAdapter<String>(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, lawList);
                        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        break;
                }

                courseSpinner.setAdapter(courseAdapter);

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
                /*selectedDepartment = departmentSpinner.getSelectedItem().toString();
                selectedClass = classSpinner.getSelectedItem().toString();*/
            }
        });

        selectedDepartment = departmentSpinner.getSelectedItem().toString();
        selectedCourse = courseSpinner.getSelectedItem().toString();


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
            departments.add("Faculty of Computer Science");
            departments.add("Faculty of Management Sciences");
            departments.add("Faculty of Commerce");
            departments.add("Faculty of Humanities and Social Sciences");
            departments.add("Faculty of Art and Fashion Design");
            departments.add("Faculty of Life and Health Sciences");
            departments.add("Faculty of Law");
        }

    }

    public void addStudent() {

        progressDialog.show();


        HashMap<String, String> businessMap = new HashMap<>();
        businessMap.put("studentId", businessDatabase.getKey());
        businessMap.put("studentName", etFullName.getText().toString());
        businessMap.put("courseName", selectedCourse);
        businessMap.put("rollNo", etRollNo.getText().toString());
        businessMap.put("parentName", etParentName.getText().toString());
        businessMap.put("parentNumber", etParentPhone.getText().toString());
        businessMap.put("birthDay", "18");
        businessMap.put("birthMonth", "2");
        businessMap.put("birthYear", "1993");
        businessMap.put("department", selectedDepartment);
        businessMap.put("contactNo", etContactNo.getText().toString());
        businessMap.put("email", etEmail.getText().toString());
        businessMap.put("imageUrl", imageUrl + "");
        businessMap.put("semester", "1");
        businessMap.put("authId", auth.getUid());
        businessMap.put("gender", selectedGender);
        businessMap.put("session", etSession.getText().toString());

        businessDatabase.setValue(businessMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(RegisterStudentActivity.this, "Registered", Toast.LENGTH_SHORT).show();

                new AddPersonTask().execute(Constants.GROUP_NAME, "Shahzad" + System.currentTimeMillis(), businessDatabase.getKey());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterStudentActivity.this, "bExcp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @OnClick(R.id.btnRegister)
    void btnRegisterOnClick(View view) {

       /* startActivity(new Intent(this, TestActivity.class));

//        new listGroupTask().execute();

        if (1 == 1) {
            return;
        }
*/
        auth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()) {

                    auth = FirebaseAuth.getInstance();

                    businessDatabase = FirebaseDatabase.getInstance().getReference().child("Students").push();
//                    addPersonToGroup(businessDatabase.getKey());
                    addStudent();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterStudentActivity.this, "excp\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @OnClick(R.id.ivCamera)
    void ivCameraOnClick(View view) {

        Intent chooseImageIntent =  new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:


                detected = false;

                // If image is selected successfully, set the image URI and bitmap.
                Uri imageUri = data.getData();
                mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                        imageUri, getContentResolver());

                mBitmap = (Bitmap) data.getExtras().get("data");
                ivCamera.setImageBitmap(mBitmap);


                if (mBitmap != null) {
                    // Show the image on screen.
//                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    ivCamera.setImageBitmap(mBitmap);

                }

                uploadFile(mBitmap);



                // Clear the information panel.
//                setInfo("");

                // Start detecting in image.
//                detect(mBitmap);

               /* final Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (bitmap != null) {
                    imageSelected = true;


                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

                    ivCamera.setImageBitmap(bitmap);
                    myBitmap = bitmap;

                    tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                    canvas = new Canvas(tempBitmap);
                    canvas.drawBitmap(bitmap, 0, 0, null);

                    progressDialog.setMessage("Detecting face");
                    progressDialog.show();
                    new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



                } else {
                    imageSelected = false;


                }*/
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());


        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }

    private void setUiBeforeBackgroundTask() {
        progressDialog.show();
    }

    private void displayFrames() {
        progressDialog.dismiss();
        if (sparseArray != null) {
            if (sparseArray.size() == 0) {
                Toast.makeText(this, "size zero", Toast.LENGTH_SHORT).show();
            }
            for (int i = 0; i < sparseArray.size(); i++) {
                Face face = sparseArray.valueAt(i);
                Log.e("data", face.toString() + "");
                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1 + face.getWidth();
                float y2 = y1 + face.getHeight();
                RectF rectF = new RectF(x1, y1, x2, y2);
                canvas.drawRoundRect(rectF, 2, 2, rectPaint);
                Log.e("Data>1", "" + face.getEulerY());
                Log.e("Data>2", "" + face.getEulerZ());
                Log.e("Data>3", "" + face.getIsLeftEyeOpenProbability());
                Log.e("Data>4", "" + face.getIsRightEyeOpenProbability());
                Log.e("Data>5", "" + face.getIsRightEyeOpenProbability());
                Log.e("Data>6", "" + face.getIsSmilingProbability());
                Log.e("Data>7", "" + face.getPosition().toString());
                Log.e("Data>8", "" + face.getContours().toString());
                Log.e("Data>9", "" + face.getId());
                Log.e("Data>10", "" + face.getLandmarks().toString());
            }
            ivCamera.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            if (isDetected) {

                uploadFile(myBitmap);
            } else {
                Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();
                isDetected = false;
            }

        }
    }

    // Show the status of background detection task on screen.

    private void uploadFile(Bitmap bitmap) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainImagesRef = storageRef.child("images/" + "Student" + System.currentTimeMillis() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("ImageFailed", task.getException().getMessage());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterStudentActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    throw task.getException();

                }

                // Continue with the task to get the download URL
                return mountainImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    /*Uri downloadUrl = taskSnapshot.getUploadSessionUri();*/
                    imageUrl = downloadUri.toString();
                    Log.d("downloadUrl-->", "" + downloadUri.toString());
                    progressBar.setVisibility(View.GONE);

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }

    private void setUiDuringBackgroundTask(String progress) {
        progressDialog.setMessage(progress);

    }

    //step 1
    public void createPersonGroup() {

        personGroupId = UUID.randomUUID().toString();

        new AddPersonGroupTask(true).execute(Constants.GROUP_NAME);


    }

    //step 3
    private void addFace() {
//        new AddFaceTask().execute();


    }

    //step 2
    public void addPersonToGroup(String key) {
//        new AddPersonTask(key).execute(myPrefs.getGroupName());

    }

    /*
        // Background task of face detection.
        private class DetectionTask extends AsyncTask<InputStream, String, com.microsoft.projectoxford.face.contract.Face[]> {
            @Override
            protected com.microsoft.projectoxford.face.contract.Face[] doInBackground(InputStream... params) {
                // Get an instance of face service client to detect faces in image.
                FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
                try{
                    publishProgress("Detecting...");

                    // Start detection.
                    return faceServiceClient.detect(
                            params[0],  *//* Input stream of image to detect *//*
                        true,       *//* Whether to return face ID *//*
                        false,       *//* Whether to return face landmarks *//*
     *//* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair *//*
                        null);
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(com.microsoft.projectoxford.face.contract.Face[] result) {
            progressDialog.dismiss();

            setAllButtonsEnabledStatus(true);

            if (result != null) {
                // Set the adapter of the ListView which contains the details of detected faces.
                mFaceListAdapter = new FaceListAdapter(result);
                ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                listView.setAdapter(mFaceListAdapter);

                if (result.length == 0) {
                    detected = false;
                    setInfo("No faces detected!");
                } else {
                    detected = true;
                    setInfo("Click on the \"Identify\" button to identify the faces in image.");
                }
            } else {
                detected = false;
            }

            refreshIdentifyButtonEnabledStatus();
        }
    }*/
    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, com.microsoft.projectoxford.face.contract.Face[]> {
        @Override
        protected com.microsoft.projectoxford.face.contract.Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Detecting...");
                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("taskDetectExcp", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(com.microsoft.projectoxford.face.contract.Face[] result) {
            progressDialog.dismiss();

            uuid = new UUID[result.length];
            if (result == null) {
                Toast.makeText(RegisterStudentActivity.this, "nothing detected", Toast.LENGTH_SHORT).show();
                return;
            }

            groupSize = result.length;

            if (result.length > 0) {

                for (int i = 0; i < result.length; i++) {
                    uuid[i] = result[i].faceId;
                }

            }

            new trainGroupTask().execute();


            if (1 == 1) {
                return;
            }

            com.microsoft.projectoxford.face.contract.Face face = result[0];
            faceRectangle = face.faceRectangle;

            try {
                detectedCroppedFace = ImageHelper.generateFaceThumbnail(mBitmap, face.faceRectangle);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("Task", "detected>" + face.faceId.toString());
            createPersonGroup();


//            setAllButtonsEnabledStatus(true);

            if (result != null) {
                // Set the adapter of the ListView which contains the details of detected faces.
           /*     mFaceListAdapter = new FaceListAdapter(result);
                ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                listView.setAdapter(mFaceListAdapter);*/

                if (result.length == 0) {
                    detected = false;
//                    setInfo("No faces detected!");
                } else {
                    detected = true;
//                    setInfo("Click on the \"Identify\" button to identify the faces in image.");
                }
            } else {
                detected = false;
            }

//            refreshIdentifyButtonEnabledStatus();
        }
    }

    class BackgroundTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            com.google.android.gms.vision.face.FaceDetector faceDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                    .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                    .build();
            if (!faceDetector.isOperational()) {
                isDetected = false;
                Toast.makeText(RegisterStudentActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
            } else {
                isDetected = true;
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                sparseArray = faceDetector.detect(frame);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            displayFrames();
        }
    }

    class AddPersonGroupTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add person in this group, or finish editing this group.
        boolean mAddPerson;

        AddPersonGroupTask(boolean addPerson) {
            mAddPerson = addPerson;
        }

        @Override
        protected String doInBackground(String... params) {
            //  addLog("Request: Creating person group " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Syncing with server to add person group...");

                // Start creating person group in server.
                faceServiceClient.createLargePersonGroup(
                        params[0],
                        "AllUsers",
                        "group desc");

                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
//                    addLog(e.getMessage());
                Log.e("taskGroupExcp", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            Log.e("Task", "group created" + result);
            if (result != null) {
                //todo
                personGroupId = result;
                if (myPrefs.getGroupName().equals("")) {
                    myPrefs.setGroupName(personGroupId);
                }
                Toast.makeText(RegisterStudentActivity.this, "face detected", Toast.LENGTH_SHORT).show();
                   /* addLog("Response: Success. Person group " + result + " created");

                    personGroupExists = true;
                    GridView gridView = (GridView) findViewById(dz.esi.facereco.R.id.gridView_persons);
                    personGridViewAdapter = new PersonGridViewAdapter();
                    gridView.setAdapter(personGridViewAdapter);

                    setInfo("Success. Group " + result + " created");

                    if (mAddPerson) {
                        addPerson();
                    } else {
                        doneAndSave(false);
                    }*/
            }
        }
    }
/*
    class AddPersonTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add face in this person, or finish editing this person.
        String key;

        public AddPersonTask(String key) {
            this.key = key;
        }

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Syncing with server to add person...");
//                addLog("Request: Creating Person in person group" + params[0]);

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0],
                        "person name",
                        key);

                return createPersonResult.personId.toString();
            } catch (Exception e) {
                publishProgress(e.getMessage());
//                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (result != null) {
//                addLog("Response: Success. Person " + result + " created.");
                Log.e("Task", "person added>" + result);
                personId = result;
//                setInfo("Successfully Synchronized!");

                addFace();

            }
        }
    }*/
/*
    // Background task of adding a face to person.
    class AddFaceTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Adding face...");
//                    UUID personId = UUID.fromString(personGroupId);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());
 *//*FaceRectangle faceRect = mFaceGridViewAdapter.faceRectList.get(index);
                        addLog("Request: Adding face to person " + mPersonId);*//*
                // Start the request to add face.
                AddPersistedFaceResult result = faceServiceClient.addPersonFaceInLargePersonGroup(
                        myPrefs.getGroupName(),
                        UUID.fromString(personId),
                        imageInputStream,
                        "User data",
                        faceRectangle);

                Log.e("taskPersistedFaceResult", "face id>" + result.persistedFaceId.toString());

//                        mFaceGridViewAdapter.faceIdList.set(index, result.persistedFaceId);
             *//*   for (Integer index: mFaceIndices) {

                }*//*
                return result.persistedFaceId.toString();
            } catch (Exception e) {
                publishProgress(e.getMessage());
//                    addLog(e.getMessage());
                Log.e("TaskFace", "" + e.getMessage());
                return "";
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            progressDialog.dismiss();
            if (result != null) {

                persistedFaceResult = result;
                persistedFaceId = result;

                Log.e("Task", "face added>" + result);
//                new gerPersonTask().execute();
                addStudent();
            } else {
                Log.e("Task", "face not added");

            }
        }
    }*/


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
                        myPrefs.getGroupName(),
                        UUID.fromString(personId));

                Log.e("taskPerson", "dat>" + person.userData);

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
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            progressDialog.dismiss();
            if (result) {

                Log.e("Task", "person Shown");
            } else {

                Log.e("Task", "person not Shown");
            }
        }
    }

    class trainGroupTask extends AsyncTask<Void, String, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Training group...");
//                    UUID personId = UUID.fromString(personGroupId);

         /*       ByteArrayOutputStream stream = new ByteArrayOutputStream();
                detectedCroppedFace.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());*/
 /*FaceRectangle faceRect = mFaceGridViewAdapter.faceRectList.get(index);
                        addLog("Request: Adding face to person " + mPersonId);*/
                // Start the request to add face.
                faceServiceClient.trainPersonGroup(
                        Constants.GROUP_NAME);
                Log.e("taskGroupName", ">" + myPrefs.getGroupName());


//                        mFaceGridViewAdapter.faceIdList.set(index, result.persistedFaceId);
             /*   for (Integer index: mFaceIndices) {

                }*/
                return true;
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("taskGroupTrain", ">" + e.getMessage());
//                    addLog(e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            progressDialog.dismiss();
            if (result) {
                Log.e("TaskGroupTrain", "group trained");
                new gerPersonIdentityTask(uuid).execute();

            } else {

                Log.e("TaskGroupTrain", "group not trained");
            }
        }
    }

    class listGroupTask extends AsyncTask<Void, String, Person> {

        @Override
        protected Person doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Training group...");
//                    UUID personId = UUID.fromString(personGroupId);

         /*       ByteArrayOutputStream stream = new ByteArrayOutputStream();
                detectedCroppedFace.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());*/
 /*FaceRectangle faceRect = mFaceGridViewAdapter.faceRectList.get(index);
                        addLog("Request: Adding face to person " + mPersonId);*/
                // Start the request to add face.
                Person largePersonGroup = faceServiceClient.getPersonInLargePersonGroup("41f49748-2806-4287-be21-27ec28383414", UUID.fromString("c61f809d-0d14-467b-a4a2-186ce665ca01"));


//                        mFaceGridViewAdapter.faceIdList.set(index, result.persistedFaceId);
             /*   for (Integer index: mFaceIndices) {

                }*/
                return largePersonGroup;
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("taskGroupList", ">" + e.getMessage());
//                    addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(Person result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            progressDialog.dismiss();

            if (result == null) {
                Log.e("TaskGroups", "groups null");
                return;
            }

            Log.e("TaskGroups", "" + result.personId);
           /* if (result.length > 0) {
                //personGroupId = result;
                for (int i = 0; i < result.length; i++) {

                }
//                new gerPersonIdentityTask(uuid).execute();
            } else {

                Log.e("TaskGroupTrain", "group not trained");
            }*/
        }
    }

    // Background task of adding a face to person.
    class gerPersonIdentityTask extends AsyncTask<Void, String, IdentifyResult[]> {

        UUID[] uuids;

        public gerPersonIdentityTask(UUID[] uuids) {
            this.uuids = uuids;
        }

        @Override
        protected IdentifyResult[] doInBackground(Void... params) {
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
                IdentifyResult[] person = faceServiceClient.identityInLargePersonGroup(
                        "41f49748-2806-4287-be21-27ec28383414",
                        uuids,
                        groupSize);

                Log.e("taskPerson", "dat>" + person.length);

//                        mFaceGridViewAdapter.faceIdList.set(index, result.persistedFaceId);
             /*   for (Integer index: mFaceIndices) {

                }*/
                return person;
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("taskPerson", ">" + e.getMessage());
//                    addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(IdentifyResult[] result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            progressDialog.dismiss();
            if (result.length > 0) {

                for (int i = 0; i < result.length; i++) {
                    IdentifyResult identifyResult = result[i];

                    Log.e("TaskIds", "" + identifyResult.candidates.get(i).personId);
                }

                Log.e("Task", "person Shown");
            } else {

                Log.e("Task", "person not Shown");
            }
        }
    }


    // Background task of adding a person to person group.
    class AddPersonTask extends AsyncTask<String, String, String> {

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values.toString());
        }

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                publishProgress("Syncing with server to add person...");
                Log.v("taskRequest", "Request: Creating Person in person group" + params[0]);

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0], //personGroupID
                        params[1], //name
                        params[2]); //userData or regNo

                return createPersonResult.personId.toString();


            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.v("taskAddPersonExcp", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String personId) {

            if (personId != null) {
                Log.v("taskResponse", "Response: Success. Person " + personId + " created.");

                //Toast.makeText(AddStudent.this, "Person with personId "+personId+" successfully created", Toast.LENGTH_SHORT).show();
                Toast.makeText(RegisterStudentActivity.this, "Student was successfully created", Toast.LENGTH_SHORT).show();
                new AddFaceTask().execute(personId);
            }
        }
    }



    class AddFaceTask extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Adding face");
        }

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {
                Log.v("taskAddFace", "Adding face...");
                UUID personId = UUID.fromString(params[0]);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());

                AddPersistedFaceResult result = faceServiceClient.addPersonFaceInLargePersonGroup(
                        Constants.GROUP_NAME,
                        personId,
                        imageInputStream,
                        "",
                        null);


             /*   File folder = new File(Environment.getExternalStorageDirectory(), "/Faces/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File photo = new File(Environment.getExternalStorageDirectory(), "/Faces/" + result.persistedFaceId.toString() + ".jpg");
                if (photo.exists()) {
                    photo.delete();
                }

                try {
                    FileOutputStream fos = new FileOutputStream(photo.getPath());

                    fos.write(stream.toByteArray());
                    fos.close();

                    Log.v("Store face in storage", "Face stored with name " + photo.getName() + " and path " + photo.getAbsolutePath());
                } catch (java.io.IOException e) {
                    Log.e("Store face in storage", "Exception in photoCallback", e);
                }*/

                return result.persistedFaceId.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String persistedFaceId) {
            progressDialog.dismiss();
            sendVerificationEmail();
            Log.v("taskPersistedFaceId", "Successfully added face with persistence id " + persistedFaceId);
            Toast.makeText(RegisterStudentActivity.this, "Face was successfully added to the student", Toast.LENGTH_SHORT).show();

            //Toast.makeText(AddStudent.this, "Face with persistedFaceId "+persistedFaceId+" successfully created", Toast.LENGTH_SHORT).show();
        }
    }



    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(RegisterStudentActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(RegisterStudentActivity.this, LoginActivity.class));
                        } else {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
}
