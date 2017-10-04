package org.farmate.securifybeta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.farmate.securifybeta.database.jobsLocal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ananda on 15/09/2017.
 */

public class securifyJobDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "jobsLocal.db";

    // jobsLocal table name
    private static final String TABLE_USER = "jobsLocal";

    private static final String COLUMN_JOBID = "jobID";
    private static final String COLUMN_USERID = "userID";
    private static final String COLUMN_JOBNICKNAME = "jobNickName";
    private static final String COLUMN_REGISTRATION_NUM = "registrationNum";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_IMG_URI = "image_uri";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_GPS_LONG = "gps_long";
    private static final String COLUMN_GPS_LATI = "gps_lati";
    private static final String COLUMN_LASTSERVICED = "lastServiced";
    private static final String COLUMN_LASTUPDATED = "lastUpdated";

    // create table sql query
    private String CREATE_JOB_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_JOBID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERID + " INTEGER,"
            + COLUMN_JOBNICKNAME + " TEXT,"
            + COLUMN_REGISTRATION_NUM + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_IMG_URI + " TEXT,"
            + COLUMN_ROLE  + " TEXT,"
            + COLUMN_GPS_LONG + " TEXT,"
            + COLUMN_GPS_LATI + " TEXT,"
            + COLUMN_LASTSERVICED + " TEXT,"
            + COLUMN_LASTUPDATED + " TEXT"
            + ")";
    // drop table sql query
    private String DROP_JOB_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    public securifyJobDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
