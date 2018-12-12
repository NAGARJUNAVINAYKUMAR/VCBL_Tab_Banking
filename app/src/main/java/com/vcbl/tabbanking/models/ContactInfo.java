package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class ContactInfo {

    private String mobile, phone, email;

    private static ContactInfo contactInfo = null;
    public static ContactInfo getInstance() {
        if (contactInfo == null) {
            contactInfo = new ContactInfo();
        }
        return contactInfo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
