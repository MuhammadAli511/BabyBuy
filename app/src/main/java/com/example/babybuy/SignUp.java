package com.example.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

public class SignUp extends AppCompatActivity {

    EditText name, email, password, phoneNum;
    Button signup;
    FirebaseAuth mAuth;
    TextView signin;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phoneNum = findViewById(R.id.phoneNum);
        signup = findViewById(R.id.signup);
        signin = findViewById(R.id.signin);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait while we create your account");

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SignUp.this.finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String nameText = name.getText().toString();
                String phoneNumText = phoneNum.getText().toString();
                String deviceIDStr = OneSignal.getDeviceState().getUserId();

                if (emailText.isEmpty() || passwordText.isEmpty() || nameText.isEmpty() || phoneNumText.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(authResult -> {
                    User user = new User(nameText, emailText, phoneNumText,passwordText, deviceIDStr);
                    db.collection("Users").document(mAuth.getCurrentUser().getUid()).set(user).addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        SignUp.this.finish();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}