//        db.execSQL(DROP_JOB_TABLE);
        db.execSQL(CREATE_JOB_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop jobsLocal Table if exist
        db.execSQL(DROP_JOB_TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * This method is to create jobsLocal record
     *
     * @param jobsLocal
     */
    public void addJob(jobsLocal jobsLocal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, jobsLocal.getUserID());
        values.put(COLUMN_JOBNICKNAME, jobsLocal.getJobNickName());
        values.put(COLUMN_REGISTRATION_NUM, jobsLocal.getRegistrationNumberString());
        values.put(COLUMN_EMAIL, jobsLocal.getEmail());
        values.put(COLUMN_PHONE, jobsLocal.getPhone());
        values.put(COLUMN_IMG_URI, jobsLocal.getImage_uri());
        values.put(COLUMN_ROLE, jobsLocal.getRole());
        values.put(COLUMN_GPS_LONG, jobsLocal.getGps_long());
        values.put(COLUMN_GPS_LATI, jobsLocal.getGps_lati());
        values.put(COLUMN_LASTSERVICED, jobsLocal.getLastServiced());
        values.put(COLUMN_LASTUPDATED, jobsLocal.getLastUpdated());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * This method is to fetch all jobsLocal and return the list of jobsLocal records
     *
     * @return list
     */
    public List<jobsLocal> getAllJob() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_JOBID,
                COLUMN_USERID,
                COLUMN_JOBNICKNAME,
                COLUMN_REGISTRATION_NUM,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_IMG_URI,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_LASTSERVICED,
                COLUMN_LASTUPDATED
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<jobsLocal> userList = new ArrayList<jobsLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the jobsLocal table
        /**
         * Here query function is used to fetch records from jobsLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM jobsLocal ORDER BY user_name;
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
                // new instance of a jobsLocal
                jobsLocal jobsLocal = new jobsLocal();
                jobsLocal.setJobID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_JOBID))));
                jobsLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                jobsLocal.setJobNickName(cursor.getString(cursor.getColumnIndex(COLUMN_JOBNICKNAME)));
                jobsLocal.setRegistrationNumberString(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_NUM)));
                jobsLocal.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                jobsLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                jobsLocal.setImage_uri(cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URI)));
                jobsLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                jobsLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                jobsLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                jobsLocal.setLastServiced(cursor.getString(cursor.getColumnIndex(COLUMN_LASTSERVICED)));
                jobsLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return jobsLocal list
        return userList;
    }

    public List<jobsLocal> getJobOnEmail(String email) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_JOBID,
                COLUMN_USERID,
                COLUMN_JOBNICKNAME,
                COLUMN_REGISTRATION_NUM,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_IMG_URI,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_LASTSERVICED,
                COLUMN_LASTUPDATED
        };

        String[] like = {
                email
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<jobsLocal> userList = new ArrayList<jobsLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_EMAIL + " = ?";

        // query the jobsLocal table
        /**
         * Here query function is used to fetch records from jobsLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM jobsLocal ORDER BY user_name;
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
                jobsLocal jobsLocal = new jobsLocal();
                jobsLocal.setJobID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_JOBID))));
                jobsLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                jobsLocal.setJobNickName(cursor.getString(cursor.getColumnIndex(COLUMN_JOBNICKNAME)));
                jobsLocal.setRegistrationNumberString(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_NUM)));
                jobsLocal.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                jobsLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                jobsLocal.setImage_uri(cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URI)));
                jobsLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                jobsLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                jobsLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                jobsLocal.setLastServiced(cursor.getString(cursor.getColumnIndex(COLUMN_LASTSERVICED)));
                jobsLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding jobsLocal record to list
                userList.add(jobsLocal);

                // Adding jobsLocal record to list
                userList.add(jobsLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return jobsLocal list
        return userList;

    }

    public List<jobsLocal> getJobOnJobID(int jobID) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_JOBID,
                COLUMN_USERID,
                COLUMN_JOBNICKNAME,
                COLUMN_REGISTRATION_NUM,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_IMG_URI,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_LASTSERVICED,
                COLUMN_LASTUPDATED
        };

        String userIDString = String.valueOf(jobID);

        String[] like = {
                userIDString
        };
        // sorting orders
        String sortOrder = COLUMN_JOBID + " ASC";
        List<jobsLocal> userList = new ArrayList<jobsLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // select to sacn on the column of the userID
        String selection = COLUMN_JOBID + " = ?";

        // query the jobsLocal table
        /**
         * Here query function is used to fetch records from jobsLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM jobsLocal ORDER BY user_name;
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
                jobsLocal jobsLocal = new jobsLocal();
                jobsLocal.setJobID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_JOBID))));
                jobsLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                jobsLocal.setJobNickName(cursor.getString(cursor.getColumnIndex(COLUMN_JOBNICKNAME)));
                jobsLocal.setRegistrationNumberString(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_NUM)));
                jobsLocal.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                jobsLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                jobsLocal.setImage_uri(cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URI)));
                jobsLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                jobsLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                jobsLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                jobsLocal.setLastServiced(cursor.getString(cursor.getColumnIndex(COLUMN_LASTSERVICED)));
                jobsLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return jobsLocal list
        return userList;

    }

    public List<jobsLocal> getJobOnUserID(int userID) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_JOBID,
                COLUMN_USERID,
                COLUMN_JOBNICKNAME,
                COLUMN_REGISTRATION_NUM,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_IMG_URI,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_LASTSERVICED,
                COLUMN_LASTUPDATED
        };

        String userIDString = String.valueOf(userID);

        String[] like = {
                userIDString
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<jobsLocal> userList = new ArrayList<jobsLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // select to sacn on the column of the userID
        String selection = COLUMN_USERID + " = ?";

        // query the jobsLocal table
        /**
         * Here query function is used to fetch records from jobsLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM jobsLocal ORDER BY user_name;
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
                jobsLocal jobsLocal = new jobsLocal();
                jobsLocal.setJobID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_JOBID))));
                jobsLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                jobsLocal.setJobNickName(cursor.getString(cursor.getColumnIndex(COLUMN_JOBNICKNAME)));
                jobsLocal.setRegistrationNumberString(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_NUM)));
                jobsLocal.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                jobsLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                jobsLocal.setImage_uri(cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URI)));
                jobsLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                jobsLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                jobsLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                jobsLocal.setLastServiced(cursor.getString(cursor.getColumnIndex(COLUMN_LASTSERVICED)));
                jobsLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return jobsLocal list
        return userList;

    }

    public List<jobsLocal> getAllJobExceptUserID(int userID) {
        // email stirng will be the first argument within the query

        // array of columns to fetch
        String[] columns = {
                COLUMN_JOBID,
                COLUMN_USERID,
                COLUMN_JOBNICKNAME,
                COLUMN_REGISTRATION_NUM,
                COLUMN_EMAIL,
                COLUMN_PHONE,
                COLUMN_IMG_URI,
                COLUMN_ROLE,
                COLUMN_GPS_LONG,
                COLUMN_GPS_LATI,
                COLUMN_LASTSERVICED,
                COLUMN_LASTUPDATED
        };

        String userIDString = String.valueOf(userID);

        String[] like = {
                userIDString
        };
        // sorting orders
        String sortOrder = COLUMN_USERID + " ASC";
        List<jobsLocal> userList = new ArrayList<jobsLocal>();

        SQLiteDatabase db = this.getReadableDatabase();

        // select to sacn on the column of the userID
        String selection = COLUMN_USERID + " != ?";

        // query the jobsLocal table
        /**
         * Here query function is used to fetch records from jobsLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM jobsLocal ORDER BY user_name;
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
                jobsLocal jobsLocal = new jobsLocal();
                jobsLocal.setJobID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_JOBID))));
                jobsLocal.setUserID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))));
                jobsLocal.setJobNickName(cursor.getString(cursor.getColumnIndex(COLUMN_JOBNICKNAME)));
                jobsLocal.setRegistrationNumberString(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_NUM)));
                jobsLocal.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                jobsLocal.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                jobsLocal.setImage_uri(cursor.getString(cursor.getColumnIndex(COLUMN_IMG_URI)));
                jobsLocal.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_ROLE)));
                jobsLocal.setGps_long(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LONG))));
                jobsLocal.setGps_lati(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_GPS_LATI))));
                jobsLocal.setLastServiced(cursor.getString(cursor.getColumnIndex(COLUMN_LASTSERVICED)));
                jobsLocal.setLastUpdated(cursor.getString(cursor.getColumnIndex(COLUMN_LASTUPDATED)));
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
                // Adding jobsLocal record to list
                userList.add(jobsLocal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return jobsLocal list
        return userList;

    }

    /**
     * This method to update jobsLocal record
     *
     * @param jobsLocal
     */
    public void updateJob(jobsLocal jobsLocal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, jobsLocal.getUserID());
        values.put(COLUMN_JOBNICKNAME, jobsLocal.getEmail());
        values.put(COLUMN_REGISTRATION_NUM, jobsLocal.getRegistrationNumberString());
        values.put(COLUMN_EMAIL, jobsLocal.getEmail());
        values.put(COLUMN_PHONE, jobsLocal.getPhone());
        values.put(COLUMN_IMG_URI, jobsLocal.getImage_uri());
        values.put(COLUMN_ROLE, jobsLocal.getRole());
        values.put(COLUMN_GPS_LONG, jobsLocal.getGps_long());
        values.put(COLUMN_GPS_LATI, jobsLocal.getGps_lati());
        values.put(COLUMN_LASTSERVICED, jobsLocal.getLastServiced());
        values.put(COLUMN_LASTUPDATED, jobsLocal.getLastUpdated());

        // updating row
        db.update(TABLE_USER, values, COLUMN_JOBID + " = ?",
                new String[]{String.valueOf(jobsLocal.getJobID())});
        db.close();
    }

    /**
     * This method is to delete jobsLocal record
     *
     * @param jobsLocal
     */
    public void deleteUser(jobsLocal jobsLocal) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete jobsLocal record by the jobID
        db.delete(TABLE_USER, COLUMN_JOBID + " = ?",
                new String[]{String.valueOf(jobsLocal.getJobID())});
        db.close();
    }

    /**
     * This method to check jobsLocal exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkJob(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USERID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query jobsLocal table with condition
        /**
         * Here query function is used to fetch records from jobsLocal table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM jobsLocal WHERE user_email = 'jack@androidtutorialshub.com';
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

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
