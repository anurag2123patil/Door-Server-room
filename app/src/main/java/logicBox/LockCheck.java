package logicBox;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import localDatabase.Locks;

import static android.content.ContentValues.TAG;


public class LockCheck {
    EventLock eventLock;
    private Map<String, Object> dataCollector;
    private localDatabase.Locks locaDBLocks;

    public LockCheck(EventLock eventLock, Context mContext) {
        this.eventLock = eventLock;
        locaDBLocks = new Locks(mContext, null);

    }

    public LockCheck() {
    }

    public void checkLockFree(final String lockNumber, final String userid) {
        //1. The lock should be available in lockentry.
        //2. The lock should not be available in lockuser

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("lockentry/" + lockNumber);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    //Lock Available in  lockentry
                    //Lock Available in  lockentry, now check if not available in lockuser

                    Log.d(TAG, "onDataChange:Pass 1 ");
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("lockuser/" + lockNumber);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.d(TAG, "onDataChange:Pass 2 ");
                                DatabaseReference reference = FirebaseDatabase.getInstance()
                                        .getReference("authorizeaccess/" + userid);
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Log.d(TAG, "onDataChange:Pass 3 ");
                                            Map<String, Object> dataCollector = (Map<String, Object>) dataSnapshot.getValue();
                                            if (dataCollector.get(lockNumber).equals(true)) {
                                                eventLock.eventLockFree(true, "guest");
                                            } else {
                                                eventLock.eventLockFree(false, null);
                                            }
                                        } else {
                                            eventLock.eventLockFree(false, null);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                eventLock.eventLockFree(true, "owner");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            eventLock.eventLockFree(false, null);//DB Error
                        }
                    });
                } else {
                    //Lock not available in lockentry.
                    eventLock.eventLockFree(false, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventLock.eventLockFree(false, null);
            }
        });
    }

    public void getLockDataFromFirebase(final String lockNumber) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("lockentry/" + lockNumber);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataCollector = (Map<String, Object>) dataSnapshot.getValue();

                locaDBLocks.id = lockNumber;   //lockid
                locaDBLocks.name = (String) dataCollector.get("lockname"); //lock name
                locaDBLocks.location = (String) dataCollector.get("address");   //location
                locaDBLocks.mac = (String) dataCollector.get("mac");   //mac

                //System.out.println(locks.id +"\n" +locks.hash+"\n" +locks.mac);
                //Setting data to local database
                locaDBLocks.setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
