package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class Introducer {

    private Name name;
    private Age age;
    private Address address;
    private AccountDetails accountDetails ;
    private String relation;
    private int knownYear;
    private Identity identity;

    private static Introducer introducer = null;
    public static Introducer getInstance() {
        if (introducer == null) {
            introducer = new Introducer();
        }
        return introducer;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
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

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public int getKnownYear() {
        return knownYear;
    }

    public void setKnownYear(int knownYear) {
        this.knownYear = knownYear;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
