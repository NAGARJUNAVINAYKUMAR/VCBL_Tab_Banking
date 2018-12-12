package com.vcbl.tabbanking.models;

/**
 * Created by Balajee on 19-12-2017.
 */

public class Products {

    private int id;
    private String Code, Description;

    public Products() {

    }

    public String getTableName() {
        return "Products";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

}
