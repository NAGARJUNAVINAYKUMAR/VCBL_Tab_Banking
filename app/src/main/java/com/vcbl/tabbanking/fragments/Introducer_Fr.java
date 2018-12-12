package com.vcbl.tabbanking.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.interactors.CustomerBO;
import com.vcbl.tabbanking.models.AccountDetails;
import com.vcbl.tabbanking.models.Address;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.Name;
import com.vcbl.tabbanking.protobuff.Enrollmentuploadresponse;
import com.vcbl.tabbanking.tools.NumericKeyBoardTransformation;
import com.vcbl.tabbanking.tools.VerhoeffCheckDigit;

import es.dmoral.toasty.Toasty;

public class Introducer_Fr extends Fragment {

    View view;
    CardView cardView;
    AppCompatEditText et_introducer_name, et_introducer_account_no, et_introducer_aadhaar_no,
            et_introducer_mobile_no;
    AppCompatButton btn_clear_details, btn_next;
    String introducerName = "", introducerAccNo = "", introducerAadharNo = "", introducerMobNo = "";
    VerhoeffCheckDigit verhoeff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.introducer_details, container, false);

        loadUiComponents();

        objInit();

        //clearFields();

        et_introducer_aadhaar_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                introducerAadharNo = et_introducer_aadhaar_no.getText().toString().trim();
                if (introducerAadharNo.length() >= 1 && !verhoeff.isValid(introducerAadharNo)) {
                    et_introducer_aadhaar_no.requestFocus();
                    et_introducer_aadhaar_no.setError("Please enter valid aadhaar no.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                introducerName = et_introducer_name.getText().toString().trim();
                introducerAccNo = et_introducer_account_no.getText().toString().trim();
                introducerAadharNo = et_introducer_aadhaar_no.getText().toString().trim();
                introducerMobNo = et_introducer_mobile_no.getText().toString().trim();

                Name name = Name.getInstance();
                name.setFirstName(introducerName);
                EnrollmentData.getIntroducerDetails().setName(name);
                AccountDetails accountDetails = AccountDetails.getInstance();
                accountDetails.setAccountNo(introducerAccNo);
                EnrollmentData.getIntroducerDetails().setAccountDetails(accountDetails);
                Address address1 = Address.getInstance();
                address1.setAadhaarNo(introducerAadharNo);
                address1.setMobileNo(introducerMobNo);
                EnrollmentData.getIntroducerDetails().setAddress(address1);

                prepareModel();
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

        et_introducer_name = view.findViewById(R.id.et_introducer_name);
        et_introducer_account_no = view.findViewById(R.id.et_introducer_account_no);
        et_introducer_aadhaar_no = view.findViewById(R.id.et_introducer_aadhaar_no);
        et_introducer_mobile_no = view.findViewById(R.id.et_introducer_mobile_no);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next = view.findViewById(R.id.btn_next);
    }

    private void objInit() {
        verhoeff = new VerhoeffCheckDigit();
    }

    private void clearFields() {
        et_introducer_name.setText("");
        et_introducer_name.setHint(R.string.name);

        et_introducer_account_no.setText("");
        et_introducer_account_no.setHint(R.string.account_no);
        et_introducer_account_no.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_introducer_account_no.setTransformationMethod(new NumericKeyBoardTransformation());

        et_introducer_aadhaar_no.setText("");
        et_introducer_aadhaar_no.setHint(R.string.aadhaar_no);

        et_introducer_mobile_no.setText("");
        et_introducer_mobile_no.setHint(R.string.mobile_no);
    }

    private void prepareModel() {
        CustomerBO customerBO = new CustomerBO(getActivity());
        customerBO.customerEnrollmentRequest();
        customerBO.setOnTaskFinishedEvent(new CustomerBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished() {
                if (Enrollmentuploadresponse.Status.SUCCESS.equals(EnrollmentData.getStatusResp().getStatus())) {
                    Toasty.success(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toasty.error(getActivity(), "Something went wrong !", Toast.LENGTH_LONG, true).show();
                }
            }
        });
    }
}
