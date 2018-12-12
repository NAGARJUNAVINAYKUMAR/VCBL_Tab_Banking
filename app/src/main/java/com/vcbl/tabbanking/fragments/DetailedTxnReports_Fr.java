package com.vcbl.tabbanking.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import com.vcbl.tabbanking.adapters.RecyclerAdapter;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.DetailedTxnBO;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.tools.NumericKeyBoardTransformation;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DetailedTxnReports_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "DetailedTxnReports_Fr";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header, tv_from_date, tv_to_date, tv_reports_not_found;
    AppCompatEditText et_account_no;
    AppCompatSpinner spinner_product_name;
    AppCompatButton btn_clear_details, btn_get_details;
    private int mYear, mMonth, mDay;
    //private int mHour, mMinute;
    String fromDate, toDate, product_code = "", account_no = "";
    DetailedTxnBO detailedTxnBO;
    RecyclerView.Adapter adapter;
    List<RecyclerItem> list;
    RecyclerView recyclerView;
    public static String reportsFlag;
    DialogsUtil dialogsUtil;
    DbFunctions dbFunctions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detailed_txn_reports, container, false);

        loadUiComponents();

        objInit();

        clearFields();

        spinner_product_name.setOnItemSelectedListener(this);
        final ArrayList<SpinnerList> productList = dbFunctions.getProductDetails();
        assert productList != null;
        final ArrayAdapter<SpinnerList> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, productList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product_name.setAdapter(arrayAdapter);

        tv_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list != null) {
                    list.clear();
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                // get from date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                //tv_from_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                fromDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                Log.i(TAG, "fromDate-->" + fromDate);
                                tv_from_date.setText(fromDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        tv_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get to date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                //tv_to_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                toDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                Log.i(TAG, "toDate-->" + toDate);
                                tv_to_date.setText(toDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // get current time
        /*final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();*/

        btn_get_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account_no = et_account_no.getText().toString().trim();
                if (tv_from_date.getText().toString().trim().isEmpty()) {
                    tv_from_date.requestFocus();
                    dialogsUtil.alertDialog("Please select from date");
                } else if (tv_to_date.getText().toString().trim().isEmpty()) {
                    tv_to_date.requestFocus();
                    dialogsUtil.alertDialog("Please select to date");
                } else if (product_code.equals("-- Select Code --")) {
                    product_code = "";
                } else {
                    prepareModel();
                }
            }
        });

        btn_clear_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*tv_from_date.setText("");
                tv_from_date.setHint(R.string.from_date);
                tv_to_date.setText("");
                tv_to_date.setHint(R.string.to_date);
                spinner_product_name.setSelection(0);
                et_account_no.setText("");
                et_account_no.setHint(R.string.account_no);
                if (list != null) {
                    list.clear();
                }*/

                clearFields();

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);

        tv_main_header = view.findViewById(R.id.tv_main_header);
        tv_from_date = view.findViewById(R.id.tv_from_date);
        tv_to_date = view.findViewById(R.id.tv_to_date);

        spinner_product_name = view.findViewById(R.id.spinner_product_name);

        et_account_no = view.findViewById(R.id.et_account_no);

        btn_get_details = view.findViewById(R.id.btn_get_details);
        btn_clear_details = view.findViewById(R.id.btn_clear_details);

        recyclerView = view.findViewById(R.id.recycler_view);

        tv_reports_not_found = view.findViewById(R.id.tv_reports_not_found);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());

        dialogsUtil = new DialogsUtil(getActivity());
    }

    private void clearFields() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        tv_main_header.setText(R.string.detailed_txn_reports);

        tv_from_date.setText("");
        tv_from_date.setHint(R.string.date_format);

        tv_to_date.setText("");
        tv_to_date.setHint(R.string.date_format);

        tv_reports_not_found.setVisibility(View.GONE);

        spinner_product_name.setSelection(0);

        et_account_no.setText("");
        et_account_no.setHint(R.string.account_no);
        et_account_no.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_account_no.setTransformationMethod(new NumericKeyBoardTransformation());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (list != null) {
            list.clear();
        }
    }

    private void prepareModel() {
        ReportSearchModel searchModel = new ReportSearchModel();
        searchModel.setFromDate(fromDate);
        searchModel.setToDate(toDate);
        searchModel.setProductCode(product_code);
        searchModel.setAccountNo(account_no);
        searchModel.setBcID(GlobalModel.bcid);
        searchModel.setBranchID(GlobalModel.branchid);
        searchModel.setTerminalID(GlobalModel.microatmid);
        detailedTxnBO = new DetailedTxnBO(getActivity());
        detailedTxnBO.detailedTxnRequest(searchModel);
        detailedTxnBO.setOnTaskFinishedEvent(new DetailedTxnBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(ReportSearchModel searchModel) {
                if (searchModel.getList() != null) {
                    list = searchModel.getList();
                    adapter = new RecyclerAdapter(list, getActivity());
                    reportsFlag = "DTR";
                    recyclerView.setAdapter(adapter);
                } else {
                    if (list != null) {
                        list.clear();
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    recyclerView.setVisibility(View.GONE);
                    tv_reports_not_found.setVisibility(View.VISIBLE);
                    tv_reports_not_found.setText(R.string.no_reports_found);
                    Toasty.error(getActivity(), "No reports found !", Toast.LENGTH_SHORT, false).show();
                }
            }
        });
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
