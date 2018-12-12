package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class Guardian {

    private Age age;
    private Address address;
    private String name, annexureFormFilled, relation;
    private AccountDetails accountDetails ;
    private Identity identity;

    private static Guardian guardian = null;
    public static Guardian getInstance() {
        if (guardian == null) {
            guardian = new Guardian();
        }
        return guardian;
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

    public String getAnnexureFormFilled() {
        return annexureFormFilled;
    }

    public void setAnnexureFormFilled(String annexureFormFilled) {
        this.annexureFormFilled = annexureFormFilled;
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

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
