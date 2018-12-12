package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.CustomerBO;
import com.vcbl.tabbanking.models.Address;
import com.vcbl.tabbanking.models.Age;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class NomineeDetails_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "NomineeDetails_Fr-->";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header, tv_nominee_dob;
    AppCompatSpinner spinner_nominee_title, spinner_nominee_relation, spinner_nominee_state,
            spinner_nominee_city;
    AppCompatEditText et_nominee_name, et_nominee_age, et_nominee_address,
            et_nominee_pin_code, et_nominee_aadhaar_no, et_nominee_mobile_no;
    AppCompatButton btn_clear_details, btn_next;
    DialogsUtil dialogsUtil;
    DbFunctions dbFunctions;
    int mYear, mMonth, mDay, nomineeTitleId, nomineeRelationId, nomineeStateId, nomineeCityId;
    String nomineeName, nomineeDOB, nomineeAge, nomineeAddress, nomineePinCode, nomineeAadhaarNo="", nomineeMobileNo="",
            nomineeTitle, nomineeRelation, nomineeState, nomineeCity, nomineeDobCheck = "0";
    VerhoeffCheckDigit verhoeff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nominee_details, container, false);

        loadUiComponents();

        objInit();

        //clearFields();

        // 1. spinner nominee title details
        spinner_nominee_title.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> titleList = dbFunctions.getTitleDetails();
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nominee_title.setAdapter(adapter);

        // 2. spinner relation details
        spinner_nominee_relation.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> relationList = dbFunctions.getSpinnerList("RSM");
        ArrayAdapter<SpinnerList> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, relationList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nominee_relation.setAdapter(adapter2);

        // 3. spinner states details
        spinner_nominee_state.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> statesList = dbFunctions.getStatesList();
        ArrayAdapter<SpinnerList> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, statesList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nominee_state.setAdapter(adapter3);

        // 4. spinner cities details
        spinner_nominee_city.setOnItemSelectedListener(this);
