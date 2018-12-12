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
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddressDetails_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddressDetails_Fr--> ";
    View view;
    CardView cardView;
    AppCompatEditText et_cust_address, et_cust_address2, et_country, et_pin_code, et_contact_no, et_id_number,
            et_issuing_authority, et_issuing_place;
    AppCompatSpinner spinner_state, spinner_city, spinner_language, spinner_id_type;
    RadioGroup rg_head;
    AppCompatRadioButton rb_head_yes, rb_head_no, rbtn_head;
    AppCompatTextView tv_issuing_date;
    AppCompatButton btn_clear_details, btn_next_cust3;
    private int mYear, mMonth, mDay;
    String address1, address2 = "", state, city, country, pinCode = "", language, headOfTheFamily, contactNo,
            idType, idNumber, issuingAuth = "", issuePlace = "", issuingDate="", issueDateFlag = "0";
    int stateId, cityId, languageId, idTypeId;
    DbFunctions dbFunctions;
    DialogsUtil dialogsUtil;
    Storage storage;
    //

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.address_details, container, false);

        loadUiComponents();

        objInit();

        //clearFields();

        et_country.setText(R.string.ind);

        // 1. spinner states
        spinner_state.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> statesList = dbFunctions.getStatesList();
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, statesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(adapter);

        // 2. spinner cities
        spinner_city.setOnItemSelectedListener(this);
