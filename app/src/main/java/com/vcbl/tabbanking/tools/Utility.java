package com.vcbl.tabbanking.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vcbl.tabbanking.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {

    private static final String TAG = "Utility-->";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    //yyyy-mm-dd hh:mm:ss format
    public static String getCurrentTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        return simpleDateFormat.format(new Date());
        //return DateFormat.getDateTimeInstance().format(new Date());
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static String addYear(int no_of_years) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(simpleDateFormat.format(date.getTime()));
        date.add(Calendar.YEAR, no_of_years);
        System.out.println(simpleDateFormat.format(date.getTime()));
        return simpleDateFormat.format(date.getTime());
    }

    public static Date addSeconds(int no_of_seconds) {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.add(Calendar.SECOND, no_of_seconds);
        return date.getTime();
    }

    public static String LeftPad(String Str, int length) {
        return String.format("%0" + length + "d", Str);
    }

    public static String padLeft(String str, int length, String padChar) {
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < length; i++) {
            pad.append(padChar);
        }
        return pad.substring(str.length()) + str;
    }

    public static String padRight(String str, int length, String padChar) {
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < length; i++) {
            pad.append(padChar);
        }
        return str + pad.substring(str.length());
    }

    public static String getHours() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        return String.valueOf(hours);
    }

    public static String getDayofYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.DAY_OF_YEAR);
        return String.valueOf(year);
    }

    public static String getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTodayDate() {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate(String dt) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        String formattedDate = "";
        try {
            date = formatter.parse(dt);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        try {
            if (date == null) {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                date = formatter.parse(dt);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = formatter.format(date);
        return formattedDate;
    }

    private static SimpleDateFormat format;

    @SuppressLint("SimpleDateFormat")
    public static String getTodaysDate() {
        //String pattern = "dd-MMM-yyyy";
        format = new SimpleDateFormat("dd-MMM-yyyy");
        return format.format(new Date());
    }

    public static long daysCalculate(String dateStart, String dateStop) {
        // String dateStart ="0101120912";
        // String dateStop = "0101121041";
        long min = 0;
        long diffDays = 0;
        // HH converts hour in 24 hours format (0-23), day calculation
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            // in milliseconds
            long diff = d2.getTime() - d1.getTime();
            Log.i(TAG, "diff--> " + diff);
            long diffSeconds = diff / 1000 % 60;
            Log.i(TAG, "diffSeconds--> " + diffSeconds);
            long diffMinutes = diff / (60 * 1000) % 60;
            Log.i(TAG, "diffMinutes--> " + diffMinutes);
            long diffHours = diff / (60 * 60 * 1000) % 24;
            Log.i(TAG, "diffHours--> " + diffHours);
            diffDays = diff / (24 * 60 * 60 * 1000);

            //   min = (diffDays * 24 * 60) + (diffHours * 60) + (diffMinutes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diffDays;
    }

    @SuppressLint("SimpleDateFormat")
    public String getPresentTime() {
        String pattern = "HH:mm";
        format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    public static String getAge(int day, int month, int year) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        Integer ageInt = age;
        return ageInt.toString();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setIcon(R.drawable.app_logo);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}


