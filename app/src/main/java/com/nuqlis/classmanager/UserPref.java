package com.nuqlis.classmanager;
import android.content.SharedPreferences;

/*
UserPref -> Manage stored User data eg. username, login-status, role, class ,etc.
 */
public class UserPref {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public UserPref( SharedPreferences sharedPref ) {
        this.sharedPref = sharedPref;
         editor = this.sharedPref.edit();
    }

    public void Login (String staffID, String hostID) {
        editor.putBoolean("isLoggedIn", true);
        editor.putString("staffID", staffID );
        editor.putString("hostID", hostID);
        editor.commit();
    }

    public String GetHostID () { return  sharedPref.getString("hostID", ""); }
    public String GetStaffID () {
        return sharedPref.getString("staffID", "");
    }

    public void ClearUserPref () {
        editor.clear();
        editor.commit();
    }

    public void SetConnectivity (boolean isCon) {
        editor.putBoolean("isConnected", isCon);
        editor.commit();
    }

    public boolean IsConnected () { return  sharedPref.getBoolean("isConnected", false); }

    public boolean IsLoggedIn() {
        return sharedPref.getBoolean("isLoggedIn", false);
    }
}
