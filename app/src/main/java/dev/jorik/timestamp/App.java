package dev.jorik.timestamp;

import android.app.Activity;
import android.app.Application;

import dev.jorik.timestamp.model.handlers.DbHandler;

public class App extends Application {
    private static DbHandler dbHandler;

    public static DbHandler getDbHandler(Activity activity){
        if (dbHandler == null) {
            return new DbHandler(activity);
        }
        return dbHandler;
    }
}
