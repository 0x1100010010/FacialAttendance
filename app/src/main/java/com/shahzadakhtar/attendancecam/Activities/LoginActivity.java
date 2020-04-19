package com.shahzadakhtar.attendancecam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shahzadakhtar.attendancecam.MainActivity;
import com.shahzadakhtar.attendancecam.MyPrefs;
import com.shahzadakhtar.attendancecam.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etPassword)
    EditText etPassword;

    FirebaseAuth auth;

    ProgressDialog progressDialog;

    String type;


    MyPrefs myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        myPrefs = new MyPrefs(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            type = bundle.getString("type");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

    }

    @OnClick(R.id.ivSignUp)
    void ivSignUpOnClick(View view){

        if(type.equals("std")){

            startActivity(new Intent(this, RegisterStudentActivity.class));
        }else if(type.equals("teacher")){
            startActivity(new Intent(this, RegisterTeacherActivity.class));

        }

    }

    @OnClick(R.id.btnLogin)
    void btnLoginOnClick(View view){

        if(TextUtils.isEmpty(etEmail.getText().toString())){
            etEmail.setError("Enter email");
            return;
        }
        if(TextUtils.isEmpty(etPassword.getText().toString())){
            etPassword.setError("Enter password");
            return;
        }

        if(type.equals("teacher")){
            if(TextUtils.isEmpty(etEmail.getText().toString())){
                etEmail.setError("Enter email");
                return;
            }
            if(!etEmail.getText().toString().endsWith("usa.edu.pk")){
                etEmail.setError("Enter valid email");
                return;
            }
        }

        progressDialog.show();

        auth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    finish();

                    myPrefs.setType(type);
                    if(type.equals("std")){
                        Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else if(type.equals("teacher")){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }



                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "excp\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}
