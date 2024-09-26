package com.project.server.room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.LockLogAdapter;

public class LockLog extends AppCompatActivity {
    List<String> keySet = new ArrayList<>();
    Map<String, Object> dataMap = new HashMap<>();
    RecyclerView logView;
    LockLogAdapter lockLogAdapter;
    Context mContext;
    List<String> userName = new ArrayList<>();
    List<String> timeStamp = new ArrayList<>();
    String lockNumber, lockName, lockAddress;
    private Map<String, Object> dataCollector;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_lock:
                    startActivity(new Intent(getApplication(), Home.class));
                    finish();
                    return true;
                case R.id.action_Users:
                    intentCall(getApplication(), LockUsers.class);
                    return true;
                case R.id.action_logs:
                    intentCall(getApplication(), LockLog.class);
                    return true;
            }
            return false;
        }
    };

    private void intentCall(Context mContext, Class aClass) {
        Intent intent = new Intent(mContext, aClass);
        intent.putExtra("lockNumber", lockNumber);
        intent.putExtra("lockName", lockName);
        intent.putExtra("lockAddress", lockAddress);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_log);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mContext = LockLog.this;
        logView = (RecyclerView) findViewById(R.id.recycleView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        logView.setLayoutManager(mLayoutManager);

        lockNumber = getIntent().getExtras().getString("lockNumber");
        lockName = getIntent().getExtras().getString("lockName");
        lockAddress = getIntent().getStringExtra("lockAddress");
        CheckAccessLevel(lockNumber);
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

    private void CheckAccessLevel(final String lockNumber) {
        FirebaseDatabase.getInstance().getReference("accesslevel/" + lockNumber + "/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equalsIgnoreCase("owner")) {
                    FirebaseDatabase.getInstance().getReference("logs").child(lockNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataCollector = (Map<String, Object>) dataSnapshot.getValue();

                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    String keyValue = childSnapshot.getKey();
                                    keySet.add(keyValue);
                                }

                                for (int i = 0; i < keySet.size(); i++) {
                                    dataMap = (Map<String, Object>) dataCollector.get(keySet.get(i));
                                    userName.add(dataMap.get("name").toString());
                                    timeStamp.add(dataMap.get("timestamp").toString());
                                }
                                lockLogAdapter = new LockLogAdapter(mContext, userName, timeStamp, lockName);
                                logView.setAdapter(lockLogAdapter);
                                lockLogAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplication(), "Empty log", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(getApplication(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
