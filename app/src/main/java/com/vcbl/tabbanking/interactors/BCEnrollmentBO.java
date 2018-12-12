package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;
import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.protobuff.BCEnrolment;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.HardwareUtil;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;

public class BCEnrollmentBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "BCEnrollmentBO--> ";

    private UiHelper mUiHelper;

    private BCEnrollmentModel bcEnrollmentModel;

    private OnTaskExecutionFinished onTaskExecutionFinished;

    public interface OnTaskExecutionFinished {
        void onTaskFinished(BCEnrollmentModel bcEnrollmentModel);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public BCEnrollmentBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void bcEnrollRequest(BCEnrollmentModel bcEnrollmentModel) {
        this.bcEnrollmentModel = bcEnrollmentModel;
        Masters.BCMaster.Builder bcMaster = Masters.BCMaster.newBuilder();
        BCEnrolment.BCEnrolmentRequest.Builder bcEnrolmentRequest = BCEnrolment.BCEnrolmentRequest.newBuilder();
        bcMaster.setFname(bcEnrollmentModel.getFname());
        bcMaster.setLname(bcEnrollmentModel.getLname());
        bcMaster.setPassword(bcEnrollmentModel.getPassword());
        bcMaster.setBranchId(bcEnrollmentModel.getBranch_id());
        bcMaster.setEmpid(bcEnrollmentModel.getEmpid());
        bcMaster.setMobile(bcEnrollmentModel.getMobile());
        bcMaster.setEmail(bcEnrollmentModel.getEmail());
        bcMaster.setBiometric(bcEnrollmentModel.getFps_data());
        bcMaster.setTitle(bcEnrollmentModel.getTitle());
        bcMaster.setStartDate(Utility.getCurrentTimeStamp());
        bcMaster.setEndDate(String.valueOf(Utility.addYear(1)));
        bcMaster.setBcId(bcEnrollmentModel.getBc_id());
        bcEnrolmentRequest.setBcMaster(bcMaster);
        bcEnrolmentRequest.setTimeStamp(Utility.getCurrentTimeStamp());
        bcEnrolmentRequest.setAppVersion(Constants.APP_VERSION);
        bcEnrolmentRequest.setMicroatmId(HardwareUtil.getMacId());
        BCEnrolment.BCEnrolmentRequest buildMessage = bcEnrolmentRequest.build();
        storage = new Storage(mContext);
        if (storage.getValue(Constants.SYNC_URL).length() > 0) {
            Log.i(TAG, "SYNC_URL---> " + storage.getValue(Constants.SYNC_URL));
            url = storage.getValue(Constants.SYNC_URL);
        }
        protobuffresponseDelegate = this;
        generateProtoRequest(buildMessage.toByteArray(), Constants.PROTO_IDENTIFIER_BC_ENROLL, Constants.PROTO_TYPE_REQUEST);
        mUiHelper.showProgress("Enrolling, Please wait...");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                BCEnrolment.BCEnrolmentResponse bcEnrolmentResponse = BCEnrolment.BCEnrolmentResponse.parseFrom(byProtoOutput);
                if (bcEnrolmentResponse.getStatus() == Masters.Status.SUCCESS) {
                    if (bcEnrolmentResponse.getMicroatmId() != null
                            && !"".equals(bcEnrolmentResponse.getMicroatmId())) {
                        bcEnrollmentModel.setMicroAtmId(bcEnrolmentResponse.getMicroatmId());
                    } else {
                        bcEnrollmentModel.setMicroAtmId("");
                    }
                    if (bcEnrolmentResponse.getAgentId() > 0) {
                        bcEnrollmentModel.setBc_id(bcEnrolmentResponse.getAgentId());
                    } else {
                        bcEnrollmentModel.setBc_id(0);
                    }
                    if (this.onTaskExecutionFinished != null) {
                        this.onTaskExecutionFinished.onTaskFinished(bcEnrollmentModel);
                    }
                } else {
                    mUiHelper.errorDialog("" + bcEnrolmentResponse.getStatusDescription());
                }

            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
