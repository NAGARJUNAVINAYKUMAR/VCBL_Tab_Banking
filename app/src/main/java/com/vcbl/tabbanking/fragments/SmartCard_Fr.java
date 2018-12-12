package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.leopard.api.HexString;
import com.leopard.api.SmartCard;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;

public class SmartCard_Fr extends Fragment {

    private static final String TAG = "SmartCard_Fr-->";
    View view;
    LinearLayout linrlayot_primary, linrlayot_secondary;
    AppCompatButton btn_PRIMARY, btn_SECONDARY;
    AppCompatButton btn_PRPowerup, btn_PRCardstatus, btn_PRPowerdown, btn_SECPowerup, btn_SECCardstatus, btn_SECPowerdown;
    OutputStream outputStream = null;
    InputStream inputStream = null;
    SmartCard smart;
    Storage storage;
    DialogsUtil dialogsUtil;
    private final int MESSAGE_BOX = 1;
    private int iRetVal;
    public static final int DEVICENOTCONNECTED = -100;
    byte[] bATRResp = new byte[300];
    ProgressDialog progressDialog;
    private static final boolean D = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dlg_select_smart_card, container, false);

        setTitle(R.string.smart_card_txns);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        loadUiComponents();

        objInit();

        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            try {
                outputStream = BluetoothComm.mosOut;
                inputStream = BluetoothComm.misIn;
                smart = new SmartCard(LoginActivity.setupInstance, outputStream, inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btn_PRIMARY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linrlayot_primary.setVisibility(View.VISIBLE);
                linrlayot_secondary.setVisibility(View.GONE);
            }
        });

        btn_SECONDARY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linrlayot_primary.setVisibility(View.GONE);
                linrlayot_secondary.setVisibility(View.VISIBLE);
            }
        });

        btn_PRPowerup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartCardAsyc smartcardasyc = new SmartCardAsyc();
                smartcardasyc.execute(0);
            }
        });

        btn_PRCardstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardStatusAsync cardstatus = new CardStatusAsync();
                cardstatus.execute(0);
            }
        });

        btn_PRPowerdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerDownAsyc powerdown = new PowerDownAsyc();
                powerdown.execute(0);
            }
        });

        btn_SECPowerup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartCardAsyc1 smartcardasyc1 = new SmartCardAsyc1();
                smartcardasyc1.execute(0);
            }
        });

        btn_SECCardstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardStatusAsync1 cardstatus1 = new CardStatusAsync1();
                cardstatus1.execute(0);
            }
        });

        btn_SECPowerdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerDownAsyc1 powerdown1 = new PowerDownAsyc1();
                powerdown1.execute(0);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadUiComponents() {
        btn_PRIMARY = view.findViewById(R.id.btn_primrysmrtcrd);

        linrlayot_primary = view.findViewById(R.id.second01);

        btn_PRPowerup = view.findViewById(R.id.btn_PrimryPwrup);
        btn_PRCardstatus = view.findViewById(R.id.btn_Primrycardstatus);
        btn_PRPowerdown = view.findViewById(R.id.btn_PrimrypPwrdwn);

        btn_SECONDARY = view.findViewById(R.id.btn_secndrysmrtcard);

        linrlayot_secondary = view.findViewById(R.id.second02);

        btn_SECPowerup = view.findViewById(R.id.btn_SecondPwrUp);
        btn_SECCardstatus = view.findViewById(R.id.btn_SecndCardStatus);
        btn_SECPowerdown = view.findViewById(R.id.btn_SecndPwrDwn);
    }

    private void objInit() {
        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    dialogsUtil.alertDialog(str);
                default:
                    break;
            }
        }
    };

    /* This method shows the CardStatusAsync AsynTask operation */
    @SuppressLint("StaticFieldLeak")
    public class SmartCardAsyc extends AsyncTask<Integer, Integer, Integer> {

        /* displays the progress dialog until background task is completed */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.smart_card);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /* Task of SmartCardAsyc performing in the background */
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = smart.iSelectSCReader(SmartCard.SC_PrimarySCReader);
                Log.e(TAG, "LEOPARD FPS Smart doInBackground Val" + iRetVal);
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    iRetVal = smart.iSCPowerUpCommand((byte) 0x27, bATRResp);
                } else {
                    handler.obtainMessage(MESSAGE_BOX, "PrimarySCReader Unsuccessfull").sendToTarget();
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICENOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This sends message to handler to display the status messages of Diagnose in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            System.out.println("Power up" + iRetVal);
            Log.e("Power value", ">>>>>>>" + iRetVal);
            progressDialog.dismiss();
            if (iRetVal > 0) { // Receiverd ATR Response
                Log.d(TAG, "Power UP ATR Response : " + HexString.bufferToHex(bATRResp, 0, iRetVal));
                smartCardBox();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Not Inserted").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted but not Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted and Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Unsuccessful operation").sendToTarget();
            } else if (iRetVal == SmartCard.NOT_IN_SMARTCARD_MODE) {
                handler.obtainMessage(MESSAGE_BOX, "Smart card mode is not selected").sendToTarget();
            } else if (iRetVal == SmartCard.READ_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Upon time out for read expires").sendToTarget();
            } else if (iRetVal == SmartCard.PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Upon incorrect number of parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns unknown driver or command").sendToTarget();
            } else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns operation Impossible with this driver").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns incorrect number of arguments").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns reader command unknown").sendToTarget();
            } else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns response exceeds buffer capacity").sendToTarget();
            } else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns wrong response upon card reset").sendToTarget();
            } else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns message is too long").sendToTarget();
            } else if (iRetVal == SmartCard.BYTE_READING_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Byte reading error").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card powered down").sendToTarget();
            } else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
                handler.obtainMessage(MESSAGE_BOX, "Command with an incorrect parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
                handler.obtainMessage(MESSAGE_BOX, "TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns error in the card reset response").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol error").sendToTarget();
            } else if (iRetVal == SmartCard.PARITY_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parity error during a microprocessor exchange").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.READER_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Reader has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
                handler.obtainMessage(MESSAGE_BOX, "RESYNCH successfully performed").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol Parameter Selection Error").sendToTarget();
            } else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card already powered on").sendToTarget();
            } else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
                handler.obtainMessage(MESSAGE_BOX, "PC-Link command not supported").sendToTarget();
            } else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
                handler.obtainMessage(MESSAGE_BOX, "Invalid 'Procedure byte").sendToTarget();
            } else if (iRetVal == SmartCard.SC_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == SmartCard.SC_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else if (iRetVal == DEVICENOTCONNECTED) {
                handler.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INACTIVE_PERIPHERAL) {
                handler.obtainMessage(MESSAGE_BOX, "Inactive Peripheral").sendToTarget();
            }
        }
    }

    /*This method shows the CardStatusAsync AsynTask operation*/
    @SuppressLint("StaticFieldLeak")
    public class CardStatusAsync extends AsyncTask<Integer, Integer, Integer> {

        /*displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.smart_card);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /*Task of CardStatusAsync performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = smart.iSelectSCReader(SmartCard.SC_PrimarySCReader);
                Log.e(TAG, "LEOPARD Smart doInBackground Val" + iRetVal);
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    iRetVal = smart.iSCGetCardStatus();
                } else {
                    handler.obtainMessage(MESSAGE_BOX, "PrimarySCReader Unsuccessfull").sendToTarget();
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICENOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This sends message to handler to display the status messages of Diagnose in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICENOTCONNECTED) {
                handler.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Unsuccessful operation").sendToTarget();
            } else if (iRetVal == SmartCard.NOT_IN_SMARTCARD_MODE) {
                handler.obtainMessage(MESSAGE_BOX, "Smart card mode is not selected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Smart Card present but not powered up").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Smart Card is present and powered up").sendToTarget();
            } else if (iRetVal == SmartCard.READ_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Upon time out for read expires").sendToTarget();
            } else if (iRetVal == SmartCard.PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Upon incorrect number of parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns unknown driver or command").sendToTarget();
            } else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns operation Impossible with this driver").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns incorrect number of arguments").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns reader command unknown").sendToTarget();
            } else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns response exceeds buffer capacity").sendToTarget();
            } else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns wrong response upon card reset").sendToTarget();
            } else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns message is too long").sendToTarget();
            } else if (iRetVal == SmartCard.BYTE_READING_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Byte reading error").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card powered down").sendToTarget();
            } else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
                handler.obtainMessage(MESSAGE_BOX, "Command with an incorrect parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
                handler.obtainMessage(MESSAGE_BOX, "TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns error in the card reset response").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol error").sendToTarget();
            } else if (iRetVal == SmartCard.PARITY_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parity error during a microprocessor exchange").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.READER_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Reader has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
                handler.obtainMessage(MESSAGE_BOX, "RESYNCH successfully performed").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol Parameter Selection Error").sendToTarget();
            } else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card already powered on").sendToTarget();
            } else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
                handler.obtainMessage(MESSAGE_BOX, "PC-Link command not supported").sendToTarget();
            } else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
                handler.obtainMessage(MESSAGE_BOX, "Invalid 'Procedure byte").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Please insert smart card").sendToTarget();
            } else if (iRetVal == SmartCard.SC_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == SmartCard.SC_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else {
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    smartCardBox();
                }
            }
            super.onPostExecute(result);
        }
    }

    /*   This method shows the PowerDown AsynTask operation */
    @SuppressLint("StaticFieldLeak")
    public class PowerDownAsyc extends AsyncTask<Integer, Integer, Integer> {

        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.smart_card);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /* Task of CardStatusAsync performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = smart.iSelectSCReader(SmartCard.SC_PrimarySCReader);
                Log.e(TAG, "LEOPARD Smart PowerDown InBackground Val" + iRetVal);
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    iRetVal = smart.iSCPowerDown();
                } else {
                    handler.obtainMessage(MESSAGE_BOX, "PrimarySCReader Unsuccessfull").sendToTarget();
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICENOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This sends message to handler to display the status messages of Diagnose in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICENOTCONNECTED) {
                handler.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Not Inserted").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted but not Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted and Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Unsuccessful operation").sendToTarget();
            } else if (iRetVal == SmartCard.NOT_IN_SMARTCARD_MODE) {
                handler.obtainMessage(MESSAGE_BOX, "Smart card mode is not selected").sendToTarget();
            } else if (iRetVal == SmartCard.READ_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Upon time out for read expires").sendToTarget();
            } else if (iRetVal == SmartCard.PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Upon incorrect number of parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns unknown driver or command").sendToTarget();
            } else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns operation Impossible with this driver").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns incorrect number of arguments").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns reader command unknown").sendToTarget();
            } else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns response exceeds buffer capacity").sendToTarget();
            } else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns wrong response upon card reset").sendToTarget();
            } else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns message is too long").sendToTarget();
            } else if (iRetVal == SmartCard.BYTE_READING_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Byte reading error").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card powered down").sendToTarget();
            } else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
                handler.obtainMessage(MESSAGE_BOX, "Command with an incorrect parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
                handler.obtainMessage(MESSAGE_BOX, "TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns error in the card reset response").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol error").sendToTarget();
            } else if (iRetVal == SmartCard.PARITY_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parity error during a microprocessor exchange").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.READER_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Reader has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
                handler.obtainMessage(MESSAGE_BOX, "RESYNCH successfully performed").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol Parameter Selection Error").sendToTarget();
            } else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card already powered on").sendToTarget();
            } else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
                handler.obtainMessage(MESSAGE_BOX, "PC-Link command not supported").sendToTarget();
            } else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
                handler.obtainMessage(MESSAGE_BOX, "Invalid 'Procedure byte").sendToTarget();
            } else if (iRetVal == SmartCard.SC_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == SmartCard.SC_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else {
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    handler.obtainMessage(MESSAGE_BOX, "Power down success").sendToTarget();
                }
            }
            super.onPostExecute(result);
        }
    }

    byte[] bAtrResp2 = new byte[300];

    public class SmartCardAsyc1 extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.smart_card);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        /* Task of SmartCardAsyc performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = smart.iSelectSCReader(SmartCard.SC_SecondarySCReader);
                Log.e(TAG, "LEOPARD FPS Smart doInBackground Val" + iRetVal);
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    iRetVal = smart.iSCPowerUpCommand((byte) 0x27, bAtrResp2);
                } else {
                    handler.obtainMessage(1, "PrimarySCReader Unsuccessfull").sendToTarget();
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICENOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This sends message to handler to display the status messages
         * of Diagnose in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            System.out.println("Power up" + iRetVal);
            Log.e("Power value", ">>>>>>>" + iRetVal);
            progressDialog.dismiss();
            if (iRetVal > 0) { // Receiverd ATR Response
                Log.d(TAG, "Power UP ATR Response : " + HexString.bufferToHex(bATRResp, 0, iRetVal));
                smartCardBox();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Not Inserted").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted but not Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted and Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Unsuccessful operation").sendToTarget();
            } else if (iRetVal == SmartCard.NOT_IN_SMARTCARD_MODE) {
                handler.obtainMessage(MESSAGE_BOX, "Smart card mode is not selected").sendToTarget();
            } else if (iRetVal == SmartCard.READ_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Upon time out for read expires").sendToTarget();
            } else if (iRetVal == SmartCard.PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Upon incorrect number of parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns unknown driver or command").sendToTarget();
            } else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns operation Impossible with this driver").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns incorrect number of arguments").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns reader command unknown").sendToTarget();
            } else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns response exceeds buffer capacity").sendToTarget();
            } else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns wrong response upon card reset").sendToTarget();
            } else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns message is too long").sendToTarget();
            } else if (iRetVal == SmartCard.BYTE_READING_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Byte reading error").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card powered down").sendToTarget();
            } else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
                handler.obtainMessage(MESSAGE_BOX, "Command with an incorrect parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
                handler.obtainMessage(MESSAGE_BOX, "TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns error in the card reset response").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol error").sendToTarget();
            } else if (iRetVal == SmartCard.PARITY_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parity error during a microprocessor exchange").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.READER_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Reader has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
                handler.obtainMessage(MESSAGE_BOX, "RESYNCH successfully performed").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol Parameter Selection Error").sendToTarget();
            } else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card already powered on").sendToTarget();
            } else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
                handler.obtainMessage(MESSAGE_BOX, "PC-Link command not supported").sendToTarget();
            } else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
                handler.obtainMessage(MESSAGE_BOX, "Invalid 'Procedure byte").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Please insert smart card").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Please insert smart card").sendToTarget();
            } else if (iRetVal == SmartCard.SC_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == SmartCard.SC_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else if (iRetVal == DEVICENOTCONNECTED) {
                handler.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INACTIVE_PERIPHERAL) {
                handler.obtainMessage(MESSAGE_BOX, "Inacive Peripheral").sendToTarget();
            }
            super.onPostExecute(result);
        }
    }

    public class CardStatusAsync1 extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.smart_card);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        /* Task of CardStatusAsync performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = smart.iSelectSCReader(SmartCard.SC_SecondarySCReader);
                Log.e(TAG, "LEOPARD Smart doInBackground Val" + iRetVal);
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    iRetVal = smart.iSCGetCardStatus();
                } else {
                    handler.obtainMessage(1, "PrimarySCReader Unsuccessfull").sendToTarget();
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICENOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This sends message to handler to display the status messages
         * of Diagnose in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICENOTCONNECTED) {
                handler.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Unsuccessful operation").sendToTarget();
            } else if (iRetVal == SmartCard.NOT_IN_SMARTCARD_MODE) {
                handler.obtainMessage(MESSAGE_BOX, "Smart card mode is not selected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Smart Card present but not powered up").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Smart Card is present and powered up").sendToTarget();
            } else if (iRetVal == SmartCard.READ_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Upon time out for read expires").sendToTarget();
            } else if (iRetVal == SmartCard.PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Upon incorrect number of parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns unknown driver or command").sendToTarget();
            } else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns operation Impossible with this driver").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns incorrect number of arguments").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns reader command unknown").sendToTarget();
            } else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns response exceeds buffer capacity").sendToTarget();
            } else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns wrong response upon card reset").sendToTarget();
            } else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns message is too long").sendToTarget();
            } else if (iRetVal == SmartCard.BYTE_READING_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Byte reading error").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card powered down").sendToTarget();
            } else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
                handler.obtainMessage(MESSAGE_BOX, "Command with an incorrect parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
                handler.obtainMessage(MESSAGE_BOX, "TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns error in the card reset response").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol error").sendToTarget();
            } else if (iRetVal == SmartCard.PARITY_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parity error during a microprocessor exchange").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.READER_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Reader has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
                handler.obtainMessage(MESSAGE_BOX, "RESYNCH successfully performed").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol Parameter Selection Error").sendToTarget();
            } else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card already powered on").sendToTarget();
            } else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
                handler.obtainMessage(MESSAGE_BOX, "PC-Link command not supported").sendToTarget();
            } else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
                handler.obtainMessage(MESSAGE_BOX, "Invalid 'Procedure byte").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Please insert smart card").sendToTarget();
            } else if (iRetVal == SmartCard.SC_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == SmartCard.SC_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else {
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    smartCardBox();
                }
            }
            super.onPostExecute(result);
        }
    }

    public class PowerDownAsyc1 extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.smart_card);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        /* Task of CardStatusAsync performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = smart.iSelectSCReader(SmartCard.SC_SecondarySCReader);
                Log.e(TAG, "LEOPARD Smart PowerDown InBackground Val" + iRetVal);
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    iRetVal = smart.iSCPowerDown();
                } else {
                    handler.obtainMessage(1, "PrimarySCReader Unsuccessfull").sendToTarget();
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICENOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This sends message to handler to display the status messages
         * of Diagnose in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICENOTCONNECTED) {
                handler.obtainMessage(DEVICENOTCONNECTED, "Device not Connected").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Not Inserted").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted but not Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
                handler.obtainMessage(MESSAGE_BOX, "Card Inserted and Powered").sendToTarget();
            } else if (iRetVal == SmartCard.SC_FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Unsuccessful operation").sendToTarget();
            } else if (iRetVal == SmartCard.NOT_IN_SMARTCARD_MODE) {
                handler.obtainMessage(MESSAGE_BOX, "Smart card mode is not selected").sendToTarget();
            } else if (iRetVal == SmartCard.READ_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Upon time out for read expires").sendToTarget();
            } else if (iRetVal == SmartCard.PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Upon incorrect number of parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns unknown driver or command").sendToTarget();
            } else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns operation Impossible with this driver").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns incorrect number of arguments").sendToTarget();
            } else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns reader command unknown").sendToTarget();
            } else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns response exceeds buffer capacity").sendToTarget();
            } else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns wrong response upon card reset").sendToTarget();
            } else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns message is too long").sendToTarget();
            } else if (iRetVal == SmartCard.BYTE_READING_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Byte reading error").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card powered down").sendToTarget();
            } else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
                handler.obtainMessage(MESSAGE_BOX, "Command with an incorrect parameters has been sent").sendToTarget();
            } else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
                handler.obtainMessage(MESSAGE_BOX, "TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Card returns error in the card reset response").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol error").sendToTarget();
            } else if (iRetVal == SmartCard.PARITY_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parity error during a microprocessor exchange").sendToTarget();
            } else if (iRetVal == SmartCard.CARD_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Card has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.READER_ABORTED) {
                handler.obtainMessage(MESSAGE_BOX, "Reader has aborted chaining").sendToTarget();
            } else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
                handler.obtainMessage(MESSAGE_BOX, "RESYNCH successfully performed").sendToTarget();
            } else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
                handler.obtainMessage(MESSAGE_BOX, "Protocol Parameter Selection Error").sendToTarget();
            } else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
                handler.obtainMessage(MESSAGE_BOX, "Card already powered on").sendToTarget();
            } else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
                handler.obtainMessage(MESSAGE_BOX, "PC-Link command not supported").sendToTarget();
            } else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
                handler.obtainMessage(MESSAGE_BOX, "Invalid 'Procedure byte").sendToTarget();
            } else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
                handler.obtainMessage(MESSAGE_BOX, "Please insert smart card").sendToTarget();
            } else if (iRetVal == SmartCard.SC_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == SmartCard.SC_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == SmartCard.SC_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else {
                if (iRetVal == SmartCard.SC_SUCCESS) {
                    handler.obtainMessage(MESSAGE_BOX, "Power down success").sendToTarget();
                }
            }
            super.onPostExecute(result);
        }
    }

    public void smartCardBox() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        @SuppressWarnings("deprecation")
        int width = display.getWidth();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dlg_send_apdu, null);
        builder.setView(view);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.smart_card);

        Dialog dialog = builder.create();
        dialog.show();

        final AppCompatTextView sendapdu_tv = dialog.findViewById(R.id.sendapdu_tv);

        AppCompatTextView textView1 = dialog.findViewById(R.id.textView1);
        textView1.setWidth(width);

        AppCompatButton sendapdu_but = dialog.findViewById(R.id.sendapdu_but);
        sendapdu_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                String str = "";
                int len;
                int iReturnvalue;
                byte[] responsebuf = new byte[500];
                try {
                    iReturnvalue = smart.iSendReceiveApduCommand("00A4000400", responsebuf);
                    len = iReturnvalue;
                    if (len > 0) {
                        str = HexString.bufferToHex(responsebuf, 0, len);
                        if (D) Log.d(TAG, "Response  1 Data: " + str);
                    }
                    responsebuf = new byte[500];
                    int sendapdu3 = smart.iSendReceiveApduCommand("00A40004027000", responsebuf);
                    if (D) Log.d(TAG, "Response 3 Data three" + sendapdu3);
                    String str3 = "";
                    if (sendapdu3 > 0) {
                        str3 = HexString.bufferToHex(responsebuf, 0, sendapdu3);
                    }
                    if (D)
                        Log.d(TAG, "App Select MF :\n" + str + "\n" + "Select DF 7000 :\n" + str3 + "\n");
                    if ((str.equals("") & (str3.equals("")))) {
                        handler.obtainMessage(MESSAGE_BOX, "Smart Card is secured.Please Insert another card").sendToTarget();
                    } else {
                        sendapdu_tv.setText("Select MF :\n" + str + "\n" + "Select DF 7000 :\n" + str3 + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }
}
