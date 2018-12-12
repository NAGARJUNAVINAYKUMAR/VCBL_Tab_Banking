package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leopard.api.FPS;
import com.leopard.api.FpsConfig;
import com.leopard.api.HexString;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.activities.MainActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.TransactionBO;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class TxnAadhar_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TxnAadhar_Fr-->";
    View view;
    CardView cardView;
    LinearLayout ll_select_bank, ll_enter_aadhaar_no, ll_enter_amount, ll_beneficiary,
            ll_select_benef_bank, ll_benef_aadhaar_no, ll_conf_benef_aadhaar_no;
    AppCompatTextView tv_main_header, tv_select_bank, tv_aadhaar_no, tv_amount, tv_biometric_result,
            tv_select_benef_bank, tv_benef_aadhaar_no, tv_conf_benef_aadhaar_no, tv_account_details_response;
    AppCompatEditText et_aadhaar_no, et_amount, et_benef_aadhaar_no, et_conf_benef_aadhaar_no;
    AppCompatSpinner spinner_bank, spinner_benef_bank;
    AppCompatButton btn_clear_details, btn_get_details;

    private DialogsUtil dialogsUtil;
    VerhoeffCheckDigit verhoeff;
    Storage storage;
    DbFunctions dbFunctions;
    private boolean blVerifyfinger = false;
    private final static int MESSAGE_BOX = 1;
    private ProgressDialog progressDialog;
    private int iRetVal;
    public static final int DEVICE_NOTCONNECTED = -100;
    private byte[] brecentminituaedata = {};
    FpsConfig fpsconfig;
    FPS fps;
    String aadhaarNo, enteredAmount, biometricData, rrn, stan, bankName, benefBankName;
    int bankId, benefBankId;
    AlertDialog dialog;
    TxnModel txnModel;
    TransactionBO transactionBO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.txn_aadhaar, container, false);

        setTitle(R.string.aadhaar_transactions);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        loadUiComponents();

        clearFields();

        objInit();

        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            fpsObjInit();
        }

        spinner_bank.setOnItemSelectedListener(this);
        spinner_benef_bank.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> bankNames;
        bankNames = dbFunctions.getBankNames();
        assert bankNames != null;
        ArrayAdapter<SpinnerList> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, bankNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_bank.setAdapter(arrayAdapter);
        spinner_benef_bank.setAdapter(arrayAdapter);

        if ("DEPOSIT".equals(AadhaarTxns_Fr.calledFrom)) {
            setSubTitle(R.string.deposit);
            tv_main_header.setText(R.string.deposit);
        } else if ("WITHDRAWAL".equals(AadhaarTxns_Fr.calledFrom)) {
            setSubTitle(R.string.withdrawal);
            tv_main_header.setText(R.string.withdrawal);
        } else if ("FUND_TRANSFER".equals(AadhaarTxns_Fr.calledFrom)) {
            setSubTitle(R.string.fund_transfer);
            tv_main_header.setText(R.string.fund_transfer);
            ll_beneficiary.setVisibility(View.VISIBLE);
        } else {
            setSubTitle(R.string.balance_enquiry);
            tv_main_header.setText(R.string.balance_enquiry);
        }

        et_aadhaar_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                aadhaarNo = et_aadhaar_no.getText().toString().trim();
                if (aadhaarNo.length() >= 1 && !verhoeff.isValid(aadhaarNo)) {
                    et_aadhaar_no.requestFocus();
                    et_aadhaar_no.setError("Please enter valid aadhaar no.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_get_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadhaarNo = et_aadhaar_no.getText().toString().trim();
                enteredAmount = et_amount.getText().toString().trim();

                if (bankId == 0) {
                    dialogsUtil.alertDialog("Please select bank name");
                } else if (aadhaarNo.isEmpty()) {
                    et_aadhaar_no.setError("Please enter aadhaar no.");
                    et_aadhaar_no.requestFocus();
                } else if (!verhoeff.isValid(aadhaarNo)) {
                    et_aadhaar_no.setError("Please enter valid aadhaar no.");
                    et_aadhaar_no.requestFocus();
                } else if (enteredAmount.isEmpty()) {
                    et_amount.setError("Please enter amount");
                /*} else if (!blVerifyfinger) {
                    CaptureFingerAsyn captureFinger = new CaptureFingerAsyn();
                    captureFinger.execute(0);*/
                } else if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                    if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                        if ((!blVerifyfinger)) {
                            CaptureFingerAsyn captureFinger = new CaptureFingerAsyn();
                            captureFinger.execute(0);
                        } else {
                            confirmDialog();
                        }
                    } else {
                        dialogsUtil.alertDialog("Bluetooth services required");
                    }
                } else {
                    confirmDialog();
                }
            }
        });

        btn_clear_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        return view;
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.amount_confirmation);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage("Please confirm entered amount is "
                + getResources().getString(R.string.Rs) + enteredAmount + " ?");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                prepareModel();
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
    }

    private void prepareModel() {
        txnModel = new TxnModel();
        stan = Utility.padLeft(getStan(), 6, "0");
        rrn = Utility.getYear().substring(3) + Utility.padLeft(Utility.getDayofYear(), 3, "0") +
                Utility.padLeft(Utility.getHours(), 2, "0") + stan;
        txnModel.setRrn(rrn);
        txnModel.setStan(stan);
        txnModel.setTxndate(Utility.getCurrentTimeStamp());
        txnModel.setMicroatmid(String.valueOf(GlobalModel.microatmid));
        txnModel.setBcid(GlobalModel.bcid);
        txnModel.setBranch_id(GlobalModel.branchid);
        txnModel.setTxnserviceid(Constants.aadhaarTxnsTxnServiceID);
        txnModel.setTxnsubserviceid(Constants.txnsubserviceid);
        txnModel.setProcessingcode(Constants.DepositProcCode);
        if ("DEPOSIT".equals(AadhaarTxns_Fr.calledFrom)) {
            txnModel.setTxnType("cr");
        } else if ("WITHDRAWAL".equals(AadhaarTxns_Fr.calledFrom)) {
            txnModel.setTxnType("dr");
        } else if ("FUND_TRANSFER".equals(AadhaarTxns_Fr.calledFrom)) {
            txnModel.setTxnType("ft");
        } else {
            txnModel.setTxnType("nf");
            //dr-withdrawal, // ft-fund transfer, beneficiary acc no, bal enq-nf
        }
        txnModel.setAccNo(aadhaarNo);
        txnModel.setAmount(enteredAmount);
        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
            txnModel.setCustName(biometricData);
        } else {
            txnModel.setCustName("");
        }
        txnModel.setProductcode("");
        txnModel.setAvlbalance("");
        txnModel.setLedgbalance("");

