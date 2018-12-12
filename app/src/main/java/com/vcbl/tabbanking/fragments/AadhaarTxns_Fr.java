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

public class AadhaarTxns_Fr extends Fragment {

    GridView grid;
    private static String[] module_name = {
            "Deposit",
            "Withdrawal",
            "Fund Transfer",
            "Balance Enquiry"
    };
    public static int[] module_image = {
            R.drawable.ic_deposits_192x192,
            R.drawable.ic_withdraw,
            R.drawable.ic_fund_transfer_192x192,
            R.drawable.ic_rupay
    };
    static String calledFrom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        setTitle(R.string.aadhaar_transactions);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        grid = view.findViewById(R.id.grid_view);
        ImageAdapter adapter = new ImageAdapter(getActivity(), module_name, module_image);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Fragment frag = new TxnAadhar_Fr();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (position == 0) {
                    calledFrom = "DEPOSIT";
                } else if (position == 1) {
                    calledFrom = "WITHDRAWAL";
                } else if (position == 2) {
                    calledFrom = "FUND_TRANSFER";
                } else if (position == 3) {
                    calledFrom = "BAL_ENQ";
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

