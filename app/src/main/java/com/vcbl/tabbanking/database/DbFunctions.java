package com.vcbl.tabbanking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.models.Errors;
import com.vcbl.tabbanking.models.NetworkSettings;
import com.vcbl.tabbanking.models.Products;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.storage.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LENOVO on 17-08-2017.
 */

public class DbFunctions {

    private static final String TAG = "DbFunctions";
    private String strError;
    private SQLiteDatabase sqLiteDatabase;
    static private DbFunctions dbFunctions = null;

    private DatabaseInterface databaseInterface;

    private DbFunctions(Context context) {
        try {
            databaseInterface = DatabaseInterface.getInstance(context);
        } catch (IOException e) {
            Log.e(Constants.MICRO_ATM_TAG, e.toString());
        }
    }

    public static DbFunctions getInstance(Context context) {
        if (null == dbFunctions) {
            dbFunctions = new DbFunctions(context);
        }
        return dbFunctions;
    }

    public void Open() throws SQLException {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
    }

    String getStrError() {
        return strError;
    }

    public String checkUser(String user_name) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("bc_master", null, "fname" + " =? ", new String[]{user_name}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXISTS";
        }
        cursor.moveToFirst();
        String userExists = cursor.getString(1);
        cursor.close();
        return userExists;
    }

    public boolean insertBCDetails(BCEnrollmentModel bcMaster) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id", bcMaster.getBc_id());
        values.put("aadhar_no", "");
        values.put("phone", bcMaster.getMobile());
        values.put("mobile", bcMaster.getMobile());
        values.put("title", "");
        values.put("fname", bcMaster.getFname());
        values.put("lname", bcMaster.getLname());
        values.put("profession_code", "");
        values.put("bc_code", "");
        values.put("branch_id", bcMaster.getBranch_id());
        values.put("empid", bcMaster.getEmpid());
        values.put("start_date", "");
        values.put("end_date", "");
        values.put("created_date", "");
        values.put("updated_date", "");
        values.put("created_by", "");
        values.put("updated_by", "");
        values.put("pincode", bcMaster.getEmail());// instead of pincode here is inserting email id
        values.put("password", bcMaster.getPassword());
        values.put("FpsData", bcMaster.getFps_data());
        return sqLiteDatabase.insert(bcMaster.getTableName(), null, values) != -1;
    }

    public boolean updateBCDetails(BCEnrollmentModel bcMaster) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("aadhar_no", "");
        values.put("phone", "");
        values.put("mobile", bcMaster.getMobile());
        values.put("title", "");
        values.put("fname", bcMaster.getFname());
        values.put("lname", bcMaster.getLname());
        values.put("profession_code", "");
        values.put("bc_code", "");
        values.put("branch_id", bcMaster.getBranch_id());
        values.put("empid", bcMaster.getEmpid());
        values.put("start_date", "");
        values.put("end_date", "");
        values.put("created_date", "");
        values.put("updated_date", "");
        values.put("created_by", "");
        values.put("updated_by", "");
        values.put("pincode", bcMaster.getEmail());//instead of pincode here is inserting email id
        values.put("password", bcMaster.getPassword());
        values.put("FpsData", bcMaster.getFps_data());
        return sqLiteDatabase.update(bcMaster.getTableName(), values, "empid= '" + bcMaster.getEmpid() + "'", null) > 0;
    }

    public boolean insertTxnInDatabase(TxnModel txnModel) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Rrn", txnModel.getRrn());
        values.put("ServerRRN", "");
        values.put("Stan", txnModel.getStan());
        values.put("DataTime", txnModel.getTxndate());
        values.put("ProcessingCode", txnModel.getProcessingcode());
        values.put("ProductCode", txnModel.getProductcode());
        values.put("Branch_Code", txnModel.getBranch_id());
        values.put("AuthCode", txnModel.getLocAccNo());
        values.put("CustRefNo", txnModel.getMicroatmid());
        values.put("AccountNo", txnModel.getAccNo());
        values.put("Amount", txnModel.getAmount());
        values.put("LedgerBalance", txnModel.getLedgbalance());
        values.put("AvblBalance", txnModel.getAvlbalance());
        values.put("AgentId", txnModel.getBcid());
        values.put("Txnservice", txnModel.getTxnserviceid());
        values.put("TxnSubservice", txnModel.getTxnsubserviceid());
        values.put("ResponseCode", txnModel.getResponsecode());
        values.put("Status", "");
        values.put("EodFlag", "");
        values.put("Overdue", "");
        return sqLiteDatabase.insert(txnModel.getTableName(), null, values) != -1;
    }

    public boolean updateTxnInDatabase(String Rrn, String LedgerBalance, String AvblBalance, String ServerRRN,
                                       String Status, String ResponseCode, String Overdue, String TransactionDetails) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("LedgerBalance", LedgerBalance);
        values.put("AvblBalance", AvblBalance);
        values.put("ServerRRN", ServerRRN);
        values.put("Status", Status);
        values.put("ResponseCode", ResponseCode);
        values.put("Overdue", Overdue);
        return sqLiteDatabase.update(TransactionDetails, values, "Rrn" + " =? ", new String[]{Rrn}) > 0;
    }

    public ArrayList<SpinnerList> getTitleDetails() {
        ArrayList<SpinnerList> branchList = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList titles = new SpinnerList();
        titles.setId(0);
        titles.setName("-- Select Title --");
        branchList.add(titles);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id, description FROM title", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList branchDetails = new SpinnerList();
                branchDetails.setId(cursor.getInt(0));
                branchDetails.setName(cursor.getString(1));
                branchList.add(branchDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return branchList;
    }

    public ArrayList<SpinnerList> getBranchDetails() {
        ArrayList<SpinnerList> branchList = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList branchDetails = new SpinnerList();
        branchDetails.setId(0);
        branchDetails.setName("-- Select Branch --");
        branchList.add(branchDetails);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT _id, Name FROM branchdetails", null);
        if (cursor.moveToNext()) {
            do {
                branchDetails = new SpinnerList();
                branchDetails.setId(cursor.getInt(0));
                branchDetails.setName(cursor.getString(1));
                branchList.add(branchDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return branchList;
    }

    public ArrayList<SpinnerList> getStatesList() {
        ArrayList<SpinnerList> statesList = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerList1 = new SpinnerList();
        spinnerList1.setId(0);
        spinnerList1.setName("-- Select State --");
        statesList.add(spinnerList1);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT state_master_id, state_name FROM state_master", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerList = new SpinnerList();
                spinnerList.setId(cursor.getInt(0));
                spinnerList.setName(cursor.getString(1));
                statesList.add(spinnerList);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return statesList;
    }

    public ArrayList<SpinnerList> getDistrictList(String s) {
        ArrayList<SpinnerList> districtList = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerList1 = new SpinnerList();
        spinnerList1.setId(0);
        spinnerList1.setName("-- Select City --");
        districtList.add(spinnerList1);

        Cursor cursor = sqLiteDatabase.rawQuery("select * from district_master d join state_master s " +
                "on d.state_master_id = s.state_master_id where s.state_master_id = '" + s + "'", null);

        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerList = new SpinnerList();
                spinnerList.setId(cursor.getInt(0));
                spinnerList.setName(cursor.getString(1));
                districtList.add(spinnerList);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return districtList;
    }

    public ArrayList<SpinnerList> getSpinnerList(String s) {
        ArrayList<SpinnerList> spinnerList = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerItem = new SpinnerList();
        spinnerItem.setId(0);
        if ("GNM".equals(s)) {
            spinnerItem.setName("-- Select Gender --");
        } else if ("MSM".equals(s)) {
            spinnerItem.setName("-- Marital Status --");
        } else if ("LNM".equals(s)) {
            spinnerItem.setName("-- Select Language --");
        } else if ("ITM".equals(s)) {
            spinnerItem.setName("-- Select ID Type --");
        } else if ("EDM".equals(s)) {
            spinnerItem.setName("-- Select Qualification --");
        } else if ("OCM".equals(s)) {
            spinnerItem.setName("-- Select Occupation --");
        } else if ("CMM".equals(s)) {
            spinnerItem.setName("-- Select Community --");
        } else if ("SCM".equals(s)) {
            spinnerItem.setName("-- Select Category --");
        } else if ("REM".equals(s)) {
            spinnerItem.setName("-- Select Religion --");
        } else if ("RSM".equals(s)) {
            spinnerItem.setName("-- Select Relation --");
        } else {
            spinnerItem.setName("-- Authorized Person --");
        }
        spinnerList.add(spinnerItem);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id, common_description FROM common_ref_master WHERE common_ref_code = '" + s + "'", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerItem1 = new SpinnerList();
                spinnerItem1.setId(cursor.getInt(0));
                spinnerItem1.setName(cursor.getString(1));
                spinnerList.add(spinnerItem1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return spinnerList;
    }

    public String getBranchName(String branchid) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("branchdetails", null, "_id" + " =? ",
                new String[]{branchid}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "";
        }
        cursor.moveToFirst();
        String branchname = cursor.getString(cursor.getColumnIndex("Name"));
        cursor.close();
        return branchname;
    }

    public String getFpsData(String branchid) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("bc_master", null, "_id" + " =? ",
                new String[]{branchid}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "";
        }
        cursor.moveToFirst();
        String fpsData = cursor.getString(cursor.getColumnIndex("FpsData"));
        cursor.close();
        return fpsData;
    }

    public String getNameByAgentID(String agentid) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("bc_master", null, "_id" + " =? ",
                new String[]{agentid}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "";
        }
        cursor.moveToFirst();
        String agentname = cursor.getString(cursor.getColumnIndex("fname")) + " " + cursor.getString(cursor.getColumnIndex("lname"));
        cursor.close();
        return agentname;
    }

    public Cursor getNameByAgentID2(String agentid) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT fname, lname FROM bc_master WHERE _id = " + agentid, null);
    }

    public ArrayList<SpinnerList> getProductDetails() {
        ArrayList<SpinnerList> productList = new ArrayList<SpinnerList>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerList = new SpinnerList();
        spinnerList.setName("-- Select Code --");
        spinnerList.setDescription("-- Select Product --");
        productList.add(spinnerList);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT code, description FROM Products", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerList1 = new SpinnerList();
                spinnerList1.setName(cursor.getString(0));
                spinnerList1.setDescription(cursor.getString(1));
                productList.add(spinnerList1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return productList;
    }

    public Map<String, String> getSecMap(Context applicationContext) {
        Map<String, String> secMap = new LinkedHashMap<String, String>();
        Map<String, String> secFinalMap = new LinkedHashMap<String, String>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        try {
            String selectQuery = "SELECT * FROM Products";
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
            int i = 0;
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    i++;
                    Log.i("SECTION CODE :", "" + cursor.getString(0));
                    Log.i("SECTION NAME :", "" + cursor.getString(1));
                    Log.i("SECTION DESC :", "" + cursor.getString(2));
                    Log.i("SECTION DESC :", "" + cursor.getString(3));
                    Log.i("SECTION DESC :", "" + cursor.getString(4));
                    // section code
                    // "\t\tSectionname \t\t Fine \t\t Section Description"
                    secMap.put("" + cursor.getString(1), "" + cursor.getString(2).toString().trim()
                            + " \t\t" + cursor.getString(3).trim() + "\t " + cursor.getString(4).trim());

                    secFinalMap.put("" + cursor.getString(2).toString().trim() + " \t\t" + cursor.getString(3).trim() + "\t "
                            + cursor.getString(4).trim(), cursor.getString(1) + ":" + cursor.getString(2) + ":" + cursor.getString(3));
                    for (String secCode : secMap.keySet()) {
                        Log.i("SECTION CODE E:", secCode + "\t sec view  :" + secMap.get(secCode));
                    }
                } while (cursor.moveToNext());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secMap;
    }

    public ArrayList<SpinnerList> getProductForLoan() {
        ArrayList<SpinnerList> productList = new ArrayList<SpinnerList>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerList = new SpinnerList();
        spinnerList.setName("-- Select Code --");
        spinnerList.setDescription("-- Select Product --");
        productList.add(spinnerList);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT code, description FROM Products where ModuleType = 30 or ModuleType = 31", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerList1 = new SpinnerList();
                spinnerList1.setName(cursor.getString(0));
                spinnerList1.setDescription(cursor.getString(1));
                productList.add(spinnerList1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return productList;
    }

    public ArrayList<SpinnerList> getProductForSaving() {
        ArrayList<SpinnerList> productList = new ArrayList<SpinnerList>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerList = new SpinnerList();
        spinnerList.setName("-- Select Code --");
        spinnerList.setDescription("-- Select Product --");
        productList.add(spinnerList);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT code, description FROM Products " +
                "where (moduleType = 11 or moduleType = 12 or moduleType = 47) AND code not in ('DDS1', 'DDSAG', 'ISC', 'DIV', 'SCA', 'SCB', 'MD')", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerList1 = new SpinnerList();
                spinnerList1.setName(cursor.getString(0));
                spinnerList1.setDescription(cursor.getString(1));
                productList.add(spinnerList1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return productList;
    }

    public String getStan() {
        String stan = "";
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT stan FROM primary_data_ctr", null);
        if (cursor.moveToNext()) {
            do {
                stan = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return stan;
    }

    public void updateStan(String stan) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("stan", stan);
        boolean bOk = true;
        try {
            long lRet = sqLiteDatabase.update("primary_data_ctr", contentValues, null, null);

            if (lRet <= 0) {
                bOk = false;
            }
        } catch (RuntimeException e) {
            strError = e.toString();
            bOk = false;
        }
        sqLiteDatabase.close();
    }

    public Cursor getStan2() {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT stan FROM primary_data_ctr", null);
    }

    public boolean updateStan2(String stan) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //(new ContentValues()).put("stan", stan);
        contentValues.put("stan", stan);
        return sqLiteDatabase.update("primary_data_ctr", contentValues, null, null) > 0;
    }

    public Cursor getTxnDetails(String product_code, String account_no) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM TransactionDetails " +
                "WHERE ProcessingCode = 492010 AND ProductCode = '" + product_code + "' AND AccountNo = " + account_no +
                " AND date(DataTime) = CURRENT_DATE ORDER BY DataTime DESC", null);
    }

    public Cursor getDepositTxnDetails(String agentID) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM TransactionDetails " +
                "WHERE AgentId = " + agentID + " AND ProcessingCode = 492010 AND date(DataTime) = CURRENT_DATE ORDER BY DataTime DESC", null);
    }

    public Cursor getTxnSummary() {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT COUNT(AgentId), SUM(Amount) FROM TransactionDetails " +
                "WHERE ProcessingCode = 492010 AND date(DataTime) = CURRENT_DATE", null);
    }

    public void syncProducts(List<Products> productList) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        try {
            for (Products products : productList) {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT id from Products where id = " + products.getId(), null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", products.getId());
                contentValues.put("code", products.getCode());
                if (cursor.getCount() > 0) {
                    sqLiteDatabase.update("Products", contentValues, "id = ?", new String[]{String.valueOf(products.getId())});
                } else {
                    sqLiteDatabase.insert("Products", null, contentValues);
                }
                cursor.close();
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncErrors(List<Errors> errorsList) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        try {
            for (Errors errors : errorsList) {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT code from Error where code = " + errors.getCode(), null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("code", errors.getCode());
                contentValues.put("description", errors.getDescription());
                if (cursor.getCount() > 0) {
                    sqLiteDatabase.update("Error", contentValues, "id = ?", new String[]{errors.getCode()});
                } else {
                    sqLiteDatabase.insert("Error", null, contentValues);
                }
                cursor.close();
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncNetworkSettings(List<NetworkSettings> networkSettingsList) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        try {
            for (NetworkSettings settings : networkSettingsList) {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT id FROM NetworkConfig WHERE id = " + settings.getId(), null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", settings.getId());
                contentValues.put("BaseUrl", settings.getUrl());
                contentValues.put("Port", settings.getPort());
                contentValues.put("Path", settings.getPath());
                if (cursor.getCount() > 0) {
                    sqLiteDatabase.update("NetworkConfig", contentValues, "id = ?", new String[]{settings.getId()});
                } else {
                    sqLiteDatabase.insert("NetworkConfig", null, contentValues);
                }
                cursor.close();
                Log.i(TAG, "BaseUrl-->" + settings.getUrl()
                        + "\nPort-->" + settings.getPort()
                        + "\nPath-->" + settings.getPath());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor getNetworkDetails() {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT BaseUrl, Port, Path FROM NetworkConfig", null);
    }

    public void syncBCMasters(List<BCEnrollmentModel> bcMasterList) {
        sqLiteDatabase = databaseInterface.getWritableDatabase();
        try {
            for (BCEnrollmentModel model : bcMasterList) {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT _id FROM bc_master WHERE _id = " + model.getBc_id(), null);
                ContentValues values = new ContentValues();
                values.put("_id", model.getBc_id());
                values.put("aadhar_no", model.getAadhar_no());
                //values.put("phone", model.getPhone());
                values.put("mobile", model.getMobile());
                values.put("pincode", model.getPincode());
                values.put("title", model.getTitle());
                values.put("fname", model.getFname());
                values.put("password", model.getPassword());
                values.put("lname", model.getLname());
                values.put("branch_id", model.getBranch_id());
                values.put("empid", model.getEmpid());
                values.put("FpsData", model.getFps_data());
                if (cursor.getCount() > 0) {
                    sqLiteDatabase.update("bc_master", values, "_id = ?", new String[]{String.valueOf(model.getBc_id())});
                } else {
                    sqLiteDatabase.insert("bc_master", null, values);
                }
                cursor.close();
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cursor getBCDetails(String employeeId) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT fname, lname, mobile, branch_id, empid, pincode FROM bc_master WHERE empid = '" + employeeId + "'", null);
    }

    public Cursor getPinNo(String emp_id) {
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT password FROM bc_master WHERE empid = '" + emp_id + "'", null);
    }

    public ArrayList<SpinnerList> getBankNames() {
        ArrayList<SpinnerList> bankNames = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerItem = new SpinnerList();
        spinnerItem.setId(0);
        spinnerItem.setName("-- Select Bank Name --");
        bankNames.add(spinnerItem);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT bank_code, bank_name FROM nbin_master", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerItem1 = new SpinnerList();
                spinnerItem1.setId(cursor.getInt(0));
                spinnerItem1.setName(cursor.getString(1));
                bankNames.add(spinnerItem1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return bankNames;
    }

    public ArrayList<SpinnerList> getReasons() {
        ArrayList<SpinnerList> reasons = new ArrayList<>();
        sqLiteDatabase = databaseInterface.getReadableDatabase();
        SpinnerList spinnerItem = new SpinnerList();
        spinnerItem.setId(0);
        spinnerItem.setName("-- Select Reason --");
        reasons.add(spinnerItem);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id, reason FROM txn_cancel_table", null);
        if (cursor.moveToNext()) {
            do {
                SpinnerList spinnerItem1 = new SpinnerList();
                spinnerItem1.setId(cursor.getInt(0));
                spinnerItem1.setName(cursor.getString(1));
                reasons.add(spinnerItem1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return reasons;

    }
}
