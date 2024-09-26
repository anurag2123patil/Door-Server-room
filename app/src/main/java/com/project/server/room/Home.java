package com.project.server.room;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.LockListAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import localDatabase.EventLockDisplayData;
import localDatabase.Locks;
import logicBox.SharedSpace;

@SuppressWarnings("deprecation")
public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, EventLockDisplayData {
    FirebaseAuth mAuth;
    SharedSpace sharedSpace;
    TextView navigationName, navigationEmail;
    CircleImageView navigationPhoto;
    localDatabase.Locks locksLocalDb;
    LockListAdapter myLockListAdapter;
    RecyclerView myList;
    Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> arrayName;
    private ArrayList<String> arrayLocation;
    private ArrayAdapter adapter;
    private List lockNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = Home.this;
        BluetoothAdapter.getDefaultAdapter().enable();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        navigationPhoto = (CircleImageView) hView.findViewById(R.id.profile_image);
        navigationName = (TextView) hView.findViewById(R.id.naviName);
        navigationEmail = (TextView) hView.findViewById(R.id.naviEmail);


        /***************************************************************/

        locksLocalDb = new Locks(Home.this, this);

        arrayName = new ArrayList<>();
        arrayLocation = new ArrayList<>();
        lockNumber = new ArrayList();

        myList = (RecyclerView) findViewById(R.id.recycle);

        myList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        sharedSpace = new SharedSpace(Home.this);

        //This will fetch anme and email from firebase as per user
        getNameandEmailFromFirebase();
        //This will fetch added blLock from SQLite database
        locksLocalDb.getLockDisplayData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplication(), RegisterLock.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.locks) {
            startActivity(new Intent(getApplication(), Home.class));
            finish();
        } else if (id == R.id.user_logout) {
            mAuth.signOut();
            sharedSpace.removeData("login");
            startActivity(new Intent(getApplication(), Login.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        fetchLock();

        //startActivity(new Intent(getApplication(), LockLog.class));
    }

    @Override
    public void eventDisplayData(String id, String name, String location) {
        System.out.println("id :" + id + "\n Name :" + name + "\n Location :" + location);

        arrayName.add(name);            //Device name added
        arrayLocation.add(location);   //Device location added
        lockNumber.add(id);              //Device lockId added

        //Intializing the adapter class
        myLockListAdapter = new LockListAdapter(Home.this, arrayName, arrayLocation, arrayName.size(), lockNumber);

        //Assiging adapter to list
        myList.setAdapter(myLockListAdapter);
    }

    @Override
    public void eventGetAllLockId(String lockId) {

    }

    public void fetchLock() {
        swipeRefreshLayout.setRefreshing(true); //enable
        arrayName.clear();
        arrayLocation.clear();
        lockNumber.clear();
        //This will fetch lockid stored in local database
        locksLocalDb.getAllLockIds();

        locksLocalDb.getLockDisplayData();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);  //after response
            }
        }, 1000);
    }

    //Delete lock data from sqlite and firebase
    public void onDeleteOption(String lockNumber) {
        FirebaseDatabase.getInstance().getReference("lockuser")
                .child(lockNumber).child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        locksLocalDb.deleteLock(lockNumber);
    }

    public void getNameandEmailFromFirebase() {
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> dataCollector = (Map<String, Object>) dataSnapshot.getValue();
                            String name = dataCollector.get("name").toString();
                            String email = dataCollector.get("email").toString();
                            sharedSpace.putData("email", email);
                            sharedSpace.putData("name", name);
                            navigationName.setText(name);
                            navigationEmail.setText(email);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
