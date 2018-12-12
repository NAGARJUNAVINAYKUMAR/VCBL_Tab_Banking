package com.vcbl.tabbanking.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.SpinnerList;

import java.util.ArrayList;
import java.util.Objects;

public class DepositWithdraw_Fr extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "DepositWithdraw_Fr-->";
    View view;
    AppCompatSpinner spinner_product_name;
    AppCompatTextView tv_main_header;
    DbFunctions dbFunctions;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.txn, container, false);

        loadUiComponents();

        objInit();

        clearFields();

        spinner_product_name.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> productList;
        if ("DEPOSIT".equals(RupayCard_Fr.calledFrom)) {
            tv_main_header.setText(R.string.deposit);
        } else if ("WITHDRAWAL".equals(RupayCard_Fr.calledFrom)) {
            tv_main_header.setText(R.string.withdrawal);
        } else if ("FUND_TRANSFER".equals(RupayCard_Fr.calledFrom)) {
            tv_main_header.setText(R.string.fund_transfer);
        } else if ("BAL_ENQ".equals(RupayCard_Fr.calledFrom)) {
            tv_main_header.setText(R.string.balance_enquiry);
        } else {
            tv_main_header.setText(R.string.mini_statement);
        }
        productList = dbFunctions.getProductDetails();
        assert productList != null;
        ArrayAdapter<SpinnerList> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, productList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product_name.setAdapter(arrayAdapter);
        return view;
    }

    private void loadUiComponents() {
        spinner_product_name = view.findViewById(R.id.spinner_product_name);
        tv_main_header = view.findViewById(R.id.tv_main_header);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
    }

    public void clearFields() {
        spinner_product_name.setSelection(0);
        tv_main_header.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
