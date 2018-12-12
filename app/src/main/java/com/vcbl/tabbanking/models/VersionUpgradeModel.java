package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.protobuff.Masters;

/**
 * Created by ecosoft2 on 19-Feb-18.
 */

public class VersionUpgradeModel {

    private String appVersion, lastUpdateDate, microAtmID, statusDescription, ftpPath, fileName, ftpUserID, ftpPassword;
    private int ftpPort;
    public Masters.Status status;

    public VersionUpgradeModel() {
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getMicroAtmID() {
        return microAtmID;
    }

    public void setMicroAtmID(String microAtmID) {
        this.microAtmID = microAtmID;
    }

    public Masters.Status getStatus() {
        return status;
    }

    public void setStatus(Masters.Status status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFtpUserID() {
        return ftpUserID;
    }

    public void setFtpUserID(String ftpUserID) {
        this.ftpUserID = ftpUserID;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }
}
