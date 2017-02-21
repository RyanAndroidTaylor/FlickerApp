package com.flicker;

import android.app.Application;

import com.flicker.database.DatabaseOpenHelper;
import com.izeni.rapidosqlite.DataConnection;
import com.squareup.picasso.Picasso;

/**
 * Created by ner on 2/18/17.
 */

public class App extends Application {

    private static Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();

        picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);

        DataConnection.Companion.init(new DatabaseOpenHelper(this));
    }

    public static Picasso getPicasso() {
        return picasso;
    }
}
