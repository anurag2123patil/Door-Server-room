package com.project.server.room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import logicBox.SharedSpace;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    EditText cEmail, cPassword;
    TextView cForgotPass;
    Button cLogin, cSignup;
    String email, password;
    SharedSpace sharedSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Initialization of ProgressDialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait..");
        FirebaseApp.initializeApp(this);
        //Initialization of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        sharedSpace = new SharedSpace(Login.this);

        //Initialization of component
        cEmail = (EditText) findViewById(R.id.et_Email);
        cPassword = (EditText) findViewById(R.id.et_Password);
        cForgotPass = (TextView) findViewById(R.id.tv_ForgotPass);
        cLogin = (Button) findViewById(R.id.bt_Login);
        cSignup = (Button) findViewById(R.id.bt_SignUp);

        //Login process
        cLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = cEmail.getText().toString();
                password = cPassword.getText().toString();

                if ((!email.isEmpty() && logicBox.Validator.isValidEmail(email)) && !password.isEmpty()) {
                    mProgressDialog.show();
                    SignIn(email, password);
                } else {
                    Toast.makeText(getApplication(), "Enter Credentials", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
        });

        //Reset password process
        cForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = cEmail.getText().toString();

                if (!email.isEmpty() && logicBox.Validator.isValidEmail(email)) {
                    ResetPassword(email);
                    mProgressDialog.show();
                } else {
                    Toast.makeText(getApplication(), "InvalidEmail", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
        });

        //Login process
        cSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), CreateAccountUsingEmail.class));
            }
        });
    }

    //Login Logic
    public void SignIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "signInWithEmail:success");
                            Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT).show();
                            sharedSpace.putData("login", "true");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sharedSpace.putData("userid", user.getUid());
                            startActivity(new Intent(getApplication(), Home.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplication(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    //Logic for reset password
    public void ResetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Email Sent", "Email sent.");
                            Toast.makeText(getApplication(), "Reset password link sent to email", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        } else {
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }
}
