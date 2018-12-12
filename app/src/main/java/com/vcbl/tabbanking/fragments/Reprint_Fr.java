package com.vcbl.tabbanking.fragments;

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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.tools.NumericKeyBoardTransformation;
import com.vcbl.tabbanking.utils.DialogsUtil;
import java.util.ArrayList;

public class Reprint_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Reprint_Fr-->";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header, tv_account_details_response;
    AppCompatSpinner spinner_product_name;
    AppCompatEditText et_account_no;
    AppCompatButton btn_clear_details, btn_search_details;
    DbFunctions dbFunctions;
    String product_code, account_no;
    static String calledFrom;
    Bundle setBundle;
    DialogsUtil dialogsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.txn, container, false);

        loadUiComponents();

        objInit();

        clearFields();

        spinner_product_name.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> productList;
        productList = dbFunctions.getProductDetails();
        assert productList != null;
        final ArrayAdapter<SpinnerList> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, productList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product_name.setAdapter(adapter);

        btn_search_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account_no = et_account_no.getText().toString().trim();
                if (product_code.equals("-- Select Code --")) {
                    dialogsUtil.alertDialog("Select product code");
                    spinner_product_name.requestFocus();
                } else if (account_no.isEmpty()) {
                    et_account_no.setError("Enter account no.");
                    et_account_no.requestFocus();
                } else {
                    calledFrom = "DAY_PRINT";
                    setBundle = new Bundle();
                    setBundle.putString("product_code", product_code);
                    setBundle.putString("account_no", account_no);
                    Fragment fragment = new DayPrints_Fr();
                    assert getFragmentManager() != null;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    fragment.setArguments(setBundle);
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

        tv_main_header = view.findViewById(R.id.tv_main_header);

        spinner_product_name = view.findViewById(R.id.spinner_product_name);

        et_account_no = view.findViewById(R.id.et_account_no);

        btn_search_details = view.findViewById(R.id.btn_get_details);
        btn_clear_details = view.findViewById(R.id.btn_clear_details);

        tv_account_details_response = view.findViewById(R.id.tv_account_details_response);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());

        dialogsUtil = new DialogsUtil(getActivity());
    }

    private void clearFields() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        tv_main_header.setText(R.string.reprint);

        spinner_product_name.setSelection(0);

        et_account_no.setText("");
        et_account_no.setHint(R.string.account_no);
        et_account_no.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_account_no.setTransformationMethod(new NumericKeyBoardTransformation());

        btn_search_details.setText(R.string.search);

        tv_account_details_response.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        product_code = String.valueOf(adapterView.getSelectedItem());
        Log.i(TAG, "SelectedItem--->" + adapterView.getSelectedItem());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
