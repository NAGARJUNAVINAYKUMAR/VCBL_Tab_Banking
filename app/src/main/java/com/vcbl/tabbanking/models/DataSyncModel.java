package com.vcbl.tabbanking.models;

/**
 * Created by ecosoft2 on 23-Jan-18.
 */

public class DataSyncModel {

    private String appVersion, timeStamp, microAtmId;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
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
}
