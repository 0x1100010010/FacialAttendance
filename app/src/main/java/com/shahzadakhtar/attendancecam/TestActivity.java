package com.shahzadakhtar.attendancecam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.shahzadakhtar.attendancecam.FaceDetectHelper.ImagePicker;
import com.shahzadakhtar.attendancecam.Model.Student;
import com.shahzadakhtar.attendancecam.Utils.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ID = 200;
    private static final int PICK_IMAGE_ID2 = 300;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;
    @BindView(R.id.ivCamera2)
    ImageView ivCamera2;
    private Bitmap mBitmap;
    private Bitmap mBitmap2;

    List<UUID> faceIds;
    String personId;

    List<Student> identifiedStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.ivCamera)
    void ivCameraOnClick(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext(), getString(R.string.pick_image_intent_text));
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @OnClick(R.id.ivCamera2)
    void ivCamera2OnClick(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext(), getString(R.string.pick_image_intent_text));
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:


                // If image is selected successfully, set the image URI and bitmap.
                Uri imageUri = data.getData();
                mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                        imageUri, getContentResolver());
                if (mBitmap != null) {
                    ivCamera.setImageBitmap(mBitmap);

                    new AddPersonTask().execute(Constants.GROUP_NAME, "Shahzad" + System.currentTimeMillis(), "RegNo" + System.currentTimeMillis());

                }
                break;
            case PICK_IMAGE_ID2:


                // If image is selected successfully, set the image URI and bitmap.
                Uri imageUri2 = data.getData();
                mBitmap2 = ImageHelper.loadSizeLimitedBitmapFromUri(
                        imageUri2, getContentResolver());
                if (mBitmap2 != null) {
                    ivCamera2.setImageBitmap(mBitmap2);

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


    class AddFaceTask extends AsyncTask<String, String, String> {

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


                File folder = new File(Environment.getExternalStorageDirectory(), "/Faces/");
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
                }

                return result.persistedFaceId.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String persistedFaceId) {
            Log.v("taskPersistedFaceId", "Successfully added face with persistence id " + persistedFaceId);
            Toast.makeText(TestActivity.this, "Face was successfully added to the student", Toast.LENGTH_SHORT).show();

            //Toast.makeText(AddStudent.this, "Face with persistedFaceId "+persistedFaceId+" successfully created", Toast.LENGTH_SHORT).show();
        }
    }


    // Background task of adding a person to person group.
    class AddPersonTask extends AsyncTask<String, String, String> {

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
                Toast.makeText(TestActivity.this, "Student was successfully created", Toast.LENGTH_SHORT).show();
                new AddFaceTask().execute(personId);
            }
        }
    }

    private class DetectionTask extends AsyncTask<InputStream, Void, Face[]> {
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
                    Log.d("", "No faces detected!");
                    Toast.makeText(TestActivity.this, "No faces detected in the picture", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TestActivity.this, "No faces detected in the picture", Toast.LENGTH_SHORT).show();

               /* findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);
                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));*/
            }
        }
    }

    class TrainPersonGroupTask extends AsyncTask<String, String, String> {

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
                Toast.makeText(TestActivity.this, "The Person Group could not be trained", Toast.LENGTH_SHORT).show();
//                takenImage.setImageDrawable(getDrawable(R.drawable.attendance_logo));
            } else {
                new IdentificationTask().execute(faceIds.toArray(new UUID[faceIds.size()]));
            }
        }
    }


    private class IdentificationTask extends AsyncTask<UUID, Void, IdentifyResult[]> {
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
                    Toast.makeText(TestActivity.this, numberOfUnidentifiedFaces + " face(s) cannot be recognized", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(TestActivity.this, "No faces found in the picture. Try Again.", Toast.LENGTH_SHORT).show();

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
            Log.e("TaskPerson","TaskPerson started");
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
//                setUiAfterAddingFace(result, mFaceIndices);
            if (result) {

                Log.e("Task", "person Shown");
            } else {

                Log.e("Task", "person not Shown");
            }
        }
    }

}