//        boolean inserted = dbFunctions.insertTxnInDatabase(txnModel);
//        if (!inserted) {
//            Log.i(TAG,  "Unable to insert in DB");
//        }

        transactionBO = new TransactionBO(getActivity());
        transactionBO.transactionRequest(txnModel);
        transactionBO.setOnTaskFinishedEvent(new TransactionBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished() {
                if (transactionBO.txnResponseModel.getStatus() == Masters.Status.SUCCESS) {
                    Toasty.success(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }
        });
    }

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

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        tv_main_header = view.findViewById(R.id.tv_main_header);

        ll_select_bank = view.findViewById(R.id.ll_select_bank);
        tv_select_bank = view.findViewById(R.id.tv_select_bank);
        spinner_bank = view.findViewById(R.id.spinner_bank);

        ll_enter_aadhaar_no = view.findViewById(R.id.ll_enter_aadhaar_no);
        tv_aadhaar_no = view.findViewById(R.id.tv_aadhaar_no);
        et_aadhaar_no = view.findViewById(R.id.et_aadhaar_no);

        ll_enter_amount = view.findViewById(R.id.ll_enter_amount);
        tv_amount = view.findViewById(R.id.tv_amount);
        et_amount = view.findViewById(R.id.et_amount);

        // layout visible gone
        ll_beneficiary = view.findViewById(R.id.ll_beneficiary);

        ll_select_benef_bank = view.findViewById(R.id.ll_select_benef_bank);
        tv_select_benef_bank = view.findViewById(R.id.tv_select_benef_bank);
        spinner_benef_bank = view.findViewById(R.id.spinner_benef_bank);

        ll_benef_aadhaar_no = view.findViewById(R.id.ll_benef_aadhaar_no);
        tv_benef_aadhaar_no = view.findViewById(R.id.tv_benef_aadhaar_no);
        et_benef_aadhaar_no = view.findViewById(R.id.et_benef_aadhaar_no);

        ll_conf_benef_aadhaar_no = view.findViewById(R.id.ll_conf_benef_aadhaar_no);
        tv_conf_benef_aadhaar_no = view.findViewById(R.id.tv_conf_benef_aadhaar_no);
        et_conf_benef_aadhaar_no = view.findViewById(R.id.et_conf_benef_aadhaar_no);

        //tv_biometric_result = view.findViewById(R.id.tv_biometric_result);

        tv_account_details_response = view.findViewById(R.id.tv_account_details_response);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_get_details = view.findViewById(R.id.btn_get_details);
    }

    private void clearFields() {
        spinner_bank.setSelection(0);

        et_aadhaar_no.setText("");
        et_aadhaar_no.setHint(R.string.enter_aadhaar_no);

        et_amount.setText("");
        et_amount.setHint(R.string.enter_amount);

        ll_beneficiary.setVisibility(View.GONE);

        //tv_biometric_result.setVisibility(View.GONE);
        //tv_biometric_result.setText("");

        tv_account_details_response.setVisibility(View.GONE);

        blVerifyfinger = false;
    }

    private void objInit() {
        dialogsUtil = new DialogsUtil(getActivity());
        verhoeff = new VerhoeffCheckDigit();
        storage = new Storage(getActivity());
        dbFunctions = DbFunctions.getInstance(getActivity());
    }

    private void fpsObjInit() {
        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
            try {
                OutputStream outSt = BluetoothComm.mosOut;
                InputStream inSt = BluetoothComm.misIn;
                if (outSt != null && inSt != null) {
                    fps = new FPS(LoginActivity.setupInstance, outSt, inSt);
                } else {
                    dialogsUtil.alertDialog("Leopard library not activated");
                }
            } catch (Exception e) {
                e.printStackTrace();
                //dialogsUtil.alertDialog("Leopard doesn't activated");
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_bank:
                bankId = parent.getSelectedItemPosition();
                bankName = String.valueOf(parent.getSelectedItem());
                Log.i(TAG, "bankId--->" + parent.getSelectedItemPosition());
                Log.i(TAG, "bankName--->" + String.valueOf(parent.getSelectedItem()));
                break;
            case R.id.spinner_benef_bank:
                benefBankId = parent.getSelectedItemPosition();
                benefBankName = String.valueOf(parent.getSelectedItem());
                Log.i(TAG, "benefBankId---> " + benefBankId);
                Log.i(TAG, "benefBankName---> " + String.valueOf(parent.getSelectedItem()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    public class CaptureFingerAsyn extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.capture_finger);
            progressDialog.setMessage("Place your finger on Biometric,\nWait a moment...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                brecentminituaedata = new byte[3500];
                fpsconfig = new FpsConfig(0, (byte) 0x0F);
                brecentminituaedata = fps.bFpsCaptureMinutiae(fpsconfig);
                iRetVal = fps.iGetReturnCode();
                byte[] bMinutiaeData = fps.bGetMinutiaeData();
                biometricData = HexString.bufferToHex(bMinutiaeData);
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (iRetVal == DEVICE_NOTCONNECTED) {
                blVerifyfinger = false;
                Toasty.warning(Objects.requireNonNull(getActivity()), "Device not connected", Toast.LENGTH_LONG).show();
            } else if (iRetVal == FPS.SUCCESS) {
                blVerifyfinger = true;
                Toasty.success(getActivity(), "Finger Captured", Toast.LENGTH_SHORT).show();
                //prepareModel();
            } else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
                blVerifyfinger = false;
                handler.obtainMessage(MESSAGE_BOX, "Peripheral is inactive").sendToTarget();
            } else if (iRetVal == FPS.TIME_OUT) {
                blVerifyfinger = false;
                handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
            } else if (iRetVal == FPS.FAILURE) {
                blVerifyfinger = false;
                handler.obtainMessage(MESSAGE_BOX, "Capture finger failed").sendToTarget();
            } else if (iRetVal == FPS.PARAMETER_ERROR) {
                blVerifyfinger = false;
                handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
            } else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
                blVerifyfinger = false;
                handler.obtainMessage(MESSAGE_BOX, "Connected device is not license authenticated.").sendToTarget();
            } else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
                blVerifyfinger = false;
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            } else {
                handler.obtainMessage(MESSAGE_BOX, "Please check whether bluetooth device is on or off" +
                        "\nif off then make it on and connect").sendToTarget();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    dialogsUtil.alertDialog(str);
                    break;
                default:
                    break;
            }
        }
    };

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }
}
