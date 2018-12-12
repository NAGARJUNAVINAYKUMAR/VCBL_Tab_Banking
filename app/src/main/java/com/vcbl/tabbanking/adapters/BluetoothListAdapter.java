package com.vcbl.tabbanking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.modelclasses.BluetoothItem;

/**
 * Created by ecosoft2 on 20-Dec-17.
 */

public class BluetoothListAdapter extends ArrayAdapter<BluetoothItem> {

    private List<BluetoothItem> heroList;
    private Context context;
    private int resource;

    public BluetoothListAdapter(Context context, int resource, List<BluetoothItem> heroList) {
        super(context, resource, heroList);
        this.context = context;
        this.resource = resource;
        this.heroList = heroList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(resource, null, false);

        TextView device_item_ble_name = view.findViewById(R.id.device_item_ble_name);
        TextView device_item_ble_mac = view.findViewById(R.id.device_item_ble_mac);
        TextView device_item_ble_cod = view.findViewById(R.id.device_item_ble_cod);
        TextView device_item_ble_rssi = view.findViewById(R.id.device_item_ble_rssi);
        TextView device_item_ble_device_type = view.findViewById(R.id.device_item_ble_device_type);
        TextView device_item_ble_bond = view.findViewById(R.id.device_item_ble_bond);

        BluetoothItem hero = heroList.get(position);

        device_item_ble_name.setText(hero.getBt_name());
        device_item_ble_mac.setText(hero.getBt_mac());
        device_item_ble_cod.setText(hero.getBt_cod());
        device_item_ble_rssi.setText(hero.getBt_rssi());
        device_item_ble_device_type.setText(hero.getBt_device());
        device_item_ble_bond.setText(hero.getBt_bond());

        return view;
    }
}
