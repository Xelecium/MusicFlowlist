package xeleciumlabs.musicflowlist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Xelecium on 6/8/2015.
 * This file stores the database functionality for the Music Flowlist application.
 * The current schema is as follows:
 *
 * LEAD_TRACKS TABLE
 * LEAD_URI TEXT PRIMARY KEY
 *
 * FOLLOW_TRACKS TABLE
 * ID INTEGER PRIMARY KEY AUTOINCREMENT
 * LEAD_URI TEXT FOREIGN KEY (REFERENCES LEAD_TRACKS(LEAD_URI))
 * FOLLOW_URI TEXT
 */
public class TrackListSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tracks.db";
    private static final int DB_VERSION = 1;

    //Table of leading tracks
    public static final String LEAD_TABLE = "LEAD_TRACKS";  //Table name
    public static final String COLUMN_LEAD_URI = "LEAD_URI";  //Track's URI

    private static String CREATE_LEAD_TRACKS =
            "CREATE TABLE " + LEAD_TABLE +                      //Create Table w/ name
                    "(" + COLUMN_LEAD_URI + " TEXT PRIMARY KEY)";       //Track's URI is primary key

    //Table of following tracks
    public static final String FOLLOW_TABLE = "FOLLOW_TRACKS";  //Table name
    public static final String COLUMN_FOREIGN_KEY_LEAD = "LEAD_URI";    //Leading Track's URI
    public static final String COLUMN_FOLLOW_URI = "FOLLOW_URI";    //Follow Track's URI

    private static String CREATE_FOLLOW_TRACKS =
            "CREATE TABLE " + FOLLOW_TABLE +                                //Create Table w/ name
                    "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + //Auto-incremented ID# as PKEY
                    COLUMN_FOREIGN_KEY_LEAD + " TEXT, " +                           //Leading Track's URI
                    COLUMN_FOLLOW_URI + " TEXT, " +                                 //Follow Track's URI
                    "FOREIGN KEY(" + COLUMN_FOREIGN_KEY_LEAD + ") " +               //Specify LeadTrack as FKEY
                    "REFERENCES " + LEAD_TABLE + "(" + COLUMN_LEAD_URI + "))";      //Referencing LeadTable

    //Constructor for the database
    public TrackListSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_LEAD_TRACKS);
        database.execSQL(CREATE_FOLLOW_TRACKS);
    }

    @Override   //Method for updating database schema between versions
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        switch (oldVersion) {
            default: break;
        }
    }
}
