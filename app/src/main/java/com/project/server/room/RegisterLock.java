package com.project.server.room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import logicBox.EventLock;
import logicBox.LockCheck;
import logicBox.SharedSpace;

public class RegisterLock extends AppCompatActivity implements EventLock {
    String lockNumber;
    ProgressDialog progressDialog;
    logicBox.LockCheck lockCheck;
    logicBox.SharedSpace sharedSpace;
    String userId, userName;
    private EditText number1, number2, number3, number4, number5;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_lock);
        progressDialog = new ProgressDialog(this);
        lockCheck = new LockCheck((EventLock) this, RegisterLock.this);
        sharedSpace = new SharedSpace(RegisterLock.this);
        userId = sharedSpace.getString("userid");
        userName = sharedSpace.getString("name");

        number1 = (EditText) findViewById(R.id.et_number1);
        number2 = (EditText) findViewById(R.id.et_number2);
        number3 = (EditText) findViewById(R.id.et_number3);
        number4 = (EditText) findViewById(R.id.et_number4);
        number5 = (EditText) findViewById(R.id.et_number5);
        register = (Button) findViewById(R.id.register);

        //Focus shift
        number1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 1) {
                    number2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        number2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 1) {
                    number3.requestFocus();
                } else if (i2 == 0) {
                    number1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        number3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 1) {
                    number4.requestFocus();
                } else if (i2 == 0) {
                    number2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        number4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 1) {
                    number5.requestFocus();
                } else if (i2 == 0) {
                    number3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        number5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 0) {
                    number4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1. Check blLock with given serial number is available or not.
                //FragmentLock is ideal or assigned to someone else.


                lockNumber = number1.getText().toString() +
                        number2.getText().toString() +
                        number3.getText().toString() +
                        number4.getText().toString() +
                        number5.getText().toString();

                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                lockCheck.checkLockFree(lockNumber, FirebaseAuth.getInstance().getCurrentUser().getUid());
                register.setEnabled(false);

            }
        });
    }

    @Override
    public void eventLockFree(boolean lockStatus, String accessLevel) {
        if (lockStatus) {

            //Enter data into lockuser table
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            myRef.child("lockuser").child(lockNumber).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userName);
            Log.d("done", "eventLockFree: " + "Done");

            //Entry in accesslevel
            DatabaseReference accesslevelRef = FirebaseDatabase.getInstance().getReference();
            accesslevelRef.child("accesslevel").child(lockNumber).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(accessLevel);

            Toast.makeText(getApplication(), " Lock Added Successful", Toast.LENGTH_SHORT).show();

            //Fetch data from firebase and save to local db
            lockCheck.getLockDataFromFirebase(lockNumber);

            register.setEnabled(true);
            progressDialog.dismiss();

            startActivity(new Intent(getApplication(), Home.class));
            finish();
        } else {
            Toast.makeText(getApplication(), " Error lock number", Toast.LENGTH_SHORT).show();
            register.setEnabled(true);
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                startActivity(new Intent(getApplication(), Home.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplication(), Home.class));
        finish();
    }
}
