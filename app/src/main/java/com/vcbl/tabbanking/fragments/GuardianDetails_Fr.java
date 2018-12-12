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
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.Address;
import com.vcbl.tabbanking.models.Age;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GuardianDetails_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "GuardianDetails_Fr-->";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header, tv_guardian_dob;
    AppCompatSpinner spinner_guardian_title, spinner_guardian_relation, spinner_guardian_state,
            spinner_guardian_city;
    AppCompatEditText et_guardian_name, et_guardian_age, et_guardian_address,
            et_guardian_pin_code, et_guardian_aadhaar_no, et_guardian_contact_no;
    AppCompatButton btn_clear_details, btn_next;
    DialogsUtil dialogsUtil;
    DbFunctions dbFunctions;
    VerhoeffCheckDigit verhoeff;
    int mYear, mMonth, mDay, guardianTitleId, guardianRelationId, guardianStateId, guardianCityId;
    String guardianName, guardianDOB, guardianAge, guardianAddress, guardianPinCode,
            guardianAadhaarNo, guardianMobileNo ="", guardianTitle, guardianRelation, guardianState,
            guardianCity, guardianDobCheck = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.guardian_details, container, false);

        loadUiComponents();

        objInit();

        //clearFields();

        // 1. spinner guardian title details
        spinner_guardian_title.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> titleList = dbFunctions.getTitleDetails();
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_guardian_title.setAdapter(adapter);

        // 2. spinner relation details
        spinner_guardian_relation.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> relationList = dbFunctions.getSpinnerList("RSM");
        ArrayAdapter<SpinnerList> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, relationList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_guardian_relation.setAdapter(adapter2);

        // 3. spinner states details
        spinner_guardian_state.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> statesList = dbFunctions.getStatesList();
        ArrayAdapter<SpinnerList> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, statesList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_guardian_state.setAdapter(adapter3);

        // 4. spinner cities details
        spinner_guardian_city.setOnItemSelectedListener(this);
