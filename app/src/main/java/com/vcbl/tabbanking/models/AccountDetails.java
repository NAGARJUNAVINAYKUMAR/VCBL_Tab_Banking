package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class AccountDetails {

    private String accountNo, branch;

    private static AccountDetails accountDetails = null;
    public static AccountDetails getInstance() {
        if (accountDetails == null) {
            accountDetails = new AccountDetails();
        }
        return accountDetails;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
