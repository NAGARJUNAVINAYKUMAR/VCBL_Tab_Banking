package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;

import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.models.TxnSummaryModel;
import com.vcbl.tabbanking.protobuff.BCTxnSummaryResponse;
import com.vcbl.tabbanking.protobuff.TxnReport;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;

public class TxnReportsBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "TxnReportsBO-->";
    private UiHelper mUiHelper;
    private ReportSearchModel searchModel;

    private OnTaskExecutionFinished onTaskExecutionFinished;
    public interface OnTaskExecutionFinished {
        void onTaskFinished();
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public TxnReportsBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void txnSummaryRequest(ReportSearchModel searchModel) {
        this.searchModel = searchModel;
        TxnReport.TxnptbReportRequest.Builder builder = TxnReport.TxnptbReportRequest.newBuilder();
        builder.setAppsVersion(Constants.APP_VERSION);
        builder.setTimeStamp(Utility.getCurrentTimeStamp());
        builder.setFromdate(searchModel.getFromDate());
        builder.setTodate(searchModel.getToDate());
        builder.setProductcode(searchModel.getProductCode());
        builder.setAccountno(searchModel.getAccountNo());
        builder.setBcid(searchModel.getBcID());
        if ("CANCEL".equals(searchModel.getToDate())) {
            builder.setReserve1(searchModel.getServerRrn());
        }
        builder.setBranchid(searchModel.getBranchID());
        builder.setMicroatmid(searchModel.getTerminalID());

        TxnReport.TxnptbReportRequest requestOrBuilder = builder.build();

        storage = new Storage(mContext);
        if (storage.getValue(Constants.TXT_URL).length() > 0) {
            url = storage.getValue(Constants.TXT_URL);
            Log.i(TAG, "TXT_URL--->" + url);
        }

        protobuffresponseDelegate = this;

        String identifier;
        if ("CANCEL".equals(searchModel.getToDate())) {
            identifier = Constants.PROTO_IDENTIFIER_TXN_CANCELLATION;
        } else if ("SUMMARY".equals(searchModel.getToDate())) {
            identifier = Constants.PROTO_IDENTIFIER_TXN_SUMMARY;
        } else {
            identifier = Constants.PROTO_IDENTIFIER_TXN_REPORTS;
        }

        generateProtoRequest(requestOrBuilder.toByteArray(), identifier, Constants.PROTO_TYPE_REQUEST);

        mUiHelper.showProgress("Please wait...");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {

    }
}
