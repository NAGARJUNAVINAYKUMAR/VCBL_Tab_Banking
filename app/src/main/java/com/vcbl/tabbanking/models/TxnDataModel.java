package com.vcbl.tabbanking.models;

public class TxnDataModel {

    private String accountNo, productCode, custName, avlBalance, ledgBalance, accountTypeDesc, accountStatusDesc, rrn;
    private int accountType, accountStatus;

    private TxnDataModel() {
    }

    private static TxnDataModel txnDataModel;

    public static TxnDataModel getInstance() {
        if (txnDataModel == null) {
            txnDataModel = new TxnDataModel();
        }
        return txnDataModel;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getAvlBalance() {
        return avlBalance;
    }

    public void setAvlBalance(String avlBalance) {
        this.avlBalance = avlBalance;
    }

    public String getLedgBalance() {
        return ledgBalance;
    }

    public void setLedgBalance(String ledgBalance) {
        this.ledgBalance = ledgBalance;
    }

    public String getAccountTypeDesc() {
        return accountTypeDesc;
    }

    public void setAccountTypeDesc(String accountTypeDesc) {
        this.accountTypeDesc = accountTypeDesc;
    }

    public String getAccountStatusDesc() {
        return accountStatusDesc;
    }

    public void setAccountStatusDesc(String accountStatusDesc) {
        this.accountStatusDesc = accountStatusDesc;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }
}
