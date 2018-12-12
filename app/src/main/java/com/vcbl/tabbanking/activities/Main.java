package com.vcbl.tabbanking.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;
import com.leopard.api.BaudChange;
import com.leopard.api.Setup;
import com.prowesspride.api.Printer_ESC;
import com.prowesspride.api.Printer_GEN;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.application.GlobalPool;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.bluetooth.BluetoothPair;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import es.dmoral.toasty.Toasty;

public class Main extends AppCompatActivity {

    private static final String TAG = "Main-->";
    PermissionManager permissionManager;
    AppCompatButton btn_scan_BT, btn_pair_BT, btn_connect_BT, btn_exit;
    AppCompatTextView tv_scan_BT, tv_device_name;
    LinearLayout ll_pair_connect, ll_bt_name;
    BluetoothAdapter bluetoothAdapter;
    Storage storage;
    Setup setupInstance;
    com.prowesspride.api.Setup mSetup;
    Context context = this;
    GlobalPool globalPool;
    DialogsUtil dialogsUtil;
    boolean mbBleStatusBefore = false, mbBonded, blnResetBtnEnable;
    public static final byte REQUEST_DISCOVERY = 0x01;
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<>();
    BluetoothDevice mBDevice;
    Dialog dlgRadioBtn, dialogScrollRadio;
    private RadioGroup radiogrp;
    private AppCompatRadioButton radioButton;
    BaudChange bdchange;
    int iRetVal;
    private static final int MESSAGE_BOX = 1;
    String sDevicetype;
    InputStream input;// = BluetoothComm.misIn;
    OutputStream outstream;
    public static Printer_GEN prnGen;
    public static Printer_ESC prnEsc;
    public static final int EXIT_ON_RETURN = 21;
    int REQUEST_ENABLE_BT = 1;

    private BroadcastReceiver _mPairingRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device;
            String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mbBonded = device.getBondState() == BluetoothDevice.BOND_BONDED;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.i(TAG, "--------Entered---------");

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        loadUiComponents();

