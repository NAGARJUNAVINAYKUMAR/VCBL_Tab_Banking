package com.vcbl.tabbanking.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.vcbl.tabbanking.R;

/**
 * Created by ecosoft2 on 14-Dec-17.
 */

public class PermissionUtil {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public PermissionUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();// PermissionUtil
    }

    public void updatePermissionPreference(String permission) {

        switch (permission) {

            case "camera":
                editor.putBoolean(context.getString(R.string.permission_camera), true);
                editor.commit();
                break;

            case "storage":
                editor.putBoolean(context.getString(R.string.permission_storage), true);
                editor.commit();
                break;

            case "contacts":
                editor.putBoolean(context.getString(R.string.permission_contacts), true);
                editor.commit();
                break;

        }
    }

    public boolean checkPermissionPreference(String permission) {

        boolean isShown = false;

        switch (permission) {

            case "camera":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_camera), false);
                break;

            case "storage":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_storage), false);
                break;

            case "contacts":
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_contacts), false);
                break;

        }
        return isShown;
    }
}
