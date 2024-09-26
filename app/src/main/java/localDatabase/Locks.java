package localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Locks {

    public String id;
    public String mac;
    public String name;
    public String location;
    private SetupDB setupDB;
    private EventLockDisplayData evtRecord;
    private String aesKey = "0E8BAAEB3CED73CB";

    public Locks(Context context, EventLockDisplayData evtRecord) {
        setupDB = new SetupDB(context);
        this.evtRecord = evtRecord;
    }


    public void setData() {
        try {

            if (ifLockExists()) //FragmentLock already in localDB, no need to update
            {
                System.out.println("Already exists");
            } else {

                //Encrypting MAC id for securing
                byte[] emac = logicBox.AesEncryptionAlgorithm.encrypt128(mac, aesKey);
                SQLiteDatabase db = setupDB.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("mac", emac);
                values.put("name", name);
                values.put("location", location);

                // Inserting Row
                db.insert("LockData", null, values);
                System.out.println("data inserted");
                db.close(); // Closing database connection
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean ifLockExists() {
        SQLiteDatabase db = setupDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM LockData WHERE id = ?", new String[]{id});

        return cursor.getCount() > 0;

    }

    public boolean ifLockAvailable() {
        SQLiteDatabase db = setupDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM LockData", null);

        return cursor.getCount() > 0;

    }


    public String getData(String lockId) {

        SQLiteDatabase db = setupDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT mac FROM LockData WHERE id = ?", new String[]{lockId});
        String data = "";// = new String[3];
        if (cursor != null) {
            cursor.moveToFirst();
            //data = cursor.getString(0);
            data = logicBox.AesEncryptionAlgorithm.decrypt128(cursor.getBlob(0), aesKey);
        }
        cursor.close();
        db.close();
        System.out.println("data fetch");
        return data;
    }

    //This will return data of first row
    public String[] getFirstRowData() {

        SQLiteDatabase db = setupDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, mac, location FROM LockData ORDER BY ROWID ASC LIMIT 1", null);

        String[] data = new String[6];
        if (cursor != null) {
            cursor.moveToFirst();
            data[0] = cursor.getString(0);
            data[1] = cursor.getString(1);
            data[2] = logicBox.AesEncryptionAlgorithm.decrypt128(cursor.getBlob(2), aesKey);
            data[3] = cursor.getString(3);
        }
        cursor.close();
        db.close();
        System.out.println("data fetch");
        return data;
    }

    public void getLockDisplayData() {
        SQLiteDatabase db = setupDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, location FROM LockData", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String[] data = new String[3];
                    data[0] = cursor.getString(0);
                    data[1] = cursor.getString(1);
                    data[2] = cursor.getString(2);
                    evtRecord.eventDisplayData(data[0], data[1], data[2]);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        //loop end
    }

    public void updatePasscode(String lockId, String passcode) {
        SQLiteDatabase db = setupDB.getWritableDatabase();

        try {

            ContentValues value = new ContentValues();
            value.put("passcode", passcode);

            String selection = "id = ?";
            String[] selectionArgs = {"" + lockId};

            db.update("LockData", value, selection, selectionArgs);
            db.close();
            System.out.println("Passcode updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLock(String lockId) {
        SQLiteDatabase db = setupDB.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {"" + lockId};

        db.delete("LockData", selection, selectionArgs);
        db.close();
        System.out.println("Lock deleted" + lockId);
    }

    public void getAllLockIds() {
        SQLiteDatabase db = setupDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM LockData", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String lockId = cursor.getString(0);
                    System.out.println("database lock " + lockId);
                    evtRecord.eventGetAllLockId(lockId);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
    }
}