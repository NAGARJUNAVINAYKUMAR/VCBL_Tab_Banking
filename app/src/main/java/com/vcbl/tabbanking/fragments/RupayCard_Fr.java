package com.vcbl.tabbanking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.utils.DialogsUtil;

public class RupayCard_Fr extends Fragment {

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
    Storage storage;
    DialogsUtil dialogsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        setTitle(R.string.rupay_card);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());

        grid = view.findViewById(R.id.grid_view);
        ImageAdapter adapter = new ImageAdapter(getActivity(), module_name, module_image);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))
                        && "YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                    Fragment fragment = new TxnRupay_Fr();
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
                    ft.replace(R.id.content_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    dialogsUtil.alertDialog("Printer services required");
                }
            }
        });
        return view;
    }

    private void selectSmartCard(String s) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dlg_select_smart_card, null);
        builder.setView(view);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.select_smart_card);

        Dialog dialog = builder.create();
        dialog.show();
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
