package logicBox;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedSpace {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //1.Initialization of SharedPreference
    public SharedSpace(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences("AppData", MODE_PRIVATE);
        sharedPreferences.edit();
        editor = sharedPreferences.edit();
    }

    //2.Get data into SharedPreference
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    //3.Insert data into SharedPreference
    public void putData(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    //4.Remove data of SharedPreference
    public void removeData(String key) {
        editor.remove(key);
        editor.commit();
    }
}