//        ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(spinner_guardian_state.getSelectedItem().toString());
//        ArrayAdapter<SpinnerList> adapter4 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
//        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_guardian_city.setAdapter(adapter4);

        tv_guardian_dob.setOnClickListener(new View.OnClickListener() {
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
                                guardianAge = Utility.getAge(dayOfMonth, monthOfYear, year);
                                Log.i(TAG, "guardianAge-->" + guardianAge);
                                try {
                                    guardianDOB = date_format.format(date_parse.parse(date));
                                    String todaysDate = Utility.getTodaysDate();
                                    long days = Utility.daysCalculate(guardianDOB, todaysDate);
                                    if (days > 6570) {
                                        tv_guardian_dob.setText(guardianDOB);
                                        guardianDobCheck = "1";
                                        et_guardian_age.setText(guardianAge);
                                        et_guardian_age.setEnabled(false);
                                        et_guardian_age.setClickable(false);
                                    } else {
                                        dialogsUtil.alertDialog("Please select DOB atleast guardian age\nshould be greater than 18 years");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    tv_guardian_dob.setText(date);
                                    et_guardian_age.setEnabled(true);
                                    et_guardian_age.setClickable(true);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        et_guardian_aadhaar_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                guardianAadhaarNo = et_guardian_aadhaar_no.getText().toString().trim();
                if (guardianAadhaarNo.length() >= 1 && !verhoeff.isValid(guardianAadhaarNo)) {
                    et_guardian_aadhaar_no.requestFocus();
                    et_guardian_aadhaar_no.setError("Please enter valid aadhaar no.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                guardianName = et_guardian_name.getText().toString().trim();
                guardianDOB = tv_guardian_dob.getText().toString().trim();
                guardianAge = et_guardian_age.getText().toString().trim();
                guardianAddress = et_guardian_address.getText().toString().trim();
                guardianPinCode = et_guardian_pin_code.getText().toString().trim();
                guardianAadhaarNo = et_guardian_aadhaar_no.getText().toString().trim();
                guardianMobileNo = et_guardian_contact_no.getText().toString().trim();

                if (guardianTitleId == 0) {
                    dialogsUtil.alertDialog("Please select title");
                } else if (guardianName.isEmpty()) {
                    et_guardian_name.setError("Please enter name");
                    et_guardian_name.requestFocus();
                } else if (guardianRelationId == 0) {
                    dialogsUtil.alertDialog("Select relation with guardian");
                } else if ("0".equals(guardianDobCheck)) {
                    dialogsUtil.alertDialog("Please select dob");
                } else if (guardianAge.isEmpty()) {
                    et_guardian_age.setError("Please enter age");
                    et_guardian_age.requestFocus();
                } else if (guardianAddress.isEmpty()) {
                    et_guardian_address.setError("Please enter address");
                    et_guardian_age.requestFocus();
                } else if (guardianStateId == 0) {
                    dialogsUtil.alertDialog("Select state");
                } else if (guardianCityId == 0) {
                    dialogsUtil.alertDialog("Select city");
                } else if (guardianPinCode.isEmpty()) {
                    et_guardian_pin_code.setError("Enter pin code");
                    et_guardian_pin_code.requestFocus();
                } else if (guardianAadhaarNo.isEmpty()){
                    et_guardian_aadhaar_no.setError("Please enter aadhaar no.");
                    et_guardian_aadhaar_no.requestFocus();
                } else if (guardianAadhaarNo != null && !verhoeff.isValid(guardianAadhaarNo)) {
                    et_guardian_aadhaar_no.setError("Enter valid aadhaar no.");
                    et_guardian_aadhaar_no.requestFocus();
                } else {
                    EnrollmentData.getGuardianDetails().setName(guardianTitle + " " + guardianName);
                    EnrollmentData.getGuardianDetails().setRelation(String.valueOf(guardianRelationId));
                    Age age = Age.getInstance();
                    age.setDob(guardianDOB);
                    age.setAge(Integer.parseInt(guardianAge));
                    EnrollmentData.getGuardianDetails().setAge(age);
                    Address address = Address.getInstance();
                    address.setAddress1(guardianAddress);
                    address.setAddress2(guardianAddress);
                    address.setState(String.valueOf(guardianStateId));
                    address.setCity(String.valueOf(guardianCityId));
                    address.setPinCode(guardianPinCode);
                    address.setAadhaarNo(guardianAadhaarNo);
                    address.setMobileNo(guardianMobileNo);
                    EnrollmentData.getGuardianDetails().setAddress(address);

                    Fragment fragment = new OtherDetails_Fr();
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

        spinner_guardian_title = view.findViewById(R.id.spinner_guardian_title);

        et_guardian_name = view.findViewById(R.id.et_guardian_name);

        spinner_guardian_relation = view.findViewById(R.id.spinner_guardian_relation);

        tv_guardian_dob = view.findViewById(R.id.tv_guardian_dob);

        et_guardian_age = view.findViewById(R.id.et_guardian_age);
        et_guardian_address = view.findViewById(R.id.et_guardian_address);

        spinner_guardian_state = view.findViewById(R.id.spinner_guardian_state);
        spinner_guardian_city = view.findViewById(R.id.spinner_guardian_city);

        et_guardian_pin_code = view.findViewById(R.id.et_guardian_pin_code);
        et_guardian_aadhaar_no = view.findViewById(R.id.et_guardian_aadhaar_no);
        et_guardian_contact_no = view.findViewById(R.id.et_guardian_contact_no);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next = view.findViewById(R.id.btn_next);
    }

    private void clearFields() {
        tv_main_header.setText(R.string.guardian_details);

        spinner_guardian_title.setSelection(0);

        et_guardian_name.setText("");
        et_guardian_name.setHint(R.string.name);

        spinner_guardian_relation.setSelection(0);

        tv_guardian_dob.setText("");
        tv_guardian_dob.setHint(R.string.date_format);

        et_guardian_age.setText("");
        et_guardian_age.setHint(R.string.age);
        et_guardian_age.setEnabled(false);
        et_guardian_age.setClickable(false);

        et_guardian_address.setText("");
        et_guardian_address.setHint(R.string.address);

        spinner_guardian_state.setSelection(1);
        spinner_guardian_city.setSelection(0);

        et_guardian_pin_code.setText("");
        et_guardian_pin_code.setHint(R.string.pin_code);

        et_guardian_aadhaar_no.setText("");
        et_guardian_aadhaar_no.setHint(R.string.aadhaar_no);

        et_guardian_contact_no.setText("");
        et_guardian_contact_no.setHint(R.string.contact_number);
    }

    private void objInit() {
        dialogsUtil = new DialogsUtil(getActivity());
        dbFunctions = DbFunctions.getInstance(getActivity());
        verhoeff = new VerhoeffCheckDigit();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_guardian_title:
                guardianTitleId = adapterView.getSelectedItemPosition();
                guardianTitle = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "guardianTitleId---> " + guardianTitleId);
                Log.i(TAG, "guardianTitle--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_guardian_relation:
                guardianRelationId = adapterView.getSelectedItemPosition();
                guardianRelation = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "guardianRelationId---> " + guardianRelationId);
                Log.i(TAG, "guardianRelation--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_guardian_state:
                guardianStateId = adapterView.getSelectedItemPosition();
                guardianState = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "guardianStateId---> " + guardianStateId);
                Log.i(TAG, "guardianState--->" + String.valueOf(adapterView.getSelectedItem()));
                ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(String.valueOf(guardianStateId));
                ArrayAdapter<SpinnerList> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_guardian_city.setAdapter(adapter1);
                break;
            case R.id.spinner_guardian_city:
                guardianCityId = adapterView.getSelectedItemPosition();
                guardianCity = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "guardianCityId---> " + guardianCityId);
                Log.i(TAG, "guardianCity--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
