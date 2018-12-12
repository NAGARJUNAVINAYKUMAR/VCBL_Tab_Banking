package com.vcbl.tabbanking.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.TxnCancelAdapter;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.DetailedTxnBO;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TxnCancel_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TxnCancel_Fr-->";
    View view;
    DetailedTxnBO detailedTxnBO, cancelBO;
    RecyclerView recyclerView;
    AppCompatTextView tv_main_header, tv_reports_not_found;
    RecyclerView.Adapter adapter;
    DbFunctions dbFunctions;
    Storage storage;
    DialogsUtil dialogsUtil;
    List<RecyclerItem> list;
    RecyclerItem recyclerItem;
    int itemPosition, reasonId;
    String reason;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.daily_txn_details, container, false);

        setTitle(R.string.direct_deposits);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
        setSubTitle(R.string.transaction_cancellation);

        initViews();

        objInit();

        if (list != null) {
            list.clear();
        }

        prepareModel();

        return view;
    }

    private void initViews() {
        tv_main_header = view.findViewById(R.id.tv_main_header);
        tv_main_header.setText(R.string.transaction_cancellation);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        tv_reports_not_found = view.findViewById(R.id.tv_reports_not_found);
        tv_reports_not_found.setVisibility(View.GONE);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    AppCompatSpinner spinner_cancel_reason;

    private void prepareModel() {
        ReportSearchModel searchModel = new ReportSearchModel();
        searchModel.setFromDate(Utility.getCurrentTimeStamp());
        searchModel.setToDate(Utility.getCurrentTimeStamp());
        searchModel.setProductCode("");
        searchModel.setAccountNo("");
        searchModel.setBcID(GlobalModel.bcid);
        searchModel.setBranchID(GlobalModel.branchid);
        searchModel.setTerminalID(GlobalModel.microatmid);
        detailedTxnBO = new DetailedTxnBO(getActivity());
        detailedTxnBO.detailedTxnRequest(searchModel);
        detailedTxnBO.setOnTaskFinishedEvent(new DetailedTxnBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(ReportSearchModel searchModel) {
                if (list != null) {
                    list.clear();
                }
                if (searchModel.getList() == null
                        || searchModel.getList().size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    tv_reports_not_found.setVisibility(View.VISIBLE);
                    tv_reports_not_found.setText(R.string.no_reports_found);
                } else {
                    list = searchModel.getList();
                    recyclerView.setAdapter(new TxnCancelAdapter(list, new TxnCancelAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final RecyclerItem item, final int position) {

                            final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            View view = inflater.inflate(R.layout.txn_cancel_reason, null);
                            builder1.setView(view);
                            builder1.setCancelable(false);
                            builder1.setIcon(R.drawable.app_logo);
                            builder1.setTitle(R.string.request);

                            final AlertDialog dialog = builder1.create();
                            dialog.show();

                            spinner_cancel_reason = view.findViewById(R.id.spinner_cancel_reason);
                            spinner_cancel_reason.setOnItemSelectedListener(TxnCancel_Fr.this);

                            ArrayList<SpinnerList> reasons = dbFunctions.getReasons();
                            assert reasons != null;
                            ArrayAdapter<SpinnerList> arrayAdapter = new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_spinner_item, reasons);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_cancel_reason.setAdapter(arrayAdapter);

                            AppCompatButton btn_yes = view.findViewById(R.id.btn_yes);
                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (reasonId == 0) {
                                        dialogsUtil.alertDialog("Select Reason");
                                    } else {
                                        itemPosition = position;
                                        recyclerItem = list.get(position);
                                        txnCancelRequest();
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AppCompatButton btn_no = view.findViewById(R.id.btn_no);
                            btn_no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });
                        }
                    }, getActivity()));
                }
            }
        });
    }

    private void txnCancelRequest() {
        ReportSearchModel searchModel1 = new ReportSearchModel();
        searchModel1.setFromDate(Utility.getCurrentTimeStamp());// used as temp for flag
        searchModel1.setToDate("CANCEL");
        //searchModel1.setProductCode(recyclerItem.getProductCode());
        searchModel1.setAccountNo(recyclerItem.getAccountNo());
        searchModel1.setServerRrn(recyclerItem.getServerRRN());
        searchModel1.setBcID(GlobalModel.bcid);
        searchModel1.setBranchID(GlobalModel.branchid);
        searchModel1.setTerminalID(GlobalModel.microatmid);
        searchModel1.setProductCode(reason);

        cancelBO = new DetailedTxnBO(getActivity());
        cancelBO.detailedTxnRequest(searchModel1);
        cancelBO.setOnTaskFinishedEvent(new DetailedTxnBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(ReportSearchModel searchModel) {
                if (searchModel.getStatus().equals(Masters.Status.SUCCESS)) {
                    Toasty.success(getActivity(), "Request processed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.warning(getActivity(), "Unable process the request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        reasonId = adapterView.getSelectedItemPosition();
        reason = String.valueOf(adapterView.getSelectedItem());
        Log.i(TAG, "reasonId--->" + adapterView.getSelectedItemPosition());
        Log.i(TAG, "reason--->" + String.valueOf(adapterView.getSelectedItem()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
