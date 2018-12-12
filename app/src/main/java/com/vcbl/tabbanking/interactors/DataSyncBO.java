package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.DataSyncModel;
import com.vcbl.tabbanking.models.Errors;
import com.vcbl.tabbanking.models.Products;
import com.vcbl.tabbanking.models.NetworkSettings;
import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.protobuff.DataSync;
import com.vcbl.tabbanking.protobuff.DataSyncResp;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.protobuff.Masters.Product;
import com.vcbl.tabbanking.protobuff.Masters.Error;
import com.vcbl.tabbanking.protobuff.Masters.BCMaster;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by ecosoft2 on 22-Jan-18.
 */

public class DataSyncBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "DataSyncBO-->";

    private UiHelper mUiHelper;

    private OnTaskExecutionFinished onTaskExecutionFinished;

    public interface OnTaskExecutionFinished {
        void onTaskFinished(DataSyncResp.DataSyncResponse syncResponse);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public DataSyncBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void dataSyncRequest(DataSyncModel dataSyncModel) {
        DataSync.SyncRequest.Builder syncRequest = DataSync.SyncRequest.newBuilder();
        syncRequest.setAppsVersion(dataSyncModel.getAppVersion());
        syncRequest.setMicroatmId(dataSyncModel.getMicroAtmId());
        syncRequest.setLastSyncDate(dataSyncModel.getTimeStamp());
        DataSync.SyncRequest buildMessage = syncRequest.build();

        Storage storage = new Storage(mContext);
        if (storage.getValue(Constants.SYNC_URL).length() > 0) {
            url = storage.getValue(Constants.SYNC_URL);
            Log.i(TAG, "SYNC_URL--->" + url);
        }
        protobuffresponseDelegate = this;
        generateProtoRequest(buildMessage.toByteArray(), Constants.PROTO_IDENTIFIER_SYNC, Constants.PROTO_TYPE_REQUEST);
        mUiHelper.showProgress("Syncing in progress\nPlease wait...");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                DataSyncResp.DataSyncResponse syncResponse = DataSyncResp.DataSyncResponse.parseFrom(byProtoOutput);

                if (syncResponse.getStatus() == Masters.Status.SUCCESS) {

                    DbFunctions dbFunctions = DbFunctions.getInstance(mContext);

                    List<Products> productsList = new ArrayList<>();
                    if (syncResponse.getProductsList() != null) {
                        for (Product proto : syncResponse.getProductsList()) {
                            Products productItem = new Products();
                            productItem.setId(proto.getId());
                            productItem.setCode(proto.getCode());
                            productItem.setDescription(proto.getDescription());
                            productsList.add(productItem);
                        }
                        dbFunctions.syncProducts(productsList);
                    }

                    List<Errors> errorsList = new ArrayList<>();
                    if (syncResponse.getErrorsList() != null) {
                        for (Error error : syncResponse.getErrorsList()) {
                            Errors errorsItem = new Errors();
                            errorsItem.setCode(String.valueOf(error.getCode()));
                            errorsItem.setDescription(error.getDescription());
                            errorsList.add(errorsItem);
                        }
                        dbFunctions.syncErrors(errorsList);
                    }

                    List<NetworkSettings> networkSettingsList = new ArrayList<>();
                    if (syncResponse.getNWSettingsList() != null) {
                        for (Masters.NetworkSettings settings : syncResponse.getNWSettingsList()) {
                            NetworkSettings networkSettingsItem = new NetworkSettings();
                            networkSettingsItem.setId(String.valueOf(settings.getId()));
                            networkSettingsItem.setPath(settings.getPath());
                            networkSettingsItem.setPort(settings.getPort());
                            networkSettingsItem.setUrl(settings.getUrl());
                            networkSettingsList.add(networkSettingsItem);
                        }
                        dbFunctions.syncNetworkSettings(networkSettingsList);
                    }

                    Cursor cursor = dbFunctions.getNetworkDetails();
                    if (cursor.getCount() < 1) {
                        Toasty.success(mContext, "Network data not Found !", Toast.LENGTH_SHORT).show();
                    } else {
                        if (cursor.moveToFirst()) {
                            List<String> urlList = new ArrayList<>();
                            do {
                                Log.i(TAG, "BaseURL-->"+cursor.getString(0) + "\nPort-->"+cursor.getString(1) + "\nPath-->"+cursor.getString(2));
                                urlList.add("http://" + cursor.getString(0) + ":" + cursor.getString(1) + "/" + cursor.getString(2));
                            } while (cursor.moveToNext());
                            syncUrl = urlList.get(0);
                            txnUrl = urlList.get(1);
                            if ((syncUrl != null && !"".equals(syncUrl) && syncUrl.length() > 0)
                                    && (txnUrl != null && !"".equals(txnUrl) && txnUrl.length() > 0)) {
                                storage = new Storage(mContext);
                                storage.saveSecure(Constants.SYNC_URL, syncUrl);
                                storage.saveSecure(Constants.TXT_URL, txnUrl);
                                Log.i(TAG, "SYNC_URL_SYNCED-->" + storage.getValue(Constants.SYNC_URL));
                                Log.i(TAG, "TXT_URL2_SYNCED-->" + storage.getValue(Constants.TXT_URL));
                            }
                        }
                    }

                    List<BCEnrollmentModel> bcMasterList = new ArrayList<>();
                    if (syncResponse.getBCMasterList() != null) {
                        for (BCMaster bcMaster : syncResponse.getBCMasterList()) {
                            BCEnrollmentModel bcEnrollmentModel = new BCEnrollmentModel();
                            bcEnrollmentModel.setAadhar_no(bcMaster.getAadharNo());
                            bcEnrollmentModel.setEmail(bcMaster.getEmail());
                            bcEnrollmentModel.setMobile(bcMaster.getMobile());
                            bcEnrollmentModel.setFname(bcMaster.getFname());
                            bcEnrollmentModel.setLname(bcMaster.getLname());
                            bcEnrollmentModel.setBc_id(bcMaster.getBcId());
                            bcEnrollmentModel.setEmpid(bcMaster.getEmpid());
                            bcEnrollmentModel.setPassword(bcMaster.getPassword());
                            bcEnrollmentModel.setPhone(bcMaster.getPhone());
                            bcEnrollmentModel.setBranch_id(bcMaster.getBranchId());
                            bcEnrollmentModel.setStart_date(bcMaster.getStartDate());
                            bcEnrollmentModel.setEnd_date(bcMaster.getEndDate());
                            bcEnrollmentModel.setPincode(bcMaster.getPincode());
                            bcEnrollmentModel.setFps_data(bcMaster.getBiometric());
                            bcMasterList.add(bcEnrollmentModel);
                        }
                        dbFunctions.syncBCMasters(bcMasterList);
                    }
                } else {
                    mUiHelper.errorDialog(syncResponse.getStatusDescription());
                }
                if (this.onTaskExecutionFinished != null) {
                    this.onTaskExecutionFinished.onTaskFinished(syncResponse);
                }
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
