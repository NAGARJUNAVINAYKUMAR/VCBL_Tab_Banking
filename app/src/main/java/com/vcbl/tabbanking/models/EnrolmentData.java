package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.enums.IsTrue;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class EnrolmentData {

    private String product;
    private PersonalInfo personalInfo;
    private FinacialInfo finacialInfo;
    private BranchInfo branchInfo;
    private Address  address;
    private ContactInfo contactInfo;
    private Identity identity;
    private OtherInfo otherInfo;
    private IsTrue isMinor;
    private Guardian guardian;
    private Nominee nominee;
    private Introducer introducer;

    public EnrolmentData() {
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public FinacialInfo getFinacialInfo() {
        return finacialInfo;
    }

    public void setFinacialInfo(FinacialInfo finacialInfo) {
        this.finacialInfo = finacialInfo;
    }

    public BranchInfo getBranchInfo() {
        return branchInfo;
    }

    public void setBranchInfo(BranchInfo branchInfo) {
        this.branchInfo = branchInfo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public OtherInfo getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(OtherInfo otherInfo) {
        this.otherInfo = otherInfo;
    }

    public IsTrue getIsMinor() {
        return isMinor;
    }

    public void setIsMinor(IsTrue isMinor) {
        this.isMinor = isMinor;
    }

    public Guardian getGuardian() {
        return guardian;
    }

    public void setGuardian(Guardian guardian) {
        this.guardian = guardian;
    }

    public Nominee getNominee() {
        return nominee;
    }

    public void setNominee(Nominee nominee) {
        this.nominee = nominee;
    }

    public Introducer getIntroducer() {
        return introducer;
    }

    public void setIntroducer(Introducer introducer) {
        this.introducer = introducer;
    }
}
