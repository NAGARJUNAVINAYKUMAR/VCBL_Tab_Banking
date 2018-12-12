package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.serversocket.HttpPost;
import com.vcbl.tabbanking.serversocket.HttpPostAsyncResponse;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Protobuffbase implements HttpPostAsyncResponse {

    Protobuffresponse protobuffresponseDelegate = null;
    private static String mStrError;
    Context mContext;
    private String mIdentifier;
    private byte[] mProtoResponseData;
    Storage storage;
    String url, syncUrl, txnUrl;

    private byte[] getmProtoResponseData() {
        return mProtoResponseData;
    }

    void generateProtoRequest(byte[] protoData, String identifier, String type) {
        mIdentifier = identifier;
        String strHeader = "";
        strHeader += Constants.PROTO_HEADER; // 3 Byte Header
        strHeader += Constants.PROTO_VERSION;
        strHeader += identifier; //Three Byte Identifier
        strHeader += type; //Two Byte Type
        strHeader += String.format("%06d", protoData.length);
        //strHeader += protoData;

        byte[] byHeader = strHeader.getBytes();
        byte[] byRequest = new byte[byHeader.length + protoData.length];

        System.arraycopy(byHeader, 0, byRequest, 0, byHeader.length);
        System.arraycopy(protoData, 0, byRequest, byHeader.length, protoData.length);

        HttpPost httpPost = new HttpPost(url, byRequest);
        httpPost.asyncResponseDelegate = this;
        httpPost.execute();
    }

    public boolean parseResponse(Context packageContext, String strServerResponse, String identifier, String type) {
        if (null == strServerResponse) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_empty_response);
            return false;
        }

        //Minimum length should be 15 because header length will be 14 bytes
        if (strServerResponse.length() < 22) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_empty_response);
            return false;
        }

        //parse first 3 bte header and comparing whether it is correct data
        String strHeader = strServerResponse.substring(0, 3);
        if (strHeader.compareTo(Constants.PROTO_HEADER) != 0) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_header_mismatch);
            return false;
        }

        //8 byte Version
        String strVersion = strServerResponse.substring(3, 11);
        if (strVersion.compareTo(Constants.PROTO_VERSION) != 0) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_version_mismatch);
            return false;
        }

        //parse next 3 byte identifier and comparing whether it is correct data
        String strIdentifier = strServerResponse.substring(11, 14);
        if (strIdentifier.compareTo(identifier) != 0) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_incorrect_identifier);
            return false;
        }

        //parse next 2 byte Message Type and comparing whether it is correct data
        String strType = strServerResponse.substring(14, 16);
        if (strType.compareTo(type) != 0) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_incorrect_message_type);
            return false;
        }

        //parse next 6 byte Length
        String strLength = strServerResponse.substring(16, 22);
        int iLength = Integer.parseInt(strLength);

        //Received length not equal to total data length
        if (strServerResponse.length() != iLength + 22) {
            mStrError = packageContext.getResources().getString(R.string.err_proto_partial_response);
            return false;
        }

        //Copy Protobuffer data to Member variable. Should read after 14 bytes upto iLength
        String strProtoResponse = strServerResponse.substring(22, iLength + 22);
        try {
            mProtoResponseData = strProtoResponse.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        char[] charData = new char[iLength];
        strServerResponse.getChars(22, iLength + 22, charData, 0);
        Log.e("protoData", mProtoResponseData + "");
        return true;
    }

    @Override
    public void onHttpPostFinished(boolean bResult, String stMessage, byte[] byOutput) {
        if (bResult) {
            boolean bOk = parseResponse(mContext, Arrays.toString(byOutput), mIdentifier, Constants.PROTO_TYPE_RESPONSE);
            if (!bOk) {
                Toast.makeText(mContext, mStrError, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, stMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHttpPostFinished(boolean bResult, String stMessage, String strOutput) {
        if (bResult) {
            boolean bOk = parseResponse(mContext, strOutput, mIdentifier, Constants.PROTO_TYPE_RESPONSE);
            protobuffresponseDelegate.onTxnProtobufferFinished(bOk, mStrError, getmProtoResponseData());
        } else {
            Toast.makeText(mContext, stMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
