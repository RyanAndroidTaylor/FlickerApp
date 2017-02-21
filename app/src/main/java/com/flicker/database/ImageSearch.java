package com.flicker.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.izeni.rapidosqlite.DataConnection;
import com.izeni.rapidosqlite.item_builder.ItemBuilder;
import com.izeni.rapidosqlite.table.Column;
import com.izeni.rapidosqlite.table.DataTable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by ner on 2/20/17.
 */

public class ImageSearch implements DataTable {

    public static final String TABLE_NAME = "ImageSearch";

    public static final Column QUERY = new Column(String.class, "Query", true, true, null);

    public static final Builder BUILDER = new Builder();

    public String query;

    public ImageSearch(String query) {
        this.query = query;
    }

    @NotNull
    @Override
    public ContentValues contentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(QUERY.getName(), query);

        return contentValues;
    }

    @NotNull
    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    static class Builder implements ItemBuilder<ImageSearch> {

        @Override
        public ImageSearch buildItem(@NotNull Cursor cursor, @NotNull DataConnection dataConnection) {
            return new ImageSearch(cursor.getString(cursor.getColumnIndex(QUERY.getName())));
        }
    }
}
