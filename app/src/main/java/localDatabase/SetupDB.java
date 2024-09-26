package localDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SetupDB extends SQLiteOpenHelper {

    //1.Create database so here we created database named as Config
    public SetupDB(Context context) {
        super(context, "lockdata", null, 1);
    }

    //2.Create database table so here table named as ConfigData with tuple sign
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + "LockData" +
//                "(" + "id" + " TEXT," + "mac" + " TEXT," + "name" + " TEXT," + "location" + " TEXT," + "passcode" + " TEXT" + ")";
                "(" + "id" + " TEXT," + "mac" + " TEXT," + "name" + " TEXT," + "location" + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    //3.Drop older table if exist to make create new table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + "ConfigData");
        db.execSQL("DROP TABLE IF EXISTS " + "LockData");
        // Create tables again
        onCreate(db);
    }
}