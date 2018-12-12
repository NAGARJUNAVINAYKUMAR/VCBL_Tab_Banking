package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.protobuff.BCEnrolment;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;

/**
 * Created by ecosoft2 on 01-Mar-18.
 */

public class BCStatusBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "BCStatusBO--> ";
    private UiHelper mUiHelper;
    private BCEnrollmentModel bcModel;

    private OnTaskExecutionFinished onTaskExecutionFinished;

    public interface OnTaskExecutionFinished {
        void onTaskFinished(BCEnrollmentModel bcEnrollmentModel);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public BCStatusBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void bcUpdateRequest(BCEnrollmentModel bcModel) {
        this.bcModel = bcModel;
        BCEnrolment.BCEnrolmentRequest.Builder builder = BCEnrolment.BCEnrolmentRequest.newBuilder();
        builder.setTimeStamp(Utility.getCurrentTimeStamp());
        builder.setAppVersion(Constants.APP_VERSION);
        builder.setMicroatmId(String.valueOf(GlobalModel.microatmid));
        builder.setAgentId(bcModel.getEmpid());
        BCEnrolment.BCEnrolmentRequest buildMessage = builder.build();
        storage = new Storage(mContext);
        if (storage.getValue(Constants.SYNC_URL).length() > 0) {
            Log.i(TAG, "SYNC_URL---> " + storage.getValue(Constants.SYNC_URL));
            url = storage.getValue(Constants.SYNC_URL);
        }
        protobuffresponseDelegate = this;
        generateProtoRequest(buildMessage.toByteArray(), Constants.PROTO_IDENTIFIER_BC_ENROLL_STATUS, Constants.PROTO_TYPE_REQUEST);
        mUiHelper.showProgress("Progressing, Please wait...");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                BCEnrolment.BCEnrolmentResponse response = BCEnrolment.BCEnrolmentResponse.parseFrom(byProtoOutput);
                bcModel.setStatus(response.getStatus());
                bcModel.setStatusDesc(response.getStatusDescription());
                if (response.getStatus() == Masters.Status.SUCCESS) {
                    if (response.getBcMaster().getTitle() != null
                            && !"".equals(response.getBcMaster().getTitle())
                            && response.getBcMaster().getTitle().length() > 0) {
                        bcModel.setTitle(response.getBcMaster().getTitle());
                    } else {
                        bcModel.setTitle("");
                    }
                    if (response.getBcMaster().getFname() != null
                            && !"".equals(response.getBcMaster().getFname())
                            && response.getBcMaster().getFname().length() > 0) {
                        bcModel.setFname(response.getBcMaster().getFname());
                    } else {
                        bcModel.setFname("");
                    }
                    if (response.getBcMaster().getLname() != null
                            && !"".equals(response.getBcMaster().getLname())
                            && response.getBcMaster().getLname().length() > 0) {
                        bcModel.setLname(response.getBcMaster().getLname());
                    } else {
                        bcModel.setLname("");
                    }
                    if (response.getBcMaster().getAadharNo() != null
                            && !"".equals(response.getBcMaster().getAadharNo())
                            && response.getBcMaster().getAadharNo().length() > 0) {
                        bcModel.setAadhar_no(response.getBcMaster().getAadharNo());
                    } else {
                        bcModel.setAadhar_no("");
                    }
                    if (response.getBcMaster().getBcCode() != null
                            && !"".equals(response.getBcMaster().getBcCode())
                            && response.getBcMaster().getBcCode().length() > 0) {
                        bcModel.setBc_code(response.getBcMaster().getBcCode());
                    } else {
                        bcModel.setBc_code("");
                    }
                    if (response.getBcMaster().getBcId() > 0) {
                        bcModel.setBc_id(response.getBcMaster().getBcId());
                    } else {
                        bcModel.setBc_id(0);
                    }
                    if (response.getBcMaster().getBranchId() > 0) {
                        bcModel.setBranch_id(response.getBcMaster().getBranchId());
                    } else {
                        bcModel.setBranch_id(0);
                    }
                    if (response.getBcMaster().getEmail() != null
                            && !"".equals(response.getBcMaster().getEmail())
                            && response.getBcMaster().getEmail().length() > 0) {
                        bcModel.setEmail(response.getBcMaster().getEmail());
                    } else {
                        bcModel.setEmail("");
                    }
                    if (response.getBcMaster().getBiometric() != null
                            && !"".equals(response.getBcMaster().getBiometric())
                            && response.getBcMaster().getBiometric().length() > 0) {
                        bcModel.setFps_data(response.getBcMaster().getBiometric());
                    } else {
                        bcModel.setFps_data("");
                    }
                    if (response.getBcMaster().getPhone() != null
                            && !"".equals(response.getBcMaster().getPhone())
                            && response.getBcMaster().getPhone().length() > 0) {
                        bcModel.setPhone(response.getBcMaster().getPhone());
                    } else {
                        bcModel.setPhone("");
                    }
                    if (response.getBcMaster().getPassword() != null
                            && !"".equals(response.getBcMaster().getPassword())
                            && response.getBcMaster().getPassword().length() > 0) {
                        bcModel.setPassword(response.getBcMaster().getPassword());
                    } else {
                        bcModel.setPassword("");
                    }
                    if (this.onTaskExecutionFinished != null) {
                        this.onTaskExecutionFinished.onTaskFinished(bcModel);
                    }
                } else {
                    mUiHelper.errorDialog("" + response.getStatusDescription());
                }
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}