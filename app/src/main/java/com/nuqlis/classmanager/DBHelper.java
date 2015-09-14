package com.nuqlis.classmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.GenericArrayType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "storageData.db";


    private final Context ctx;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Classroom\" (\"SchoolTimeID\" TEXT PRIMARY KEY, \"ClassName\" TEXT );");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"StudentClass\" (\"CID\" TEXT, \"StudentName\" TEXT, \"StudentNo\" INTEGER, \"StudentID\" INTEGER, \"Gender\" TEXT, \"SchoolTimeID\" TEXT );");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"AttendData\" (\"CID\" TEXT, \"SchoolTimeID\" TEXT, \"AttendDate\" TEXT, \"AttendType\" TEXT, \"SYNC\" INTEGER);");

    }

    public void InsertClassroom(String schoolTimeID, String className) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SchoolTimeID", schoolTimeID);
        values.put("ClassName", className);

        db.insert("Classroom", null, values);
        db.close();
    }

    public void DeleteAllClassroom () {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Classroom", null, null);
        db.close();
    }

    public int CountClassroom () {
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from Classroom", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        db.close();
        return  count;
    }

    public void DeleteAllStudent () {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("StudentClass", null, null);
        db.close();
    }

    public void InsertStudent(String CID, String studentName, int number, int studentID, String gender, String schoolTimeID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CID", CID);
        values.put("StudentName", studentName);
        values.put("StudentNo", number);
        values.put("StudentID", studentID);
        values.put("Gender", gender);
        values.put("SchoolTimeID", schoolTimeID);

        db.insert("StudentClass", null, values);
        db.close();
    }

    public int CountStudent () {

        SQLiteDatabase db = getWritableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from StudentClass", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        db.close();
        return  count;

    }

    public ArrayList<HashMap<String, String>> GetAllClass() {
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM Classroom;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("TITLE", cursor.getString(1));
                map.put("DESC", cursor.getString(0));

                result.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public ArrayList<HashMap<String, String>> GetStudentInClass(String schoolTimeID) {
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT StudentClass.CID, StudentClass.StudentName, AttendData.AttendType FROM StudentClass LEFT JOIN AttendData ON StudentClass.CID = AttendData.CID WHERE StudentClass.schoolTimeID like  '" + schoolTimeID + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("CID", cursor.getString(0));
                map.put("TITLE", cursor.getString(1));
                map.put("isFirst", "1");
                if (cursor.getString(2) == null) {
                    map.put("ATTEND", "มา");

                } else {
                    map.put("ATTEND", cursor.getString(2));
                }
                Log.d("DB", map.get("ATTEND"));
                result.add(map);
            } while (cursor.moveToNext());
        }
        db.close();

        return result;
    }

    public void InsertAttendData(String cid, String attendType, String date, String schoolTimeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("AttendData", "cid = '" + cid + "' AND AttendDate ='" + date + "' AND SchoolTimeID ='" + schoolTimeID + "'", null);

        ContentValues values = new ContentValues();
        values.put("CID", cid);
        values.put("SchoolTimeID", schoolTimeID);
        values.put("AttendDate", date);
        values.put("AttendType",  attendType);
        values.put("Sync", "0");

        db.insert("AttendData", null, values);
        db.close();
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}