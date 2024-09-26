package com.project.server.room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import logicBox.SharedSpace;

public class CreateAccountUsingEmail extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText cName, cEmail, cPassword, cConfirmPass, cMobileNo;
    CheckBox checkBox;
    Button cCreateAccount;
    ProgressDialog mProgressDialog;
    SharedSpace sharedSpace;
    String name, mobileNo;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_accountusingemail);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait..");
        sharedSpace = new SharedSpace(CreateAccountUsingEmail.this);

        cName = (EditText) findViewById(R.id.et_Name);
        cEmail = (EditText) findViewById(R.id.et_eMail);
        cPassword = (EditText) findViewById(R.id.et_pass);
        cConfirmPass = (EditText) findViewById(R.id.et_con_pass);
        cMobileNo = (EditText) findViewById(R.id.et_mobNo);
        checkBox = (CheckBox) findViewById(R.id.cb_Term);
        cCreateAccount = (Button) findViewById(R.id.btCreate);

        cCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = cEmail.getText().toString();
                String password = cPassword.getText().toString();
                name = cName.getText().toString();
                mobileNo = cMobileNo.getText().toString();
                String confirmpassword = cConfirmPass.getText().toString();
                Boolean matchpass = password.equals(confirmpassword);

                if ((!email.isEmpty() && logicBox.Validator.isValidEmail(email)) && !name.isEmpty() && matchpass && checkBox.isChecked() && !mobileNo.isEmpty()) {
                    mProgressDialog.show();
                    CreateAccount(email, password);
                } else {
                    Toast.makeText(getApplication(), "Check Entry", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    public void CreateAccount(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Email", "createUserWithEmail:success");

                            mProgressDialog.setCancelable(false);
                            sharedSpace.putData("name", name);
                            sharedSpace.putData("email", email);
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Users Node dataset
                            HashMap<String, Object> UserValue = new HashMap<>();
                            UserValue.put("name", name);
                            UserValue.put("email", email);
                            UserValue.put("mobile", logicBox.Validator.sanitizePhoneNumber(mobileNo));
                            myRef = FirebaseDatabase.getInstance().getReference();
                            myRef.child("users").child(user.getUid()).setValue(UserValue);

                            //emailauth Node dataset
                            HashMap<String, Object> EmailAuthValue = new HashMap<>();
                            EmailAuthValue.put(user.getUid(), name);
                            myRef.child("userlink").child(logicBox.Validator.sanitizePhoneNumber(mobileNo)).setValue(EmailAuthValue);

                            SignIn(email, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Email", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplication(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    public void SignIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "signInWithEmail:success");
                            Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT).show();
                            sharedSpace.putData("login", "true");
                            startActivity(new Intent(getApplication(), Home.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                            sharedSpace.putData("userid", user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplication(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
}
