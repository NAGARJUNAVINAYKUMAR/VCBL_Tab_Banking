package com.vcbl.tabbanking.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;

import es.dmoral.toasty.Toasty;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity--> ";
    Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        configToasty();

        storage = new Storage(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (storage.getValue(Constants.DEVICE_TYPE).length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.app_logo);
                    builder.setMessage("Please select bluetooth device");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Leopard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int id) {
                            dialogInterface.cancel();
                            storage.saveSecure(Constants.DEVICE_TYPE, "LEOPARD");
                            mainOrLogin();
                        }
                    });
                    builder.setNegativeButton("Other", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            storage.saveSecure(Constants.DEVICE_TYPE, "OTHER");
                            dialogInterface.cancel();
                            mainOrLogin();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    mainOrLogin();
                }
                Log.i(TAG, "DEVICE_TYPE--->" + storage.getValue(Constants.DEVICE_TYPE));
            }
        }, 2000);
    }

    private void mainOrLogin() {
        if (storage.getValue(Constants.BT_SERVICE).length() == 0) {
            if (storage.getValue(Constants.BT_ADDRESS).length() == 0) {
                intent(MainActivity.class);
            } else {
                Log.i(TAG, "BT_NAME--->" + storage.getValue(Constants.BT_NAME));
                Log.i(TAG, "BT_ADDRESS--->" + storage.getValue(Constants.BT_ADDRESS));
                intent(LoginActivity.class);
            }
        } else {
            intent(LoginActivity.class);
        }
    }

    public void intent(Class<?> mClass) {
        Intent intent = new Intent(getApplicationContext(), mClass);
        startActivity(intent);
        finish();
    }

    public void showErrorToasty(View view) {
        Toasty.error(getApplicationContext(), "This is an error message", Toast.LENGTH_SHORT, true).show();
    }

    public void showWarningToasty(View view) {
        Toasty.warning(getApplicationContext(), "This is a warning message", Toast.LENGTH_SHORT).show();
    }

    public void showSuccessToasty(View view) {
        Toasty.success(getApplicationContext(), "This is a Success message", Toast.LENGTH_SHORT).show();
    }

    public void showInfoToasty(View view) {
        Toasty.info(getApplicationContext(), "This is an Info message", Toast.LENGTH_SHORT).show();
    }

    public void configToasty() {
        Toasty.Config.getInstance()
                .setErrorColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setWarningColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setSuccessColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setInfoColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                .apply();
    }
}