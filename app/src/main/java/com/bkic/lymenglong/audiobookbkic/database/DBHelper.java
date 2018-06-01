package com.bkic.lymenglong.audiobookbkic.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_BOOK;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_BOOK_FAVORITE_SYNC;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_BOOK_HISTORY_SYNC;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_CATEGORY;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_CHAPTER;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_DOWNLOAD_STATUS;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_FAVORITE;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_HISTORY;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_MENU;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_PLAYBACK_HISTORY;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_REVIEW;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.CREATE_TABLE_SEARCH_BOOK;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.INSERT_MENU_VALUES;

public class DBHelper extends SQLiteOpenHelper {
    private String TAG = "DBHelper";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void QueryData(String sql){
        SQLiteDatabase dbSqLiteDatabase = getWritableDatabase();
        dbSqLiteDatabase.execSQL(sql);
        Log.d(TAG, "QueryData: "+sql);
    }

    public Cursor GetData(String sql){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase dbHelper) {
        Log.d(TAG,"OnCreate Database");
        //create table
        dbHelper.execSQL(CREATE_TABLE_MENU);
        dbHelper.execSQL(CREATE_TABLE_CATEGORY);
        dbHelper.execSQL(CREATE_TABLE_BOOK);
        dbHelper.execSQL(CREATE_TABLE_CHAPTER);
        dbHelper.execSQL(CREATE_TABLE_HISTORY);
        dbHelper.execSQL(CREATE_TABLE_FAVORITE);
        dbHelper.execSQL(CREATE_TABLE_REVIEW);
        dbHelper.execSQL(CREATE_TABLE_PLAYBACK_HISTORY);
        dbHelper.execSQL(CREATE_TABLE_SEARCH_BOOK);
        dbHelper.execSQL(CREATE_TABLE_DOWNLOAD_STATUS);
        dbHelper.execSQL(CREATE_TABLE_BOOK_HISTORY_SYNC);
        dbHelper.execSQL(CREATE_TABLE_BOOK_FAVORITE_SYNC);
        //insert menu value
        dbHelper.execSQL(INSERT_MENU_VALUES);

//        String s = "SELECT * FROM book ORDER BY book-title ASC"; //DESC
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: From version "+oldVersion+" to version "+newVersion);
    }
}