        objInit();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toasty.error(getApplicationContext(), "Bluetooth does not support on this device", Toast.LENGTH_LONG, true).show();
            finish();
        }

        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
            try {
                setupInstance = new Setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean activate = setupInstance.blActivateLibrary(context, R.raw.licence_full);
            if (!activate) {
                dialogsUtil.alertDialogCancelable("Leopard Library not Activated");
            }
        } else {
            try {
                mSetup = new com.prowesspride.api.Setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean activate = mSetup.blActivateLibrary(context, R.raw.licence);
            if (!activate) {
                dialogsUtil.alertDialogCancelable("Evolute Library not Activated");
            }
        }

        btn_scan_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTServiceDialog();
            }
        });

        tv_scan_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTServiceDialog();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlgExit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;
        for (String item : granted) {
            Toasty.info(getApplicationContext(), "Granted:\n" + item).show();
        }
        for (String item : denied) {
            Toasty.error(getApplicationContext(), "Denied:\n" + item).show();
            dialogsUtil.alertDialog("Denied:\n" + item + "\nGo to Settings to enable required permissions");
        }
    }

    private void loadUiComponents() {
        btn_scan_BT = findViewById(R.id.btn_scan_BT);
        btn_pair_BT = findViewById(R.id.btn_pair_BT);
        btn_connect_BT = findViewById(R.id.btn_connect_BT);
        btn_exit = findViewById(R.id.btn_exit);

        tv_scan_BT = findViewById(R.id.tv_scan_BT);
        tv_device_name = findViewById(R.id.tv_device_name);

        ll_pair_connect = findViewById(R.id.ll_pair_connect);
        ll_bt_name = findViewById(R.id.ll_bt_name);
    }

    private void objInit() {
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(Main.this);

        storage = new Storage(Main.this);

        dialogsUtil = new DialogsUtil(context);

        globalPool = ((GlobalPool) getApplicationContext());
    }


    private void BTServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage("Need printer services ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.cancel();
                storage.saveSecure(Constants.BT_SERVICE, "YES");
                new startBluetoothDeviceTask().execute("");
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                storage.saveSecure(Constants.BT_SERVICE, "NO");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation animationRight = AnimationUtils.loadAnimation(this, R.anim.set_right);
        Animation animationLeft = AnimationUtils.loadAnimation(this, R.anim.set);
        //Animation animationBottom = AnimationUtils.loadAnimation(this, R.anim.set_bottom);
        //Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        //Animation animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        btn_pair_BT.setAnimation(animationLeft);
        tv_scan_BT.setAnimation(animationRight);
        btn_scan_BT.setAnimation(animationLeft);
        btn_exit.setAnimation(animationRight);
        btn_connect_BT.setAnimation(animationLeft);
    }

    public void onClickBtnPair(View view) {
        new PairTask().execute(mhtDeviceInfo.get("MAC"));
        btn_pair_BT.setEnabled(false);
    }

    public void onClickBtnConn(View view) {
        Log.i("mDevice", "--->" + mBDevice.getAddress());
        storage.saveSecure(Constants.BT_ADDRESS, "" + mBDevice.getAddress());
        new connSocketTask().execute(mBDevice.getAddress());
    }

    @SuppressLint("StaticFieldLeak")
    private class startBluetoothDeviceTask extends AsyncTask<String, String, Integer> {

        private static final int RET_BLUETOOTH_IS_START = 0x0001;
        private static final int RET_BLUETOOTH_START_FAIL = 0x04;
        private static final int miWATI_TIME = 15;
        private static final int miSLEEP_TIME = 150;
        private ProgressDialog progressDialog;

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(Main.this);
            progressDialog.setTitle(R.string.bluetooth);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage(getString(R.string.actDiscovery_msg_starting_device));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mbBleStatusBefore = bluetoothAdapter.isEnabled();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int iWait = miWATI_TIME * 1000;
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                while (iWait > 0) {
                    if (!bluetoothAdapter.isEnabled()) {
                        iWait -= miSLEEP_TIME;
                    } else {
                        break;
                    }
                    SystemClock.sleep(miSLEEP_TIME);
                }
                if (iWait < 0) {
                    return RET_BLUETOOTH_START_FAIL;
                }
            }
            return RET_BLUETOOTH_IS_START;
        }

        @Override
        public void onPostExecute(Integer result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (RET_BLUETOOTH_START_FAIL == result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder.setIcon(R.drawable.app_logo);
                builder.setTitle(getString(R.string.dialog_title_sys_err));
                builder.setMessage(getString(R.string.actDiscovery_msg_start_bluetooth_fail));
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothAdapter.disable();
                        finish();
                    }
                });
                builder.create().show();
            } else if (RET_BLUETOOTH_IS_START == result) {
                openDiscovery();
            }
        }
    }

    private void openDiscovery() {
        Intent intent = new Intent(this, BT_DiscoveryActivity.class);
        startActivityForResult(intent, REQUEST_DISCOVERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ll_pair_connect.setVisibility(View.VISIBLE);
        if (requestCode == REQUEST_DISCOVERY) {
            if (Activity.RESULT_OK == resultCode) {
                ll_bt_name.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("NAME", data.getStringExtra("NAME"));
                mhtDeviceInfo.put("MAC", data.getStringExtra("MAC"));
                mhtDeviceInfo.put("COD", data.getStringExtra("COD"));
                mhtDeviceInfo.put("RSSI", data.getStringExtra("RSSI"));
                mhtDeviceInfo.put("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
                mhtDeviceInfo.put("BOND", data.getStringExtra("BOND"));
                showDeviceInfo();
                if (mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))) {
                    btn_pair_BT.setVisibility(View.VISIBLE);
                    btn_connect_BT.setVisibility(View.GONE);
                } else {
                    mBDevice = bluetoothAdapter.getRemoteDevice(mhtDeviceInfo.get("MAC"));
                    btn_pair_BT.setVisibility(View.GONE);
                    btn_connect_BT.setVisibility(View.VISIBLE);
                }
            } else if (Activity.RESULT_CANCELED == resultCode) {
                finish();
            }
        } else if (requestCode == 3) {
            finish();
        }
    }

    @SuppressLint("StringFormatMatches")
    private void showDeviceInfo() {
        tv_device_name.setText(
                String.format(getString(R.string.actMain_device_info),
                        mhtDeviceInfo.get("NAME"),
                        mhtDeviceInfo.get("MAC"),
                        mhtDeviceInfo.get("COD"),
                        mhtDeviceInfo.get("RSSI"),
                        mhtDeviceInfo.get("DEVICE_TYPE"),
                        mhtDeviceInfo.get("BOND"))
        );
        Log.i("mtvDeviceInfo", "==>" + tv_device_name.getText().toString());
        storage.saveSecure(Constants.BT_NAME, "" + tv_device_name.getText().toString());
    }

    public void dlgExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setIcon(R.drawable.app_logo);
        alertDialogBuilder.setMessage("Do you want to exit ?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    BluetoothComm.mosOut = null;
                    BluetoothComm.misIn = null;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                System.gc();
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class PairTask extends AsyncTask<String, String, Integer> {

        static private final int RET_BOND_OK = 0x00;
        static private final int RET_BOND_FAIL = 0x01;
        static private final int iTIMEOUT = 1000 * 15;

        @Override
        public void onPreExecute() {
            Toasty.info(getApplicationContext(), getString(R.string.actMain_msg_bluetooth_Bonding), Toast.LENGTH_SHORT).show();
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothPair.PAIRING_REQUEST));
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            final int iStepTime = 150;
            int iWait = iTIMEOUT;

            try {
                Log.i("mBDevice", "-->" + mBDevice);
                mBDevice = bluetoothAdapter.getRemoteDevice(arg0[0]); //arg0[0] is MAC address
                BluetoothPair.createBond(mBDevice);
                mbBonded = false;
            } catch (Exception e1) {
                Log.d(getString(R.string.app_name), "create Bond failed!");
                e1.printStackTrace();
                return RET_BOND_FAIL;
            }
            while (!mbBonded && iWait > 0) {
                SystemClock.sleep(iStepTime);
                iWait -= iStepTime;
            }
            if (iWait > 0) {
                Log.e("Application", "create Bond failed! RET_BOND_OK ");
            } else {
                Log.e("Application", "create Bond failed! RET_BOND_FAIL ");
            }
            return (iWait > 0) ? RET_BOND_OK : RET_BOND_FAIL;
        }

        @Override
        public void onPostExecute(Integer result) {
            /*try {
                //unregisterReceiver(_mPairingRequest);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception occured in unregistering reciever..." + e);
            }*/
            if (RET_BOND_OK == result) {
                Toasty.success(getApplicationContext(), getString(R.string.actMain_msg_bluetooth_Bond_Success), Toast.LENGTH_SHORT).show();
                btn_pair_BT.setVisibility(View.GONE);
                btn_connect_BT.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("BOND", getString(R.string.actDiscovery_bond_bonded));
                showDeviceInfo();
                //showServiceUUIDs();
            } else {
                Toasty.error(getApplicationContext(), getString(R.string.actMain_msg_bluetooth_Bond_fail), Toast.LENGTH_SHORT, true).show();
                try {
                    BluetoothPair.removeBond(mBDevice);
                } catch (Exception e) {
                    Log.d(getString(R.string.app_name), "removeBond failed!");
                    e.printStackTrace();
                }
                btn_pair_BT.setEnabled(true);
                try {
                    new connSocketTask().execute(mBDevice.getAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception :" + e);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class connSocketTask extends AsyncTask<String, String, Integer> {

        private ProgressDialog progressDialog;
        private static final int CONN_FAIL = 0x01;
        private static final int CONN_SUCCESS = 0x02;

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(Main.this);
            progressDialog.setTitle(R.string.app_name);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage(getString(R.string.actMain_msg_device_connecting));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
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
            progressDialog.dismiss();
            if (CONN_SUCCESS == result) {
                btn_connect_BT.setVisibility(View.GONE);
                Toasty.success(getApplicationContext(), getString(R.string.actMain_msg_device_connect_succes), Toast.LENGTH_SHORT).show();
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                    showBaudRateSelection();
                } else {
                    addListenerOnButton();
                }
            } else {
                Toasty.error(getApplicationContext(), getString(R.string.actMain_msg_device_connect_fail), Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    public void showBaudRateSelection() {
        dlgRadioBtn = new Dialog(context);
        dlgRadioBtn.setCancelable(false);
        dlgRadioBtn.setTitle(R.string.app_name);
        dlgRadioBtn.setContentView(R.layout.dlg_bardchange);
        dlgRadioBtn.show();

        RadioButton radioBtn9600 = dlgRadioBtn.findViewById(R.id.first_radio);
        radioBtn9600.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blnResetBtnEnable = false;
                dlgRadioBtn.dismiss();
                Intent all_intent = new Intent(getApplicationContext(), LoginActivity.class);
                all_intent.putExtra("connected", false);
                startActivityForResult(all_intent, 3);
            }
        });

        RadioButton radioBtn1152 = dlgRadioBtn.findViewById(R.id.second_radio);
        radioBtn1152.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ResetBtnEnable = true;
                blnResetBtnEnable = true;
                dlgRadioBtn.dismiss();
                BaudRateTask increaseBaudRate = new BaudRateTask();
                increaseBaudRate.execute(0);
            }
        });

        RadioButton ibc = dlgRadioBtn.findViewById(R.id.ibc_radio);
        ibc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ResetBtnEnable = false;
                dlgRadioBtn.dismiss();
                Intent all_intent = new Intent(getApplicationContext(), LoginActivity.class);
                all_intent.putExtra("connected", false);
                startActivityForResult(all_intent, 3);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class BaudRateTask extends AsyncTask<Integer, Integer, Integer> {

        private static final int CONN_FAIL = 0x01;
        private static final int CONN_SUCCESS = 0x02;
        private static final int CONN_NO_DEVICE = 0x03;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Main.this);
            progressDialog.setTitle(R.string.app_name);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage("Please wait ...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            try {
                bdchange = new BaudChange(setupInstance,
                        BluetoothComm.mosOut, BluetoothComm.misIn);
                iRetVal = bdchange.iSwitchPeripheral1152();
                Log.e(TAG, "iRetval....." + iRetVal);
                Log.e(TAG, "bdchange is instantiated");
                if (iRetVal == BaudChange.BC_SUCCESS) {
                    Log.e(TAG, "BaudChange.BC_SUCCESS");
                    //SystemClock.sleep(2000);
                    Log.e(TAG, "1");
                    //BluetoothComm.mosOut=null;
                    //BluetoothComm.misIn=null;
                    GlobalPool.mosOut = null;
                    GlobalPool.misIn = null;
                    //btcomm.closeConn();
                    globalPool.mBTcomm.closeConn();
                    Log.e(TAG, "2");
                    //mGP.mBTcomm.closeConn();
                    //SystemClock.sleep(3000);
                    if (bluetoothAdapter != null) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    Log.e(TAG, "3");
                    SystemClock.sleep(3000);
                    //boolean b = mGP.createConn("");
                    //Log.e(TAG, "baudchangereset task.... arg[0]"+arg0);
                    boolean b = globalPool.mBTcomm.createConn();
                    Log.e(TAG, "+++++++++++bConnected......" + b);
                    //boolean b = mGP.createConn();
                    Log.e(TAG, "4");
                    if (b) {
                        globalPool.mBTcomm.isConnect();
                    }
                    Log.e(TAG, "5");
                    SystemClock.sleep(3000);
                    bdchange.iSwitchBT1152(BluetoothComm.mosOut, BluetoothComm.misIn);
                    //	select1152_RadioBtn = false;
                    return CONN_SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return CONN_FAIL;
            }
            return CONN_FAIL;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (CONN_SUCCESS == result) {
                handler.obtainMessage(MESSAGE_BOX, "Baud Change Successful").sendToTarget();
                Intent all_intent = new Intent(getApplicationContext(), LoginActivity.class);
                all_intent.putExtra("connected", false);
                startActivityForResult(all_intent, 3);
            } else if (CONN_NO_DEVICE == result) {
                Log.e(TAG, "Bluetooth No device is set");
                handler.obtainMessage(MESSAGE_BOX, "Please connect to Bluetooth and chagne baudrate").sendToTarget();
            } else {
                handler.obtainMessage(MESSAGE_BOX, "Baud Change FAIL").sendToTarget();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    showDialog(str);
                default:
                    break;
            }
        }
    };

    public void showDialog(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setIcon(R.drawable.app_logo);
        alertDialogBuilder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addListenerOnButton() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.dlg_redioscroll, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.select_protocol);

        dialogScrollRadio = builder.create();
        dialogScrollRadio.show();

        radiogrp = view.findViewById(R.id.radiogrp);
        AppCompatButton btnDisplay = view.findViewById(R.id.btnContinue);

        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedId = radiogrp.getCheckedRadioButtonId();
                radioButton = view.findViewById(selectedId);
                if (globalPool.connection) {
                    if (radioButton.getText().equals("General Protocol")) {
                        Log.e(TAG, "Gen Protocol");
                        genGetSerialNo genSerial = new genGetSerialNo();
                        genSerial.execute(0);
                    } else {
                        Log.e(TAG, "ESC Protocol");
                        escGetSerialNo escSerial = new escGetSerialNo();
                        escSerial.execute(0);
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class genGetSerialNo extends AsyncTask<Integer, Integer, String> {

        private ProgressDialog progressDialog;
        //private static final int CONN_FAIL = 0x01;
       // private static final int CONN_SUCCESS = 0x02;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Main.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                try {
                    input = BluetoothComm.misIn;
                    outstream = BluetoothComm.mosOut;
                    Log.e(TAG, "Printer Gen 1");
                    prnGen = new Printer_GEN(mSetup, outstream, input);
                    Log.e(TAG, "Printer Gen is activated");
                } catch (Exception e) {
                    Log.e(TAG, "Excep " + e);
                    e.printStackTrace();
                }
                sDevicetype = prnGen.sGetSerialNumber();
                Log.e(TAG, "GEN Get Serial Number Result " + sDevicetype);
                //Toast.makeText(context, "Serial No. is " + sDevicetype, Toast.LENGTH_SHORT).show();
                Toasty.success(getApplicationContext(), "Serial No. is " + sDevicetype, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "GEN Exception in Serial No. API Result " + e);
            }
            return sDevicetype;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_LONG).show();
            //Toasty.success(getApplicationContext(), "Serial No. is " + sDevicetype, Toast.LENGTH_SHORT).show();
            Intent protocol8a = new Intent(Main.this, LoginActivity.class);
            startActivityForResult(protocol8a, EXIT_ON_RETURN);
            progressDialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class escGetSerialNo extends AsyncTask<Integer, Integer, String> {

        private ProgressDialog progressDialog;
        //private static final int CONN_FAIL = 0x01;
       // private static final int CONN_SUCCESS = 0x02;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Main.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                try {
                    input = BluetoothComm.misIn;
                    outstream = BluetoothComm.mosOut;
                    prnEsc = new Printer_ESC(mSetup, outstream, input);
                    Log.e(TAG, "pirnter Esc is activated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sDevicetype = prnEsc.sGetDeviceInfo(Printer_ESC.DEVICE_SERIAL_NUMBER);
                Log.e(TAG, "ESC Get Serial Number  " + sDevicetype);
                Toast.makeText(Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "ESC Exception in Serial No. API Result " + e);
            }
            return sDevicetype;
        }

        @Override
        protected void onPostExecute(String result) {
            //SystemClock.sleep(2000);
            Toast.makeText(Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_LONG).show();
            Intent escprotocol = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(escprotocol, EXIT_ON_RETURN);
            progressDialog.dismiss();
        }
    }
}
