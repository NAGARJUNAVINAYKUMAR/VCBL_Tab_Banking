package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.Address;
import com.vcbl.tabbanking.models.Age;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ecosoft2 on 21-Mar-18.
 */

public class NomineeGuardianDetails_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "NomineeGuardianDetails_";
    View view;
    AppCompatTextView tv_main_header, tv_nom_guard_dob;
    AppCompatSpinner spinner_nom_guard_title, spinner_nom_guard_relation, spinner_nom_guard_state, spinner_nom_guard_city;
    AppCompatEditText et_nom_guard_name, et_nom_guard_age, et_nom_guard_address, et_nom_guard_pin_code,
            et_nom_guard_aadhaar_no, et_nom_guard_contact_no;
    AppCompatButton btn_clear_details, btn_next;
    DialogsUtil dialogsUtil;
    DbFunctions dbFunctions;
    int mYear, mMonth, mDay, nomGuardTitleId,nomGuardRelationId, nomGuardStateId, nomGuardCityId;
    private String nomGuardName, nomGuardDOB, nomGuardAge, nomGuardAddress, nomGuardPinCode, nomGuardAadhaarNo,
            nomGuardMobileNo, nomGuardTitle, nomGuardRelation, nomGuardState, nomGuardCity, nomGuardDobCheck = "0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nominee_guardian_details, container, false);
        getActivity().setTitle(R.string.nominee_guardian_details);

        loadUiComponents();

        clearFields();

        objInit();

        // 1. spinner nominee title details
        spinner_nom_guard_title.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> titleList = dbFunctions.getTitleDetails();
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nom_guard_title.setAdapter(adapter);

        // 2. spinner relation details
        spinner_nom_guard_relation.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> relationList = dbFunctions.getSpinnerList("RSM");
        ArrayAdapter<SpinnerList> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, relationList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nom_guard_relation.setAdapter(adapter2);

        // 3. spinner states details
        spinner_nom_guard_state.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> statesList = dbFunctions.getStatesList();
        ArrayAdapter<SpinnerList> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, statesList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nom_guard_state.setAdapter(adapter3);

        // 4. spinner cities details
        spinner_nom_guard_city.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(spinner_nom_guard_state.getSelectedItem().toString());
        ArrayAdapter<SpinnerList> adapter4 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nom_guard_city.setAdapter(adapter4);

        tv_nom_guard_dob.setOnClickListener(new View.OnClickListener() {
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
                                nomGuardAge = Utility.getAge(dayOfMonth, monthOfYear, year);
                                Log.i(TAG, "nomGuardAge-->" + nomGuardAge);
                                try {
                                    nomGuardDOB = date_format.format(date_parse.parse(date));
                                    String todaysDate = Utility.getTodaysDate();
                                    long days = Utility.daysCalculate(nomGuardDOB, todaysDate);
                                    if (days > 6570) {
                                        tv_nom_guard_dob.setText(nomGuardDOB);
                                        et_nom_guard_age.setText(nomGuardAge);
                                        et_nom_guard_age.setEnabled(false);
                                        et_nom_guard_age.setClickable(false);
                                    } else {
                                        dialogsUtil.alertDialog("Please select DOB atleast person age\nshould be greater than 18 years");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    tv_nom_guard_dob.setText(date);
                                    et_nom_guard_age.setEnabled(true);
                                    et_nom_guard_age.setClickable(true);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                nomGuardDobCheck = "1";
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nomGuardName = et_nom_guard_name.getText().toString().trim();
                nomGuardDOB = tv_nom_guard_dob.getText().toString().trim();
                nomGuardAge = et_nom_guard_age.getText().toString().trim();
                nomGuardAddress = et_nom_guard_address.getText().toString().trim();
                nomGuardPinCode = et_nom_guard_pin_code.getText().toString().trim();
                nomGuardAadhaarNo = et_nom_guard_aadhaar_no.getText().toString().trim();
                nomGuardMobileNo = et_nom_guard_contact_no.getText().toString().trim();

                if (nomGuardTitleId == 0) {
                    dialogsUtil.alertDialog("Please select title");
                } else if (nomGuardName.isEmpty()) {
                    et_nom_guard_name.setError("Please enter name");
                    et_nom_guard_name.requestFocus();
                } else if (nomGuardRelationId == 0) {
                    dialogsUtil.alertDialog("Please select relation");
                } else if ("0".equals(nomGuardDobCheck)) {
                    dialogsUtil.alertDialog("Please select dob");
                } else if (nomGuardAge.isEmpty()) {
                    et_nom_guard_age.setError("Please enter age");
                    et_nom_guard_age.requestFocus();
                } else if (nomGuardStateId == 0) {
                    dialogsUtil.alertDialog("Please select state");
                } else if (nomGuardCityId == 0) {
                    dialogsUtil.alertDialog("Please select city");
                } else if (nomGuardPinCode.isEmpty()) {
                    et_nom_guard_pin_code.setError("Enter pin code");
                    et_nom_guard_pin_code.requestFocus();
                } else if (nomGuardAadhaarNo.isEmpty()) {
                    et_nom_guard_aadhaar_no.setError("Enter aadhaar no.");
                    et_nom_guard_aadhaar_no.requestFocus();
                } else {
                    EnrollmentData.getGuardianDetails().setName(nomGuardTitle + " " + nomGuardName);
                    EnrollmentData.getNomineeDetails().setRelation(nomGuardRelation);
                    Age age = Age.getInstance();
                    age.setDob(nomGuardDOB);
                    age.setAge(Integer.parseInt(nomGuardAge));
                    EnrollmentData.getNomineeDetails().setAge(age);
                    Address address = Address.getInstance();
                    address.setAddress1(nomGuardAddress);
                    address.setAddress2(nomGuardAddress);
                    address.setState(nomGuardState);
                    address.setCity(nomGuardCity);
                    address.setPinCode(nomGuardPinCode);
                    address.setAadhaarNo(nomGuardAadhaarNo);
                    address.setMobileNo(nomGuardMobileNo);
                    EnrollmentData.getNomineeDetails().setAddress(address);
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

    private void prepareModel() {

    }

    private void loadUiComponents() {
        tv_main_header = view.findViewById(R.id.tv_main_header);

        spinner_nom_guard_title = view.findViewById(R.id.spinner_nom_guard_title);

        et_nom_guard_name = view.findViewById(R.id.et_nom_guard_name);

        spinner_nom_guard_relation = view.findViewById(R.id.spinner_nom_guard_relation);

        tv_nom_guard_dob = view.findViewById(R.id.tv_nom_guard_dob);

        et_nom_guard_age = view.findViewById(R.id.et_nom_guard_age);
        et_nom_guard_address = view.findViewById(R.id.et_nom_guard_address);

        spinner_nom_guard_state = view.findViewById(R.id.spinner_nom_guard_state);
        spinner_nom_guard_city = view.findViewById(R.id.spinner_nom_guard_city);

        et_nom_guard_pin_code = view.findViewById(R.id.et_nom_guard_pin_code);
        et_nom_guard_aadhaar_no = view.findViewById(R.id.et_nom_guard_aadhaar_no);
        et_nom_guard_contact_no = view.findViewById(R.id.et_nom_guard_contact_no);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next = view.findViewById(R.id.btn_next);
    }

    public void clearFields() {
        tv_main_header.setText(R.string.nominee_guardian_details);

        spinner_nom_guard_title.setSelection(0);

        et_nom_guard_name.setText("");
        et_nom_guard_name.setHint(R.string.name);

        spinner_nom_guard_relation.setSelection(0);

        tv_nom_guard_dob.setText("");
        tv_nom_guard_dob.setHint(R.string.date_format);

        et_nom_guard_age.setText("");
        et_nom_guard_age.setHint(R.string.age);

        et_nom_guard_address.setText("");
        et_nom_guard_address.setHint(R.string.address);

        spinner_nom_guard_state.setSelection(0);
        spinner_nom_guard_city.setSelection(0);

        et_nom_guard_pin_code.setText("");
        et_nom_guard_pin_code.setHint(R.string.pin_code);

        et_nom_guard_aadhaar_no.setText("");
        et_nom_guard_aadhaar_no.setHint(R.string.aadhaar_no);

        et_nom_guard_contact_no.setText("");
        et_nom_guard_contact_no.setHint(R.string.mobile_no);
    }

    private void objInit() {
        dialogsUtil = new DialogsUtil(getActivity());
        dbFunctions = DbFunctions.getInstance(getActivity());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_nom_guard_title:
                nomGuardTitleId = adapterView.getSelectedItemPosition();
                nomGuardTitle = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomGuardTitleId---> " + nomGuardTitleId);
                Log.i(TAG, "nomGuardTitle--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_nom_guard_relation:
                nomGuardRelationId = adapterView.getSelectedItemPosition();
                nomGuardRelation = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomGuardRelationId---> " + nomGuardRelationId);
                Log.i(TAG, "nomGuardRelation--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_nom_guard_state:
                nomGuardStateId = adapterView.getSelectedItemPosition();
                nomGuardState = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomGuardStateId---> " + nomGuardStateId);
                Log.i(TAG, "nomGuardState--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_nom_guard_city:
                nomGuardCityId = adapterView.getSelectedItemPosition();
                nomGuardCity = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomGuardCityId---> " + nomGuardCityId);
                Log.i(TAG, "nomGuardCity--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
