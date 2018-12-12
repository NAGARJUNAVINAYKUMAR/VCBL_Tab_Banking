package com.vcbl.tabbanking.modelclasses;

/**
 * Created by ecosoft2 on 20-Dec-17.
 */

public class BluetoothItem {

    String bt_name, bt_mac, bt_rssi, bt_cod, bt_device, bt_bond;

    public BluetoothItem(String bt_name, String bt_mac, String bt_rssi,
                         String bt_cod, String bt_device, String bt_bond) {
        this.bt_name = bt_name;
        this.bt_mac = bt_mac;
        this.bt_rssi = bt_rssi;
        this.bt_cod = bt_cod;
        this.bt_device = bt_device;
        this.bt_bond = bt_bond;
    }

    public String getBt_name() {
        return bt_name;
    }

    public String getBt_mac() {
        return bt_mac;
    }

    public String getBt_rssi() {
        return bt_rssi;
    }

    public String getBt_cod() {
        return bt_cod;
    }

    public String getBt_device() {
        return bt_device;
    }

    public String getBt_bond() {
        return bt_bond;
    }
}
