package com.vcbl.tabbanking.serversocket;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpPost extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "HttpPost-->";
    private String mstrUrl, mstrError = "", mStrResponse;
    private byte[] mbyData;
    public HttpPostAsyncResponse asyncResponseDelegate = null;

    public HttpPost(final String url, final byte[] Data) {
        super();
        this.mstrUrl = url;
        this.mbyData = Data;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpClient httpclient = new DefaultHttpClient();
        org.apache.http.client.methods.HttpPost httppost = new org.apache.http.client.methods.HttpPost(mstrUrl);
        HttpParams params1 = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params1, 45000);
        HttpConnectionParams.setSoTimeout(params1, 45000);
        try {
            httppost.setEntity(new ByteArrayEntity(mbyData));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            mStrResponse = EntityUtils.toString(entity);
            Log.e(TAG, "mStrResponse--> " + mStrResponse);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "ClientProtocolException--> " + e.getMessage());
            mstrError = e.getMessage();
        } catch (IOException e) {
            Log.e(TAG, "IOException--> " + e.getMessage());
            mstrError = e.getMessage();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        asyncResponseDelegate.onHttpPostFinished(aBoolean, mstrError, mStrResponse);
    }
}
