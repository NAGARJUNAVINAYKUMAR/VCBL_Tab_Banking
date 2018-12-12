package com.vcbl.tabbanking.models;

public class SpouseInfo {

    private String firstName, lastName, aadhaarNo;
    private static SpouseInfo spouseInfo = null;

    public static SpouseInfo getInstance() {
        if (spouseInfo == null) {
            spouseInfo = new SpouseInfo();
        }
        return spouseInfo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAadhaarNo() {
        return aadhaarNo;
    }

    public void setAadhaarNo(String aadhaarNo) {
        this.aadhaarNo = aadhaarNo;
    }
}
