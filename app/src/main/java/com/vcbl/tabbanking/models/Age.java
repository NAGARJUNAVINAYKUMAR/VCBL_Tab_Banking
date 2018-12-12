package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class Age {

    private int age;
    private String dob;

    private static Age mAge = null;
    public static Age getInstance() {
        if (mAge == null) {
            mAge = new Age();
        }
        return mAge;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
