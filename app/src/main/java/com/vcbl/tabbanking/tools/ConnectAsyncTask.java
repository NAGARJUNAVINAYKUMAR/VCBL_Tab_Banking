package com.vcbl.tabbanking.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.application.GlobalPool;
import com.vcbl.tabbanking.utils.DialogsUtil;

import es.dmoral.toasty.Toasty;

public class ConnectAsyncTask extends AsyncTask<String, String, Integer> {

    private static final String TAG = "ConnectAsyncTask-->";
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private DialogsUtil dialogsUtil;
    private GlobalPool globalPool;
    private static final int CONN_FAIL = 0x01;
    private static final int CONN_SUCCESS = 0x02;

    public ConnectAsyncTask(Context context1) {
        this.context = context1;
        globalPool = ((GlobalPool) this.context);
        dialogsUtil = new DialogsUtil(context);
    }

    @Override
    public void onPreExecute() {
        dialogsUtil.showProgress((context.getResources().getString(R.string.actMain_msg_device_connecting)));
    }

    @Override
    protected Integer doInBackground(String... arg0) {
        if (globalPool.createConn(arg0[0])) {
            return CONN_SUCCESS;
        } else {
            return CONN_FAIL;
        }
    }

    @Override
    public void onPostExecute(Integer result) {
        dialogsUtil.dismissProgress();
        if (CONN_SUCCESS == result) {
            if (dialogsUtil.isShowing()) {
                dialogsUtil.dismissProgress();
            }
            Toasty.success(context, context.getResources().getString(R.string.actMain_msg_device_connect_succes), Toast.LENGTH_SHORT).show();
        } else {
            Toasty.error(context, "Connection failed !", Toast.LENGTH_SHORT, true).show();
        }
    }
}