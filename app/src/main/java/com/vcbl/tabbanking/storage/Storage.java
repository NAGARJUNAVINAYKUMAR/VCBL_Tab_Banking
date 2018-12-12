package com.vcbl.tabbanking.storage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Storage {

    private final SharedPreferences settings;

    public Storage(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveSecure(String key, String value) {
        settings.edit().putString(key, value).apply();
    }

    public String getValue(String key) {
        return settings.getString(key, "");
    }

    public void clearValue(String s) {
        settings.edit().remove(s).apply();
    }

    public void saveSecureBoolean(String key, boolean value) {
        settings.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key) {
        return settings.getBoolean(key, false);
    }

    public void clearBoolean(String s) {
        settings.edit().remove(s).apply();
    }

    public void saveSecureInteger(String key, int value) {
        settings.edit().putInt(key, value).apply();
    }

    public int getIntegerValue(String key) {
        return settings.getInt(key, 0);
    }

    public void clearAll() {
        settings.edit().
                remove(Constants.BT_ADDRESS).
                remove(Constants.BT_NAME).
                apply();
    }
}
