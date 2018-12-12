package com.vcbl.tabbanking.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;
import com.leopard.api.Setup;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.BluetoothListAdapter;
import com.vcbl.tabbanking.modelclasses.BluetoothItem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothActivity";
    PermissionManager permissionManager;
    private static final int MESSAGE_BOX = 1;
    int REQUEST_ENABLE_BT = 1, REQUEST_DISCOVERABLE = 2;
    Context context;
    ListView listView, listView2;
    AppCompatTextView tv_main_header;
    ArrayAdapter<String> listAdapter;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<BluetoothItem> pairedDevices;
    IntentFilter filter;
    Setup setupInstance;
    private static int iCurrDev = 1;
    public final static String EXTRA_DEVICE_TYPE = "android.bluetooth.device.extra.DEVICE_TYPE";
    private boolean _discoveryFinished;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e(TAG, ">>>>>>>>>>>> FINISH DISCOVERY");
                Log.e(TAG, "" + getString(R.string.app_name) + ">>Bluetooth scanning is finished");
                _discoveryFinished = true; //set scan is finished
                unregisterReceiver(receiver);
                if (null != pairedDevices && pairedDevices.size() > 0) {
                    Toasty.info(getApplicationContext(), getString(R.string.actDiscovery_msg_select_device), Toast.LENGTH_LONG).show();
                } else {
                    Toasty.error(getApplicationContext(), getString(R.string.actDiscovery_msg_not_find_device),
                            Toast.LENGTH_LONG, true).show();
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Log.i(TAG, "--------Entered---------");

        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);

        try {
            setupInstance = new Setup();
            boolean activate = setupInstance.blActivateLibrary(context, R.raw.licence_full);
            if (activate) {
                Log.d(TAG, "Leopard Library Activated......");
            } else {
                handler.obtainMessage(MESSAGE_BOX, "Leopard Library Not Activated...").sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toasty.error(getApplicationContext(), "Device doesn't supports for Bluetooth", Toast.LENGTH_LONG, false).show();
            finish();
        } else {
            if (!btAdapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
                filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(receiver, filter);
            }
            devicesArray = btAdapter.getBondedDevices();
            pairedDevices = new ArrayList<>();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                }
            }
            btAdapter.cancelDiscovery();
            btAdapter.startDiscovery();
        }
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth Enabling Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_DISCOVERABLE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_BOX:
                    String s = (String) msg.obj;
                    showDialog(s);
                default:
                    break;
            }
        }
    };

    public void showDialog(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setIcon(R.drawable.app_logo);
        alertDialogBuilder.setMessage(str);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        if(listAdapter.getItem(i).contains("Paired")) {
            Toasty.success(getApplicationContext(), "Device is paired", Toast.LENGTH_SHORT).show();
        } else {
            Toasty.success(getApplicationContext(), "Device not paired", Toast.LENGTH_SHORT).show();
        }
    }*/
}
