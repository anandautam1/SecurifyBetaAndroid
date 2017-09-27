package org.farmate.securifybeta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.farmate.securifybeta.database.usersLocal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ananda on 15/09/2017.
 */

public class securifyUserDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "usersLocal.db";

    // usersLocal table name
    private static final String TABLE_USER = "usersLocal";

    private static final String COLUMN_USERID = "userID";
    private static final String COLUMN_FNAME = "fname";
    private static final String COLUMN_LNAME = "lname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASS_HASHED = "pass_hashed";
    private static final String COLUMN_PASS_SALT = "pass_salt";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_GPS_LONG = "gps_long";
    private static final String COLUMN_GPS_LATI = "gps_lati";
    private static final String COLUMN_ISONLINE = "isOnline";
    private static final String COLUMN_LASTUPDATED = "lastUpdated";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USERID + " INTEGER PRIMARY KEY," + COLUMN_FNAME + " TEXT,"
            + COLUMN_LNAME + " TEXT," + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PHONE + " TEXT," + COLUMN_PASS_HASHED + " TEXT,"
            + COLUMN_PASS_SALT + " TEXT," + COLUMN_ROLE + " TEXT,"
            + COLUMN_GPS_LONG + " TEXT," + COLUMN_GPS_LATI + " TEXT,"
            + COLUMN_ISONLINE + " INTEGER," + COLUMN_LASTUPDATED + " TEXT"
            + ")";
    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    public securifyUserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop usersLocal Table if exist
        db.execSQL(DROP_USER_TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * This method is to create usersLocal record
     *
     * @param usersLocal
     */
    public void addUser(usersLocal usersLocal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, usersLocal.getUserID());
        values.put(COLUMN_FNAME, usersLocal.getFname());
        values.put(COLUMN_LNAME, usersLocal.getLname());
        values.put(COLUMN_EMAIL, usersLocal.getEmail());
        values.put(COLUMN_PHONE, usersLocal.getPhone());
        values.put(COLUMN_PASS_HASHED, usersLocal.getPass_hashed());
        values.put(COLUMN_PASS_SALT, usersLocal.getPass_salt());
        values.put(COLUMN_ROLE, usersLocal.getRole());
        values.put(COLUMN_GPS_LONG, usersLocal.getGps_long());
        values.put(COLUMN_GPS_LATI, usersLocal.getGps_lati());
        values.put(COLUMN_ISONLINE, usersLocal.getIsOnline());
        values.put(COLUMN_LASTUPDATED, usersLocal.getLastUpdated());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * This method is to fetch all usersLocal and return the list of usersLocal records
     *
     * @return list
     */
    public List<usersLocal> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID,
                COLUMN_FNAME,
                COLUMN_LNAME,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_PASS_HASHED,
                COLUMN_PASS_SALT,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_ISONLINE,
                COLUMN_LASTUPDATED
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<usersLocal> userList = new ArrayList<usersLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the usersLocal table
        /**
         * Here query function is used to fetch records from usersLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM usersLocal ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // new instance of a userLocal
                usersLocal userLocal = new usersLocal();

                userLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                userLocal.setFname(cursor.getString(cursor.getColumnIndex(COLUMN_FNAME)));
                userLocal.setLname(cursor.getString(cursor.getColumnIndex(COLUMN_LNAME)));
                userLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                userLocal.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                userLocal.setPass_hashed(cursor.getString(cursor.getColumnIndex(COLUMN_PASS_HASHED)));
                userLocal.setPass_salt(cursor.getString(cursor.getColumnIndex(COLUMN_PASS_SALT)));
                userLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                userLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                userLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                userLocal.setIsOnline(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ISONLINE))));
                userLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding usersLocal record to list
                userList.add(userLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return usersLocal list
        return userList;
    }

    public List<usersLocal> getUserOnEmail(String email) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID,
                COLUMN_FNAME,
                COLUMN_LNAME,
                COLUMN_PHONE,
                COLUMN_PASS_HASHED,
                COLUMN_ROLE
        };

        String[] like = {
                email
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<usersLocal> userList = new ArrayList<usersLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_EMAIL + " = ?";

        // query the usersLocal table
        /**
         * Here query function is used to fetch records from usersLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM usersLocal ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                like,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                sortOrder);

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // new instance of a userLocal
                usersLocal userLocal = new usersLocal();
                userLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                userLocal.setFname(cursor.getString(cursor.getColumnIndex(COLUMN_FNAME)));
                userLocal.setLname(cursor.getString(cursor.getColumnIndex(COLUMN_LNAME)));
                userLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                userLocal.setPass_hashed(cursor.getString(cursor.getColumnIndex(COLUMN_PASS_HASHED)));
                userLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));

                // Adding usersLocal record to list
                userList.add(userLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return usersLocal list
        return userList;

    }

    public List<usersLocal> getUserOnUserID(int userID) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID,
                COLUMN_FNAME,
                COLUMN_LNAME,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_PASS_HASHED,
                COLUMN_PASS_SALT,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_ISONLINE,
                COLUMN_LASTUPDATED
        };

        String userIDString = String.valueOf(userID);

        String[] like = {
                userIDString
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<usersLocal> userList = new ArrayList<usersLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // select to sacn on the column of the userID
        String selection = COLUMN_USERID + " = ?";

        // query the usersLocal table
        /**
         * Here query function is used to fetch records from usersLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM usersLocal ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                like,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                sortOrder);

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // new instance of a userLocal
                usersLocal userLocal = new usersLocal();
                userLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                userLocal.setFname(cursor.getString(cursor.getColumnIndex(COLUMN_FNAME)));
                userLocal.setLname(cursor.getString(cursor.getColumnIndex(COLUMN_LNAME)));
                userLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                userLocal.setPass_hashed(cursor.getString(cursor.getColumnIndex(COLUMN_PASS_HASHED)));
                userLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                userLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                userLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                userLocal.setIsOnline(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ISONLINE))));
                userLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding usersLocal record to list
                userList.add(userLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return usersLocal list
        return userList;

    }

    public List<usersLocal> getAllUserExceptUserID(int userID) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID,
                COLUMN_FNAME,
                COLUMN_LNAME,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_PASS_HASHED,
                COLUMN_PASS_SALT,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_ISONLINE,
                COLUMN_LASTUPDATED
        };

        String userIDString = String.valueOf(userID);

        String[] like = {
                userIDString
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<usersLocal> userList = new ArrayList<usersLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // select to sacn on the column of the userID
        String selection = COLUMN_USERID + " != ?";

        // query the usersLocal table
        /**
         * Here query function is used to fetch records from usersLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM usersLocal ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                like,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                sortOrder);

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // new instance of a userLocal
                usersLocal userLocal = new usersLocal();
                userLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                userLocal.setFname(cursor.getString(cursor.getColumnIndex(COLUMN_FNAME)));
                userLocal.setLname(cursor.getString(cursor.getColumnIndex(COLUMN_LNAME)));
                userLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                userLocal.setPass_hashed(cursor.getString(cursor.getColumnIndex(COLUMN_PASS_HASHED)));
                userLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                userLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                userLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                userLocal.setIsOnline(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ISONLINE))));
                userLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding usersLocal record to list
                userList.add(userLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return usersLocal list
        return userList;

    }

    /**
     * This method to update usersLocal record
     *
     * @param usersLocal
     */
    public void updateUser(usersLocal usersLocal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, usersLocal.getUserID());
        values.put(COLUMN_FNAME, usersLocal.getFname());
        values.put(COLUMN_LNAME, usersLocal.getLname());
        values.put(COLUMN_EMAIL, usersLocal.getEmail());
        values.put(COLUMN_PHONE, usersLocal.getPhone());
        values.put(COLUMN_PASS_HASHED, usersLocal.getPass_hashed());
        values.put(COLUMN_PASS_SALT, usersLocal.getPass_salt());
        values.put(COLUMN_ROLE, usersLocal.getRole());
        values.put(COLUMN_GPS_LONG, usersLocal.getGps_long());
        values.put(COLUMN_GPS_LATI, usersLocal.getGps_lati());
        values.put(COLUMN_ISONLINE, usersLocal.getIsOnline());
        values.put(COLUMN_LASTUPDATED, usersLocal.getLastUpdated());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USERID + " = ?",
                new String[]{String.valueOf(usersLocal.getUserID())});
        db.close();
    }

    /**
     * This method is to delete usersLocal record
     *
     * @param usersLocal
     */
    public void deleteUser(usersLocal usersLocal) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete usersLocal record by id
        db.delete(TABLE_USER, COLUMN_USERID + " = ?",
                new String[]{String.valueOf(usersLocal.getUserID())});
        db.close();
    }

    /**
     * This method to check usersLocal exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query usersLocal table with condition
        /**
         * Here query function is used to fetch records from usersLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM usersLocal WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check usersLocal exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_EMAIL + " = ?" + " AND " + COLUMN_PASS_HASHED + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query usersLocal table with conditions
        /**
         * Here query function is used to fetch records from usersLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM usersLocal WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
