package com.vcbl.tabbanking.interactors;

public interface Protobuffresponse {

    void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput);
}
