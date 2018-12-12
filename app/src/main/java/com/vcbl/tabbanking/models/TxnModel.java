package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.protobuff.Masters;

/**
 * Created by ecosoft2 on 30-Dec-17.
 */

public class TxnModel {

    private String rrn, stan, accNo, txndate, valuedate, microatmid, requestFrom, locAccNo,
            amount, ledgbalance, avlbalance, processingcode, custno, custRefNo, txnStatus, responsecode,
            txnType, custName, submitbalance, productcode, pinNo, accountStatusDesc, accountTypeDesc;
    private int bcid, branch_id, txnserviceid, txnsubserviceid, printCount, accountStatus, accountType;
    private Masters.Status status;

    public TxnModel() {
    }

    public String getTableName() {
        return "TransactionDetails";
    }

    public String getLocAccNo() {
        return locAccNo;
    }

    public void setLocAccNo(String locAccNo) {
        this.locAccNo = locAccNo;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getTxndate() {
        return txndate;
    }

    public void setTxndate(String txndate) {
        this.txndate = txndate;
    }

    public String getValuedate() {
        return valuedate;
    }

    public void setValuedate(String valuedate) {
        this.valuedate = valuedate;
    }

    public String getMicroatmid() {
        return microatmid;
    }

    public void setMicroatmid(String microatmid) {
        this.microatmid = microatmid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLedgbalance() {
        return ledgbalance;
    }

    public void setLedgbalance(String ledgbalance) {
        this.ledgbalance = ledgbalance;
    }

    public String getAvlbalance() {
        return avlbalance;
    }

    public void setAvlbalance(String avlbalance) {
        this.avlbalance = avlbalance;
    }

    public String getProcessingcode() {
        return processingcode;
    }

    public void setProcessingcode(String processingcode) {
        this.processingcode = processingcode;
    }

    public String getCustno() {
        return custno;
    }

    public void setCustno(String custno) {
        this.custno = custno;
    }

    public String getCustRefNo() {
        return custRefNo;
    }

    public void setCustRefNo(String custRefNo) {
        this.custRefNo = custRefNo;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public int getBcid() {
        return bcid;
    }

    public void setBcid(int bcid) {
        this.bcid = bcid;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public int getTxnserviceid() {
        return txnserviceid;
    }

    public void setTxnserviceid(int txnserviceid) {
        this.txnserviceid = txnserviceid;
    }

    public int getTxnsubserviceid() {
        return txnsubserviceid;
    }

    public void setTxnsubserviceid(int txnsubserviceid) {
        this.txnsubserviceid = txnsubserviceid;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getSubmitbalance() {
        return submitbalance;
    }

    public void setSubmitbalance(String submitbalance) {
        this.submitbalance = submitbalance;
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }

    public int getPrintCount() {
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getPinNo() {
        return pinNo;
    }

    public void setPinNo(String pinNo) {
        this.pinNo = pinNo;
    }

    public Masters.Status getStatus() {
        return status;
    }

    public void setStatus(Masters.Status status) {
        this.status = status;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccountStatusDesc() {
        return accountStatusDesc;
    }

    public void setAccountStatusDesc(String accountStatusDesc) {
        this.accountStatusDesc = accountStatusDesc;
    }

    public String getAccountTypeDesc() {
        return accountTypeDesc;
    }

    public void setAccountTypeDesc(String accountTypeDesc) {
        this.accountTypeDesc = accountTypeDesc;
    }
}
