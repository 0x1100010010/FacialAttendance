package com.shahzadakhtar.attendancecam.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.shahzadakhtar.attendancecam.R;
import com.shahzadakhtar.attendancecam.SampleApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class TestActivity extends AppCompatActivity {

    public static Bitmap imageBitmap;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_CAPTURE_IMAGE = 0;
    public Uri selectedImage;
    Face[] facesDetected;
    //Put your FACE API KEY AND ITS END POINT
//    private FaceServiceClient faceServiceClient=new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0","fdb32a683f9048b89c622f37a09813eaivate final String personGroupId = "celebs";
    String personGroupId = "one";
    private Button cameraButton;
    private Button galleryButton;
    private Button detectFace;
    private Button identifyFace;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Initlasing the Views
        photoView = (ImageView) findViewById(R.id.image_detect_view);
        cameraButton = (Button) findViewById(R.id.camera_open_btn);
        galleryButton = (Button) findViewById(R.id.gallery_open_btn);
        detectFace = (Button) findViewById(R.id.detect_face_btn);
        identifyFace = (Button) findViewById(R.id.identify_face_btn);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        detectFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageBitmap != null) {
                    datectFaceFrame(imageBitmap);
                } else {
                    Toast.makeText(TestActivity.this, "Firstthe Image", Toast.LENGTH_SHORT).show();
                }
            }

        });

        identifyFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facesDetected != null) {
                    final UUID[] faceIds = new UUID[facesDetected.length];
                    for (int i = 0; i < facesDetected.length; i++) {
                        faceIds[i] = facesDetected[i].faceId;
                    }

                    new IdentificationTask(personGroupId).execute(faceIds);

                } else {
                    Toast.makeText(TestActivity.this, "Firstct The Face", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void datectFaceFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        @SuppressLint("StaticFieldLeak") AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
            private ProgressDialog progressDialog = new ProgressDialog(TestActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                progressDialog.setMessage(values[0]);
            }

            @Override
            protected void onPostExecute(Face[] faces) {
                progressDialog.dismiss();
                if (faces == null) {
                    return;
                } else {
                    facesDetected = faces;
                    photoView.setImageBitmap(drawFaceRectangleOnBitmap(imageBitmap, facesDetected, "Unknown"));
                }


            }

            @Override
            protected Face[] doInBackground(InputStream... inputStreams) {
                publishProgress("Detecting...");
                try {
                    Face[] result = SampleApp.getFaceServiceClient().detect(inputStreams[0], true, false, null);
                    if (result == null) {
                        publishProgress("Detection Finished.Nothing Detected!");
                        return null;
                    }

                    publishProgress(String.format("Detection Finished.%d face(s) detected", result.length));
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    publishProgress("Detection Failed");
                    return null;


                }
            }
        };

        detectTask.execute(byteArrayInputStream);

    }

    private Bitmap drawFaceRectangleOnBitmap(Bitmap imageBitmap, Face[] faces, String name) {
        Bitmap changedBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(changedBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);

        int strokeWidth = 12;
        paint.setStrokeWidth(strokeWidth);

        if (faces != null) {
            int length = faces.length;
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
                drawTextOnCanvas(canvas, 100 / length, ((faceRectangle.left + faceRectangle.width) / 2) + 100, (faceRectangle.top + faceRectangle.height) + 50, Color.WHITE, name);

            }
        }
        return changedBitmap;
    }

    private void drawTextOnCanvas(Canvas canvas, int textSize, int x, int y, int color, String name) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setTextSize(textSize);

        float textWidth = paint.measureText(name);

        canvas.drawText(name, x - (textWidth / 2), y - (textSize / 2), paint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAPTURE_IMAGE && resultCode == RESULT_OK && null != data) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            photoView.setImageBitmap(imageBitmap);
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            photoView.setImageURI(selectedImage);
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]> {
        String personGroupId;

        private ProgressDialog mDialog = new ProgressDialog(TestActivity.this);

        public IdentificationTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {

            try {
                publishProgress("Getting person group status...");
                TrainingStatus trainingStatus = SampleApp.getFaceServiceClient().getPersonGroupTrainingStatus(this.personGroupId);
                if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                    publishProgress("Person group training status is " + trainingStatus.status);
                    return null;
                }
                publishProgress("Identifying...");

                IdentifyResult[] results = SampleApp.getFaceServiceClient().identity(personGroupId, // person group id
                        params // face ids
                        , 5); // max number of candidates returned

                return results;

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            mDialog.dismiss();


            if (identifyResults != null) {
                for (IdentifyResult identifyResult : identifyResults) {
                    new PersonDetectionTask(personGroupId).execute(identifyResult.candidates.get(0).personId);
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mDialog.setMessage(values[0]);
        }
    }

    private class PersonDetectionTask extends AsyncTask<UUID, String, Person> {
        private ProgressDialog mDialog = new ProgressDialog(TestActivity.this);
        private String personGroupId;

        public PersonDetectionTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected Person doInBackground(UUID... params) {
            try {
                publishProgress("Getting person group status...");

                return SampleApp.getFaceServiceClient().getPerson(personGroupId, params[0]);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Person person) {
            mDialog.dismiss();

            photoView.setImageBitmap(drawFaceRectangleOnBitmap(imageBitmap, facesDetected, person.name));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mDialog.setMessage(values[0]);
        }
    }


}