package com.dhwanishah.todoittoday.helpers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dhwanishah.todoittoday.helpers.database.TodoItTodayContract.MainTodoIt;

/**
 * Created by DhwaniShah on 2/16/17.
 */

public class TodoItDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MainTodoIt.TABLE_NAME + " (" +
                    MainTodoIt._ID + " INTEGER PRIMARY KEY," +
                    MainTodoIt.COLUMN_NAME_TASK + " TEXT," +
                    MainTodoIt.COLUMN_NAME_CATEGORY + " TEXT," +
                    MainTodoIt.COLUMN_NAME_PRIORITY + " TEXT," +
                    MainTodoIt.COLUMN_NAME_CREATE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MainTodoIt.TABLE_NAME;
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "TodoItToday.db";

    public TodoItDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
