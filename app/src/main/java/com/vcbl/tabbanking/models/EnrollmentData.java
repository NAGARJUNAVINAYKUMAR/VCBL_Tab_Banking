package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.protobuff.Masters;

public class EnrollmentData {

    public static TxnDataModel getTxnDataModel() {
        return TxnDataModel.getInstance();
    }

    public static LoanHistoryData getLoanHistoryData() {
        return LoanHistoryData.getInstance();
    }

    public static PersonalInfo getPersonalInfo() {
        return PersonalInfo.getInstance();
    }

    public static SpouseInfo getSpouseInfo() {
        return SpouseInfo.getInstance();
    }

    public static ContactInfo getContactInfo() {
        return ContactInfo.getInstance();
    }

    public static FinacialInfo getFinancialInfo() {
        return FinacialInfo.getInstance();
    }

    public static BranchInfo getBranchInfo() {
        return BranchInfo.getInstance();
    }

    public static OtherInfo getOtherInfo() {
        return OtherInfo.getInstance();
    }

    public static Name getNameInfo() {
        return Name.getInstance();
    }

    public static Age getAgeInfo() {
        return Age.getInstance();
    }

    public static Address getAddressInfo() {
        return Address.getInstance();
    }

    public static Identity getIdentityDetails() {
        return Identity.getInstance();
    }

    public static IdDetails getIdDetails() {
        return IdDetails.getInstance();
    }

    public static AccountDetails getAccountDetails() {
        return AccountDetails.getInstance();
    }

    public static Guardian getGuardianDetails() {
        return Guardian.getInstance();
    }

    public static Nominee getNomineeDetails() {
        return Nominee.getInstance();
    }

    public static Introducer getIntroducerDetails() {
        return Introducer.getInstance();
    }

    public static StatusResponse getStatusResp() {
        return StatusResponse.getInstance();
    }
}
