package com.vcbl.tabbanking.models;

import com.vcbl.tabbanking.protobuff.Enrollmentuploadresponse;

public class StatusResponse {

    private Enrollmentuploadresponse.Status status;

    private static StatusResponse statusResponse;
    public static StatusResponse getInstance() {
        if (statusResponse == null) {
            statusResponse = new StatusResponse();
        }
        return statusResponse;
    }

    public Enrollmentuploadresponse.Status getStatus() {
        return status;
    }

    public void setStatus(Enrollmentuploadresponse.Status status) {
        this.status = status;
    }
}
