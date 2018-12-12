package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.leopard.api.FPS;
import com.leopard.api.FpsConfig;
import com.leopard.api.HexString;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.adapters.SpinnerAdapter;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.BCEnrollmentBO;
import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.CryptoHelper;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BCEnrollment_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "BCEnrollment_Fr";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header;
    AppCompatEditText et_first_name, et_last_name, et_password, et_conf_pass,
            et_employee_id, et_mobile_no, et_email_id;
    AppCompatSpinner spinner_branch_name, spinner_title;
    AppCompatButton btn_submit, btn_clear_details;

    String first_name, last_name, password, conf_pass,
            employee_id, mobile_no, email_id, biometric_data;
    int branchid, titleid;
    String branch, title;

    private ProgressDialog progressDialog;
    private byte[] brecentminituaedata = {};
    FpsConfig fpsconfig;
    FPS fps;
    private boolean blVerifyfinger = false;
    private final static int MESSAGE_BOX = 1;
    private int iRetVal;
    public static final int DEVICE_NOTCONNECTED = -100;
    DbFunctions dbFunctions;
    BCEnrollmentModel enrollResp;
    Storage storage;
    Bundle getData;
    DialogsUtil dialogsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        view = inflater.inflate(R.layout.bc_enrollment, container, false);
        getActivity().setTitle(R.string.bc_enrollment);

        loadUiComponents();

        objInit();

        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            fpsObjInit();
        }

        //clearFields();

        // 1. title details
        spinner_title.setOnItemSelectedListener(this);
        dbFunctions = DbFunctions.getInstance(getActivity());
        ArrayList<SpinnerList> titleList = dbFunctions.getTitleDetails();
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_title.setAdapter(adapter);

        // 2. branch names details
        spinner_branch_name.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> branchList = dbFunctions.getBranchDetails();
        ArrayAdapter<SpinnerList> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, branchList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch_name.setAdapter(adapter1);

        if ("STAFF_UPDATE".equals(Staff_Fr.calledFrom)) {
            if (getData != null) {
                tv_main_header.setText(R.string.staff_update);
                int title, branchid;
                if (!"".equals(getData.getString("getTitle"))) {
                    title = Integer.parseInt(getData.getString("getTitle"));
                    Log.i(TAG, "getTitle--> " + getData.getString("getTitle"));
                    spinner_title.setSelection(((ArrayAdapter<SpinnerList>) spinner_title.getAdapter()).getPosition(titleList.get(title)));
                } else {
                    spinner_title.setSelection(0);
                }
                et_first_name.setText(getData.getString("getFname"));
                et_last_name.setText(getData.getString("getLname"));
                if (!"".equals(getData.getString("getBranch_id"))) {
                    branchid = Integer.parseInt(getData.getString("getBranch_id"));
                    spinner_branch_name.setSelection(((ArrayAdapter<SpinnerList>) spinner_branch_name.getAdapter()).getPosition(branchList.get(branchid)));
                    spinner_branch_name.setEnabled(false);
                    spinner_branch_name.setClickable(false);
                } else {
                    spinner_branch_name.setSelection(0);
                }
                et_employee_id.setText(getData.getString("getEmpid"));
                et_employee_id.setEnabled(false);
                et_employee_id.setClickable(false);
                et_mobile_no.setText(getData.getString("getPhone"));
                et_email_id.setText(getData.getString("getEmail"));
            }
        } else {
            clearFields();
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                        && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                    fpsObjInit();
                }
                first_name = et_first_name.getText().toString().trim();
                last_name = et_last_name.getText().toString().trim();
                password = et_password.getText().toString().trim();
                conf_pass = et_conf_pass.getText().toString().trim();
                employee_id = et_employee_id.getText().toString().trim();
                mobile_no = et_mobile_no.getText().toString().trim();
                email_id = et_email_id.getText().toString().trim();
                if (titleid == 0) {
                    dialogsUtil.alertDialog("Please select title");
                } else if (first_name.isEmpty()) {
                    et_first_name.setError("Enter First Name");
                    et_first_name.requestFocus();
                } else if (last_name.isEmpty()) {
                    et_last_name.setError("Enter Last Name");
                    et_last_name.requestFocus();
                } else if (password.isEmpty()) {
                    et_password.setError("Enter Password");
                    et_password.requestFocus();
                } else if (password.length() < 4) {
                    et_password.setError("PIN length should be min 4 digits");
                    et_password.requestFocus();
                } else if (conf_pass.isEmpty()) {
                    et_conf_pass.setError("Re-Enter Password");
                    et_conf_pass.requestFocus();
                } else if (!conf_pass.equals(password)) {
                    et_conf_pass.setError("Password Doesn't Match");
                    et_conf_pass.requestFocus();
                } else if (branchid == 0) {
                    dialogsUtil.alertDialog("Please select branch");
                } else if (employee_id.isEmpty()) {
                    et_employee_id.setError("Enter Employee ID");
                    et_employee_id.requestFocus();
                } else if (mobile_no.isEmpty()) {
                    et_mobile_no.setError("Enter Mobile No.");
                    et_mobile_no.requestFocus();
                } else if (mobile_no.length() < 10) {
                    et_mobile_no.setError("Enter Proper Mobile No.");
                    et_mobile_no.requestFocus();
                } else if (email_id.isEmpty()) {
                    et_email_id.setError("Enter Email ID豈");
                    et_email_id.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_id).matches()) {
                    et_email_id.setError("Enter Valid Email ID");
                    et_email_id.requestFocus();
                } else if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                        && "YES".equals(storage.getValue(Constants.BT_SERVICE))
                        && !blVerifyfinger) {
                    CaptureFingerAsyn captureFinger = new CaptureFingerAsyn();
                    captureFinger.execute(0);
                } else {
                    prepareModel();
                }
            }
        });

        btn_clear_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });
        return view;
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        tv_main_header = view.findViewById(R.id.tv_main_header);

        spinner_title = view.findViewById(R.id.spinner_title);

        et_first_name = view.findViewById(R.id.et_first_name);
        et_last_name = view.findViewById(R.id.et_last_name);
        et_password = view.findViewById(R.id.et_password);
        et_conf_pass = view.findViewById(R.id.et_conf_pass);

        spinner_branch_name = view.findViewById(R.id.spinner_branch_name);

        et_employee_id = view.findViewById(R.id.et_employee_id);
        et_mobile_no = view.findViewById(R.id.et_mobile_no);
        et_email_id = view.findViewById(R.id.et_email_id);

        btn_submit = view.findViewById(R.id.btn_submit);
        btn_clear_details = view.findViewById(R.id.btn_clear_details);
    }

    private void clearFields() {
        spinner_title.setSelection(0);

        et_first_name.setText("");
        et_first_name.setHint(R.string.first_name);

        et_last_name.setText("");
        et_last_name.setHint(R.string.last_name);

        et_password.setText("");
        et_password.setHint(R.string.password);

        et_conf_pass.setText("");
        et_conf_pass.setHint(R.string.conf_pass);

        spinner_branch_name.setSelection(0);

        et_employee_id.setText("");
        et_employee_id.setHint(R.string.employee_id);

        et_mobile_no.setText("");
        et_mobile_no.setHint(R.string.mobile_no);

        et_email_id.setText("");
        et_email_id.setHint(R.string.email_id);

        blVerifyfinger = false;
    }

    private void objInit() {
        storage = new Storage(getActivity());

        dialogsUtil = new DialogsUtil(getActivity());

        getData = this.getArguments();
    }

    private void fpsObjInit() {
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

    private void prepareModel() {
        BCEnrollmentBO bcEnrollmentBO = new BCEnrollmentBO(getActivity());
        final BCEnrollmentModel bcModel = new BCEnrollmentModel();
        bcModel.setTitle(String.valueOf(titleid));
        bcModel.setFname(first_name);
        bcModel.setLname(last_name);
        try {
            bcModel.setPassword(CryptoHelper.encrypt(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        bcModel.setBranch_id(branchid);
        bcModel.setEmpid(employee_id);
        bcModel.setMobile(mobile_no);
        bcModel.setEmail(email_id);
        if ("STAFF_UPDATE".equals(Staff_Fr.calledFrom)) {
            bcModel.setBc_id(Integer.parseInt(getData.getString("getBc_id")));
        } else {
            bcModel.setBc_id(0);
        }
        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            bcModel.setFps_data(biometric_data);
        } else {
            bcModel.setFps_data("");
        }
        bcEnrollmentBO.bcEnrollRequest(bcModel);
        bcEnrollmentBO.setOnTaskFinishedEvent(new BCEnrollmentBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(BCEnrollmentModel response) {
                enrollResp = response;
                if (enrollResp.getMicroAtmId() != null) {
                    bcModel.setMicroAtmId(enrollResp.getMicroAtmId());
                    bcModel.setBc_id(enrollResp.getBc_id());
                    if ("STAFF_UPDATE".equals(Staff_Fr.calledFrom)) {
                        boolean updated = dbFunctions.updateBCDetails(bcModel);
                        if (!updated) {
                            Toasty.warning(getActivity(), "Unable to update in DB", Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.success(getActivity(), "BC updated successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                            getActivity().finish();
                        }
                    } else {
                        boolean inserted = dbFunctions.insertBCDetails(bcModel);
                        if (!inserted) {
                            Toasty.warning(getActivity(), "Unable to insert in DB", Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.success(getActivity(), "BC enrolled successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                            getActivity().finish();
                        }
                    }
                } else {
                    blVerifyfinger = false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(R.string.bc_enrollment);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_branch_name:
                branchid = adapterView.getSelectedItemPosition();
                Log.i(TAG, "branchid--> " + branchid);
                break;
            case R.id.spinner_title:
                titleid = adapterView.getSelectedItemPosition();
                Log.i(TAG, "titleid--> " + titleid);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                biometric_data = HexString.bufferToHex(bMinutiaeData);
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
				Toasty.success(getActivity(), "Finger capture success", Toast.LENGTH_SHORT).show();
                blVerifyfinger = true;
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

    public void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    }
}
