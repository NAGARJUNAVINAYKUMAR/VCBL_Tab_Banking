package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.protobuff.Transaction;
import com.vcbl.tabbanking.protobuff.TxnReport;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.HardwareUtil;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;

import java.util.ArrayList;
import java.util.List;

public class DetailedTxnBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "DetailedTxnBO-->";
    private ReportSearchModel searchModel;
    private UiHelper mUiHelper;

    public DetailedTxnBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    private OnTaskExecutionFinished onTaskExecutionFinished;

    public interface OnTaskExecutionFinished {
        void onTaskFinished(ReportSearchModel searchModel);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public void detailedTxnRequest(ReportSearchModel searchModel) {
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
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                TxnReport.TxnptbReportResponse reportResponse = TxnReport.TxnptbReportResponse.parseFrom(byProtoOutput);
                if (reportResponse.getStatus().equals(Masters.Status.SUCCESS)) {
                    searchModel.setStatus(reportResponse.getStatus());
                    if (reportResponse.getTransactionProtoList() != null) {
                        List<RecyclerItem> list = new ArrayList<>();
                        for (Transaction.TransactionProto proto : reportResponse.getTransactionProtoList()) {
                            RecyclerItem recyclerItem = new RecyclerItem();
                            recyclerItem.setServerRRN(proto.getRrn());
                            recyclerItem.setAccountNo(proto.getAccountno());
                            recyclerItem.setAmount(proto.getAmount());
                            recyclerItem.setAvblBalance(proto.getAvlbalance());
                            recyclerItem.setCustRefNo(proto.getCustRefNo());
                            recyclerItem.setDataTime(proto.getTxndate());
                            recyclerItem.setBranch_Code(String.valueOf(proto.getBranchid()));
                            recyclerItem.setOverdue(proto.getLedgbalance());
                            recyclerItem.setCustName(proto.getCustname());
                            recyclerItem.setStan(proto.getStan());
                            recyclerItem.setAgentId(String.valueOf(proto.getBcid()));
                            recyclerItem.setProcessingCode(proto.getProcessingcode());
                            recyclerItem.setProductCode(proto.getProductcode());
                            recyclerItem.setCustno(proto.getCustno());
                            list.add(recyclerItem);
                        }
                        searchModel.setList(list);
                    }

                    if (reportResponse.getTxnsummaryProto() != null) {
                        Log.i(TAG, "No of deposits--> " + reportResponse.getTxnsummaryProto().getNoofDeposits());
                        Log.i(TAG, "Sum of Amount--> " + reportResponse.getTxnsummaryProto().getTotalDepositAmount());
                        searchModel.setNoofTxns(reportResponse.getTxnsummaryProto().getNoofDeposits());
                        searchModel.setSumofTxns(reportResponse.getTxnsummaryProto().getTotalDepositAmount());
                    }

                    if (this.onTaskExecutionFinished != null) {
                        this.onTaskExecutionFinished.onTaskFinished(searchModel);
                    }
                } else {
                    mUiHelper.errorDialog("" + reportResponse.getStatusDescription());
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
