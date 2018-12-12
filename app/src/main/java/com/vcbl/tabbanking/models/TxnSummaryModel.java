package com.vcbl.tabbanking.models;

public class TxnSummaryModel {

    private String agentName, branchName, noofDeposits, totalDepositAmount, noofLoanDeposits,
            totalLoanDepositAmount, noofSavingDeposits, totalSavingDepositAmount, serviceId, subServiceId;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getNoofDeposits() {
        return noofDeposits;
    }

    public void setNoofDeposits(String noofDeposits) {
        this.noofDeposits = noofDeposits;
    }

    public String getTotalDepositAmount() {
        return totalDepositAmount;
    }

    public void setTotalDepositAmount(String totalDepositAmount) {
        this.totalDepositAmount = totalDepositAmount;
    }

    public String getNoofLoanDeposits() {
        return noofLoanDeposits;
    }

    public void setNoofLoanDeposits(String noofLoanDeposits) {
        this.noofLoanDeposits = noofLoanDeposits;
    }

    public String getTotalLoanDepositAmount() {
        return totalLoanDepositAmount;
    }

    public void setTotalLoanDepositAmount(String totalLoanDepositAmount) {
        this.totalLoanDepositAmount = totalLoanDepositAmount;
    }

    public String getNoofSavingDeposits() {
        return noofSavingDeposits;
    }

    public void setNoofSavingDeposits(String noofSavingDeposits) {
        this.noofSavingDeposits = noofSavingDeposits;
    }

    public String getTotalSavingDepositAmount() {
        return totalSavingDepositAmount;
    }

    public void setTotalSavingDepositAmount(String totalSavingDepositAmount) {
        this.totalSavingDepositAmount = totalSavingDepositAmount;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSubServiceId() {
        return subServiceId;
    }

    public void setSubServiceId(String subServiceId) {
        this.subServiceId = subServiceId;
    }
}
