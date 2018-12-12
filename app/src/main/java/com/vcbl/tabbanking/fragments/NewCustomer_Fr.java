package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leopard.api.FPS;
import com.leopard.api.FpsConfig;
import com.leopard.api.HexString;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.Age;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.Name;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class NewCustomer_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "NewCustomer_Fr-->";
    View view;
    CardView cardView;
    LinearLayout ll_spouse_details;
    AppCompatEditText et_aadhaar_no, et_first_name, et_last_name, et_father_name, et_mother_name,
            et_age, et_spouse_first_name, et_spouse_last_name, et_spouse_aadhaar_no;
    AppCompatSpinner spinner_title, spinner_gender, spinner_marital_status;
    AppCompatTextView tv_date_of_birth, tv_spouse_details;
    AppCompatButton btn_get_aadhaar_details, btn_clear_details, btn_next_cust2;
    DbFunctions dbFunctions;
    String aadhaarNo, title, gender, maritalStatus,
            firstName, lastName, fatherName, motherName="", age, dob, dobCheck = "0", biometricData = "",
            spouseFirstName = "", spouseLastName = "", spouseAadhaarNo = "";
    private DialogsUtil dialogsUtil;
    private int titleId, genderId, maritalStatusId, mYear, mMonth, mDay;
    VerhoeffCheckDigit verhoeff;
    Storage storage;
    private boolean blVerifyfinger = false;
    private final static int MESSAGE_BOX = 1;
    private ProgressDialog progressDialog;
    private int iRetVal;
    public static final int DEVICE_NOTCONNECTED = -100;
    private byte[] brecentminituaedata = {};
    FpsConfig fpsconfig;
    FPS fps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        view = inflater.inflate(R.layout.new_customer, container, false);

        loadUiComponents();

        objInit();

        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            fpsObjInit();
        }

        //clearFields();

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

        EnrollmentData.getPersonalInfo().setBiometricData(biometricData);

        btn_get_aadhaar_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                        && "YES".equals(storage.getValue(Constants.BT_SERVICE))
                        && !blVerifyfinger) {
                    CaptureFingerAsyn captureFinger = new CaptureFingerAsyn();
                    captureFinger.execute(0);
                } else {
                    dialogsUtil.alertDialog("Printer services required");
                    EnrollmentData.getPersonalInfo().setBiometricData(biometricData);
                }
            }
        });

        et_spouse_aadhaar_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                spouseAadhaarNo = et_spouse_aadhaar_no.getText().toString().trim();
                if (spouseAadhaarNo.length() >= 1 && !verhoeff.isValid(spouseAadhaarNo)) {
                    et_spouse_aadhaar_no.requestFocus();
                    et_spouse_aadhaar_no.setError("Please enter valid aadhaar no.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 1. spinner title details
        spinner_title.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> titleList = dbFunctions.getTitleDetails();
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_title.setAdapter(adapter);

        // 2. spinner gender details
        spinner_gender.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> genderList = dbFunctions.getSpinnerList("GNM");
        ArrayAdapter<SpinnerList> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, genderList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter2);

        // 3. spinner marital status details
        spinner_marital_status.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> maritalStatusList = dbFunctions.getSpinnerList("MSM");
        ArrayAdapter<SpinnerList> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, maritalStatusList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_marital_status.setAdapter(adapter3);

        tv_date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat date_parse = new SimpleDateFormat("dd/MM/yyyy");
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                age = Utility.getAge(dayOfMonth, monthOfYear, year);
                                try {
                                    dob = date_format.format(date_parse.parse(date));
                                    String todaysDate = Utility.getTodaysDate();
                                    long days = Utility.daysCalculate(dob, todaysDate);
                                    //if (days > 6570) {
                                    tv_date_of_birth.setText(dob);
                                    dobCheck = "1";
                                    et_age.setEnabled(false);
                                    et_age.setClickable(false);
                                    et_age.setText(age);
                                    //if (Integer.parseInt(age) < 18) {
                                    storage.clearValue(Constants.CUSTOMER_AGE);
                                    storage.saveSecure(Constants.CUSTOMER_AGE, age);
                                    Log.i(TAG, "age-->" + age);
                                    // }
                                    //} else {
                                    // dialogsUtil.alertDialog("Please select DOB atleast person age\nshould be greater than 18 years");
                                    //}
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    tv_date_of_birth.setText(date);
                                    et_age.setEnabled(true);
                                    et_age.setClickable(true);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btn_next_cust2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View view1) {
                aadhaarNo = et_aadhaar_no.getText().toString().trim();
                firstName = et_first_name.getText().toString().trim();
                lastName = et_last_name.getText().toString().trim();
                fatherName = et_father_name.getText().toString().trim();
                motherName = et_mother_name.getText().toString().trim();
                Log.i(TAG, "motherName-->" + motherName);
                spouseFirstName = et_spouse_first_name.getText().toString().trim();
                spouseLastName = et_spouse_last_name.getText().toString().trim();
                spouseAadhaarNo = et_spouse_aadhaar_no.getText().toString().trim();
                age = et_age.getText().toString().trim();

                if (aadhaarNo.isEmpty()) {
                    et_aadhaar_no.setError("Please enter aadhaar no.");
                    et_aadhaar_no.requestFocus();
                } else if (aadhaarNo != null && !verhoeff.isValid(aadhaarNo)
                        && (aadhaarNo.length() != 12 || aadhaarNo.length() != 28)) {
                    et_aadhaar_no.requestFocus();
                    et_aadhaar_no.setError("Please enter valid aadhaar no.");
                } else if (titleId == 0) {
                    dialogsUtil.alertDialog("Please select title");
                } else if (firstName.isEmpty()) {
                    et_first_name.requestFocus();
                    et_first_name.setError("Please enter first name");
                } else if (lastName.isEmpty()) {
                    et_last_name.requestFocus();
                    et_last_name.setError("Please enter last name");
                } else if (fatherName.isEmpty()) {
                    et_father_name.requestFocus();
                    et_father_name.setError("Please enter father name");
                } else if (genderId == 0) {
                    dialogsUtil.alertDialog("Please select gender");
                } else if (maritalStatusId == 0) {
                    dialogsUtil.alertDialog("Select marital status");
                } else if (maritalStatusId == 2 && spouseFirstName.isEmpty()) {
                    et_spouse_first_name.setError("Please enter first name");
                    et_spouse_first_name.requestFocus();
                } else if (maritalStatusId == 2 && spouseLastName.isEmpty()) {
                    et_spouse_last_name.setError("Please enter last name");
                    et_spouse_last_name.requestFocus();
                } else if ("0".equals(dobCheck)) {
                    dialogsUtil.alertDialog("Please select date of birth");
                } else if (age.isEmpty()) {
                    et_age.setEnabled(true);
                    et_age.setClickable(true);
                    et_age.requestFocus();
                    et_age.setError("Please enter age");
                } else if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                        && "YES".equals(storage.getValue(Constants.BT_SERVICE))
                        && !blVerifyfinger) {
                    CaptureFingerAsyn captureFinger = new CaptureFingerAsyn();
                    captureFinger.execute(0);
                } else {
                    EnrollmentData.getIdentityDetails().setUid(aadhaarNo);
                    Name name = Name.getInstance();
                    name.setTitle(String.valueOf(titleId));
                    name.setFirstName(firstName);
                    name.setLastName(lastName);
                    EnrollmentData.getPersonalInfo().setCustomerName(name);
                    EnrollmentData.getPersonalInfo().setFatherName(fatherName);
                    EnrollmentData.getPersonalInfo().setMotherName(motherName);
                    Log.i(TAG, "motherName_again-->" + motherName);
                    EnrollmentData.getPersonalInfo().setGender(String.valueOf(genderId));
                    EnrollmentData.getPersonalInfo().setMaritalStatus(String.valueOf(maritalStatusId));
                    EnrollmentData.getSpouseInfo().setFirstName(spouseFirstName);
                    EnrollmentData.getSpouseInfo().setLastName(spouseLastName);
                    EnrollmentData.getSpouseInfo().setAadhaarNo(spouseAadhaarNo);
                    Age age1 = Age.getInstance();
                    age1.setAge(Integer.parseInt(age));
                    age1.setDob(dob);
                    EnrollmentData.getPersonalInfo().setAge(age1);
                    Fragment fragment = new AddressDetails_Fr();
                    assert getFragmentManager() != null;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
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

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
        verhoeff = new VerhoeffCheckDigit();
        storage = new Storage(getActivity());
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);

        et_aadhaar_no = view.findViewById(R.id.et_aadhaar_no);

        btn_get_aadhaar_details = view.findViewById(R.id.btn_get_aadhaar_details);

        spinner_title = view.findViewById(R.id.spinner_title);

        et_first_name = view.findViewById(R.id.et_first_name);
        et_last_name = view.findViewById(R.id.et_last_name);
        et_father_name = view.findViewById(R.id.et_father_name);
        et_mother_name = view.findViewById(R.id.et_mother_name);

        spinner_gender = view.findViewById(R.id.spinner_gender);
        spinner_marital_status = view.findViewById(R.id.spinner_marital_status);

        ll_spouse_details = view.findViewById(R.id.ll_spouse_details);
        et_spouse_first_name = view.findViewById(R.id.et_spouse_first_name);
        et_spouse_last_name = view.findViewById(R.id.et_spouse_last_name);
        et_spouse_aadhaar_no = view.findViewById(R.id.et_spouse_aadhaar_no);

        tv_date_of_birth = view.findViewById(R.id.tv_date_of_birth);
        tv_spouse_details = view.findViewById(R.id.tv_spouse_details);

        et_age = view.findViewById(R.id.et_age);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next_cust2 = view.findViewById(R.id.btn_next_cust2);

        tv_spouse_details.setVisibility(View.GONE);
        ll_spouse_details.setVisibility(View.GONE);

        et_age.setText("");
        et_age.setHint(R.string.age);
        et_age.setEnabled(false);
        et_age.setClickable(false);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);
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

    private void clearFields() {
        et_aadhaar_no.setText("");
        et_aadhaar_no.setHint(R.string.aadhaar_no_hint);

        blVerifyfinger = false;

        spinner_title.setSelection(0);

        et_first_name.setText("");
        et_first_name.setHint(R.string.first_name);

        et_last_name.setText("");
        et_last_name.setHint(R.string.last_name);

        et_father_name.setText("");
        et_father_name.setHint(R.string.father_name);

        et_mother_name.setText("");
        et_mother_name.setHint(R.string.mother_name);

        spinner_gender.setSelection(0);
        spinner_marital_status.setSelection(0);

        tv_spouse_details.setVisibility(View.GONE);
        ll_spouse_details.setVisibility(View.GONE);

        tv_date_of_birth.setText("");
        tv_date_of_birth.setHint(R.string.date_format);

        et_age.setText("");
        et_age.setHint(R.string.age);
        et_age.setEnabled(false);
        et_age.setClickable(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_title:
                titleId = adapterView.getSelectedItemPosition();
                title = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "titleId---> " + titleId);
                Log.i(TAG, "title---> " + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_gender:
                genderId = adapterView.getSelectedItemPosition();
                gender = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "genderId---> " + genderId);
                Log.i(TAG, "gender---> " + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_marital_status:
                maritalStatusId = adapterView.getSelectedItemPosition();
                maritalStatus = String.valueOf(adapterView.getSelectedItem());
                if (maritalStatusId == 2) {
                    ll_spouse_details.setVisibility(View.VISIBLE);
                    tv_spouse_details.setVisibility(View.VISIBLE);
                } else {
                    ll_spouse_details.setVisibility(View.GONE);
                    tv_spouse_details.setVisibility(View.GONE);
                    EnrollmentData.getSpouseInfo().setFirstName(spouseFirstName);
                    EnrollmentData.getSpouseInfo().setLastName(spouseLastName);
                    EnrollmentData.getSpouseInfo().setAadhaarNo(spouseAadhaarNo);
                }
                Log.i(TAG, "maritalStatusId---> " + maritalStatusId);
                Log.i(TAG, "maritalStatus---> " + String.valueOf(adapterView.getSelectedItem()));
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
                biometricData = HexString.bufferToHex(bMinutiaeData);
                EnrollmentData.getPersonalInfo().setBiometricData(biometricData);
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
                Toasty.success(getActivity(), "Capture finger success", Toast.LENGTH_SHORT).show();
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
                    //showDialog(str);
                    dialogsUtil.alertDialog(str);
                    break;
                default:
                    break;
            }
        }
    };
}
