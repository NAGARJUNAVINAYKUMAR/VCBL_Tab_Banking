package com.vcbl.tabbanking.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vcbl.tabbanking.R;

import java.util.ArrayList;

/**
 * Created by ecosoft2 on 20-Feb-18.
 */

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int resourceID;


    public DeviceListAdapter(Context context, int resource, ArrayList<BluetoothDevice> devices) {
        super(context, resource, devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = mLayoutInflater.inflate(resourceID, null);

        BluetoothDevice device = mDevices.get(position);

        if (device!=null) {
            TextView deviceName = (TextView)convertView.findViewById(R.id.tv_deviceName);
            TextView deviceAddress = (TextView)convertView.findViewById(R.id.tv_deviceAddress);

            if (deviceName!=null) {
                deviceName.setText(device.getName());
            }

            if (deviceAddress!=null) {
                deviceAddress.setText(device.getAddress());
            }
        }
        //return super.getView(position, convertView, parent);
        return convertView;
    }
}
