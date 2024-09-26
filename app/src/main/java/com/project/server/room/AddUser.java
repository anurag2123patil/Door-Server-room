package com.project.server.room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity {
    String lockNumber;
    String lockName;
    String lockAddress;
    Button cAddUser;
    EditText mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        lockNumber = getIntent().getExtras().getString("lockNumber");
        lockName = getIntent().getExtras().getString("lockName");
        lockAddress = getIntent().getStringExtra("lockAddress");

        cAddUser = (Button) findViewById(R.id.bt_addUser);
        mobileNumber = (EditText) findViewById(R.id.input_phone);

        cAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMobileNumberUserExists("91" + mobileNumber.getText().toString());
            }
        });
    }

    public void checkMobileNumberUserExists(final String mobileNumber) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("userlink/" + mobileNumber);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        Map<String, Object> authData = new HashMap<>();
                        authData.put(lockNumber, true);
                        FirebaseDatabase.getInstance().getReference("authorizeaccess")
                                .child(userId).setValue(authData);
                        Toast.makeText(getApplication(), "User Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mobileNumber));
                        String message = "You have been invited to access lock: " + lockName +
                                ".\nPlease add lock serial: " + lockNumber + " to start accessing.";
                        intent.putExtra("sms_body", message);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplication(), "User does not exists..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
    protected void onStop() {
        super.onStop();
        mobileNumber.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplication(), Home.class));
        finish();
    }
}

