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

public class Home_Fr extends Fragment {

    GridView grid;

    private static String[] module_name = {
            "Transaction",
            "Accounts",
            "Services",
            "Reports",
            "EMI Calculator",
            "Reprint"
    };
    private static int[] module_image = {
            R.drawable.ic_transactions,
            R.drawable.ic_accounts,
            R.drawable.ic_service,
            R.drawable.ic_reports,
            R.drawable.ic_calculator,
            R.drawable.ic_reprint
    };

    static String calledFrom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        setTitle(R.string.home);
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
                    frag = new Transaction();
                    calledFrom = "TRANSACTION";
                    setTitle(R.string.transaction);
                } else if (position == 1) {
                    frag = new Accounts_Fr();
                    calledFrom = "ACCOUNTS";
                    setTitle(R.string.accounts);
                } else if (position == 2) {
                    frag = new Services_Fr();
                    calledFrom = "SERVICES";
                    setTitle(R.string.service);
                } else if (position == 3) {
                    frag = new Reports_Fr();
                    calledFrom = "REPORTS";
                    setTitle(R.string.reports);
                } else if (position == 4) {
                    frag = new EMICalculator_Fr();
                    calledFrom = "EMI_CALCULATOR";
                    setTitle(R.string.emi_calculator);
                } else if (position == 5) {
                    frag = new DayPrints_Fr();
                    calledFrom = "REPRINT";
                    setTitle(R.string.reprint);
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
