package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class BranchInfo {

    private String state, district, tehsil, branch, village, panchayat, pinCode;

    private static BranchInfo branchInfo = null;
    public static BranchInfo getInstance() {
        if (branchInfo == null) {
            branchInfo = new BranchInfo();
        }
        return branchInfo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTehsil() {
        return tehsil;
    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}
