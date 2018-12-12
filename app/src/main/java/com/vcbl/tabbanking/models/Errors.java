package com.vcbl.tabbanking.models;

/**
 * Created by Balajee on 19-12-2017.
 */

public class Errors {

    private String Code, Description;
    private int TxnChannelId;

    public Errors() {
    }

    public String getTableName() {
        return "Errors";
    }

    public Errors(String code, String description, int txnChannelId) {
        Code = code;
        Description = description;
        TxnChannelId = txnChannelId;
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

    public int getTxnChannelId() {
        return TxnChannelId;
    }

    public void setTxnChannelId(int txnChannelId) {
        TxnChannelId = txnChannelId;
    }

}
