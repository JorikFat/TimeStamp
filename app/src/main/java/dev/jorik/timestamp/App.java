package dev.jorik.timestamp;

import android.app.Activity;
import android.app.Application;

import dev.jorik.timestamp.model.handlers.DbHandler;

public class App extends Application {
    private static DbHandler dbHandler;

    public static void createDbHandler(Activity activity) {
        dbHandler = new DbHandler(activity);
    }

    public static DbHandler getDbHandler(){
        return dbHandler;
    }
}
