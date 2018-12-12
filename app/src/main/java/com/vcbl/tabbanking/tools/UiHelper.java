package com.vcbl.tabbanking.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.vcbl.tabbanking.R;

public class UiHelper {

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private Dialog mDialog;

    public UiHelper(Context packageContext) {
        mContext = packageContext;
    }

    public void showProgress(String strMessage) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage(strMessage);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(R.drawable.app_logo);
        mProgressDialog.setTitle(R.string.app_name);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
        }
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showErrorDialog(String strMessage) {
        if (mDialog == null) {
            mDialog = new Dialog(mContext);
            mDialog.setContentView(R.layout.dialog_design);
            mDialog.setCancelable(false);
            AppCompatTextView txtErr = (AppCompatTextView) mDialog.findViewById(R.id.tv_err);
            txtErr.setText(R.string.response);

            AppCompatTextView txtErrMsg = (AppCompatTextView) mDialog.findViewById(R.id.tv_err_msg);
            txtErrMsg.setText(strMessage);
            mDialog.show();

            AppCompatButton btnOk = (AppCompatButton) mDialog.findViewById(R.id.btn_err);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
        }
    }

    public void errorDialog(String strMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.response);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage(strMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void alertDialog(String strMessage, String strTitle) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(strTitle);
        alertDialog.setMessage(strMessage);
        alertDialog.setIcon(R.drawable.ic_exit);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void alertDialog1(String strMessage, String strTitle) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(strTitle);
        alertDialog.setMessage(strMessage);
        alertDialog.setIcon(R.drawable.ic_exit);
        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        alertDialog.show();
    }

    //@Override
    public void onDestroy() {
        //super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}


