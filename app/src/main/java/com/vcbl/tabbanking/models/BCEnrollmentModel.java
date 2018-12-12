package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.protobuff.Masters;

/**
 * Created by ecosoft2 on 29-Dec-17.
 */

public class BCEnrollmentModel {

    private int bc_id, branch_id;
    private String aadhar_no, phone, mobile, title, fname, lname, cashInHand, profession_code, bc_code, empid, start_date, end_date,
            created_date, updated_date, created_by, updated_by, pincode, password, email, fps_data, microAtmId, statusDesc;
    private Masters.Status status;

    public BCEnrollmentModel() {

    }

    public int getBc_id() {
        return bc_id;
    }

    public String getTableName() {
        return "bc_master";
    }

    public void setBc_id(int bc_id) {
        this.bc_id = bc_id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getProfession_code() {
        return profession_code;
    }

    public void setProfession_code(String profession_code) {
        this.profession_code = profession_code;
    }

    public String getBc_code() {
        return bc_code;
    }

    public void setBc_code(String bc_code) {
        this.bc_code = bc_code;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFps_data() {
        return fps_data;
    }

    public void setFps_data(String fps_data) {
        this.fps_data = fps_data;
    }

    public String getMicroAtmId() {
        return microAtmId;
    }

    public void setMicroAtmId(String microAtmId) {
        this.microAtmId = microAtmId;
    }

    public String getCashInHand() {
        return cashInHand;
    }

    public void setCashInHand(String cashInHand) {
        this.cashInHand = cashInHand;
    }

    public Masters.Status getStatus() {
        return status;
    }

    public void setStatus(Masters.Status status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
