package com.vcbl.tabbanking.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.leopard.api.FPS;
import com.leopard.api.FpsConfig;
import com.leopard.api.HexString;
import com.leopard.api.Setup;

import java.io.InputStream;
import java.io.OutputStream;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.application.GlobalPool;
import com.vcbl.tabbanking.interactors.LoginBO;
import com.vcbl.tabbanking.interactors.OnTaskFinishedListener;
import com.vcbl.tabbanking.models.AgentLoginModel;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;

import es.dmoral.toasty.Toasty;

import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.tools.CryptoHelper;
import com.vcbl.tabbanking.tools.HardwareUtil;
import com.vcbl.tabbanking.utils.DialogsUtil;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity--> ";
    private AppCompatEditText et_emp_id, et_password;
    AppCompatButton btn_login;
    public static Setup setupInstance = null;
    public static com.prowesspride.api.Setup mSetup = null;
    String emp_id, pin_no;
    Context context = this;
    public static final int DEVICE_NOTCONNECTED = -100;
    private static final int MESSAGE_BOX = 1;
    FPS fps;
    OutputStream outputStream;
    InputStream inputstream;
    Storage storage;
    LoginBO loginBO;
    AgentLoginModel agentLoginResp;
    private GlobalPool globalPool;
    public static BluetoothAdapter bluetoothAdapter;
    //int id;
    int iRetVal;
    private boolean blVerifyfinger = false;
    private byte[] brecentminituaedata = {};
    DialogsUtil dialogsUtil;
    private ProgressDialog progressDialog;
    int REQUEST_ENABLE_BT = 1;
    //TextInputLayout textInputLayout1, textInputLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        Log.i(TAG, "--------Entered---------");

        /*if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }*/

        storage = new Storage(LoginActivity.this);
        dialogsUtil = new DialogsUtil(LoginActivity.this);
        globalPool = ((GlobalPool) getApplicationContext());

        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                try {
                    setupInstance = new Setup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean activate = setupInstance.blActivateLibrary(context, R.raw.licence_full);
                if (activate) {
                    Log.d(TAG, "Leopard library activated......");
                } else {
                    dialogsUtil.alertDialog("Leopard library not activated");
                }
            } else {
                try {
                    mSetup = new com.prowesspride.api.Setup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean activate = mSetup.blActivateLibrary(context, R.raw.licence);
                if (activate) {
                    Log.d(TAG, "Evolute Library Activated......");
                } else {
                    dialogsUtil.alertDialog("Evolute Library not Activated");
                }
            }

            // BT initialisation
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (null == bluetoothAdapter) {
                dialogsUtil.alertDialogCancelable("Bluetooth doesn't supports");
                finish();
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
                }
            }

            // replaced from onResume()
            try {
                new connSocketTask().execute(storage.getValue(Constants.BT_ADDRESS));
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }

        Log.i(TAG, "connection-->" + globalPool.connection);
        Log.i(TAG, "DEVICE_TYPE--->" + storage.getValue(Constants.DEVICE_TYPE));
        Log.i(TAG, "BT_SERVICE--->" + storage.getValue(Constants.BT_SERVICE));
        Log.i(TAG, "BT_NAME--->" + storage.getValue(Constants.BT_NAME));
        Log.i(TAG, "BT_ADDRESS--->" + storage.getValue(Constants.BT_ADDRESS));
        Log.i(TAG, "SYNC_URL--->" + storage.getValue(Constants.SYNC_URL));
        Log.i(TAG, "TXT_URL--->" + storage.getValue(Constants.TXT_URL));
        Log.i(TAG, "LOGIN_TYPE--->" + storage.getValue(Constants.LOGIN_TYPE));

        //textInputLayout1 = (TextInputLayout) findViewById(R.id.textInputLayout1);
        //textInputLayout2 = (TextInputLayout) findViewById(R.id.textInputLayout2);

        et_emp_id = findViewById(R.id.et_emp_id);
        et_emp_id.requestFocus();
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
            et_emp_id.setVisibility(View.VISIBLE);
            et_password.setVisibility(View.GONE);
            if (storage.getValue(Constants.LOGIN_TYPE).equals("BIOMETRIC")) {
                et_emp_id.setVisibility(View.VISIBLE);
                et_password.setVisibility(View.GONE);
                et_emp_id.setText("");
                et_emp_id.setHint(R.string.employee_id);
                et_emp_id.requestFocus();
            } else {
                et_emp_id.setVisibility(View.VISIBLE);
                et_password.setVisibility(View.VISIBLE);
                et_emp_id.setText("");
                et_password.setText("");
                et_emp_id.setHint(R.string.employee_id);
                et_emp_id.requestFocus();
                et_password.setHint(R.string.password);
            }
        } else {
            et_emp_id.setVisibility(View.VISIBLE);
            et_password.setVisibility(View.VISIBLE);
            et_emp_id.setText("");
            et_password.setText("");
            et_emp_id.setHint(R.string.employee_id);
            et_emp_id.requestFocus();
            et_password.setHint(R.string.password);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //textInputLayout1.setError("");
                //textInputLayout2.setError("");

                emp_id = et_emp_id.getText().toString().trim();
                pin_no = et_password.getText().toString().trim();
                if (!isOnline()) {
                    dialogsUtil.alertDialog("Please check network connection");
                } else {
                    if (storage.getValue(Constants.SYNC_URL).length() == 0
                            && storage.getValue(Constants.TXT_URL).length() == 0) {
                        dialogsUtil.alertDialog("Please save network settings");
                    } else {
                        if (storage.getValue(Constants.LOGIN_TYPE).equals("BIOMETRIC")) {
                            biometricLogin();
                        } else {
                            pinLogin();
                        }
                    }
                }

                /*Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "BT enabled successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth enabling cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            //if (globalPool.connection) {
            globalPool.closeConn();
            //}
        }
        //if (null != bluetoothAdapter && !mbBleStatusBefore)
        //bluetoothAdapter.disable();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            try {
                new connSocketTask().execute(storage.getValue(Constants.BT_ADDRESS));
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        dlgExit();
        //dlgNormalExit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_biometric) {
            if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                et_emp_id.setVisibility(View.VISIBLE);
                et_password.setVisibility(View.GONE);
                et_emp_id.setText("");
                et_emp_id.setHint(R.string.employee_id);
                storage.saveSecure(Constants.LOGIN_TYPE, "BIOMETRIC");
            } else {
                dialogsUtil.alertDialog("Biometric is not allowed !");
            }
            return true;
        } else if (id == R.id.action_pin_no) {
            et_emp_id.setVisibility(View.VISIBLE);
            et_password.setVisibility(View.VISIBLE);
            et_emp_id.setText("");
            et_password.setText("");
            et_emp_id.setHint(R.string.employee_id);
            et_password.setHint(R.string.password);
            storage.saveSecure(Constants.LOGIN_TYPE, "PIN");
            return true;
        } else if (id == R.id.action_check_bt_conn) {
            if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                try {
                    //if (!globalPool.connection) {
                    globalPool.closeConn();
                    BluetoothComm.misIn = null;
                    BluetoothComm.mosOut = null;
                    //}
                    new connSocketTask().execute(storage.getValue(Constants.BT_ADDRESS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dialogsUtil.alertDialog("Printer services required");
            }
            return true;
        } else if (id == R.id.action_settings) {
            networkSettings();
            return true;
        } else if (id == R.id.action_configure_bt) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.app_logo);
            builder.setMessage(R.string.configure_bt);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    if (globalPool.connection) {
                        globalPool.closeConn();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else if (id == R.id.action_select_bt_device) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.app_logo);
            builder.setMessage("Select/Change bluetooth device");
            builder.setCancelable(false);
            builder.setPositiveButton("Leopard", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    mainOrLogin("LEOPARD");
                }
            });
            builder.setNegativeButton("Other", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mainOrLogin("OTHER");
                }
            });
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mainOrLogin(String deviceType) {
        storage.saveSecure(Constants.DEVICE_TYPE, deviceType);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void biometricLogin() {
        if (emp_id.isEmpty()) {
            et_emp_id.setError("Enter employee id");
            et_emp_id.requestFocus();
        } else {
            prepareModel();
        }
    }

    private void pinLogin() {
        if (emp_id.equals("") && pin_no.equals("")) {
            et_emp_id.setError("Enter employee id");
//            textInputLayout1.setError("Enter employee id");
            et_emp_id.requestFocus();
        } else if (emp_id.equals("")) {
            et_emp_id.setError("Enter employee id");
//            textInputLayout1.setError("Enter employee id");
            et_emp_id.requestFocus();
        } else if (pin_no.equals("")) {
            et_password.setError("Please enter pin");
//            textInputLayout2.setError("Please enter pin");
            et_password.requestFocus();
        } else if (pin_no.length() < 4) {
            et_password.setError("PIN length should be min 4 Digits");
//            textInputLayout2.setError("PIN length should be min 4 Digits");
            et_password.requestFocus();
        } else {
            prepareModel();
        }
    }

    private void prepareModel() {
        loginBO = new LoginBO(context);
        AgentLoginModel agentLoginModel = new AgentLoginModel();
        agentLoginModel.setLoginId(emp_id);
        if (storage.getValue(Constants.LOGIN_TYPE).equals("PIN")) {
            agentLoginModel.setPin(pin_no);
        } else {
            agentLoginModel.setPin("");
        }
        agentLoginModel.setBiometric("dummy");
        loginBO.LoginRequest(agentLoginModel);
        loginBO.setOnTaskFinishedEvent(new OnTaskFinishedListener() {
            @Override
            public void onTaskFinished(AgentLoginModel agentLoginRes) {
                agentLoginResp = agentLoginRes;
                if (storage.getValue(Constants.LOGIN_TYPE).equals("BIOMETRIC")) {
                    if (agentLoginResp.getFps() != null && !"".equals(agentLoginResp.getFps())
                            && agentLoginResp.getFps().length() > 0) {
                        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                            try {
                                new VerifyTempleAsync().execute(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            dialogsUtil.alertDialog("Printer services required");
                        }
                    } else {
                        et_emp_id.setText("");
                    }
                } else {
                    if (agentLoginResp.getPin() != null && !"".equals(agentLoginResp.getPin())
                            && agentLoginResp.getPin().length() > 0) {
                        try {
                            if (!pin_no.equals(CryptoHelper.decrypt(agentLoginResp.getPin()))) {
                                dialogsUtil.alertDialog("Invalid credentials !");
                                et_password.setText("");
                            } else {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                et_emp_id.setText("");
                                et_password.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class connSocketTask extends AsyncTask<String, String, Integer> {

        private static final int CONN_FAIL = 0x01;
        private static final int CONN_SUCCESS = 0x02;

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle(R.string.bluetooth);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage(getString(R.string.actMain_msg_device_connecting));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(true);
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
                Log.i(TAG, "connection-->" + globalPool.connection);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                    try {
                        outputStream = BluetoothComm.mosOut;
                        inputstream = BluetoothComm.misIn;
                        if (outputStream != null && inputstream != null) {
                            fps = new FPS(setupInstance, outputStream, inputstream);
                            Log.d("TEMP", "setupInstance--->" + setupInstance);
                            Log.d("TEMP", "outputStream--->" + outputStream);
                            Log.d("TEMP", "inputstream--->" + inputstream);
                            Toasty.success(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("FAILED", "Failed to Connect FPS");
                            Log.d(TAG, "setupInstance--->" + setupInstance);
                            Log.d(TAG, "outputStream--->" + outputStream);
                            Log.d(TAG, "inputstream--->" + inputstream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.success(getApplicationContext(), "Connection established", Toast.LENGTH_SHORT).show();
                }
                //showBaudRateSelection();
            } else {
                Toasty.error(getApplicationContext(), "Connection failed, Please check whether your device printer is On/Off",
                        Toast.LENGTH_LONG, true).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VerifyTempleAsync extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle(R.string.verify_figure);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage(getString(R.string.place_your_finger));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                brecentminituaedata = HexString.hexToBuffer(agentLoginResp.getFps());
                iRetVal = fps.iFpsVerifyMinutiae(brecentminituaedata, new FpsConfig(1, (byte) 0x0f));
                Log.i(TAG, "iRetVal-->" + String.valueOf(iRetVal));
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                Log.i(TAG, "iRetVal-->" + String.valueOf(iRetVal));
                e.printStackTrace();
            }
            return iRetVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICE_NOTCONNECTED) {
                handler.obtainMessage(DEVICE_NOTCONNECTED, "Device not connected").sendToTarget();
            } else if (iRetVal == FPS.SUCCESS) {
                blVerifyfinger = true;
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                et_emp_id.setText("");
            } else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
                handler.obtainMessage(MESSAGE_BOX, "Peripheral is inactive").sendToTarget();
            } else if (iRetVal == FPS.TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
            } else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Illegal library").sendToTarget();
            } else if (iRetVal == FPS.FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Captured template verification is failed,\nWrong finger placed").sendToTarget();
            } else if (iRetVal == FPS.PARAMETER_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
            } else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else {
                handler.obtainMessage(MESSAGE_BOX, "Invalid credentials").sendToTarget();
            }
            super.onPostExecute(result);
        }
    }

    public void dlgExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage("Do you want to exit ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*try {
                    BluetoothComm.mosOut = null;
                    BluetoothComm.misIn = null;
                    finish();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                System.gc();*/
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    dialogsUtil.alertDialog(str);
                default:
                    break;
            }
        }
    };

    /*public void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    public Boolean isOnline() {
        ConnectivityManager conManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = null;
        if (conManager != null) {
            nwInfo = conManager.getActiveNetworkInfo();
        }
        return nwInfo != null;
    }

    private void networkSettings() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dlg_network_settings, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.network_settings);

        final AppCompatEditText et_mac_address = view.findViewById(R.id.et_mac_address);
        final AppCompatEditText et_url = view.findViewById(R.id.et_url);
        final AppCompatEditText et_port = view.findViewById(R.id.et_port);
        final AppCompatEditText et_app = view.findViewById(R.id.et_app);
        final AppCompatButton btn_clear_nw_settings = view.findViewById(R.id.btn_clear_nw_settings);
        final AppCompatButton btn_save_nw_settings = view.findViewById(R.id.btn_save_nw_settings);

        final AlertDialog dialog = builder.create();
        dialog.show();

        et_mac_address.setText(HardwareUtil.getMacId());
        Log.i(TAG, "HardwareUtil.getMacId()-->" + HardwareUtil.getMacId());

        et_mac_address.setEnabled(false);
        et_mac_address.setClickable(false);
        et_url.setText(R.string.urlLink);
        et_port.setText(R.string.port_no);
        et_app.setText(R.string.port_app_name);

        btn_save_nw_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_url.getText().toString().trim().isEmpty()) {
                    et_url.setError("Please enter URL");
                    et_url.requestFocus();
                } else if (et_port.getText().toString().trim().isEmpty()) {
                    et_port.setError("Please enter port no.");
                    et_port.requestFocus();
                } else if (et_app.getText().toString().trim().isEmpty()) {
                    et_app.setError("Please enter app name");
                    et_app.requestFocus();
                } else {
                    storage.saveSecure(Constants.SYNC_URL, "http://" + et_url.getText().toString().trim()
                            + ":" + et_port.getText().toString().trim() + "/" + et_app.getText().toString().trim() + Constants.sync);
                    storage.saveSecure(Constants.TXT_URL, "http://" + et_url.getText().toString().trim()
                            + ":" + et_port.getText().toString().trim() + "/" + et_app.getText().toString().trim() + Constants.txn);
                    if (storage.getValue(Constants.SYNC_URL).length() > 0 && storage.getValue(Constants.TXT_URL).length() > 0) {
                        Toasty.success(getApplicationContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "CHANGED_SYNC_URL-->" + storage.getValue(Constants.SYNC_URL));
                        Log.i(TAG, "CHANGED_TXT_URL-->" + storage.getValue(Constants.TXT_URL));
                        dialog.dismiss();
                    }
                }
            }
        });

        btn_clear_nw_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
}
