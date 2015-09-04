package com.nuqlis.classmanager;
import android.content.SharedPreferences;

/**
 * Created by Chadpong on 4/9/2558.
 */
public class UserPref {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public UserPref( SharedPreferences sharedPref ) {
        this.sharedPref = sharedPref;
         editor = this.sharedPref.edit();
    }

    public void Login (String username , String staffID) {
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.putString("staffID", staffID );
        editor.commit();
    }

    public String GetUsername () {
        return sharedPref.getString("username", "");
    }

    public String GetStaffID () {
        return sharedPref.getString("staffID", "");
    }

    public boolean IsLoggedIn() {
        return sharedPref.getBoolean("isLoggedIn", false);
    }
}