//        ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(spinner_nominee_state.getSelectedItem().toString());
//        ArrayAdapter<SpinnerList> adapter4 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
//        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_nominee_city.setAdapter(adapter4);

        tv_nominee_dob.setOnClickListener(new View.OnClickListener() {
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
                                nomineeAge = Utility.getAge(dayOfMonth, monthOfYear, year);
                                Log.i(TAG, "nomineeAge-->" + nomineeAge);
                                try {
                                    nomineeDOB = date_format.format(date_parse.parse(date));
                                    String todaysDate = Utility.getTodaysDate();
                                    long days = Utility.daysCalculate(nomineeDOB, todaysDate);
//                                    if (days > 6570) {
                                    tv_nominee_dob.setText(nomineeDOB);
                                    nomineeDobCheck = "1";
                                    et_nominee_age.setText(nomineeAge);
                                    et_nominee_age.setEnabled(false);
                                    et_nominee_age.setClickable(false);
                                    Log.i(TAG, "nomineeDOB-->" + nomineeDOB);
//                                    } else {
//                                        dialogsUtil.alertDialog("Please select DOB atleast person age\nshould be greater than 18 years");
//                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    tv_nominee_dob.setText(date);
                                    et_nominee_age.setEnabled(true);
                                    et_nominee_age.setClickable(true);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        et_nominee_aadhaar_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nomineeAadhaarNo = et_nominee_aadhaar_no.getText().toString().trim();
                if (nomineeAadhaarNo.length() >= 1 && !verhoeff.isValid(nomineeAadhaarNo)) {
                    et_nominee_aadhaar_no.requestFocus();
                    et_nominee_aadhaar_no.setError("Please enter valid aadhaar no.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nomineeName = et_nominee_name.getText().toString().trim();
                nomineeDOB = tv_nominee_dob.getText().toString().trim();
                nomineeAge = et_nominee_age.getText().toString().trim();
                nomineeAddress = et_nominee_address.getText().toString().trim();
                nomineePinCode = et_nominee_pin_code.getText().toString().trim();
                nomineeAadhaarNo = et_nominee_aadhaar_no.getText().toString().trim();
                nomineeMobileNo = et_nominee_mobile_no.getText().toString().trim();

                if (nomineeTitleId == 0) {
                    dialogsUtil.alertDialog("Please select title");
                } else if (nomineeName.isEmpty()) {
                    et_nominee_name.requestFocus();
                    et_nominee_name.setError("Please enter name");
                } else if (nomineeRelationId == 0) {
                    dialogsUtil.alertDialog("Please select relation");
                } else if ("0".equals(nomineeDobCheck)) {
                    dialogsUtil.alertDialog("Please select dob");
                } else if (nomineeAge.isEmpty()) {
                    et_nominee_age.requestFocus();
                    et_nominee_age.setError("Please enter age");
                } else if (nomineeAddress.isEmpty()) {
                    et_nominee_address.requestFocus();
                    et_nominee_address.setError("Please enter address");
                } else if (nomineeStateId == 0) {
                    dialogsUtil.alertDialog("Please select state");
                } else if (nomineeCityId == 0) {
                    dialogsUtil.alertDialog("Please select city");
                } else if (nomineePinCode.isEmpty()) {
                    et_nominee_pin_code.requestFocus();
                    et_nominee_pin_code.setError("Enter pin code");
                } else {
                    EnrollmentData.getNomineeDetails().setName(nomineeTitle + " " + nomineeName);
                    EnrollmentData.getNomineeDetails().setRelation(String.valueOf(nomineeRelationId));
                    Age age = Age.getInstance();
                    age.setDob(nomineeDOB);
                    age.setAge(Integer.parseInt(nomineeAge));
                    EnrollmentData.getNomineeDetails().setAge(age);
                    Address address = Address.getInstance();
                    address.setAddress1(nomineeAddress);
                    address.setAddress2(nomineeAddress);
                    address.setState(String.valueOf(nomineeStateId));
                    address.setCity(String.valueOf(nomineeCityId));
                    address.setPinCode(nomineePinCode);
                    address.setAadhaarNo(nomineeAadhaarNo);
                    address.setMobileNo(nomineeMobileNo);
                    EnrollmentData.getNomineeDetails().setAddress(address);

                    Fragment fragment = new PhotoUpload_Fr();
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

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        tv_main_header = view.findViewById(R.id.tv_main_header);

        spinner_nominee_title = view.findViewById(R.id.spinner_nominee_title);

        et_nominee_name = view.findViewById(R.id.et_nominee_name);

        spinner_nominee_relation = view.findViewById(R.id.spinner_nominee_relation);

        tv_nominee_dob = view.findViewById(R.id.tv_nominee_dob);

        et_nominee_age = view.findViewById(R.id.et_nominee_age);
        et_nominee_address = view.findViewById(R.id.et_nominee_address);

        spinner_nominee_state = view.findViewById(R.id.spinner_nominee_state);
        spinner_nominee_city = view.findViewById(R.id.spinner_nominee_city);

        et_nominee_pin_code = view.findViewById(R.id.et_nominee_pin_code);
        et_nominee_aadhaar_no = view.findViewById(R.id.et_nominee_aadhaar_no);
        et_nominee_mobile_no = view.findViewById(R.id.et_nominee_mobile_no);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next = view.findViewById(R.id.btn_next);
    }

    private void clearFields() {
        tv_main_header.setText(R.string.nominee_details);

        spinner_nominee_title.setSelection(0);

        et_nominee_name.setText("");
        et_nominee_name.setHint(R.string.name);

        spinner_nominee_relation.setSelection(0);

        tv_nominee_dob.setText("");
        tv_nominee_dob.setHint(R.string.date_format);

        et_nominee_age.setText("");
        et_nominee_age.setHint(R.string.age);
        et_nominee_age.setEnabled(false);
        et_nominee_age.setClickable(false);

        et_nominee_address.setText("");
        et_nominee_address.setHint(R.string.address);

        spinner_nominee_state.setSelection(1);
        spinner_nominee_city.setSelection(0);

        et_nominee_pin_code.setText("");
        et_nominee_pin_code.setHint(R.string.pin_code);

        et_nominee_aadhaar_no.setText("");
        et_nominee_aadhaar_no.setHint(R.string.aadhaar_no);

        et_nominee_mobile_no.setText("");
        et_nominee_mobile_no.setHint(R.string.mobile_no);
    }

    private void objInit() {
        dialogsUtil = new DialogsUtil(getActivity());
        dbFunctions = DbFunctions.getInstance(getActivity());
        verhoeff = new VerhoeffCheckDigit();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_nominee_title:
                nomineeTitleId = adapterView.getSelectedItemPosition();
                nomineeTitle = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomineeTitleId---> " + nomineeTitleId);
                Log.i(TAG, "nomineeTitle--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_nominee_relation:
                nomineeRelationId = adapterView.getSelectedItemPosition();
                nomineeRelation = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomineeRelationId---> " + nomineeRelationId);
                Log.i(TAG, "nomineeRelation--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_nominee_state:
                nomineeStateId = adapterView.getSelectedItemPosition();
                nomineeState = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomineeStateId---> " + nomineeStateId);
                Log.i(TAG, "nomineeState--->" + String.valueOf(adapterView.getSelectedItem()));
                ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(String.valueOf(nomineeStateId));
                ArrayAdapter<SpinnerList> adapter4 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
                adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_nominee_city.setAdapter(adapter4);
                break;
            case R.id.spinner_nominee_city:
                nomineeCityId = adapterView.getSelectedItemPosition();
                nomineeCity = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "nomineeCityId---> " + nomineeCityId);
                Log.i(TAG, "nomineeCity--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
