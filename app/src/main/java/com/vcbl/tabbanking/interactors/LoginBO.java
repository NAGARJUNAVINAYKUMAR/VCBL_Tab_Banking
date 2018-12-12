package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.HardwareUtil;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.protobuff.AgentLoginRequest;
import com.vcbl.tabbanking.protobuff.AgentLoginResponse;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.models.AgentLoginModel;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class LoginBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "LoginBO--> ";
    private UiHelper mUiHelper;
    private AgentLoginModel agentLoginModel;
    private DbFunctions dbFunctions;

    /*private OnTaskExecutionFinished onTaskExecutionFinished;
    public interface OnTaskExecutionFinished {
        void onTaskFinished(AgentLoginModel agentLoginModel);
    }
    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }*/

    private OnTaskFinishedListener taskFinishedListener;

    public void setOnTaskFinishedEvent(OnTaskFinishedListener onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.taskFinishedListener = onTaskFinishedEvent;
        }
    }

    public LoginBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void LoginRequest(AgentLoginModel agentLoginModel) {
        this.agentLoginModel = agentLoginModel;
        AgentLoginRequest.AgentLoginReq.Builder builder = AgentLoginRequest.AgentLoginReq.newBuilder();
        builder.setProtoVersion(Constants.PROTO_VERSION);
        builder.setAppsVersion(Constants.APP_VERSION);
        builder.setTimeStamp(Utility.getCurrentTimeStamp());
        Log.i(TAG, Utility.getCurrentTimeStamp());
        builder.setTerminalId(HardwareUtil.getMacId());
        builder.setLoginId(agentLoginModel.getLoginId());
        builder.setBiometric(agentLoginModel.getBiometric());
        builder.setPin(agentLoginModel.getPin());

        storage = new Storage(mContext);

        if (storage.getValue(Constants.SYNC_URL).length() == 0) {
            Cursor cursor = dbFunctions.getNetworkDetails();
            if (cursor.getCount() < 1) {
                Toasty.info(mContext, "Network data not Found !", Toast.LENGTH_SHORT).show();
            } else {
                if (cursor.moveToFirst()) {
                    List<String> urlList = new ArrayList<>();
                    do {
                        Log.i(TAG, "BaseURL-->" + cursor.getString(0) + "\nPort-->" + cursor.getString(1) + "\nPath-->" + cursor.getString(2));
                        urlList.add("http://" + cursor.getString(0) + ":" + cursor.getString(1) + "/" + cursor.getString(2));
                    } while (cursor.moveToNext());
                    syncUrl = urlList.get(0);
                    txnUrl = urlList.get(1);
                    if ((syncUrl != null && !"".equals(syncUrl) && syncUrl.length() > 0)
                            && (txnUrl != null && !"".equals(txnUrl) && txnUrl.length() > 0)) {
                        storage.saveSecure(Constants.SYNC_URL, syncUrl);
                        storage.saveSecure(Constants.TXT_URL, txnUrl);
                        url = syncUrl;
                        Log.i(TAG, "IF_EMPTY_UPDATE_SYNC_FROM_DB-->" + storage.getValue(Constants.SYNC_URL));
                        Log.i(TAG, "IF_EMPTY_UPDATE_TXN_FROM_DB-->" + storage.getValue(Constants.TXT_URL));
                    }
                }
            }
        } else {
            if (storage.getValue(Constants.SYNC_URL).length() > 0) {
                url = storage.getValue(Constants.SYNC_URL);
                Log.i(TAG, "SYNC_URL--> " + url);
            }
        }

        AgentLoginRequest.AgentLoginReq buildMessage = builder.build();
        protobuffresponseDelegate = this;
        generateProtoRequest(buildMessage.toByteArray(), Constants.PROTO_IDENTIFIER_LOGIN, Constants.PROTO_TYPE_REQUEST);
        mUiHelper.showProgress("Logging, Please wait....");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                AgentLoginResponse.AgentLoginRes agentLoginRes = AgentLoginResponse.AgentLoginRes.parseFrom(byProtoOutput);
                if (agentLoginRes.getStatus().equals(AgentLoginResponse.Status.SUCCESS)) {
                    if (agentLoginRes.getPOSIdId() != null
                            && !"".equals(agentLoginRes.getPOSIdId())) {
                        GlobalModel.microatmid = Integer.parseInt(agentLoginRes.getPOSIdId());
                    } else {
                        GlobalModel.microatmid = 0;
                    }
                    if (agentLoginRes.getBranchid() != null
                            && !"".equals(agentLoginRes.getBranchid())) {
                        GlobalModel.branchid = Integer.parseInt(agentLoginRes.getBranchid());
                    } else {
                        GlobalModel.branchid = 0;
                    }
                    if (agentLoginRes.getBcid() != null
                            && !"".equals(agentLoginRes.getBcid())) {
                        GlobalModel.bcid = Integer.parseInt(agentLoginRes.getBcid());
                    } else {
                        GlobalModel.bcid = 0;
                    }
                    if (agentLoginRes.getFpsData() != null
                            && !"".equals(agentLoginRes.getFpsData())) {
                        agentLoginModel.setFps(agentLoginRes.getFpsData());
                    } else {
                        agentLoginModel.setFps("");
                    }
                    if (agentLoginRes.getPin() != null
                            && !"".equals(agentLoginRes.getPin())) {
                        agentLoginModel.setPin(agentLoginRes.getPin());
                    } else {
                        agentLoginModel.setPin("");
                    }
                    if (this.taskFinishedListener != null) {
                        this.taskFinishedListener.onTaskFinished(agentLoginModel);
                    }
                } else {
                    mUiHelper.errorDialog("" + agentLoginRes.getStatusDescription());
                    GlobalModel.bcid = 0;
                    GlobalModel.branchid = 0;
                    GlobalModel.microatmid = 0;
                }
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
