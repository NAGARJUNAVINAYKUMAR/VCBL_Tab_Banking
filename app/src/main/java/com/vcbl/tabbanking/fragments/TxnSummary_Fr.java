package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.TxnCancelAdapter;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.DetailedTxnBO;
import com.vcbl.tabbanking.interactors.TxnReportsBO;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.models.TxnSummaryModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import es.dmoral.toasty.Toasty;

public class TxnSummary_Fr extends Fragment {

    private static final String TAG = "TxnSummary_Fr-->";
    View view;
    CardView txn_response_card_view;
    LinearLayout ll_agent_id_name, ll_no_of_txns, ll_sum_of_txns;
    AppCompatTextView tv_header, tv_agent_id_name, tv_no_of_txns, tv_sum_of_txns, tv_txn_summary;
    DbFunctions dbFunctions;
    Storage storage;
    DialogsUtil dialogsUtil;
    DetailedTxnBO detailedTxnBO;
    ReportSearchModel searchModel;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.txn_summary, container, false);

        loadUiComponents();

        objInit();

        prepareModel();

        return view;
    }

    private void prepareModel() {
        searchModel = new ReportSearchModel();
        searchModel.setFromDate(Utility.getCurrentTimeStamp());
        searchModel.setToDate("SUMMARY");
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
                if (searchModel.getStatus().equals(Masters.Status.SUCCESS)) {
                    txn_response_card_view.setVisibility(View.VISIBLE);
                    String agentName = String.valueOf(GlobalModel.bcid) + "/"
                            + dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid));
                    tv_agent_id_name.setText(agentName);
                    if (searchModel.getNoofTxns() != null && searchModel.getNoofTxns().length() > 0) {
                        tv_no_of_txns.setText(searchModel.getNoofTxns());
                    } else {
                        tv_no_of_txns.setText("0");
                    }
                    if (searchModel.getSumofTxns() != null && searchModel.getSumofTxns().length() > 0) {
                        tv_sum_of_txns.setText(searchModel.getSumofTxns());
                    } else {
                        tv_sum_of_txns.setText("0");
                    }
                } else {
                    tv_txn_summary.setVisibility(View.VISIBLE);
                    tv_txn_summary.setText(R.string.no_reports_found);
                }
            }
        });
    }

    private void loadUiComponents() {
        txn_response_card_view = view.findViewById(R.id.txn_response_card_view);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        txn_response_card_view.setAnimation(animation);
        txn_response_card_view.setVisibility(View.GONE);

        tv_header = view.findViewById(R.id.tv_header);

        ll_agent_id_name = view.findViewById(R.id.ll_agent_id_name);
        ll_no_of_txns = view.findViewById(R.id.ll_no_of_txns);
        ll_sum_of_txns = view.findViewById(R.id.ll_sum_of_txns);

        tv_agent_id_name = view.findViewById(R.id.tv_agent_id_name);
        tv_no_of_txns = view.findViewById(R.id.tv_no_of_txns);
        tv_sum_of_txns = view.findViewById(R.id.tv_sum_of_txns);

        tv_txn_summary = view.findViewById(R.id.tv_txn_summary);
        tv_txn_summary.setVisibility(View.GONE);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
    }

    private void clearFields() {
        tv_agent_id_name.setText("");
        tv_no_of_txns.setText("");
        tv_sum_of_txns.setText("");

        tv_txn_summary.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
