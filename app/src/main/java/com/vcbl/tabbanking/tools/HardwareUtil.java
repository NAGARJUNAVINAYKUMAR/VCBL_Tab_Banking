package com.vcbl.tabbanking.tools;

import android.content.Context;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class HardwareUtil {

    private String eStrError;
    private Context context;

    public HardwareUtil(Context context) {
        this.context = context;
    }

    public String getError() {
        return eStrError;
    }

    boolean setMacId() {
        String strMAcId = "";
        //Get Macid here. Any error occur return false and set error in eStrError
        //TabBankingGlobals.getInstance().setmStrMacId(strMAcId);
        return true;
    }

    public static String getMacId() {
       /* WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        String replace = macAddress.replace(":","");
        Log.e("replacedMac",replace);
        return replace;*/

        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF));
                }
                Log.e("afterForLoop", res1.toString());
                /*if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }*/
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}

