package dev.jorik.timestamp.model.handlers;

import android.content.SharedPreferences;

import dev.jorik.timestamp.SettingContract;

public class PreferencesHandler implements SettingContract.Model{
    public static class Const{
        public static final String USE_SECONDS = "use_seconds";
    }
    private SharedPreferences preference;

    public PreferencesHandler(SharedPreferences prefs){
        preference = prefs;
    }

    @Override
    public void writeSetting(boolean value) {
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(Const.USE_SECONDS, value);
        editor.apply();
//        preference.edit().putBoolean(Const.USE_SECONDS, value).apply();
    }

    @Override
    public boolean readSetting() {
        return preference.getBoolean(Const.USE_SECONDS, false);
    }
}