//        ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(spinner_state.getSelectedItem().toString());
//        ArrayAdapter<SpinnerList> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_city.setAdapter(adapter1);

        // 3. spinner languages
        spinner_language.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> languagesList = dbFunctions.getSpinnerList("LNM");
        ArrayAdapter<SpinnerList> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, languagesList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_language.setAdapter(adapter2);
        spinner_language.setSelection(1);

        // 4. spinner id types
        spinner_id_type.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> idList = dbFunctions.getSpinnerList("ITM");
        ArrayAdapter<SpinnerList> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, idList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_id_type.setAdapter(adapter3);

        /*rg_head.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_head_yes) {
                    headOfTheFamily = "1";
                    Toasty.success(getActivity(), "Selected- " + headOfTheFamily, Toast.LENGTH_SHORT).show();
                } else {
                    headOfTheFamily = "2";
                    Toasty.success(getActivity(), "Selected- " + headOfTheFamily, Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        tv_issuing_date.setOnClickListener(new View.OnClickListener() {
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
                                try {
                                    issuingDate = date_format.format(date_parse.parse(date));
                                    Log.i(TAG, "issuingDate-->" + issuingDate);
                                    tv_issuing_date.setText(issuingDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    tv_issuing_date.setText(date);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                issueDateFlag = "1";
            }
        });

        btn_next_cust3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                address1 = et_cust_address.getText().toString().trim();
                address2 = et_cust_address2.getText().toString().trim();
                country = et_country.getText().toString().trim();
                pinCode = et_pin_code.getText().toString().trim();
                contactNo = et_contact_no.getText().toString().trim();

                int selectedId = rg_head.getCheckedRadioButtonId();
                rbtn_head = view.findViewById(selectedId);
                String rbtnValue = rbtn_head.getText().toString().trim();
                if ("Yes".equals(rbtnValue))
                    headOfTheFamily = "1";
                else
                    headOfTheFamily = "2";
                Log.i(TAG, "rbtnValue-->" + rbtnValue);
                Log.i(TAG, "headOfTheFamily-->" + headOfTheFamily);

                idNumber = et_id_number.getText().toString().trim();
                issuingAuth = et_issuing_authority.getText().toString().trim();
                issuePlace = et_issuing_place.getText().toString().trim();

                if (address1.isEmpty()) {
                    et_cust_address.requestFocus();
                    et_cust_address.setError("Please enter address");
                } else if (stateId == 0) {
                    dialogsUtil.alertDialog("Please select state");
                } else if (cityId == 0) {
                    dialogsUtil.alertDialog("Please select city");
                } else if (country.isEmpty()) {
                    et_country.requestFocus();
                    et_country.setError("Please enter country name");
                } else if (!rb_head_yes.isChecked() && !rb_head_no.isChecked()) {
                    dialogsUtil.alertDialog("Please select whether the person\nis family head or not ?");
                } else if (contactNo.isEmpty()) {
                    et_contact_no.requestFocus();
                    et_contact_no.setError("Please enter contact no.");
                } else if (contactNo.length() < 10) {
                    et_contact_no.requestFocus();
                    et_contact_no.setError("Please enter valid mobile no.");
                    // identity details
                } else if (idTypeId == 0) {
                    dialogsUtil.alertDialog("Please select ID");
                } else if (idNumber.isEmpty()) {
                    et_id_number.requestFocus();
                    et_id_number.setError("Please enter ID number");
                } else {
                    EnrollmentData.getAddressInfo().setAddress1(address1);
                    EnrollmentData.getAddressInfo().setAddress2(address2);
                    EnrollmentData.getAddressInfo().setState(String.valueOf(stateId));
                    EnrollmentData.getAddressInfo().setCity(String.valueOf(cityId));
                    EnrollmentData.getAddressInfo().setCountry(country);
                    EnrollmentData.getAddressInfo().setPinCode(pinCode);
                    EnrollmentData.getPersonalInfo().setLanguage(String.valueOf(languageId));
                    EnrollmentData.getPersonalInfo().setHeadOfFamily(headOfTheFamily);
                    EnrollmentData.getContactInfo().setMobile(contactNo);
                    EnrollmentData.getIdDetails().setIdType(String.valueOf(idTypeId));
                    EnrollmentData.getIdDetails().setIdNo(idNumber);
                    EnrollmentData.getIdDetails().setIssueAuthority(issuingAuth);
                    EnrollmentData.getIdDetails().setIssuePlace(issuePlace);
                    EnrollmentData.getIdDetails().setIssueDate(issuingDate);
                    Fragment fragment;
                    if ((Integer.parseInt(storage.getValue(Constants.CUSTOMER_AGE))) < 18) {
                        fragment = new GuardianDetails_Fr();
                    } else {
                        fragment = new OtherDetails_Fr();
                    }
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

        et_cust_address = view.findViewById(R.id.et_cust_address);
        et_cust_address2 = view.findViewById(R.id.et_cust_address2);

        spinner_city = view.findViewById(R.id.spinner_city);
        spinner_state = view.findViewById(R.id.spinner_state);

        et_country = view.findViewById(R.id.et_country);
        et_pin_code = view.findViewById(R.id.et_pin_code);

        spinner_language = view.findViewById(R.id.spinner_language);

        rg_head = view.findViewById(R.id.rg_head);
        rb_head_yes = view.findViewById(R.id.rb_head_yes);
        rb_head_no = view.findViewById(R.id.rb_head_no);

        et_contact_no = view.findViewById(R.id.et_contact_no);

        spinner_id_type = view.findViewById(R.id.spinner_id_type);

        et_id_number = view.findViewById(R.id.et_id_number);
        et_issuing_authority = view.findViewById(R.id.et_issuing_authority);
        et_issuing_place = view.findViewById(R.id.et_issuing_place);

        tv_issuing_date = view.findViewById(R.id.tv_issuing_date);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next_cust3 = view.findViewById(R.id.btn_next_cust3);
    }

    private void clearFields() {
        et_cust_address.setText("");
        et_cust_address.setHint(R.string.address);
        et_cust_address.requestFocus();

        et_cust_address2.setText("");
        et_cust_address2.setHint(R.string.address);

        spinner_state.setSelection(0);

        spinner_city.setSelection(0);

        et_country.setText(R.string.ind);

        et_pin_code.setText("");
        et_pin_code.setHint(R.string.pin_code);

        spinner_language.setSelection(0);

        et_contact_no.setText("");
        et_contact_no.setHint(R.string.contact_number);

        int selectedId = rg_head.getCheckedRadioButtonId();
        rbtn_head = view.findViewById(selectedId);

        spinner_id_type.setSelection(0);

        et_id_number.setText("");
        et_id_number.setHint(R.string.id_number);

        et_issuing_authority.setText("");
        et_issuing_authority.setHint(R.string.issuing_authority);

        et_issuing_place.setText("");
        et_issuing_place.setHint(R.string.issue_place);

        tv_issuing_date.setText("");
        tv_issuing_date.setHint(R.string.date_format);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
        storage = new Storage(getActivity());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_state:
                stateId = adapterView.getSelectedItemPosition();
                state = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "titleId---> " + stateId);
                Log.i(TAG, "state--->" + String.valueOf(adapterView.getSelectedItem()));
                ArrayList<SpinnerList> citiesList = dbFunctions.getDistrictList(String.valueOf(stateId));
                ArrayAdapter<SpinnerList> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesList);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_city.setAdapter(adapter1);
                break;
            case R.id.spinner_city:
                cityId = adapterView.getSelectedItemPosition();
                city = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "cityId---> " + cityId);
                Log.i(TAG, "city--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_language:
                languageId = adapterView.getSelectedItemPosition();
                language = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "languageId--->" + languageId);
                Log.i(TAG, "language--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_id_type:
                idTypeId = adapterView.getSelectedItemPosition();
                idType = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "idTypeId---> " + idTypeId);
                Log.i(TAG, "idType--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
