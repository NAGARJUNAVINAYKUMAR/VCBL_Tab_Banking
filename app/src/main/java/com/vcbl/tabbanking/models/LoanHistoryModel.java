package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 06-Jan-18.
 */

public class LoanHistoryModel {

    private int agentid, microatmid, branchid, Noofinstallments, pendingInstlmnts, accountStatus, accountType;
    private String branchcode, AccountNom, CustName, ScanctionAmount, Scanctiondt, InstallmentAmt,
            OutstandingBal, Expirydate, Overdue, txndate, txnStatus, reserve1, reserve2, reserve3,
            accountStatusDesc, accountTypeDesc;

    public LoanHistoryModel() {

    }

    public LoanHistoryModel(int agentid, int microatmid, int branchid, int noofinstallments,
                            int pendingInstlmnts, String branchcode, String accountNom, String custName,
                            String scanctionAmount, String scanctiondt, String installmentAmt,
                            String outstandingBal, String expirydate, String overdue, String txndate,
                            String txnStatus, String reserve1, String reserve2, String reserve3) {
        this.agentid = agentid;
        this.microatmid = microatmid;
        this.branchid = branchid;
        Noofinstallments = noofinstallments;
        this.pendingInstlmnts = pendingInstlmnts;
        this.branchcode = branchcode;
        AccountNom = accountNom;
        CustName = custName;
        ScanctionAmount = scanctionAmount;
        Scanctiondt = scanctiondt;
        InstallmentAmt = installmentAmt;
        OutstandingBal = outstandingBal;
        Expirydate = expirydate;
        Overdue = overdue;
        this.txndate = txndate;
        this.txnStatus = txnStatus;
        this.reserve1 = reserve1;
        this.reserve2 = reserve2;
        this.reserve3 = reserve3;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public int getMicroatmid() {
        return microatmid;
    }

    public void setMicroatmid(int microatmid) {
        this.microatmid = microatmid;
    }

    public int getBranchid() {
        return branchid;
    }

    public void setBranchid(int branchid) {
        this.branchid = branchid;
    }

    public int getNoofinstallments() {
        return Noofinstallments;
    }

    public void setNoofinstallments(int noofinstallments) {
        Noofinstallments = noofinstallments;
    }

    public int getPendingInstlmnts() {
        return pendingInstlmnts;
    }

    public void setPendingInstlmnts(int pendingInstlmnts) {
        this.pendingInstlmnts = pendingInstlmnts;
    }

    public String getBranchcode() {
        return branchcode;
    }

    public void setBranchcode(String branchcode) {
        this.branchcode = branchcode;
    }

    public String getAccountNom() {
        return AccountNom;
    }

    public void setAccountNom(String accountNom) {
        AccountNom = accountNom;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getScanctionAmount() {
        return ScanctionAmount;
    }

    public void setScanctionAmount(String scanctionAmount) {
        ScanctionAmount = scanctionAmount;
    }

    public String getScanctiondt() {
        return Scanctiondt;
    }

    public void setScanctiondt(String scanctiondt) {
        Scanctiondt = scanctiondt;
    }

    public String getInstallmentAmt() {
        return InstallmentAmt;
    }

    public void setInstallmentAmt(String installmentAmt) {
        InstallmentAmt = installmentAmt;
    }

    public String getOutstandingBal() {
        return OutstandingBal;
    }

    public void setOutstandingBal(String outstandingBal) {
        OutstandingBal = outstandingBal;
    }

    public String getExpirydate() {
        return Expirydate;
    }

    public void setExpirydate(String expirydate) {
        Expirydate = expirydate;
    }

    public String getOverdue() {
        return Overdue;
    }

    public void setOverdue(String overdue) {
        Overdue = overdue;
    }

    public String getTxndate() {
        return txndate;
    }

    public void setTxndate(String txndate) {
        this.txndate = txndate;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getReserve3() {
        return reserve3;
    }

    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3;
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
