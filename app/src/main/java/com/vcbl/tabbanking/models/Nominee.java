package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.enums.IsTrue;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class Nominee {

    private String name;
    private Age age;
    private Address address;
    private String authorizedPerson, relation;
    private AccountDetails accountDetails ;
    private IsTrue isMinor;
    private Guardian guardian;
    private Identity identity;

    private static Nominee nominee = null;
    public static Nominee getInstance() {
        if (nominee == null) {
            nominee = new Nominee();
        }
        return nominee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAuthorizedPerson() {
        return authorizedPerson;
    }

    public void setAuthorizedPerson(String authorizedPerson) {
        this.authorizedPerson = authorizedPerson;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
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

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
