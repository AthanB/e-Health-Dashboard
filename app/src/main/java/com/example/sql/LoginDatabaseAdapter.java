package com.example.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LoginDatabaseAdapter {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="Login.db";
    private static SQLiteDatabase db;
    private DataBaseHelper dbHelper;

    public LoginDatabaseAdapter(Context context){
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public LoginDatabaseAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    //Method to get password of userName
    public String getSingleEntry(String un)
    {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("User", null, "userName=?", new String[]{un}, null, null, null);
        if (cursor.getCount() <1) //Username not exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String getPassword = cursor.getString(cursor.getColumnIndex("userPassword"));
        cursor.close();
        return getPassword;
    }

    public String getSingleEntry2(String units) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("User", null, "userName=?", new String[]{units}, null, null, null);
        if (cursor.getCount() < 1) //Username not exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String getUnits = cursor.getString(cursor.getColumnIndex("userUnits"));
        cursor.close();
        return getUnits;
    }

    //To stop duplicate usernames
    public Boolean CheckExists(String un){
        Cursor cursor = db.query("User", null, "userName=?", new String[]{un}, null, null, null);
        //if it finds a user with the same username
                    if(cursor.getCount() >0){
              return false;
          }
          else{ return true;
    }
    }


    //Creating account
    public void insertEntry(String un, String pw, String userUnits)
    {
        ContentValues newValues = new ContentValues();
        //Assign values for each row.
        newValues.put("userName", un);
        newValues.put("userPassword", pw);
        newValues.put("userUnits", userUnits);


        //Insert the row into your table
        db.insert("User", null, newValues);
    }
}
