package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class OtherInfo {

    private String education, occupation, community, specialCategory, authorisedPerson,
            caste, religion, minority, category, beneficiaryType, customerType, customerStatus;

    private static OtherInfo otherInfo = null;
    public static OtherInfo getInstance() {
        if (otherInfo == null) {
            otherInfo = new OtherInfo();
        }
        return otherInfo;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getSpecialCategory() {
        return specialCategory;
    }

    public void setSpecialCategory(String specialCategory) {
        this.specialCategory = specialCategory;
    }

    public String getAuthorisedPerson() {
        return authorisedPerson;
    }

    public void setAuthorisedPerson(String authorisedPerson) {
        this.authorisedPerson = authorisedPerson;
    }

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getMinority() {
        return minority;
    }

    public void setMinority(String minority) {
        this.minority = minority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
}
