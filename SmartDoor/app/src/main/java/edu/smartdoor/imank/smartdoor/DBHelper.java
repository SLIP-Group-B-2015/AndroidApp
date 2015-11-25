package edu.smartdoor.imank.smartdoor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates DB to store all door events to show to the user while phone is offline
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DBHelper.class.getSimpleName();

    public static final String DB_NAME = "timeline.db";
    public static final int DB_VERSION = 1;
    public static final String EVENT_TABLE = "events";
    public static final String RASPBERRY_ID = "Raspberry_ID";
    public static final String EVENT_ID = "Event_ID";
    public static final String EVENT_TYPE = "Event_Type";
    public static final String TIME = "Event_Time";
    public static final String NOTE = "Event_Note";
    public static final String NAME = "Name";

    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s text, %s text, %s text, %s text, %s text, %s text)", EVENT_TABLE, RASPBERRY_ID, EVENT_ID, EVENT_TYPE, TIME, NOTE, NAME);
        Log.d(LOG_TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + EVENT_TABLE;
        Log.d(LOG_TAG, sql);
        db.execSQL(sql);
        this.onCreate(db);
    }
}
