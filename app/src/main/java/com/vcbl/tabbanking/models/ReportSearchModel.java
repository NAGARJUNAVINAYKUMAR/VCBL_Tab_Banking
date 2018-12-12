package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.protobuff.Masters;

import java.util.List;

public class ReportSearchModel {

    private String fromDate, toDate, productCode, accountNo, serverRrn, noofTxns, sumofTxns;
    private int bcID, branchID, terminalID;
    private List<RecyclerItem> list;
    private Masters.Status status;

    public ReportSearchModel() {
    }

    public Masters.Status getStatus() {
        return status;
    }

    public void setStatus(Masters.Status status) {
        this.status = status;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int getBcID() {
        return bcID;
    }

    public void setBcID(int bcID) {
        this.bcID = bcID;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public int getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }

    public List<RecyclerItem> getList() {
        return list;
    }

    public void setList(List<RecyclerItem> list) {
        this.list = list;
    }

    public String getServerRrn() {
        return serverRrn;
    }

    public void setServerRrn(String serverRrn) {
        this.serverRrn = serverRrn;
    }

    public String getNoofTxns() {
        return noofTxns;
    }

    public void setNoofTxns(String noofTxns) {
        this.noofTxns = noofTxns;
    }

    public String getSumofTxns() {
        return sumofTxns;
    }

    public void setSumofTxns(String sumofTxns) {
        this.sumofTxns = sumofTxns;
    }
}
