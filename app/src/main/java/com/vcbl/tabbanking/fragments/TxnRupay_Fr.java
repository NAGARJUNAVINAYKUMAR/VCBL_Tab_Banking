package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leopard.api.MagCard;
import com.leopard.api.Printer;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.TransactionBO;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class TxnRupay_Fr extends Fragment {

    private static final String TAG = "TxnRupay_Fr-->";
    View view;
    CardView cardView;
    AppCompatTextView tv_header, tv_account_name, tv_card_no, tv_serial_no, tv_account_details_response;
    LinearLayout ll_account_name, ll_card_no, ll_serial_no, ll_enter_amount, ll_pin_no;
    AppCompatEditText et_enter_amount, et_pin_no;
    AppCompatButton btn_back, btn_submit;

    Storage storage;
    DialogsUtil dialogsUtil;
    DbFunctions dbFunctions;

    OutputStream outputStream = null;
    InputStream inputStream = null;
    MagCard mag;
    ProgressDialog progressDialog;
    private int iRetVal;
    private final static int MESSAGE_BOX = 1;
    public static final int DEVICE_NOTCONNECTED = -100;
    private static final boolean D = true;

    String rrn, stan, custName, cardNo, serialNo, amount, pinNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rupay_txns, container, false);

        setTitle(R.string.rupay_card);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        initViews();

        initObj();

        if ("DEPOSIT".equals(MagnetCard_Fr.calledFrom)) {
            tv_header.setText(R.string.deposit);
            setSubTitle(R.string.deposit);
        } else if ("WITHDRAWAL".equals(MagnetCard_Fr.calledFrom)) {
            tv_header.setText(R.string.withdrawal);
            setSubTitle(R.string.withdrawal);
        } else if ("FUND_TRANSFER".equals(MagnetCard_Fr.calledFrom)) {
            tv_header.setText(R.string.fund_transfer);
            setSubTitle(R.string.fund_transfer);
        } else {
            tv_header.setText(R.string.balance_enquiry);
            setSubTitle(R.string.balance_enquiry);
        }

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
                Fragment fragment;
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragment = new RupayCard_Fr();
                ft.replace(R.id.content_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = et_enter_amount.getText().toString().trim();
                pinNo = et_pin_no.getText().toString().trim();
                if (amount.isEmpty()) {
                    et_enter_amount.setError("Please enter amount");
                } else if (Integer.parseInt(amount) < 1) {
                    et_enter_amount.requestFocus();
                    et_enter_amount.setError("Enter valid amount");
                } else if (pinNo.isEmpty()) {
                    et_pin_no.setError("Enter card pin no.");
                } else {
                    prepareModel();
                }
            }
        });

        return view;
    }

    private void prepareModel() {
        TxnModel txnModel = new TxnModel();
        txnModel.setTxndate(Utility.getCurrentTimeStamp());
        txnModel.setMicroatmid(String.valueOf(GlobalModel.microatmid));
        txnModel.setBcid(GlobalModel.bcid);
        txnModel.setBranch_id(GlobalModel.branchid);
        txnModel.setTxnserviceid(Constants.txnserviceid);
        txnModel.setTxnsubserviceid(Constants.rupayCardTxnServiceID);
        txnModel.setProcessingcode(Constants.DepositProcCode);
        stan = Utility.padLeft(getStan(), 6, "0");
        rrn = Utility.getYear().substring(3)
                + Utility.padLeft(Utility.getDayofYear(), 3, "0")
                + Utility.padLeft(Utility.getHours(), 2, "0")
                + stan;
        txnModel.setRrn(rrn);
        txnModel.setStan(stan);
        txnModel.setCustName(custName);
        txnModel.setAccNo(cardNo);
        txnModel.setProductcode("");
        txnModel.setAmount(amount);
        txnModel.setPinNo(pinNo);
        txnModel.setAvlbalance("");
        txnModel.setLedgbalance("");
        txnModel.setTxnType("dr");
        final TransactionBO transactionBO = new TransactionBO(getActivity());
        transactionBO.transactionRequest(txnModel);
        transactionBO.setOnTaskFinishedEvent(new TransactionBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished() {
                if (transactionBO.txnResponseModel.getStatus() == Masters.Status.FAILURE) {
                    Toasty.error(getActivity(), "Something went wrong !", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.success(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initObj() {
        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
        dbFunctions = DbFunctions.getInstance(getActivity());
    }

    private void initViews() {
        cardView = view.findViewById(R.id.cardView);
        cardView.setVisibility(View.GONE);

        tv_header = view.findViewById(R.id.tv_header);

        ll_account_name = view.findViewById(R.id.ll_account_name);
        ll_card_no = view.findViewById(R.id.ll_card_no);
        ll_serial_no = view.findViewById(R.id.ll_serial_no);
        ll_enter_amount = view.findViewById(R.id.ll_enter_amount);
        ll_pin_no = view.findViewById(R.id.ll_pin_no);

        tv_account_name = view.findViewById(R.id.tv_account_name);
        tv_card_no = view.findViewById(R.id.tv_card_no);
        tv_serial_no = view.findViewById(R.id.tv_serial_no);
        tv_account_details_response = view.findViewById(R.id.tv_account_details_response);
        tv_account_details_response.setVisibility(View.GONE);

        et_enter_amount = view.findViewById(R.id.et_enter_amount);
        et_pin_no = view.findViewById(R.id.et_pin_no);

        btn_back = view.findViewById(R.id.btn_back);
        btn_submit = view.findViewById(R.id.btn_submit);
    }

    @SuppressLint("StaticFieldLeak")
    public class ReadMagcardAsyc extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.magnet_card);
            progressDialog.setMessage("Please wait,\nReading card...");
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
                //radioBox();
                String magCardData = mag.sGetTrack1Data();
                if (magCardData != null && magCardData.length() > 0 && magCardData.contains("^")) {
                    cardView.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
                    cardView.setAnimation(animation);
                    et_enter_amount.requestFocus();
                    String[] magCardDetails = magCardData.split("\\^");
                    custName = magCardDetails[1];
                    tv_account_name.setText(custName);
                    cardNo = magCardDetails[0];
                    String subString = "XXXXXXXXXXXX" + (magCardDetails[0]).substring(12);
                    tv_card_no.setText(subString);
                    tv_serial_no.setText(magCardDetails[2]);
                } else {
                    tv_account_details_response.setVisibility(View.VISIBLE);
                    tv_account_details_response.setText(R.string.track1_data_not_available);
                }

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

    private String getStan() {
        String stan = dbFunctions.getStan();
        if (stan.equals("0") || stan.equals("")) {
            stan = String.valueOf(Integer.parseInt(stan) + 1);
            dbFunctions.updateStan(stan);
        } else {
            stan = String.valueOf(Integer.parseInt(stan) + 1);
            dbFunctions.updateStan(stan);
        }
        return stan;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }

}
