package com.project.server.room;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import localDatabase.EventLockDisplayData;
import localDatabase.Locks;
import logicBox.SharedSpace;

public class LockOperate extends AppCompatActivity implements EventLockDisplayData {

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "bluetooth2";
    final int RECIEVE_MESSAGE = 1;
    String address;
    String lockNumber;
    String lockName;
    String lockAddress;
    TextView cLockAddress;
    Button unlock;
    ImageView lockView;
    localDatabase.Locks locksLocalDb;
    ProgressDialog progressDialog;
    Handler h;
    String dataFromBoard;
    SharedSpace sharedSpace;
    int operateMode = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;
    private StringBuilder sb;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_lock:
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(getApplication(), Home.class));
                    finish();
                    return true;
                case R.id.action_Users:
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    intentCall(getApplication(), LockUsers.class);
                    return true;
                case R.id.action_logs:
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_operate);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        progressDialog = new ProgressDialog(this);
        locksLocalDb = new Locks(LockOperate.this, this);
        sharedSpace = new SharedSpace(LockOperate.this);
        lockView = (ImageView) findViewById(R.id.lockpic);
        unlock = (Button) findViewById(R.id.unlock);
        cLockAddress = (TextView) findViewById(R.id.tv_lockAddress);
        lockNumber = getIntent().getExtras().getString("lockNumber");
        lockName = getIntent().getExtras().getString("lockName");
        lockAddress = getIntent().getStringExtra("lockAddress");
        cLockAddress.setText(lockAddress);

        //Bluetooth logic
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        address = locksLocalDb.getData(lockNumber);
        Log.d(TAG, "onCreate: " + address);
        //address = "20:15:06:26:10:43";
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        sb = new StringBuilder();
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage

                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);// create string from bytes array
                        sb.append(strIncom);                                                // append string

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dataFromBoard = sb.toString();
                                Log.d(TAG, "handleMessage: " + dataFromBoard);
                            }
                        }, 50);
                        break;
                }
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set up a pointer to the remote node using it's address.
                try {
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    try {
                        btSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                    }
                    btAdapter.cancelDiscovery();
                    Log.d(TAG, "...Connecting...");
                    try {
                        btSocket.connect();
                        Log.d(TAG, "....Connection ok...");
                    } catch (IOException e) {
                        try {
                            btSocket.close();
                        } catch (IOException e2) {
                            errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        }
                    }
                    Log.d(TAG, "...Create Socket...");

                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    startActivity(new Intent(getApplication(), Home.class));
                    finish();
                    Toast.makeText(getApplication(), "Invalid mac", Toast.LENGTH_SHORT).show();
                }
            }
        }, 50);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                }

            }
        }, 1500);

        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operateMode == 0) {
                    mConnectedThread.write("91194");    // Send data via Bluetooth
                    progressDialog.setMessage("Unlocking...");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "handleMe: " + dataFromBoard);
                            if (dataFromBoard != null && dataFromBoard.length() == 8) {
                                mConnectedThread.write("unlock");    // Send data via Bluetooth
                                lockView.setImageResource(R.drawable.unlock);
                                progressDialog.dismiss();
                                pushLogToFire(lockNumber);
                                Toast.makeText(getApplication(), "Unlock Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplication(), "Connection Error..", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }, 3000);
                    operateMode = 1;
                } else if (operateMode == 1) {
                    mConnectedThread.write("lock");    // Send data via Bluetooth
                    progressDialog.setMessage("Locking...");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lockView.setImageResource(R.drawable.lock);
                            progressDialog.dismiss();
                            Toast.makeText(getApplication(), "Lock Successfully", Toast.LENGTH_SHORT).show();
                            unlock.setEnabled(false);
                            try {
                                btSocket.close();
                                btAdapter.disable();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 2000);
                }
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplication(), Home.class));
        finish();
    }

    @Override
    public void eventDisplayData(String id, String name, String location) {

    }

    @Override
    public void eventGetAllLockId(String lockId) {

    }

    private void pushLogToFire(String lockNumber) {
        Map<String, Object> logData = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy HH:mm");
        Date now = new Date();
        String timeStamp = dateFormat.format(now);
        logData.put("name", sharedSpace.getString("name"));
        logData.put("timestamp", timeStamp);
        FirebaseDatabase.getInstance().getReference("logs").child(lockNumber).push().setValue(logData);
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

    private class ConnectedThread extends Thread {
        private final OutputStream mmOutStream;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmOutStream = tmpOut;
            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                    Log.d(TAG, "run: " + buffer);
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}
