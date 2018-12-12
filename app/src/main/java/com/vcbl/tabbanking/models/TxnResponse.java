package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.protobuff.Masters;

/**
 * Created by ecosoft2 on 30-Dec-17.
 */

public class TxnResponse {
    public int microatmid;
    private String timestamp;
    private String appVersion;
    public Masters.Status status ;
    private String statusDescription;
    private TxnModel txnModel;

    private LoanHistoryModel loanHistoryModel;

    public int getMicroatmid() {
        return microatmid;
    }

    public void setMicroatmid(int microatmid) {
        this.microatmid = microatmid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Masters.Status getStatus() {
        return status;
    }

    public void setStatus(Masters.Status status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public TxnModel getTxnModel() {
        return txnModel;
    }

    public void setTxnModel(TxnModel txnModel) {
        this.txnModel = txnModel;
    }

    public LoanHistoryModel getLoanHistoryModel() {
        return loanHistoryModel;
    }

    public void setLoanHistoryModel(LoanHistoryModel loanHistoryModel) {
        loanHistoryModel = loanHistoryModel;
    }
}
