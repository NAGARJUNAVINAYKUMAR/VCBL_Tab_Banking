package com.vcbl.tabbanking.application;

import android.app.Application;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

import com.vcbl.tabbanking.bluetooth.BluetoothComm;

public class GlobalPool extends Application {
    /**
     * Bluetooth communication connection object
     */
    private static final String TAG = "GlobalPool-->";
    public BluetoothComm mBTcomm = null;
    public static InputStream misIn = BluetoothComm.misIn;
    /**
     * Output stream object
     */
    public static OutputStream mosOut = BluetoothComm.mosOut;
    public static GlobalPool globalPool = null;
    public boolean connection = false;

    public static GlobalPool getGlobalPoolContext() {
        if (globalPool == null) {
            globalPool = new GlobalPool();
        }
        return globalPool;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Set up a Bluetooth connection
     *
     * @return Boolean
     */

    public boolean createConn(String sMac) {
        Log.e(TAG, "Create Connection");
        if (null == this.mBTcomm) {
            this.mBTcomm = new BluetoothComm(sMac);
            if (this.mBTcomm.createConn()) {
                connection = true;
                return true;
            } else {
                this.mBTcomm = null;
                connection = false;
                Log.i(TAG, "else");
                return false;
            }
        } else
            return true;
    }

    /**
     * Close and release the connection
     *
     * @return void
     */
    public void closeConn() {
        if (null != this.mBTcomm) {
            this.mBTcomm.closeConn();
            this.mBTcomm = null;
        }
    }
}
