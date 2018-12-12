package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.models.VersionUpgradeModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.protobuff.VersionUpgrade;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.UiHelper;

/**
 * Created by ecosoft2 on 19-Feb-18.
 */

public class VersionUpgradeBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "VersionUpgradeBO-->";
    private UiHelper mUiHelper;
    private VersionUpgradeModel upgradeModel;

    public VersionUpgradeBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    private OnTaskExecutionFinished onTaskExecutionFinished;
    public interface OnTaskExecutionFinished {
        void onTaskFinished(VersionUpgradeModel upgradeModel);
    }
    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public void versionUpgradeRequest(VersionUpgradeModel upgradeModel) {
        this.upgradeModel = upgradeModel;
        VersionUpgrade.VerUpgradeptbReq.Builder builder = VersionUpgrade.VerUpgradeptbReq.newBuilder();
        builder.setAppsVersion(upgradeModel.getAppVersion());
        builder.setLastUpdateDate(upgradeModel.getLastUpdateDate());
        builder.setMicroatmid(upgradeModel.getMicroAtmID());
        VersionUpgrade.VerUpgradeptbReq upgradeptbReq = builder.build();
        storage = new Storage(mContext);
        if (storage.getValue(Constants.SYNC_URL).length() > 0) {
            url = storage.getValue(Constants.SYNC_URL);
            Log.i(TAG, "SYNC_URL-->" + url);
        }
        protobuffresponseDelegate = this;
        generateProtoRequest(upgradeptbReq.toByteArray(), Constants.PROTO_IDENTIFIER_VERSION, Constants.PROTO_TYPE_REQUEST);
        mUiHelper.showProgress("Please wait...");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                VersionUpgrade.VerUpgradeptbResp upgradeptbResp = VersionUpgrade.VerUpgradeptbResp.parseFrom(byProtoOutput);
                if (upgradeptbResp.getStatus().equals(Masters.Status.SUCCESS)) {
                    upgradeModel = new VersionUpgradeModel();
                    upgradeModel.setStatus(upgradeptbResp.getStatus());
                    upgradeModel.setStatusDescription(upgradeptbResp.getStatusDescription());
                    if (upgradeptbResp.getVersionInfo().getFtpUrl() != null
                            && !"".equals(upgradeptbResp.getVersionInfo().getFtpUrl())
                            && upgradeptbResp.getVersionInfo().getFtpUrl().length() > 0) {
                        upgradeModel.setFtpPath(upgradeptbResp.getVersionInfo().getFtpUrl());
                    } else {
                        upgradeModel.setFtpPath("");
                    }
                    if (upgradeptbResp.getVersionInfo().getFileName() != null
                            && !"".equals(upgradeptbResp.getVersionInfo().getFileName())
                            && upgradeptbResp.getVersionInfo().getFileName().length() > 0) {
                        upgradeModel.setFileName(upgradeptbResp.getVersionInfo().getFileName());
                    } else {
                        upgradeModel.setFileName("");
                    }
                    if (upgradeptbResp.getVersionInfo().getUserName() != null
                            && !"".equals(upgradeptbResp.getVersionInfo().getUserName())
                            && upgradeptbResp.getVersionInfo().getUserName().length() > 0) {
                        upgradeModel.setFtpUserID(upgradeptbResp.getVersionInfo().getUserName());
                    } else {
                        upgradeModel.setFtpUserID("");
                    }
                    if (upgradeptbResp.getVersionInfo().getPassword() !=null
                            && !"".equals(upgradeptbResp.getVersionInfo().getPassword())
                            && upgradeptbResp.getVersionInfo().getPassword().length() > 0) {
                        upgradeModel.setFtpPassword(upgradeptbResp.getVersionInfo().getPassword());
                    } else {
                        upgradeModel.setFtpPassword("");
                    }

                    if (upgradeptbResp.getVersionInfo().getId() > 0) {
                        upgradeModel.setFtpPort(upgradeptbResp.getVersionInfo().getId());
                    } else {
                        upgradeModel.setFtpPort(0);
                    }
                } else {
                    mUiHelper.errorDialog(""+upgradeptbResp.getStatusDescription());
                }
                if (this.onTaskExecutionFinished != null) {
                    this.onTaskExecutionFinished.onTaskFinished(upgradeModel);
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
