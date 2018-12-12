package com.vcbl.tabbanking.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.ImageAdapter;

public class DirectDeposits_Fr extends Fragment {

    GridView grid;
    private static String[] module_name = {
            "Loan",
            "Saving",
            "Loan History",
            "Balance Enquiry",
            "Transaction\nCancellation"
    };
    public static int[] module_image = {
            R.drawable.ic_loan_192x192,
            R.drawable.ic_saving_192x192,
            R.drawable.ic_history,
            R.drawable.ic_bal_enquiry,
            R.drawable.ic_transactions
    };
    static String calledFrom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        setTitle(R.string.direct_deposits);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        grid = view.findViewById(R.id.grid_view);
        ImageAdapter adapter = new ImageAdapter(getActivity(), module_name, module_image);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment frag = null;
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (position == 0) {
                    frag = new Txn_Fr();
                    calledFrom = "LOAN";
                } else if (position == 1) {
                    frag = new Txn_Fr();
                    calledFrom = "SAVING";
                } else if (position == 2) {
                    frag = new Txn_Fr();
                    calledFrom = "LOAN_HISTORY";
                } else if (position == 3) {
                    frag = new Txn_Fr();
                    calledFrom = "BAL_ENQ";
                } else if (position == 4) {
                    frag = new TxnCancel_Fr();
                    calledFrom = "TXN_CANCEL";
                }
                ft.replace(R.id.content_frame, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }
}
