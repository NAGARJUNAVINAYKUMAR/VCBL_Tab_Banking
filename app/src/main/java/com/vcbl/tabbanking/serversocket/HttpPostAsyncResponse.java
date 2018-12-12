package com.vcbl.tabbanking.serversocket;

public interface HttpPostAsyncResponse {

    void onHttpPostFinished(boolean bResult, String strMessage, byte[] byOutput);

    void onHttpPostFinished(boolean bResult, String strMessage, String strOutput);
}
