package com.vcbl.tabbanking.modelclasses;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by ecosoft2 on 13-Dec-17.
 */

public class RecyclerItem {

    private String Rrn, ServerRRN, Stan, DataTime, ProcessingCode, ProductCode, Branch_Code, AuthCode,
            CustRefNo, custName, AccountNo, Amount, LedgerBalance, AvblBalance, AgentId, Txnservice, TxnSubservice,
            ResponseCode, Status, Overdue, custno;
    private int EodFlag, PrintCount;

    public RecyclerItem() {

    }

    public RecyclerItem(String rrn, String serverRRN, String stan, String dataTime,
                        String processingCode, String productCode, String branch_Code,
                        String authCode, String custRefNo, String accountNo, String amount,
                        String ledgerBalance, String avblBalance, String agentId, String txnservice,
                        String txnSubservice, String responseCode, String status,
                        int eodFlag, int printCount, String Overdue) {
        this.Rrn = rrn;
        this.ServerRRN = serverRRN;
        this.Stan = stan;
        this.DataTime = dataTime;
        this.ProcessingCode = processingCode;
        this.ProductCode = productCode;
        this.Branch_Code = branch_Code;
        this.AuthCode = authCode;
        this.CustRefNo = custRefNo;
        this.AccountNo = accountNo;
        this.Amount = amount;
        this.LedgerBalance = ledgerBalance;
        this.AvblBalance = avblBalance;
        this.AgentId = agentId;
        this.Txnservice = txnservice;
        this.TxnSubservice = txnSubservice;
        this.ResponseCode = responseCode;
        this.Status = status;
        this.EodFlag = eodFlag;
        this.PrintCount = printCount;
        this.Overdue = Overdue;
    }

    public String getRrn() {
        return Rrn;
    }

    public void setRrn(String rrn) {
        Rrn = rrn;
    }

    public String getServerRRN() {
        return ServerRRN;
    }

    public void setServerRRN(String serverRRN) {
        ServerRRN = serverRRN;
    }

    public String getStan() {
        return Stan;
    }

    public void setStan(String stan) {
        Stan = stan;
    }

    public String getDataTime() {
        return DataTime;
    }

    public void setDataTime(String dataTime) {
        DataTime = dataTime;
    }

    public String getProcessingCode() {
        return ProcessingCode;
    }

    public void setProcessingCode(String processingCode) {
        ProcessingCode = processingCode;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getBranch_Code() {
        return Branch_Code;
    }

    public void setBranch_Code(String branch_Code) {
        Branch_Code = branch_Code;
    }

    public String getAuthCode() {
        return AuthCode;
    }

    public void setAuthCode(String authCode) {
        AuthCode = authCode;
    }

    public String getCustRefNo() {
        return CustRefNo;
    }

    public void setCustRefNo(String custRefNo) {
        CustRefNo = custRefNo;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(String accountNo) {
        AccountNo = accountNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getLedgerBalance() {
        return LedgerBalance;
    }

    public void setLedgerBalance(String ledgerBalance) {
        LedgerBalance = ledgerBalance;
    }

    public String getAvblBalance() {
        return AvblBalance;
    }

    public void setAvblBalance(String avblBalance) {
        AvblBalance = avblBalance;
    }

    public String getAgentId() {
        return AgentId;
    }

    public void setAgentId(String agentId) {
        AgentId = agentId;
    }

    public String getTxnservice() {
        return Txnservice;
    }

    public void setTxnservice(String txnservice) {
        Txnservice = txnservice;
    }

    public String getTxnSubservice() {
        return TxnSubservice;
    }

    public void setTxnSubservice(String txnSubservice) {
        TxnSubservice = txnSubservice;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getEodFlag() {
        return EodFlag;
    }

    public void setEodFlag(int eodFlag) {
        EodFlag = eodFlag;
    }

    public int getPrintCount() {
        return PrintCount;
    }

    public void setPrintCount(int printCount) {
        PrintCount = printCount;
    }

    public String getOverdue() {
        return Overdue;
    }

    public void setOverdue(String overdue) {
        Overdue = overdue;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustno() {
        return custno;
    }

    public void setCustno(String custno) {
        this.custno = custno;
    }
}
