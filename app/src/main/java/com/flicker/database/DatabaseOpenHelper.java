package com.flicker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.izeni.rapidosqlite.table.Column;
import com.izeni.rapidosqlite.table.TableBuilder;

/**
 * Created by ner on 2/20/17.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context) {
        super(context, "com.flicker.app", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableBuilder tableBuilder = new TableBuilder();

        db.execSQL(tableBuilder.buildTable(ImageSearch.TABLE_NAME, new Column[] {ImageSearch.QUERY}));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
