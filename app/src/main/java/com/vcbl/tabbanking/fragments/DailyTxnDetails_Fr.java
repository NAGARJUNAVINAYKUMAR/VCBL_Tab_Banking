package com.vcbl.tabbanking.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.RecyclerAdapter;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.DetailedTxnBO;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.util.List;

public class DailyTxnDetails_Fr extends Fragment {

    View view;
    DetailedTxnBO detailedTxnBO;
    RecyclerView recyclerView;
    AppCompatTextView tv_main_header, tv_reports_not_found;
    RecyclerView.Adapter adapter;
    List<RecyclerItem> list;
    DbFunctions dbFunctions;
    Storage storage;
    DialogsUtil dialogsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.daily_txn_details, container, false);
        getActivity().setTitle(R.string.daily_txn_details);

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
        tv_main_header.setText(R.string.daily_txn_details);

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
                    adapter = new RecyclerAdapter(list, getActivity());
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}