package com.vcbl.tabbanking.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.util.ArrayList;

public class OtherDetails_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "OtherDetails_Fr-->";
    View view;
    CardView cardView;
    RadioGroup rg_house_type, rg_bpl, rg_minority;
    AppCompatRadioButton rb_own, rb_rental, rb_bpl_yes, rb_bpl_no, rb_minority_yes, rb_minority_no,
            rbtn_house_type, rbtn_bpl, rbtn_minority;
    AppCompatEditText et_land_holding, et_dependent, et_pan_gir_no, et_nrega_no,
            et_ssp_no;
    AppCompatSpinner spinner_education, spinner_occupation, spinner_community,
            spinner_spec_category, spinner_religion, spinner_auth_person;
    AppCompatButton btn_clear_details, btn_next;
    private String houseType, bpl, minority, landHolding = "", dependent="", panGirNo = "", nregaNo = "", sspNo = "",
            education, occupation, community, specCategory, religion, authPerson;
    private int educationId, occupationId, communityId, specCategoryId, religionId, authPersonId;
    DbFunctions dbFunctions;
    DialogsUtil dialogsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.other_details, container, false);

        loadUiComponents();

        objInit();

        //clearFields();

        // 1. spinner education details
        spinner_education.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> educationList = dbFunctions.getSpinnerList("EDM");
        ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, educationList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_education.setAdapter(adapter);

        // 2. spinner occupation details
        spinner_occupation.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> occupationList = dbFunctions.getSpinnerList("OCM");
        ArrayAdapter<SpinnerList> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, occupationList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_occupation.setAdapter(adapter2);

        // 3. spinner community details
        spinner_community.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> communityList = dbFunctions.getSpinnerList("CMM");
        ArrayAdapter<SpinnerList> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, communityList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_community.setAdapter(adapter3);

        // 4. spinner special category details
        spinner_spec_category.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> specCateList = dbFunctions.getSpinnerList("SCM");
        ArrayAdapter<SpinnerList> adapter4 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, specCateList);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_spec_category.setAdapter(adapter4);
        spinner_spec_category.setSelection(1);

        // 5. spinner religion details
        spinner_religion.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> religionList = dbFunctions.getSpinnerList("REM");
        ArrayAdapter<SpinnerList> adapter5 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, religionList);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_religion.setAdapter(adapter5);

        // 6. spinner auth person details
        spinner_auth_person.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> authPersonList = dbFunctions.getSpinnerList("AUP");
        ArrayAdapter<SpinnerList> adapter6 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, authPersonList);
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_auth_person.setAdapter(adapter6);

        /*rg_house_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_own) {
                    houseType = "1";
                    Toasty.success(getActivity(), "Selected- " + houseType, Toast.LENGTH_SHORT).show();
                } else {
                    houseType = "2";
                    Toasty.success(getActivity(), "Selected- " + houseType, Toast.LENGTH_SHORT).show();
                }
            }
        });

        rg_bpl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_bpl_yes) {
                    bpl = "1";
                    Toasty.success(getActivity(), "Selected- " + bpl, Toast.LENGTH_SHORT).show();
                } else {
                    bpl = "2";
                    Toasty.success(getActivity(), "Selected- " + bpl, Toast.LENGTH_SHORT).show();
                }
            }
        });

        rg_minority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_minority_yes) {
                    minority = "1";
                    Toasty.success(getActivity(), "Selected- " + minority, Toast.LENGTH_SHORT).show();
                } else {
                    minority = "2";
                    Toasty.success(getActivity(), "Selected- " + minority, Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                int selectedId = rg_house_type.getCheckedRadioButtonId();
                rbtn_house_type = view.findViewById(selectedId);
                String rbtnHouseType = rbtn_house_type.getText().toString().trim();
                if ("Own".equals(rbtnHouseType))
                    houseType = "1";
                else
                    houseType = "2";
                Log.i(TAG, "rbtnHouseType-->" + rbtnHouseType);
                Log.i(TAG, "houseType-->" + houseType);

                int selectedId2 = rg_bpl.getCheckedRadioButtonId();
                rbtn_bpl = view.findViewById(selectedId2);
                String rbtnBpl = rbtn_bpl.getText().toString().trim();
                if ("Yes".equals(rbtnBpl))
                    bpl = "1";
                else
                    bpl = "2";
                Log.i(TAG, "rbtnBpl-->" + rbtnBpl);
                Log.i(TAG, "bpl-->" + bpl);

                landHolding = et_land_holding.getText().toString().trim();
                dependent = et_dependent.getText().toString().trim();
                panGirNo = et_pan_gir_no.getText().toString().trim();
                nregaNo = et_nrega_no.getText().toString().trim();
                sspNo = et_ssp_no.getText().toString().trim();

                int selectedId3 = rg_minority.getCheckedRadioButtonId();
                rbtn_minority = view.findViewById(selectedId3);
                String rbtnMinority = rbtn_minority.getText().toString().trim();
                if ("Yes".equals(rbtnMinority))
                    minority = "1";
                else
                    minority = "2";
                Log.i(TAG, "rbtnMinority-->" + rbtnMinority);
                Log.i(TAG, "minority-->" + minority);

                if (!rb_own.isChecked() && !rb_rental.isChecked()) {
                    dialogsUtil.alertDialog("Please select whether house type is\nown or rental");
                } else if (!rb_bpl_yes.isChecked() && !rb_bpl_no.isChecked()) {
                    dialogsUtil.alertDialog("Please select whether the gas connection is\nbpl or not ?");
                } else if (educationId == 0) {
                    dialogsUtil.alertDialog("Please select education");
                } else if (occupationId == 0) {
                    dialogsUtil.alertDialog("Please select occupation");
                } else if (communityId == 0) {
                    dialogsUtil.alertDialog("Please select community");
                } else if (specCategoryId == 0) {
                    dialogsUtil.alertDialog("Select spec category");
                } else if (religionId == 0) {
                    dialogsUtil.alertDialog("Please select religion");
                } else if (!rb_minority_yes.isChecked() && !rb_minority_no.isChecked()) {
                    dialogsUtil.alertDialog("Please select whether person is\nminority or not ?");
                } else if (authPersonId == 0) {
                    dialogsUtil.alertDialog("Select auth person");
                } else {
                    EnrollmentData.getFinancialInfo().setHouseType(houseType);
                    EnrollmentData.getFinancialInfo().setBpl(bpl);
                    EnrollmentData.getFinancialInfo().setLandHolding(landHolding);
                    EnrollmentData.getFinancialInfo().setNoOfDependents(dependent);
                    EnrollmentData.getIdentityDetails().setPan(panGirNo);
                    EnrollmentData.getIdentityDetails().setNregaNo(nregaNo);
                    EnrollmentData.getIdentityDetails().setSspNo(sspNo);
                    EnrollmentData.getOtherInfo().setEducation(String.valueOf(educationId));
                    EnrollmentData.getOtherInfo().setOccupation(String.valueOf(occupationId));
                    EnrollmentData.getOtherInfo().setCommunity(String.valueOf(communityId));
                    EnrollmentData.getOtherInfo().setSpecialCategory(String.valueOf(specCategoryId));
                    EnrollmentData.getOtherInfo().setReligion(String.valueOf(religionId));
                    EnrollmentData.getOtherInfo().setMinority(minority);
                    EnrollmentData.getOtherInfo().setAuthorisedPerson(String.valueOf(authPersonId));
                    Fragment fragment = new NomineeDetails_Fr();
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

        rg_house_type = view.findViewById(R.id.rg_house_type);
        rb_own = view.findViewById(R.id.rb_own);
        rb_rental = view.findViewById(R.id.rb_rental);

        rg_bpl = view.findViewById(R.id.rg_bpl);
        rb_bpl_yes = view.findViewById(R.id.rb_bpl_yes);
        rb_bpl_no = view.findViewById(R.id.rb_bpl_no);

        et_land_holding = view.findViewById(R.id.et_land_holding);
        et_dependent = view.findViewById(R.id.et_dependent);
        et_pan_gir_no = view.findViewById(R.id.et_pan_gir_no);
        et_nrega_no = view.findViewById(R.id.et_nrega_no);
        et_ssp_no = view.findViewById(R.id.et_ssp_no);

        spinner_education = view.findViewById(R.id.spinner_education);
        spinner_occupation = view.findViewById(R.id.spinner_occupation);
        spinner_community = view.findViewById(R.id.spinner_community);
        spinner_spec_category = view.findViewById(R.id.spinner_spec_category);
        spinner_religion = view.findViewById(R.id.spinner_religion);

        rg_minority = view.findViewById(R.id.rg_minority);
        rb_minority_yes = view.findViewById(R.id.rb_minority_yes);
        rb_minority_no = view.findViewById(R.id.rb_minority_no);

        spinner_auth_person = view.findViewById(R.id.spinner_auth_person);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next = view.findViewById(R.id.btn_next);
    }

    private void clearFields() {
        et_land_holding.setText("");
        et_land_holding.setHint(R.string.land_holding);
        et_land_holding.requestFocus();

        et_dependent.setText("");
        et_dependent.setHint(R.string.dependent);

        et_pan_gir_no.setText("");
        et_pan_gir_no.setHint(R.string.pan_gir_no);

        et_nrega_no.setText("");
        et_nrega_no.setHint(R.string.nrega_no);

        et_ssp_no.setText("");
        et_ssp_no.setHint(R.string.ssp_no);

        spinner_education.setSelection(0);
        spinner_occupation.setSelection(0);
        spinner_community.setSelection(0);
        spinner_spec_category.setSelection(0);
        spinner_religion.setSelection(0);
        spinner_auth_person.setSelection(0);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_education:
                educationId = adapterView.getSelectedItemPosition();
                education = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "educationId---> " + educationId);
                Log.i(TAG, "education--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_occupation:
                occupationId = adapterView.getSelectedItemPosition();
                occupation = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "occupationId---> " + occupationId);
                Log.i(TAG, "occupation--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_community:
                communityId = adapterView.getSelectedItemPosition();
                community = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "communityId---> " + communityId);
                Log.i(TAG, "community--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_spec_category:
                specCategoryId = adapterView.getSelectedItemPosition();
                specCategory = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "specCategoryId---> " + specCategoryId);
                Log.i(TAG, "specCategory--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_religion:
                religionId = adapterView.getSelectedItemPosition();
                religion = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "religionId---> " + religionId);
                Log.i(TAG, "religion--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            case R.id.spinner_auth_person:
                authPersonId = adapterView.getSelectedItemPosition();
                authPerson = String.valueOf(adapterView.getSelectedItem());
                Log.i(TAG, "authPersonId---> " + authPersonId);
                Log.i(TAG, "authPerson--->" + String.valueOf(adapterView.getSelectedItem()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
