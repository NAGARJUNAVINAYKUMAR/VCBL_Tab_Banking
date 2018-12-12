package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class Identity {

    private IdDetails idDetails;
    private String uid, pan, uidAppNo, nregaNo, sspNo, studentId, empId;

    private static Identity identity = null;
    public static Identity getInstance() {
        if (identity == null) {
            identity = new Identity();
        }
        return identity;
    }


    public IdDetails getIdDetails() {
        return idDetails;
    }

    public void setIdDetails(IdDetails idDetails) {
        this.idDetails = idDetails;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getUidAppNo() {
        return uidAppNo;
    }

    public void setUidAppNo(String uidAppNo) {
        this.uidAppNo = uidAppNo;
    }

    public String getNregaNo() {
        return nregaNo;
    }

    public void setNregaNo(String nregaNo) {
        this.nregaNo = nregaNo;
    }

    public String getSspNo() {
        return sspNo;
    }

    public void setSspNo(String sspNo) {
        this.sspNo = sspNo;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }
}
