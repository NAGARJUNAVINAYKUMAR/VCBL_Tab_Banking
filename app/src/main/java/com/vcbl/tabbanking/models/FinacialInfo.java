package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class FinacialInfo {

    private String financialCategory, houseType, bpl, landHolding, size, sizeType, house, annualIncome,
            noOfDependents, landCategory, noOfAnimals, tds, insurance, pensionScheme;

    private static FinacialInfo finacialInfo = null;
    public static FinacialInfo getInstance() {
        if (finacialInfo == null) {
            finacialInfo = new FinacialInfo();
        }
        return finacialInfo;
    }

    public String getFinancialCategory() {
        return financialCategory;
    }

    public void setFinancialCategory(String financialCategory) {
        this.financialCategory = financialCategory;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getBpl() {
        return bpl;
    }

    public void setBpl(String bpl) {
        this.bpl = bpl;
    }

    public String getLandHolding() {
        return landHolding;
    }

    public void setLandHolding(String landHolding) {
        this.landHolding = landHolding;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSizeType() {
        return sizeType;
    }

    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(String annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getNoOfDependents() {
        return noOfDependents;
    }

    public void setNoOfDependents(String noOfDependents) {
        this.noOfDependents = noOfDependents;
    }

    public String getLandCategory() {
        return landCategory;
    }

    public void setLandCategory(String landCategory) {
        this.landCategory = landCategory;
    }

    public String getNoOfAnimals() {
        return noOfAnimals;
    }

    public void setNoOfAnimals(String noOfAnimals) {
        this.noOfAnimals = noOfAnimals;
    }

    public String getTds() {
        return tds;
    }

    public void setTds(String tds) {
        this.tds = tds;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPensionScheme() {
        return pensionScheme;
    }

    public void setPensionScheme(String pensionScheme) {
        this.pensionScheme = pensionScheme;
    }
}
