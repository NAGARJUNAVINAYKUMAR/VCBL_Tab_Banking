package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leopard.api.Printer;
import com.prowesspride.api.Printer_GEN;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Conversions;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class PrintPreview_Fr extends Fragment {

    private static final String TAG = "PrintPreview_Fr-->";
    View view;
    CardView cardView;
    public static Printer ptr;
    public static Printer_GEN ptrGen;
    LinearLayout ll_branch_name, ll_date_time, ll_customer_name, ll_account_no, ll_entered_amount,
            ll_aadhaar_no, ll_mobile_no, ll_pan_no, ll_balance, ll_overdue, ll_ref_no, ll_agent_id_name, ll_btns;
    AppCompatTextView tv_preview_header, tv_pvw_branch_name, tv_pvw_date_time, tv_pvw_customer_name, tv_pvw_account_no, tv_pvw_entered_amount,
            tv_pvw_aadhaar_no, tv_pvw_mobile_no, tv_pvw_pan_no, tv_pvw_balance, tv_pvw_overdue, tv_pvw_overdue_label, tv_pvw_ref_no, tv_pvw_agent_id_name;
    AppCompatButton btn_submit_print, btn_cancel_print;
    private final static int MESSAGE_BOX = 1;
    private int iRetVal;
    ProgressDialog progressDialog;
    public static final int DEVICE_NOTCONNECTED = -100;
    String current_date_time, branchname, agentname, avlBalance, ledgBalance;
    Bundle getPreviewData = null;
    DbFunctions dbFunctions;
    DialogsUtil dialogsUtil;
    public static String calledFrom;
    Storage storage;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.print_preview, container, false);

        setTitle(R.string.direct_deposits);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        loadUiComponents();

        objInit();

        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            printerObjInit();
        }

        branchname = dbFunctions.getBranchName(String.valueOf(GlobalModel.branchid));
        agentname = dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid));
        //current_date_time = DateFormat.getDateTimeInstance().format(new Date());
        //current_date_time = android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss ", new Date()).toString();
        current_date_time = Utility.getCurrentTimeStamp();
        Log.i(TAG, current_date_time);

        if ("LOAN_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
            setSubTitle(R.string.loan_preview);
            if (getPreviewData != null) {
                if (getPreviewData.getString("accountStatus") != null
                        && !"".equals(getPreviewData.getString("accountStatus"))) {
                    if (Integer.parseInt(getPreviewData.getString("accountStatus")) == 11) {
                        tv_preview_header.setTextColor(getResources().getColor(R.color.colorWhite));
                        tv_preview_header.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        tv_preview_header.setText("Loan Preview" + " (NPA Account)");
                    }
                } else {
                    tv_preview_header.setText(R.string.loan_preview);
                }
                tv_pvw_branch_name.setText(branchname);
                tv_pvw_date_time.setText(current_date_time);
                tv_pvw_customer_name.setText(getPreviewData.getString("getCustName"));
                tv_pvw_account_no.setText(getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no"));
                tv_pvw_entered_amount.setText(getResources().getString(R.string.Rs) + getPreviewData.getString("entered_amount"));
                ll_aadhaar_no.setVisibility(View.GONE);
                ll_mobile_no.setVisibility(View.GONE);
                ll_pan_no.setVisibility(View.GONE);
                if (getPreviewData.getString("getAvlbalance") != null
                        && !"".equals(getPreviewData.getString("getAvlbalance"))
                        && getPreviewData.getString("getAvlbalance").length() > 0) {
                    if (getPreviewData.getString("getAvlbalance").contains("-")) {
                        avlBalance = (getPreviewData.getString("getAvlbalance")).substring(1);
                    } else {
                        avlBalance = (getPreviewData.getString("getAvlbalance"));
                    }
                    tv_pvw_balance.setText(getResources().getString(R.string.Rs) + avlBalance);
                } else {
                    avlBalance = "0";
                    tv_pvw_balance.setText(getResources().getString(R.string.Rs) + avlBalance);
                }
                if (getPreviewData.getString("getLedgbalance") != null
                        && !"".equals(getPreviewData.getString("getLedgbalance"))
                        && getPreviewData.getString("getLedgbalance").length() > 0) {

                    if (getPreviewData.getString("getLedgbalance").contains("-")) {
                        ledgBalance = (getPreviewData.getString("getLedgbalance").substring(1));
                        tv_pvw_overdue.setText(getResources().getString(R.string.Rs) + ledgBalance);
                    } else {
                        tv_pvw_overdue_label.setText(getResources().getString(R.string.advanced_balance));
                        ledgBalance = (getPreviewData.getString("getLedgbalance"));
                        tv_pvw_overdue.setText(getResources().getString(R.string.Rs) + ledgBalance);
                    }
                } else {
                    ledgBalance = "0.00";
                    tv_pvw_overdue.setText(getResources().getString(R.string.Rs) + ledgBalance);
                }
                tv_pvw_ref_no.setText(getPreviewData.getString("getRrn"));
                tv_pvw_agent_id_name.setText(GlobalModel.bcid + "/" + agentname);
                calledFrom = "LOAN_PREVIEW_DONE";
            }
        } else if ("SAVING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
            setSubTitle(R.string.saving_preview);
            if (getPreviewData != null) {
                tv_preview_header.setText(R.string.saving_preview);
                tv_pvw_branch_name.setText(branchname);
                tv_pvw_date_time.setText(current_date_time);
                tv_pvw_customer_name.setText(getPreviewData.getString("getCustName"));
                tv_pvw_account_no.setText(getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no"));
                tv_pvw_entered_amount.setText(getResources().getString(R.string.Rs) + getPreviewData.getString("entered_amount"));
                ll_aadhaar_no.setVisibility(View.GONE);
                ll_mobile_no.setVisibility(View.GONE);
                ll_pan_no.setVisibility(View.GONE);
                if (getPreviewData.getString("getAvlbalance") != null
                        && !"".equals(getPreviewData.getString("getAvlbalance"))
                        && getPreviewData.getString("getAvlbalance").length() > 0) {
                    avlBalance = (getPreviewData.getString("getAvlbalance"));
                    tv_pvw_balance.setText(getResources().getString(R.string.Rs) + avlBalance);
                } else {
                    avlBalance = "0.00";
                    tv_pvw_balance.setText(getResources().getString(R.string.Rs) + avlBalance);
                }
                ll_overdue.setVisibility(View.GONE);
                tv_pvw_ref_no.setText(getPreviewData.getString("getRrn"));
                tv_pvw_agent_id_name.setText(GlobalModel.bcid + "/" + agentname);
                calledFrom = "SAVING_PREVIEW_DONE";
            }
        } else if ("AADHAAR_SEEDING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
            setTitle(R.string.service);
            setSubTitle(R.string.aadhaar_seeding_preview);
            if (getPreviewData != null) {
                tv_preview_header.setText(R.string.aadhaar_seeding_preview);
                tv_pvw_branch_name.setText(branchname);
                tv_pvw_date_time.setText(current_date_time);
                tv_pvw_customer_name.setText(getPreviewData.getString("getCustName"));
                tv_pvw_account_no.setText(getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no"));
                ll_entered_amount.setVisibility(View.GONE);
                ll_aadhaar_no.setVisibility(View.VISIBLE);
                tv_pvw_aadhaar_no.setText(getPreviewData.getString("aadhaar_no"));
                ll_mobile_no.setVisibility(View.GONE);
                ll_balance.setVisibility(View.GONE);
                ll_pan_no.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.GONE);
                tv_pvw_ref_no.setText(getPreviewData.getString("getRrn"));
                tv_pvw_agent_id_name.setText(GlobalModel.bcid + "/" + agentname);
                calledFrom = "AADHAAR_PREVIEW_DONE";
            }
        } else if ("MOBILE_SEEDING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
            setTitle(R.string.service);
            setSubTitle(R.string.mobile_seeding_preview);
            if (getPreviewData != null) {
                tv_preview_header.setText(R.string.mobile_seeding_preview);
                tv_pvw_branch_name.setText(branchname);
                tv_pvw_date_time.setText(current_date_time);
                tv_pvw_customer_name.setText(getPreviewData.getString("getCustName"));
                tv_pvw_account_no.setText(getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no"));
                ll_entered_amount.setVisibility(View.GONE);
                ll_aadhaar_no.setVisibility(View.GONE);
                ll_mobile_no.setVisibility(View.VISIBLE);
                ll_pan_no.setVisibility(View.GONE);
                tv_pvw_mobile_no.setText(getPreviewData.getString("mobile_no"));
                ll_balance.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.GONE);
                tv_pvw_ref_no.setText(getPreviewData.getString("getRrn"));
                tv_pvw_agent_id_name.setText(GlobalModel.bcid + "/" + agentname);
                calledFrom = "MOBILE_PREVIEW_DONE";
            }
        } else {
            setTitle(R.string.service);
            setSubTitle(R.string.pan_update_preview);
            if (getPreviewData != null) {
                tv_preview_header.setText(R.string.pan_update_preview);
                tv_pvw_branch_name.setText(branchname);
                tv_pvw_date_time.setText(current_date_time);
                tv_pvw_customer_name.setText(getPreviewData.getString("getCustName"));
                tv_pvw_account_no.setText(getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no"));
                ll_entered_amount.setVisibility(View.GONE);
                ll_aadhaar_no.setVisibility(View.GONE);
                ll_mobile_no.setVisibility(View.GONE);
                ll_pan_no.setVisibility(View.VISIBLE);
                tv_pvw_pan_no.setText(getPreviewData.getString("pan_no"));
                ll_balance.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.GONE);
                tv_pvw_ref_no.setText(getPreviewData.getString("getRrn"));
                tv_pvw_agent_id_name.setText(GlobalModel.bcid + "/" + agentname);
                calledFrom = "PAN_PREVIEW_DONE";
            }
        }

        btn_submit_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                    printerObjInit();
                    if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                        EnterTextTask asynctask = new EnterTextTask();
                        asynctask.execute(0);
                    } else {
                        EnterAsycForMobile asynctask = new EnterAsycForMobile();
                        asynctask.execute(0);
                    }
                } else {
                    dialogsUtil.alertDialog("Printer services required");
                }
            }
        });

        btn_cancel_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printToBack();
            }
        });

        return view;
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        ll_branch_name = view.findViewById(R.id.ll_branch_name);
        ll_date_time = view.findViewById(R.id.ll_date_time);
        ll_customer_name = view.findViewById(R.id.ll_customer_name);
        ll_account_no = view.findViewById(R.id.ll_account_no);
        ll_entered_amount = view.findViewById(R.id.ll_entered_amount);
        ll_aadhaar_no = view.findViewById(R.id.ll_aadhaar_no);
        ll_mobile_no = view.findViewById(R.id.ll_mobile_no);
        ll_pan_no = view.findViewById(R.id.ll_pan_no);
        ll_balance = view.findViewById(R.id.ll_balance);
        ll_overdue = view.findViewById(R.id.ll_overdue);
        ll_ref_no = view.findViewById(R.id.ll_ref_no);
        ll_agent_id_name = view.findViewById(R.id.ll_agent_id_name);

        ll_btns = view.findViewById(R.id.ll_btns);

        tv_preview_header = view.findViewById(R.id.tv_preview_header);
        tv_pvw_branch_name = view.findViewById(R.id.tv_pvw_branch_name);
        tv_pvw_date_time = view.findViewById(R.id.tv_pvw_date_time);
        tv_pvw_customer_name = view.findViewById(R.id.tv_pvw_customer_name);
        tv_pvw_account_no = view.findViewById(R.id.tv_pvw_account_no);
        tv_pvw_entered_amount = view.findViewById(R.id.tv_pvw_entered_amount);
        tv_pvw_aadhaar_no = view.findViewById(R.id.tv_pvw_aadhaar_no);
        tv_pvw_mobile_no = view.findViewById(R.id.tv_pvw_mobile_no);
        tv_pvw_pan_no = view.findViewById(R.id.tv_pvw_pan_no);
        tv_pvw_balance = view.findViewById(R.id.tv_pvw_balance);
        tv_pvw_overdue = view.findViewById(R.id.tv_pvw_overdue);
        tv_pvw_overdue_label = view.findViewById(R.id.tv_pvw_overdue_label);
        tv_pvw_ref_no = view.findViewById(R.id.tv_pvw_ref_no);
        tv_pvw_agent_id_name = view.findViewById(R.id.tv_pvw_agent_id_name);

        btn_submit_print = view.findViewById(R.id.btn_submit_print);
        btn_cancel_print = view.findViewById(R.id.btn_cancel_print);
    }

    private void objInit() {
        storage = new Storage(getActivity());
        dbFunctions = DbFunctions.getInstance(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
        getPreviewData = this.getArguments();
    }

    private void printerObjInit() {
        if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
            try {
                OutputStream outSt = BluetoothComm.mosOut;
                InputStream inSt = BluetoothComm.misIn;
                if (outSt != null && inSt != null) {
                    ptr = new Printer(LoginActivity.setupInstance, outSt, inSt);
                } else {
                    dialogsUtil.alertDialog("Leopard library not activated");
                }
            } catch (Exception e) {
                e.printStackTrace();
                //dialogsUtil.alertDialog("Leopard doesn't activated");
            }
        } else {
            try {
                InputStream input = BluetoothComm.misIn;
                OutputStream outstream = BluetoothComm.mosOut;
                ptrGen = new Printer_GEN(LoginActivity.mSetup, outstream, input);
                Log.e(TAG, "printer gen is activated");
            } catch (Exception e) {
                e.printStackTrace();
                //dialogsUtil.alertDialog("Leopard doesn't activated");
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    public class EnterTextTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.print);
            progressDialog.setMessage("Printing in progress...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ptr.iFlushBuf();
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "         RECEIPT");
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                ptr.iBmpPrint(getActivity(), R.drawable.logo3);
                Character ch = '-';
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Branch    : " + branchname.trim());
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Date:" + current_date_time.trim());
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);
                if ("LOAN_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                            byteToRupeeConversion("Amount    : ", "" + getPreviewData.getString("entered_amount")));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                            byteToRupeeConversion("Balance   : ", "" + avlBalance));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");

                    if (getPreviewData.getString("getLedgbalance") != null
                            && !"".equals(getPreviewData.getString("getLedgbalance"))
                            && getPreviewData.getString("getLedgbalance").length() > 0) {
                        if (getPreviewData.getString("getLedgbalance").contains("-")) {
                            ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                    byteToRupeeConversion("Overdue   : ", "" + getPreviewData.getString("getLedgbalance").substring(1)));
                        } else {
                            ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                    byteToRupeeConversion("Adv Bal   : ", "" + getPreviewData.getString("getLedgbalance")));
                        }
                    } else {
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, byteToRupeeConversion("Overdue   : ", "0"));
                    }
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "LOAN_PREVIEW_DONE";
                } else if ("SAVING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                            byteToRupeeConversion("Amount    : ", "" + getPreviewData.getString("entered_amount")));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                            byteToRupeeConversion("Balance   : ", "" + avlBalance));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "SAVING_PREVIEW_DONE";
                } else if ("AADHAAR_SEEDING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Aadhar No : " + getPreviewData.getString("aadhaar_no"));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "AADHAAR_PREVIEW_DONE";
                } else if ("MOBILE_SEEDING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Mobile No : " + getPreviewData.getString("mobile_no"));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "MOBILE_PREVIEW_DONE";
                } else if ("PAN_UPDATE_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "PAN No.   : " + getPreviewData.getString("pan_no"));
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "PAN_PREVIEW_DONE";
                } else {
                    handler.obtainMessage(MESSAGE_BOX, "Print data not found !").sendToTarget();
                }
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "       Thank you");
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);
                ptr.iPaperFeed();
                String empty = " \n" + " \n" + " \n" + " \n";
                ptr.iPrinterAddData((byte) 0x01, empty);
                iRetVal = ptr.iStartPrinting(1);
            } catch (NullPointerException e) {
                e.printStackTrace();
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICE_NOTCONNECTED) {
                handler.obtainMessage(DEVICE_NOTCONNECTED, "Device not connected").sendToTarget();
            } else if (iRetVal == Printer.PR_SUCCESS) {
                printToBack();
            } else if (iRetVal == Printer.PR_PLATEN_OPEN) {
                handler.obtainMessage(MESSAGE_BOX, "Printer platen is open").sendToTarget();
            } else if (iRetVal == Printer.PR_PAPER_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Printer paper is out").sendToTarget();
            } else if (iRetVal == Printer.PR_IMPROPER_VOLTAGE) {
                handler.obtainMessage(MESSAGE_BOX, "Printer improper voltage").sendToTarget();
            } else if (iRetVal == Printer.PR_FAIL) {
                handler.obtainMessage(MESSAGE_BOX, "Print failed,\nPlease check BT connection").sendToTarget();
            } else if (iRetVal == Printer.PR_PARAM_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Printer param error").sendToTarget();
            } else if (iRetVal == Printer.PR_NO_RESPONSE) {
                handler.obtainMessage(MESSAGE_BOX, "No response from Leopard device").sendToTarget();
            } else if (iRetVal == Printer.PR_DEMO_VERSION) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else if (iRetVal == Printer.PR_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Connected  device is not license authenticated.").sendToTarget();
            } else if (iRetVal == Printer.PR_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Library not valid").sendToTarget();
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class EnterAsycForMobile extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.print);
            progressDialog.setMessage("Printing in progress...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ptrGen.iFlushBuf();
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "         RECEIPT");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                ptrGen.iBmpPrint(getActivity(), R.drawable.logo3);
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Branch    : " + branchname.trim());
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Date:" + current_date_time.trim());
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                if ("LOAN_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                            byteToRupeeConversion("Amount    : ", "" + getPreviewData.getString("entered_amount")));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                            byteToRupeeConversion("Balance   : ", "" + avlBalance));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");

                    if (getPreviewData.getString("getLedgbalance") != null
                            && !"".equals(getPreviewData.getString("getLedgbalance"))
                            && getPreviewData.getString("getLedgbalance").length() > 0) {
                        if (getPreviewData.getString("getLedgbalance").contains("-")) {
                            ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                    byteToRupeeConversion("Overdue   : ", "" + getPreviewData.getString("getLedgbalance").substring(1)));
                        } else {
                            ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                    byteToRupeeConversion("Adv Bal   : ", "" + getPreviewData.getString("getLedgbalance")));
                        }
                    } else {
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, byteToRupeeConversion("Overdue   : ", "0"));
                    }
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "LOAN_PREVIEW_DONE";
                } else if ("SAVING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                            byteToRupeeConversion("Amount    : ", "" + getPreviewData.getString("entered_amount")));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                            byteToRupeeConversion("Balance   : ", "" + avlBalance));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "SAVING_PREVIEW_DONE";
                } else if ("AADHAAR_SEEDING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Aadhar No : " + getPreviewData.getString("aadhaar_no"));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "AADHAAR_PREVIEW_DONE";
                } else if ("MOBILE_SEEDING_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Mobile No : " + getPreviewData.getString("mobile_no"));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "MOBILE_PREVIEW_DONE";
                } else if ("PAN_UPDATE_PREVIEW".equals(LoanSaving_Fr.calledFrom)) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : " + getPreviewData.getString("getCustName").trim() + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                            + getPreviewData.getString("product_code") + "/" + getPreviewData.getString("account_no") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "PAN No.   : " + getPreviewData.getString("pan_no"));
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : " + getPreviewData.getString("getRrn") + "\n");
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
                    calledFrom = "PAN_PREVIEW_DONE";
                } else {
                    handler.obtainMessage(MESSAGE_BOX, "Print data not found !").sendToTarget();
                }
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "       Thank you");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                String empty = " \n" + " \n" + " \n" + " \n";
                ptrGen.iAddData((byte) 0x01, empty);
                iRetVal = ptrGen.iStartPrinting(1);
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICE_NOTCONNECTED) {
                handler.obtainMessage(1, "Device not connected").sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                printToBack();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                handler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                handler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                handler.obtainMessage(1, "Printer at improper voltage").sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                handler.obtainMessage(1, "Print failed,\nPlease check BT connection").sendToTarget();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR) {
                handler.obtainMessage(1, "Parameter error").sendToTarget();
            } else if (iRetVal == Printer_GEN.NO_RESPONSE) {
                handler.obtainMessage(1, "No response from Pride device").sendToTarget();
            } else if (iRetVal == Printer_GEN.DEMO_VERSION) {
                handler.obtainMessage(1, "Library in demo version").sendToTarget();
            } else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID) {
                handler.obtainMessage(1, "Connected  device is not authenticated.").sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_ACTIVATED) {
                handler.obtainMessage(1, "Library not activated").sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_SUPPORTED) {
                handler.obtainMessage(1, "Not Supported").sendToTarget();
            } else {
                handler.obtainMessage(1, "Unknown Response from Device").sendToTarget();
            }
            super.onPostExecute(result);
        }
    }

    private byte[] byteToRupeeConversion(String hexText, String hexValue) {
        byte[] rupeeBal;
        String hexText1 = Conversions.stringToHex(hexText);
        String hexValue1 = Conversions.stringToHex(hexValue);
        String ptrBalance = hexText1 + "B0" + hexValue1;
        rupeeBal = Conversions.hexToByteArray(ptrBalance);
        return rupeeBal;
    }

    private void printToBack() {
        Fragment fragment;
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if ("AADHAAR_PREVIEW_DONE".equals(PrintPreview_Fr.calledFrom)
                || "MOBILE_PREVIEW_DONE".equals(PrintPreview_Fr.calledFrom)
                || "PAN_PREVIEW_DONE".equals(PrintPreview_Fr.calledFrom)) {
            fragment = new AadhaarMobile_Fr();
        } else {
            fragment = new Txn_Fr();
        }
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    dialogsUtil.alertDialog(str);
                    break;
                default:
                    break;
            }
        }
    };

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }

}
