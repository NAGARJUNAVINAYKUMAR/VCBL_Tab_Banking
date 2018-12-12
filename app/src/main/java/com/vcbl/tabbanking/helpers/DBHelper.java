package com.vcbl.tabbanking.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by ecosoft2 on 06-Dec-17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vcbltab.db";
    private static final int DATABASE_VERSION = 1;

    private static final String USER_TABLE = "user_table";
    private static final String COL_ID = "id";
    private static final String COL_USER_NAME = "user_name";
    private static final String COL_ROLE_ID = "role_id";
    private static final String COL_PASSWORD = "password";
    private static final String COL_BRANCH_NAME = "branch_name";
    private static final String COL_BRANCH_CODE = "branch_code";
    private static final String COL_EMPLOYEE_ID = "employee_id";
    private static final String COL_MOBILE_NO = "mobile_no";
    private static final String COL_EMAIL_ID = "email_id";
    private static final String COL_PASS_TYPE = "pass_type";
    private static final String COL_CREATE_DATE = "create_date";
    private static final String COL_CREATE_BY = "create_by";
    private static final String COL_UPDATE_DATE = "update_date";
    private static final String COL_UPDATE_BY = "update_by";
    private static final String COL_EXP_DATE = "expiry_date";
    private static final String COL_BRANCH_ID = "branch_id";
    private static final String COL_BIOMETRIC_DATA = "biometric_data";

    private static final String TRANSACTION_TABLE = "transaction_table";
    private static final String TXN_NAME = "txn_name";
    private static final String TXN_ACC_NO = "txn_acc_no";
    private static final String TXN_BAL = "txn_bal";
    private static final String TXN_AMT = "txn_amt";
    private static final String TXN_TYPE_3 = "txn_type_3";
    private static final String TXN_REF_NO = "txn_ref_no";
    private static final String TXN_AGENT_ID = "txn_agent_id";
    private static final String TXN_BRANCH_ID = "txn_branch_id";
    private static final String TXN_BAL_ID = "txn_bal_id";
    private static final String TXN_PROD_ID = "txn_prod_id";
    private static final String TXN_DATE = "txn_date";
    private static final String TXN_TYPE = "txn_type";

    public DBHelper(Context context) {
       super(context,"/mnt/sdcard/VCBL/"+ DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String sql = "CREATE TABLE " + USER_TABLE + " (\n" +
                "    " + COL_ID + " INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + COL_USER_NAME + " varchar(200) NOT NULL,\n" +
                "    " + COL_PASSWORD + " varchar(200) NOT NULL,\n" +
                "    " + COL_PASS_TYPE + " varchar(200) NOT NULL,\n" +
                "    " + COL_ROLE_ID + " varchar(200) NOT NULL,\n" +
                "    " + COL_BRANCH_ID + " varchar(200) NOT NULL,\n" +
                "    " + COL_CREATE_BY + " varchar(200) NOT NULL,\n" +
                "    " + COL_CREATE_DATE + " datetime NOT NULL,\n" +
                "    " + COL_UPDATE_BY + " varchar(200) NOT NULL,\n" +
                "    " + COL_UPDATE_DATE + " datetime NOT NULL,\n" +
                "    " + COL_EXP_DATE + " datetime NOT NULL,\n" +
                "    " + COL_BIOMETRIC + " varchar(200) NOT NULL,\n" +
                ");";*/

        // user_table creation
        String sql = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (\n" +
                "    " + COL_ID + " INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + COL_USER_NAME + " varchar(200) NOT NULL,\n" +
                "    " + COL_PASSWORD + " varchar(200) NOT NULL,\n" +
                "    " + COL_BRANCH_NAME + " varchar(200) NOT NULL,\n" +
                "    " + COL_BRANCH_CODE + " varchar(200) NOT NULL,\n" +
                "    " + COL_EMPLOYEE_ID + " varchar(200) NOT NULL,\n" +
                "    " + COL_MOBILE_NO + " varchar(200) NOT NULL,\n" +
                "    " + COL_EMAIL_ID + " varchar(200) NOT NULL,\n" +
                "    " + COL_BIOMETRIC_DATA + " varchar(200) NOT NULL\n" +
                ");";
        db.execSQL(sql);

        // transaction_table creation
        String transaction_sql = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (\n" +
                "    " + COL_ID + " INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    " + TXN_NAME + " varchar(200) NOT NULL,\n" +
                "    " + TXN_ACC_NO + " varchar(200) NOT NULL,\n" +
                "    " + TXN_BAL + " varchar(200) NOT NULL,\n" +
                "    " + TXN_AMT + " varchar(200) NOT NULL,\n" +
                "    " + TXN_TYPE_3 + " varchar(200) NOT NULL,\n" +
                "    " + TXN_REF_NO + " varchar(200) NOT NULL,\n" +
                "    " + TXN_AGENT_ID + " varchar(200) NOT NULL,\n" +
                "    " + TXN_BRANCH_ID + " varchar(200) NOT NULL,\n" +
                "    " + TXN_BAL_ID + " varchar(200) NOT NULL,\n" +
                "    " + TXN_PROD_ID + " varchar(200) NOT NULL,\n" +
                "    " + TXN_DATE + " varchar(200) NOT NULL,\n" +
                "    " + TXN_TYPE + " varchar(200) NOT NULL\n" +
                ");";
        db.execSQL(transaction_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        onCreate(db);
    }

    public boolean addEmployee(String user_name, String password, String branch_name,
                        String branch_code, String employee_id, String mobile_no, String email_id, String biometric_data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user_name);
        values.put(COL_PASSWORD, password);
        values.put(COL_BRANCH_NAME, branch_name);
        values.put(COL_BRANCH_CODE, branch_code);
        values.put(COL_EMPLOYEE_ID, employee_id);
        values.put(COL_MOBILE_NO, mobile_no);
        values.put(COL_EMAIL_ID, email_id);
        values.put(COL_BIOMETRIC_DATA, biometric_data);
        return db.insert(USER_TABLE, null, values) != -1;
    }

    public boolean addTransactionRecord(String txn_name, String txn_acc_no, String txn_balance,
                                        String txn_amount, String txn_type_3, String txn_ref_no,
                                        String txn_agent_id, String txn_branch_id, String txn_bal_id,
                                        String txn_prod_id, String txn_date, String txn_type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TXN_NAME, txn_name);
        values.put(TXN_ACC_NO, txn_acc_no);
        values.put(TXN_BAL, txn_balance);
        values.put(TXN_AMT, txn_amount);
        values.put(TXN_TYPE_3, txn_type_3);
        values.put(TXN_REF_NO, txn_ref_no);
        values.put(TXN_AGENT_ID, txn_agent_id);
        values.put(TXN_BRANCH_ID, txn_branch_id);
        values.put(TXN_BAL_ID, txn_bal_id);
        values.put(TXN_PROD_ID, txn_prod_id);
        values.put(TXN_DATE, txn_date);
        values.put(TXN_TYPE, txn_type);
        return db.insert(USER_TABLE, null, values) != -1;
    }

    Cursor getAllEmployees() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + USER_TABLE, null);
    }

    boolean updateEmployee(int id, String name, String dept, double salary) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_UPDATE_DATE, name);
        values.put(COL_UPDATE_BY, dept);
        values.put(COL_PASSWORD, String.valueOf(salary));
        return db.update(USER_TABLE, values, COL_ID + " =? ", new String[]{String.valueOf(id)}) > 0;
    }

    boolean deleteEmployee(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(USER_TABLE, COL_ID + " =? ", new String[]{String.valueOf(id)}) > 0;
    }

    public String getSingleEntry(String user_name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE, null, COL_USER_NAME + " =? ", new String[]{user_name}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
        cursor.close();
        return password;
    }

    public String getSingleEntryBiometricData(String user_name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE, null, COL_USER_NAME + " =? ", new String[]{user_name}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String biometric_data_result = cursor.getString(cursor.getColumnIndex(COL_BIOMETRIC_DATA));
        cursor.close();
        return biometric_data_result;
    }

    public String checkUser(String user_name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE, null, COL_USER_NAME + " =? ", new String[]{user_name}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String userExists = cursor.getString(1);
        cursor.close();
        return userExists;
    }
}