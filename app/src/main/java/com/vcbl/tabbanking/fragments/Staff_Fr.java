package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.ImageAdapter;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.BCEnrollmentBO;
import com.vcbl.tabbanking.interactors.BCStatusBO;
import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.tools.CryptoHelper;
import com.vcbl.tabbanking.utils.DialogsUtil;

/**
 * Created by ecosoft2 on 27-Feb-18.
 */

public class Staff_Fr extends Fragment {

    private static final String TAG = "Staff_Fr";
    GridView grid;
    private static String[] module_name = {
            "Staff Create",
            "Staff Update"
    };
    public static int[] module_image = {
            R.drawable.ic_new_cst,
            R.drawable.ic_new_cst
    };
    public static String calledFrom;
    AlertDialog dialog, pwrdDialog;
    private final int MESSAGE_BOX = 1;
    DialogsUtil dialogsUtil;

    // dialog variables
    AppCompatEditText et_employee_id, et_pin_no;
    AppCompatButton btn_go,btn_submit;
    String emp_id, pin_no, db_pin_no;
    DbFunctions dbFunctions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        dialogsUtil = new DialogsUtil(getActivity());

        grid = view.findViewById(R.id.grid_view);
        ImageAdapter adapter = new ImageAdapter(getActivity(), module_name, module_image);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    calledFrom = "STAFF_CREATE";
                    Fragment fragment = new BCEnrollment_Fr();
                    /*assert getFragmentManager() != null;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();*/
                    fragmentNavigation(fragment);
                    setTitle(R.string.staff);
                } else if (position == 1) {
                    calledFrom = "STAFF_UPDATE";
                    staffUpdateAlert();
                }
            }
        });
        return view;
    }

    private void staffUpdateAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dlg_bc_update, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.bc_update);

        et_employee_id = view.findViewById(R.id.et_employee_id);
        btn_go = view.findViewById(R.id.btn_go);

        dialog = builder.create();
        dialog.show();

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emp_id = et_employee_id.getText().toString().trim();
                if (emp_id.isEmpty()) {
                    et_employee_id.setError("Enter Employee ID");
                    et_employee_id.requestFocus();
                } else {
                    dbFunctions = DbFunctions.getInstance(getActivity());
                    Cursor cursor = dbFunctions.getPinNo(emp_id);
                    if (cursor.getCount() < 1) {
                        cursor.close();
                    } else {
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    db_pin_no = CryptoHelper.decrypt(cursor.getString(0));
                                    Log.i(TAG, "db_pin_no-->" + db_pin_no);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } while (cursor.moveToNext());
                            if (db_pin_no != null && !"".equals(db_pin_no) && db_pin_no.length() > 0) {
                                dialog.dismiss();
                                passwordVerify();
                            } else {
                                dialog.dismiss();
                                dialogsUtil.alertDialog("User Doesn't exist");
                            }
                        }
                    }
                }
            }
        });
    }

    private void passwordVerify() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dlg_pw_verify, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.bc_update);

        et_pin_no = view.findViewById(R.id.et_pin_no);
        btn_submit = view.findViewById(R.id.btn_submit);

        pwrdDialog = builder.create();
        pwrdDialog.show();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pin_no = et_pin_no.getText().toString().trim();
                if (pin_no.isEmpty()) {
                    et_pin_no.setError("Enter Employee ID");
                    et_pin_no.requestFocus();
                } else {
                    if (!db_pin_no.equals(pin_no)) {
                        dialogsUtil.alertDialog("Password doesn't match");
                    } else {
                        prepareModel();
                    }
                }
            }
        });
    }

    private void prepareModel() {
        BCStatusBO bcStatusBO = new BCStatusBO(getActivity());
        BCEnrollmentModel bcModel = new BCEnrollmentModel();
        bcModel.setEmpid(emp_id);
        bcStatusBO.bcUpdateRequest(bcModel);
        bcStatusBO.setOnTaskFinishedEvent(new BCStatusBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(BCEnrollmentModel response) {
                if (response != null) {
                    pwrdDialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putString("getTitle", response.getTitle());
                    bundle.putString("getFname", response.getFname());
                    bundle.putString("getLname", response.getLname());
                    bundle.putString("branch_name", response.getBc_code());
                    bundle.putString("getEmpid", response.getEmpid());
                    bundle.putString("getPhone", response.getPhone());
                    bundle.putString("getEmail", response.getEmail());
                    bundle.putString("getBc_id", String.valueOf(response.getBc_id()));
                    bundle.putString("getBranch_id", String.valueOf(response.getBranch_id()));
                    Fragment fragment = new BCEnrollment_Fr();
                    fragment.setArguments(bundle);
                    fragmentNavigation(fragment);
                } else {
                    et_pin_no.setText("");
                    et_pin_no.setHint(R.string.password);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void fragmentNavigation(Fragment fragment) {
        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }
}
