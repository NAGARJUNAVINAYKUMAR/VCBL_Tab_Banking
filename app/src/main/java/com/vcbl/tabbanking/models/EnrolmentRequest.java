package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.enums.Entity;

/**
 * Created by ecosoft2 on 15-Mar-18.
 */

public class EnrolmentRequest {

    private String appsVersion, timeStamp, microAtmId, bcId, enrollmentNo;
    private Entity entity;
    private EnrolmentData enrolmentData;

    public EnrolmentRequest() {
    }

    public String getAppsVersion() {
        return appsVersion;
    }

    public void setAppsVersion(String appsVersion) {
        this.appsVersion = appsVersion;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMicroAtmId() {
        return microAtmId;
    }

    public void setMicroAtmId(String microAtmId) {
        this.microAtmId = microAtmId;
    }

    public String getBcId() {
        return bcId;
    }

    public void setBcId(String bcId) {
        this.bcId = bcId;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }

    public void setEnrollmentNo(String enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public EnrolmentData getEnrolmentData() {
        return enrolmentData;
    }

    public void setEnrolmentData(EnrolmentData enrolmentData) {
        this.enrolmentData = enrolmentData;
    }
}
