package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leopard.api.MagCard;
import com.leopard.api.Printer;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class MagCardTxns_Fr extends Fragment {

    private static final String TAG = "MagCardTxns_Fr-->";
    View view;
    AppCompatButton btn_magnet_card;
    Storage storage;
    DialogsUtil dialogsUtil;
    OutputStream outputStream = null;
    InputStream inputStream = null;
    MagCard mag;
    ProgressDialog progressDialog;
    private int iRetVal;
    private final static int MESSAGE_BOX = 1;
    public static final int DEVICE_NOTCONNECTED = -100;
    private static final boolean D = true;// BluetoothConnect.D;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.magnet_card, container, false);

        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());

//        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
//                && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
        try {
            outputStream = BluetoothComm.mosOut;
            inputStream = BluetoothComm.misIn;
            mag = new MagCard(LoginActivity.setupInstance, outputStream, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.magnet_card);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage("Please swipe the card,\nWait a moment");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ReadMagcardAsyc magcard = new ReadMagcardAsyc();
                magcard.execute(0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
//        } else {
//            // do else part
//        }

        /*btn_magnet_card = view.findViewById(R.id.btn_magnet_card);
        btn_magnet_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                        && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                    ReadMagcardAsyc magcard = new ReadMagcardAsyc();
                    magcard.execute(0);
                } else {
                    dialogsUtil.alertDialog("Printer services required");
                }
            }
        });*/

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class ReadMagcardAsyc extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.magnet_card);
            progressDialog.setMessage("Please wait,\nReading Magnet card...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                mag.vReadMagCardData(10000);
                iRetVal = mag.iGetReturnCode();
                if (D) {
                    Log.d(TAG, "<<<<VALUE>>>>" + iRetVal);
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "exception...." + e);
                return MagCard.MAG_FAIL;
            }
            return iRetVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.e(TAG, "onPostExecute...Ireval....." + iRetVal);
            progressDialog.dismiss();
            if (iRetVal == DEVICE_NOTCONNECTED) {
                handler.obtainMessage(DEVICE_NOTCONNECTED, "Device not connected").sendToTarget();
            } else if (iRetVal == MagCard.MAG_TRACK1_READERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Track1 data read failed").sendToTarget();
            } else if (iRetVal == MagCard.MAG_TRACK2_READERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Track2 data read failed").sendToTarget();
            } else if (iRetVal == MagCard.MAG_SUCCESS) {
                radioBox();
            } else if (iRetVal == MagCard.MAG_FAIL) {
                handler.obtainMessage(MESSAGE_BOX, "MagCard Read Error").sendToTarget();
            } else if (iRetVal == MagCard.MAG_LRC_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "LRC Error").sendToTarget();
            } else if (iRetVal == MagCard.MAG_NO_DATA) {
                handler.obtainMessage(MESSAGE_BOX, "IMPROPER SWIPE").sendToTarget();
            } else if (iRetVal == MagCard.MAG_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Illegal Library").sendToTarget();
            } else if (iRetVal == MagCard.MAG_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "API not supported for demo version").sendToTarget();
            } else if (iRetVal == MagCard.MAG_TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Swipe Card TimeOut").sendToTarget();
            } else if (iRetVal == MagCard.MAG_INACTIVE_PERIPHERAL) {
                handler.obtainMessage(MESSAGE_BOX, "Peripheral is inactive").sendToTarget();
            } else if (iRetVal == MagCard.MAG_PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Passed incorrect parameter").sendToTarget();
            } else if (iRetVal == Printer.PR_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            }
            super.onPostExecute(result);
        }
    }

    private void radioBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View view1 = inflater.inflate(R.layout.dlg_mag_card_data_display, null);
        builder.setView(view1);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.amount_confirmation);
        final Dialog dialog = builder.create();
        dialog.show();

        final AppCompatTextView tv_magnet_card_name = view1.findViewById(R.id.tv_magnet_card_name);
        tv_magnet_card_name.setVisibility(View.GONE);
        final AppCompatTextView tv_magnet_card_no = view1.findViewById(R.id.tv_magnet_card_no);
        tv_magnet_card_no.setVisibility(View.GONE);
        final AppCompatTextView tv_magnet_card_secret_no = view1.findViewById(R.id.tv_magnet_card_secret_no);
        tv_magnet_card_secret_no.setVisibility(View.GONE);
        AppCompatButton btn_display_track1_data = view1.findViewById(R.id.btn_display_track1_data);
        AppCompatButton btn_display_track2_data = view1.findViewById(R.id.btn_display_track2_data);
        AppCompatButton btn_display_track3_data = view1.findViewById(R.id.btn_display_track3_data);
        AppCompatButton btn_dlg_cancel = view1.findViewById(R.id.btn_dlg_cancel);
        AppCompatButton btn_dlg_next = view1.findViewById(R.id.btn_dlg_next);

        btn_display_track1_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String magCardData = mag.sGetTrack1Data();
                if (magCardData != null && magCardData.length() > 0 && magCardData.contains("^")) {
                    tv_magnet_card_name.setVisibility(View.VISIBLE);
                    tv_magnet_card_no.setVisibility(View.VISIBLE);
                    tv_magnet_card_secret_no.setVisibility(View.VISIBLE);
                    String[] magCardDetails = magCardData.split("\\^");
                    tv_magnet_card_name.setText(magCardDetails[1]);
                    tv_magnet_card_no.setText(magCardDetails[0]);
                    tv_magnet_card_secret_no.setText(magCardDetails[2]);
                } else {
                    tv_magnet_card_name.setVisibility(View.VISIBLE);
                    tv_magnet_card_no.setVisibility(View.GONE);
                    tv_magnet_card_secret_no.setVisibility(View.GONE);
                    tv_magnet_card_name.setText(R.string.track1_data_not_available);
                }
            }
        });

        btn_display_track2_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String magCardData = mag.sGetTrack2Data();
                if (magCardData != null && magCardData.length() > 0 && magCardData.length() == 3) {
                    tv_magnet_card_name.setVisibility(View.VISIBLE);
                    tv_magnet_card_no.setVisibility(View.VISIBLE);
                    tv_magnet_card_secret_no.setVisibility(View.VISIBLE);
                    String[] magCardDetails = magCardData.split("\\^");
                    tv_magnet_card_name.setText(magCardDetails[1]);
                    tv_magnet_card_no.setText(magCardDetails[0]);
                    tv_magnet_card_secret_no.setText(magCardDetails[2]);
                } else {
                    tv_magnet_card_name.setVisibility(View.VISIBLE);
                    tv_magnet_card_no.setVisibility(View.GONE);
                    tv_magnet_card_secret_no.setVisibility(View.GONE);
                    tv_magnet_card_name.setText(R.string.track2_data_not_available);
                }
            }
        });

        btn_display_track3_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String magCardData = mag.sGetTrack3Data();
                if (magCardData != null && magCardData.length() > 0 && magCardData.length() == 3) {
                    tv_magnet_card_name.setVisibility(View.VISIBLE);
                    tv_magnet_card_no.setVisibility(View.VISIBLE);
                    tv_magnet_card_secret_no.setVisibility(View.VISIBLE);
                    String[] magCardDetails = magCardData.split("\\^");
                    tv_magnet_card_name.setText(magCardDetails[1]);
                    tv_magnet_card_no.setText(magCardDetails[0]);
                    tv_magnet_card_secret_no.setText(magCardDetails[2]);
                } else {
                    tv_magnet_card_name.setVisibility(View.VISIBLE);
                    tv_magnet_card_no.setVisibility(View.GONE);
                    tv_magnet_card_secret_no.setVisibility(View.GONE);
                    tv_magnet_card_name.setText(R.string.track3_data_not_available);
                }
            }
        });

        btn_dlg_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str1 = (String) msg.obj;
                    dialogsUtil.alertDialog(str1);
                    break;
                default:
                    break;
            }
        }
    };

}
