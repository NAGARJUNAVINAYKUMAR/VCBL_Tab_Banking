package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leopard.api.FPS;
import com.leopard.api.FpsConfig;
import com.leopard.api.HexString;
import com.leopard.api.Printer;
import com.prowesspride.api.Printer_GEN;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.TransactionBO;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Conversions;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import org.apache.commons.validator.routines.checkdigit.VerhoeffCheckDigit;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoanSaving_Fr extends Fragment {

    private static final String TAG = "LoanSaving_Fr--> ";
    View view;
    CardView cardView;
    LinearLayout ll_account_name, ll_avl_balance, ll_overdue, ll_account_status, ll_account_type,
            ll_enter_amount, ll_branch_code, ll_account_num, ll_sanctioned_dt, ll_sanctioned_amount,
            ll_installment_amount, ll_expiry_date, ll_no_of_installments, ll_outstand_balance,
            ll_reserve1, ll_reserve2, ll_reserve3, ll_installment_start_dt, ll_pending_installments, ll_btns;
    AppCompatTextView tv_header, tv_account_name, tv_avl_balance, tv_overdue, tv_overdue_label, tv_account_status,
            tv_account_type, tv_enter_amount, tv_branch_code, tv_account_num, tv_sanctioned_date,
            tv_sanctioned_amount, tv_installment_amount, tv_expiry_date, tv_no_of_installments, tv_outstand_balance,
            tv_loan_amount, tv_reserve1, tv_reserve2, tv_reserve3, tv_installment_start_dt,
            tv_pending_installments, tv_account_details_response;
    AppCompatEditText et_enter_amount;
    AppCompatButton btn_back_loan, btn_submit_loan;
    Bundle getBundle = null;
    String rrn, stan, entered_amount, aadhaar_no, mobile_no, pan_no, pin_no, avlBalance, branchname, agentname, current_date_time;
    AlertDialog dialog;
    private final int MESSAGE_BOX = 1;
    DbFunctions dbFunctions;
    TxnModel txnModel;
    TransactionBO transactionBO;
    static String calledFrom;
    public static String loanSaving;
    int maxLength;
    InputFilter[] fArray;
    FPS fps;
    private ProgressDialog progressDialog;
    int iRetVal, accountType, accountStatus;
    private boolean blVerifyfinger = false;
    private byte[] brecentminituaedata = {};
    public static final int DEVICE_NOTCONNECTED = -100;
    Bundle setPreviewData;
    Storage storage;
    public static Printer ptr;
    public static Printer_GEN ptrGen;
    DialogsUtil dialogsUtil;
    VerhoeffCheckDigit verhoeff;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.loan_saving, container, false);

        setTitle(R.string.direct_deposits);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        loadUiComponents();

        objInit();

        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            printerObjInit();
        }

        clearFields();

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        branchname = dbFunctions.getBranchName(String.valueOf(GlobalModel.branchid));
        agentname = dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid));
        //current_date_time = DateFormat.getDateTimeInstance().format(new Date());
        //current_date_time = android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss ", new Date()).toString();
        current_date_time = Utility.getCurrentTimeStamp();

        if ("TXN_LOAN".equals(Txn_Fr.calledFrom)) {
            setSubTitle(R.string.loan);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.VISIBLE);
                ll_overdue.setVisibility(View.VISIBLE);
                ll_account_status.setVisibility(View.VISIBLE);
                ll_account_type.setVisibility(View.VISIBLE);
                ll_enter_amount.setVisibility(View.VISIBLE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.GONE);
                ll_sanctioned_dt.setVisibility(View.GONE);
                ll_sanctioned_amount.setVisibility(View.GONE);
                ll_installment_amount.setVisibility(View.GONE);
                ll_expiry_date.setVisibility(View.GONE);
                ll_no_of_installments.setVisibility(View.GONE);
                ll_outstand_balance.setVisibility(View.GONE);
                ll_reserve1.setVisibility(View.GONE);
                ll_reserve2.setVisibility(View.GONE);
                ll_reserve3.setVisibility(View.GONE);
                ll_installment_start_dt.setVisibility(View.GONE);
                ll_pending_installments.setVisibility(View.GONE);

                if (getBundle.getString("product_code") != null && !"".equals(getBundle.getString("product_code"))) {
                    tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                }
                if (getBundle.getString("getCustName") != null) {
                    tv_account_name.setText(getBundle.getString("getCustName"));
                }
                if (getBundle.getString("getAvlbalance") != null
                        && !"".equals(getBundle.getString("getAvlbalance"))) {
                    if (Objects.requireNonNull(getBundle.getString("getAvlbalance")).contains("-")) {
                        avlBalance = Objects.requireNonNull(getBundle.getString("getAvlbalance")).substring(1);
                        tv_avl_balance.setText(getResources().getString(R.string.Rs) + (Objects.requireNonNull(getBundle.getString("getAvlbalance"))).substring(1));
                    } else {
                        avlBalance = getBundle.getString("getAvlbalance");
                        tv_avl_balance.setText(getResources().getString(R.string.Rs) + getBundle.getString("getAvlbalance"));
                    }
                }

                // 26-April-18
                if (getBundle.getString("getLedgbalance") != null
                        && !"".equals(getBundle.getString("getLedgbalance"))) {
                    if (Objects.requireNonNull(getBundle.getString("getLedgbalance")).contains("-")) {
                        tv_overdue.setText(getResources().getString(R.string.Rs) + Objects.requireNonNull(getBundle.getString("getLedgbalance")).substring(1));
                    } else {
                        tv_overdue_label.setText(getResources().getString(R.string.advanced_balance));
                        tv_overdue.setText(getResources().getString(R.string.Rs) + Objects.requireNonNull(getBundle.getString("getLedgbalance")));
                    }
                }

                if (getBundle.getString("getAccountStatusDesc") != null
                        && !"".equals(getBundle.getString("getAccountStatusDesc"))) {
                    tv_account_status.setText(getBundle.getString("getAccountStatusDesc"));
                }
                if (getBundle.getString("getAccountTypeDesc") != null
                        && !"".equals(getBundle.getString("getAccountTypeDesc"))) {
                    tv_account_type.setText(getBundle.getString("getAccountTypeDesc"));
                }
                if (getBundle.getString("getAccountType") != null
                        && !"".equals(getBundle.getString("getAccountType"))) {
                    accountType = Integer.parseInt(getBundle.getString("getAccountType"));
                }
                if (getBundle.getString("getAccountStatus") != null
                        && !"".equals(getBundle.getString("getAccountStatus"))) {
                    accountStatus = Integer.parseInt(getBundle.getString("getAccountStatus"));
                }
                if ((accountStatus == 1 || accountStatus == 2 || accountStatus == 11) && accountType == 1) {
                    if (accountStatus == 11) {
                        setPreviewData.putString("accountStatus", String.valueOf(accountStatus));
                        tv_header.setTextColor(getResources().getColor(R.color.colorWhite));
                        tv_header.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        tv_header.setText(getBundle.getString("product_code") + "/"
                                + getBundle.getString("account_no") + " (NPA Account)");
                    } else {
                        tv_header.setText(getBundle.getString("product_code") + "/"
                                + getBundle.getString("account_no"));
                    }

                    tv_account_details_response.setVisibility(View.GONE);

                    btn_submit_loan.setVisibility(View.VISIBLE);
                    btn_submit_loan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            entered_amount = et_enter_amount.getText().toString().trim();
                            if (entered_amount.isEmpty()) {
                                et_enter_amount.requestFocus();
                                et_enter_amount.setError("Please enter amount");
                            } else if (Integer.parseInt(entered_amount) < 1) {
                                et_enter_amount.requestFocus();
                                et_enter_amount.setError("Enter valid amount");
                            } else if (avlBalance != null && !"".equals(avlBalance) && avlBalance.length() > 0) {
                                if (Integer.parseInt(entered_amount) > (Integer.parseInt(avlBalance))) {
                                    dialogsUtil.alertDialog("Entered amount should not to be\ngreater than loan amount");
                                } else {
                                    confirmDialog("LOAN");
                                }
                            } else {
                                confirmDialog("LOAN");
                            }
                        }
                    });

                    btn_back_loan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment txn;
                            assert getFragmentManager() != null;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            txn = new Txn_Fr();
                            ft.replace(R.id.content_frame, txn);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });

                } else {
                    ll_enter_amount.setVisibility(View.GONE);
                    ll_btns.setVisibility(View.GONE);
                    tv_account_details_response.setVisibility(View.VISIBLE);
                    if ((accountStatus == 1 || accountStatus == 2 || accountStatus == 11)) {
                        tv_account_details_response.setText(R.string.account_freezed);

                    } else {
                        tv_account_details_response.setText(R.string.account_closed);
                    }
                }
            }
            loanSaving = "1";
        } else if ("TXN_SAVING".equals(Txn_Fr.calledFrom)) {
            setSubTitle(R.string.saving);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.VISIBLE);
                ll_overdue.setVisibility(View.GONE);
                ll_account_status.setVisibility(View.VISIBLE);
                ll_account_type.setVisibility(View.VISIBLE);
                ll_enter_amount.setVisibility(View.VISIBLE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.GONE);
                ll_sanctioned_dt.setVisibility(View.GONE);
                ll_sanctioned_amount.setVisibility(View.GONE);
                ll_installment_amount.setVisibility(View.GONE);
                ll_expiry_date.setVisibility(View.GONE);
                ll_no_of_installments.setVisibility(View.GONE);
                ll_outstand_balance.setVisibility(View.GONE);
                ll_reserve1.setVisibility(View.GONE);
                ll_reserve2.setVisibility(View.GONE);
                ll_reserve3.setVisibility(View.GONE);
                ll_installment_start_dt.setVisibility(View.GONE);
                ll_pending_installments.setVisibility(View.GONE);

                if (getBundle.getString("product_code") != null && !"".equals(getBundle.getString("product_code"))) {
                    tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                }
                if (getBundle.getString("getCustName") != null) {
                    tv_account_name.setText(getBundle.getString("getCustName"));
                }
                if (getBundle.getString("getAvlbalance") != null
                        && !"".equals(getBundle.getString("getAvlbalance"))) {
                    if (Objects.requireNonNull(getBundle.getString("getAvlbalance")).contains("-")) {
                        avlBalance = Objects.requireNonNull(getBundle.getString("getAvlbalance")).substring(1);
                        tv_avl_balance.setText(getResources().getString(R.string.Rs) + (Objects.requireNonNull(getBundle.getString("getAvlbalance"))).substring(1));
                    } else {
                        avlBalance = getBundle.getString("getAvlbalance");
                        tv_avl_balance.setText(getResources().getString(R.string.Rs) + getBundle.getString("getAvlbalance"));
                    }
                }
                if (getBundle.getString("getAccountStatusDesc") != null
                        && !"".equals(getBundle.getString("getAccountStatusDesc"))) {
                    tv_account_status.setText(getBundle.getString("getAccountStatusDesc"));
                }
                if (getBundle.getString("getAccountTypeDesc") != null
                        && !"".equals(getBundle.getString("getAccountTypeDesc"))) {
                    tv_account_type.setText(getBundle.getString("getAccountTypeDesc"));
                }
                if (getBundle.getString("getAccountType") != null
                        && !"".equals(getBundle.getString("getAccountType"))) {
                    accountType = Integer.parseInt(getBundle.getString("getAccountType"));
                }
                if (getBundle.getString("getAccountStatus") != null
                        && !"".equals(getBundle.getString("getAccountStatus"))) {
                    accountStatus = Integer.parseInt(getBundle.getString("getAccountStatus"));
                }
                if ((accountStatus == 1 || accountStatus == 2 || accountStatus == 11) && accountType == 1) {
                    tv_account_details_response.setVisibility(View.GONE);
                    btn_submit_loan.setVisibility(View.VISIBLE);
                    btn_submit_loan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            entered_amount = et_enter_amount.getText().toString().trim();
                            if (entered_amount.isEmpty()) {
                                et_enter_amount.requestFocus();
                                et_enter_amount.setError("Please enter amount");
                            } else if (Integer.parseInt(entered_amount) < 1) {
                                et_enter_amount.requestFocus();
                                et_enter_amount.setError("Enter valid amount");
                            } else {
                                confirmDialog("SAVING");
                            }
                        }
                    });
                    btn_back_loan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment txn;
                            assert getFragmentManager() != null;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            txn = new Txn_Fr();
                            ft.replace(R.id.content_frame, txn);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                } else {
                    ll_enter_amount.setVisibility(View.GONE);
                    ll_btns.setVisibility(View.GONE);
                    tv_account_details_response.setVisibility(View.VISIBLE);
                    if ((accountStatus == 1 || accountStatus == 2 || accountStatus == 11)) {
                        tv_account_details_response.setText(R.string.account_closed);
                    } else {
                        tv_account_details_response.setText(R.string.account_freezed);
                    }
                }
                loanSaving = "2";
            }
        } else if ("TXN_LOAN_HISTORY".equals(Txn_Fr.calledFrom)) {
            setSubTitle(R.string.customer_loan_history);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.VISIBLE);
                ll_account_status.setVisibility(View.VISIBLE);
                ll_account_type.setVisibility(View.VISIBLE);
                ll_enter_amount.setVisibility(View.GONE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.VISIBLE);
                ll_sanctioned_dt.setVisibility(View.VISIBLE);
                ll_sanctioned_amount.setVisibility(View.VISIBLE);
                ll_installment_amount.setVisibility(View.VISIBLE);
                ll_expiry_date.setVisibility(View.VISIBLE);
                ll_no_of_installments.setVisibility(View.VISIBLE);
                ll_outstand_balance.setVisibility(View.VISIBLE);
                ll_reserve1.setVisibility(View.VISIBLE);
                ll_reserve2.setVisibility(View.VISIBLE);
                ll_reserve3.setVisibility(View.VISIBLE);
                ll_installment_start_dt.setVisibility(View.VISIBLE);
                ll_pending_installments.setVisibility(View.VISIBLE);

                if (getBundle.getString("product_code") != null && !"".equals(getBundle.getString("product_code"))) {
                    tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                }
                if (getBundle.getString("getCustName") != null && !"".equals(getBundle.getString("getCustName"))) {
                    tv_account_name.setText(getBundle.getString("getCustName"));
                }

                // 30-April-18
                if (getBundle.getString("getOverdue") != null
                        && !"".equals(getBundle.getString("getOverdue"))) {
                    if (getBundle.getString("getOverdue").contains("-")) {
                        tv_overdue.setText(getResources().getString(R.string.Rs) + getBundle.getString("getOverdue").substring(1));
                    } else {
                        tv_overdue_label.setText(getResources().getString(R.string.advanced_balance));
                        tv_overdue.setText(getResources().getString(R.string.Rs) + getBundle.getString("getOverdue"));
                    }
                }
                if (getBundle.getString("getAccountStatusDesc") != null && !"".equals(getBundle.getString("getAccountStatusDesc"))) {
                    tv_account_status.setText(getBundle.getString("getAccountStatusDesc"));
                }
                if (getBundle.getString("getAccountTypeDesc") != null && !"".equals(getBundle.getString("getAccountTypeDesc"))) {
                    tv_account_type.setText(getBundle.getString("getAccountTypeDesc"));
                }
                if (getBundle.getString("getAccountType") != null
                        && !"".equals(getBundle.getString("getAccountType"))) {
                    accountType = Integer.parseInt(getBundle.getString("getAccountType"));
                }
                if (getBundle.getString("getAccountStatus") != null
                        && !"".equals(getBundle.getString("getAccountStatus"))) {
                    accountStatus = Integer.parseInt(getBundle.getString("getAccountStatus"));

                    if (accountStatus == 11) {
                        tv_header.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no") + " (NPA Account)");
                        tv_header.setTextColor(getResources().getColor(R.color.colorWhite));
                    } else {
                        tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                    }

                }
                if (getBundle.getString("product_code") != null && !"".equals(getBundle.getString("product_code"))) {
                    tv_account_num.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                }
                if (getBundle.getString("getScanctiondt") != null && !"".equals(getBundle.getString("getScanctiondt"))) {
                    tv_sanctioned_date.setText(getBundle.getString("getScanctiondt"));
                }
                if (getBundle.getString("getScanctionAmount") != null && !"".equals(getBundle.getString("getScanctionAmount"))) {
                    tv_sanctioned_amount.setText(getResources().getString(R.string.Rs) + getBundle.getString("getScanctionAmount"));
                }
                if (getBundle.getString("getInstallmentAmt") != null && !"".equals(getBundle.getString("getInstallmentAmt"))) {
                    tv_installment_amount.setText(getResources().getString(R.string.Rs) + getBundle.getString("getInstallmentAmt"));
                }
                if (getBundle.getString("getExpirydate") != null && !"".equals(getBundle.getString("getExpirydate"))) {
                    tv_expiry_date.setText(getBundle.getString("getExpirydate"));
                }
                if (getBundle.getString("getNoofinstallments") != null && !"".equals(getBundle.getString("getNoofinstallments"))) {
                    tv_no_of_installments.setText(getBundle.getString("getNoofinstallments"));
                }
                if (getBundle.getString("getOutstandingBal") != null && !"".equals(getBundle.getString("getOutstandingBal"))) {
                    tv_outstand_balance.setText(getResources().getString(R.string.Rs) + getBundle.getString("getOutstandingBal"));
                }
                if (getBundle.getString("getReserve1") != null && !"".equals(getBundle.getString("getReserve1"))) {
                    tv_reserve1.setText(getBundle.getString("getReserve1"));
                }
                if (getBundle.getString("getReserve2") != null && !"".equals(getBundle.getString("getReserve2"))) {
                    tv_reserve2.setText(getResources().getString(R.string.Rs) + getBundle.getString("getReserve2"));
                }
                if (getBundle.getString("getReserve3") != null && !"".equals(getBundle.getString("getReserve3"))) {
                    tv_reserve3.setText(getResources().getString(R.string.Rs) + getBundle.getString("getReserve3"));
                }
                if (getBundle.getString("getTxndate") != null && !"".equals(getBundle.getString("getTxndate"))) {
                    tv_installment_start_dt.setText(getBundle.getString("getTxndate"));
                }
                if (getBundle.getString("getPendingInstlmnts") != null && !"".equals(getBundle.getString("getPendingInstlmnts"))) {
                    tv_pending_installments.setText(getBundle.getString("getPendingInstlmnts"));
                }

                btn_submit_loan.setText(R.string.print);
                btn_submit_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogsUtil.alertDialog("Printer services required");
                    }
                });

                btn_back_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment txn;
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        txn = new Txn_Fr();
                        ft.replace(R.id.content_frame, txn);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });

            }
            loanSaving = "3";

        } else if ("TXN_BAL_ENQ".equals(Txn_Fr.calledFrom)) {
            setSubTitle(R.string.balance_enquiry);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.VISIBLE);
                ll_overdue.setVisibility(View.GONE);
                ll_account_status.setVisibility(View.VISIBLE);
                ll_account_type.setVisibility(View.VISIBLE);
                ll_enter_amount.setVisibility(View.GONE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.GONE);
                ll_sanctioned_dt.setVisibility(View.GONE);
                ll_sanctioned_amount.setVisibility(View.GONE);
                ll_installment_amount.setVisibility(View.GONE);
                ll_expiry_date.setVisibility(View.GONE);
                ll_no_of_installments.setVisibility(View.GONE);
                ll_outstand_balance.setVisibility(View.GONE);
                ll_reserve1.setVisibility(View.GONE);
                ll_reserve2.setVisibility(View.GONE);
                ll_reserve3.setVisibility(View.GONE);
                ll_installment_start_dt.setVisibility(View.GONE);
                ll_pending_installments.setVisibility(View.GONE);

                if (getBundle.getString("product_code") != null && !"".equals(getBundle.getString("product_code"))) {
                    tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                }
                if (getBundle.getString("getCustName") != null) {
                    tv_account_name.setText(getBundle.getString("getCustName"));
                }
                if (getBundle.getString("getAvlbalance") != null
                        && !"".equals(getBundle.getString("getAvlbalance"))) {
                    if (Objects.requireNonNull(getBundle.getString("getAvlbalance")).contains("-")) {
                        avlBalance = Objects.requireNonNull(getBundle.getString("getAvlbalance")).substring(1);
                        tv_avl_balance.setText(getResources().getString(R.string.Rs) + (Objects.requireNonNull(getBundle.getString("getAvlbalance"))).substring(1));
                    } else {
                        avlBalance = getBundle.getString("getAvlbalance");
                        tv_avl_balance.setText(getResources().getString(R.string.Rs) + getBundle.getString("getAvlbalance"));
                    }
                }

                if ("CD".equals(getBundle.getString("product_code"))
                        || "DDS".equals(getBundle.getString("product_code"))
                        || "SB".equals(getBundle.getString("product_code"))) {
                    ll_overdue.setVisibility(View.GONE);
                } else {
                    if (getBundle.getString("getLedgbalance") != null
                            && !"".equals(getBundle.getString("getLedgbalance"))
                            && !"0".equals(getBundle.getString("getLedgbalance"))) {
                        ll_overdue.setVisibility(View.VISIBLE);

                        if (Objects.requireNonNull(getBundle.getString("getLedgbalance")).contains("-")) {
                            tv_overdue.setText(getResources().getString(R.string.Rs) + Objects.requireNonNull(getBundle.getString("getLedgbalance")).substring(1));
                        } else {
                            tv_overdue_label.setText(getResources().getString(R.string.advanced_balance));
                            tv_overdue.setText(getResources().getString(R.string.Rs) + Objects.requireNonNull(getBundle.getString("getLedgbalance")));
                        }

                        if (getBundle.getString("getAccountStatus") != null
                                && !"".equals(getBundle.getString("getAccountStatus"))) {
                            accountStatus = Integer.parseInt(getBundle.getString("getAccountStatus"));
                            if (accountStatus == 11) {
                                tv_header.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no") + " (NPA Account)");
                                tv_header.setTextColor(getResources().getColor(R.color.colorWhite));
                            } else {
                                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                            }
                        }
                    } else {
                        ll_overdue.setVisibility(View.GONE);
                        if (getBundle.getString("getAccountStatus") != null
                                && !"".equals(getBundle.getString("getAccountStatus"))) {
                            accountStatus = Integer.parseInt(getBundle.getString("getAccountStatus"));
                            if (accountStatus == 11) {
                                tv_header.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no") + " (NPA Account)");
                                tv_header.setTextColor(getResources().getColor(R.color.colorWhite));
                            } else {
                                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                            }
                        }
                    }
                }
                if (getBundle.getString("getAccountStatusDesc") != null
                        && !"".equals(getBundle.getString("getAccountStatusDesc"))) {
                    tv_account_status.setText(getBundle.getString("getAccountStatusDesc"));
                }
                if (getBundle.getString("getAccountTypeDesc") != null
                        && !"".equals(getBundle.getString("getAccountTypeDesc"))) {
                    tv_account_type.setText(getBundle.getString("getAccountTypeDesc"));
                }
                if (getBundle.getString("getAccountType") != null
                        && !"".equals(getBundle.getString("getAccountType"))) {
                    accountType = Integer.parseInt(getBundle.getString("getAccountType"));
                }

                btn_back_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment txn;
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        txn = new Txn_Fr();
                        ft.replace(R.id.content_frame, txn);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });

                btn_submit_loan.setText(R.string.print);
                btn_submit_loan.setOnClickListener(new View.OnClickListener() {
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
                loanSaving = "6";
            }
        } else if ("TXN_AADHAAR_SEEDING".equals(AadhaarMobile_Fr.calledFrom)) {
            setTitle(R.string.service);
            setSubTitle(R.string.aadhaar_seeding);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.GONE);
                ll_account_status.setVisibility(View.GONE);
                ll_account_type.setVisibility(View.GONE);
                ll_enter_amount.setVisibility(View.VISIBLE);
                et_enter_amount.setVisibility(View.VISIBLE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.GONE);
                ll_sanctioned_dt.setVisibility(View.GONE);
                ll_sanctioned_amount.setVisibility(View.GONE);
                ll_installment_amount.setVisibility(View.GONE);
                ll_expiry_date.setVisibility(View.GONE);
                ll_no_of_installments.setVisibility(View.GONE);
                ll_outstand_balance.setVisibility(View.GONE);
                ll_reserve1.setVisibility(View.GONE);
                ll_reserve2.setVisibility(View.GONE);
                ll_reserve3.setVisibility(View.GONE);
                ll_installment_start_dt.setVisibility(View.GONE);
                ll_pending_installments.setVisibility(View.GONE);

                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                tv_account_name.setText(getBundle.getString("getCustName"));
                tv_enter_amount.setText(R.string.aadhaar_no);
                et_enter_amount.setText("");
                et_enter_amount.setHint(R.string.enter_aadhaar_no);
                et_enter_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_aadhaar_seeding_24x24, 0, 0, 0);

                maxLength = 12;
                fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                et_enter_amount.setFilters(fArray);

                et_enter_amount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        aadhaar_no = et_enter_amount.getText().toString().trim();
                        if (aadhaar_no.length() >= 1 && !verhoeff.isValid(aadhaar_no)) {
                            et_enter_amount.requestFocus();
                            et_enter_amount.setError("Please enter valid aadhaar no.");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                btn_submit_loan.setVisibility(View.VISIBLE);
                btn_submit_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aadhaar_no = et_enter_amount.getText().toString().trim();
                        if (aadhaar_no.isEmpty()) {
                            et_enter_amount.requestFocus();
                            et_enter_amount.setError("Please enter aadhaar no.");
                        } else if (!verhoeff.isValid(aadhaar_no) && (aadhaar_no.length() != 12 || aadhaar_no.length() != 28)) {
                            et_enter_amount.requestFocus();
                            et_enter_amount.setError("Please enter valid aadhaar No.");
                        } else {
                            prepareModel(Constants.AadhaarSeedingProcCode);
                        }
                    }
                });

                btn_back_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment aadhaarMobile;
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        aadhaarMobile = new AadhaarMobile_Fr();
                        ft.replace(R.id.content_frame, aadhaarMobile);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
            }
            loanSaving = "4";
        } else if ("TXN_MOBILE_SEEDING".equals(AadhaarMobile_Fr.calledFrom)) {
            setTitle(R.string.service);
            setSubTitle(R.string.mobile_seeding);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.GONE);
                ll_account_status.setVisibility(View.GONE);
                ll_account_type.setVisibility(View.GONE);
                ll_enter_amount.setVisibility(View.VISIBLE);
                tv_enter_amount.setVisibility(View.VISIBLE);
                et_enter_amount.setVisibility(View.VISIBLE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.GONE);
                ll_sanctioned_dt.setVisibility(View.GONE);
                ll_sanctioned_amount.setVisibility(View.GONE);
                ll_installment_amount.setVisibility(View.GONE);
                ll_expiry_date.setVisibility(View.GONE);
                ll_no_of_installments.setVisibility(View.GONE);
                ll_outstand_balance.setVisibility(View.GONE);
                ll_reserve1.setVisibility(View.GONE);
                ll_reserve2.setVisibility(View.GONE);
                ll_reserve3.setVisibility(View.GONE);
                ll_installment_start_dt.setVisibility(View.GONE);
                ll_pending_installments.setVisibility(View.GONE);

                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                tv_account_name.setText(getBundle.getString("getCustName"));
                tv_enter_amount.setText(R.string.mobile_no);
                et_enter_amount.setText("");
                et_enter_amount.setHint(R.string.enter_mobile_no);
                if (!"OTHER".equals(storage.getValue(Constants.DEVICE_TYPE))) {
                    et_enter_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile_24x24, 0, 0, 0);
                } else {
                    et_enter_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile_18x18, 0, 0, 0);
                }
                maxLength = 10;
                fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                et_enter_amount.setFilters(fArray);

                btn_submit_loan.setVisibility(View.VISIBLE);
                btn_submit_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mobile_no = et_enter_amount.getText().toString().trim();
                        if (mobile_no.isEmpty()) {
                            et_enter_amount.setError("Please enter mobile no.");
                            et_enter_amount.requestFocus();
                        } else if (mobile_no.length() < 10) {
                            et_enter_amount.setError("Enter valid mobile no.");
                            et_enter_amount.requestFocus();
                        } else {
                            prepareModel(Constants.MobileSeedingProcCode);
                        }
                    }
                });

                btn_back_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment aadhaarMobile;
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        aadhaarMobile = new AadhaarMobile_Fr();
                        ft.replace(R.id.content_frame, aadhaarMobile);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
            }
            loanSaving = "5";
        } else if ("TXN_PAN_UPDATE".equals(AadhaarMobile_Fr.calledFrom)) {
            setTitle(R.string.service);
            setSubTitle(R.string.pan_update);
            if (getBundle != null) {
                ll_account_name.setVisibility(View.VISIBLE);
                ll_avl_balance.setVisibility(View.GONE);
                ll_overdue.setVisibility(View.GONE);
                ll_account_status.setVisibility(View.GONE);
                ll_account_type.setVisibility(View.GONE);
                ll_enter_amount.setVisibility(View.VISIBLE);
                et_enter_amount.setVisibility(View.VISIBLE);
                ll_branch_code.setVisibility(View.GONE);
                ll_account_num.setVisibility(View.GONE);
                ll_sanctioned_dt.setVisibility(View.GONE);
                ll_sanctioned_amount.setVisibility(View.GONE);
                ll_installment_amount.setVisibility(View.GONE);
                ll_expiry_date.setVisibility(View.GONE);
                ll_no_of_installments.setVisibility(View.GONE);
                ll_outstand_balance.setVisibility(View.GONE);
                ll_reserve1.setVisibility(View.GONE);
                ll_reserve2.setVisibility(View.GONE);
                ll_reserve3.setVisibility(View.GONE);
                ll_installment_start_dt.setVisibility(View.GONE);
                ll_pending_installments.setVisibility(View.GONE);

                tv_header.setText(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
                tv_account_name.setText(getBundle.getString("getCustName"));
                tv_enter_amount.setText(R.string.pan_no);
                et_enter_amount.setText("");
                et_enter_amount.setHint(R.string.enter_pan_no);
                et_enter_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_aadhaar_seeding_24x24, 0, 0, 0);

                maxLength = 10;
                fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                et_enter_amount.setFilters(fArray);
                et_enter_amount.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                btn_submit_loan.setVisibility(View.VISIBLE);
                btn_submit_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pan_no = et_enter_amount.getText().toString().trim();
                        if (pan_no.isEmpty()) {
                            et_enter_amount.requestFocus();
                            et_enter_amount.setError("Please enter PAN no.");
                        } else if (pan_no.length() < 10) {
                            et_enter_amount.requestFocus();
                            et_enter_amount.setError("Please enter valid PAN No.");
                        } else {
                            prepareModel(Constants.panUpdateProcCode);
                        }
                    }
                });

                btn_back_loan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment aadhaarMobile;
                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        aadhaarMobile = new AadhaarMobile_Fr();
                        ft.replace(R.id.content_frame, aadhaarMobile);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
            }
            loanSaving = "7";
        }
        return view;
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);

        tv_header = view.findViewById(R.id.tv_header);

        ll_account_name = view.findViewById(R.id.ll_account_name);
        ll_avl_balance = view.findViewById(R.id.ll_avl_balance);
        ll_branch_code = view.findViewById(R.id.ll_branch_code);
        ll_account_num = view.findViewById(R.id.ll_account_num);
        ll_sanctioned_dt = view.findViewById(R.id.ll_sanctioned_dt);
        ll_sanctioned_amount = view.findViewById(R.id.ll_sanctioned_amount);
        ll_installment_amount = view.findViewById(R.id.ll_installment_amount);
        ll_expiry_date = view.findViewById(R.id.ll_expiry_date);
        ll_no_of_installments = view.findViewById(R.id.ll_no_of_installments);
        ll_outstand_balance = view.findViewById(R.id.ll_outstand_balance);
        ll_overdue = view.findViewById(R.id.ll_overdue);
        ll_account_status = view.findViewById(R.id.ll_account_status);
        ll_account_type = view.findViewById(R.id.ll_account_type);
        ll_enter_amount = view.findViewById(R.id.ll_enter_amount);
        ll_reserve1 = view.findViewById(R.id.ll_reserve1);
        ll_reserve2 = view.findViewById(R.id.ll_reserve2);
        ll_reserve3 = view.findViewById(R.id.ll_reserve3);
        ll_installment_start_dt = view.findViewById(R.id.ll_installment_start_dt);
        ll_pending_installments = view.findViewById(R.id.ll_pending_installments);
        ll_btns = view.findViewById(R.id.ll_btns);

        tv_account_name = view.findViewById(R.id.tv_account_name);
        tv_avl_balance = view.findViewById(R.id.tv_avl_balance);
        tv_branch_code = view.findViewById(R.id.tv_branch_code);
        tv_account_num = view.findViewById(R.id.tv_account_num);
        tv_sanctioned_date = view.findViewById(R.id.tv_sanctioned_date);
        tv_sanctioned_amount = view.findViewById(R.id.tv_sanctioned_amount);
        tv_installment_amount = view.findViewById(R.id.tv_installment_amount);
        tv_expiry_date = view.findViewById(R.id.tv_expiry_date);
        tv_no_of_installments = view.findViewById(R.id.tv_no_of_installments);
        tv_outstand_balance = view.findViewById(R.id.tv_outstand_balance);
        tv_overdue = view.findViewById(R.id.tv_overdue);
        tv_overdue_label = view.findViewById(R.id.tv_overdue_label);
        tv_account_status = view.findViewById(R.id.tv_account_status);
        tv_account_type = view.findViewById(R.id.tv_account_type);

        tv_enter_amount = view.findViewById(R.id.tv_enter_amount);

        et_enter_amount = view.findViewById(R.id.et_enter_amount);

        tv_reserve1 = view.findViewById(R.id.tv_reserve1);
        tv_reserve2 = view.findViewById(R.id.tv_reserve2);
        tv_reserve3 = view.findViewById(R.id.tv_reserve3);
        tv_installment_start_dt = view.findViewById(R.id.tv_installment_start_dt);
        tv_pending_installments = view.findViewById(R.id.tv_pending_installments);

        btn_back_loan = view.findViewById(R.id.btn_back_loan);
        btn_submit_loan = view.findViewById(R.id.btn_submit_loan);

        tv_account_details_response = view.findViewById(R.id.tv_account_details_response);
    }

    private void clearFields() {
        ll_account_name.setVisibility(View.VISIBLE);
        ll_avl_balance.setVisibility(View.VISIBLE);
        ll_overdue.setVisibility(View.VISIBLE);
        ll_enter_amount.setVisibility(View.VISIBLE);
        ll_branch_code.setVisibility(View.GONE);
        ll_account_num.setVisibility(View.GONE);
        ll_sanctioned_dt.setVisibility(View.GONE);
        ll_sanctioned_amount.setVisibility(View.GONE);
        ll_installment_amount.setVisibility(View.GONE);
        ll_expiry_date.setVisibility(View.GONE);
        ll_no_of_installments.setVisibility(View.GONE);
        ll_outstand_balance.setVisibility(View.GONE);
        ll_reserve1.setVisibility(View.GONE);
        ll_reserve2.setVisibility(View.GONE);
        ll_reserve3.setVisibility(View.GONE);
        ll_installment_start_dt.setVisibility(View.GONE);
        ll_pending_installments.setVisibility(View.GONE);

        tv_account_details_response.setVisibility(View.GONE);
    }

    private void objInit() {
        storage = new Storage(getActivity());

        dialogsUtil = new DialogsUtil(getActivity());

        dbFunctions = DbFunctions.getInstance(getActivity());

        getBundle = this.getArguments();

        setPreviewData = new Bundle();

        verhoeff = new VerhoeffCheckDigit();
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
                Log.e("", "printer gen is activated");
            } catch (Exception e) {
                //dialogsUtil.alertDialog("Leopard doesn't activated");
            }
        }
    }

    LinearLayout ll_make_confirm;
    AppCompatTextView tv_information, tv_biometric;
    LinearLayout ll_pin_no;
    AppCompatEditText et_pin_no;
    AppCompatCheckBox cb_accept;
    AppCompatButton btn_no, btn_yes;

    private void confirmDialog(String loanSaving) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dlg_make_confirm, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.app_logo);
        builder.setTitle(R.string.amount_confirmation);

        ll_make_confirm = view.findViewById(R.id.ll_make_confirm);

        tv_information = view.findViewById(R.id.tv_information);
        tv_biometric = view.findViewById(R.id.tv_biometric);
        ll_pin_no = view.findViewById(R.id.ll_pin_no);
        et_pin_no = view.findViewById(R.id.et_pin_no);
        //Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        //et_pin_no.setAnimation(animation);

        ll_pin_no.setVisibility(View.GONE);
        cb_accept = view.findViewById(R.id.cb_accept);
        btn_no = view.findViewById(R.id.btn_no);
        btn_yes = view.findViewById(R.id.btn_yes);

        dialog = builder.create();
        dialog.show();

        tv_information.setText("Please confirm entered amount is " + getResources().getString(R.string.Rs) + entered_amount + " ?");

        final Storage storage = new Storage(getActivity());
        if ("BIOMETRIC".equals(storage.getValue(Constants.LOGIN_TYPE))) {
            tv_biometric.setText(R.string.place_your_finger);
        } else {
            tv_biometric.setVisibility(View.GONE);
            ll_pin_no.setVisibility(View.VISIBLE);
            et_pin_no.setText("");
            et_pin_no.setHint(R.string.pin_no);
        }

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                tv_account_details_response.setVisibility(View.GONE);
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("BIOMETRIC".equals(storage.getValue(Constants.LOGIN_TYPE))) {
                    if (!cb_accept.isChecked()) {
                        dialogsUtil.alertDialog("Please accept the payment !");
                    } else {
                        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                            new VerifyTempleAsync().execute(0);
                        }
                    }
                } else {
                    pin_no = et_pin_no.getText().toString().trim();
                    if (pin_no.isEmpty()) {
                        et_pin_no.setError("Please enter PIN");
                        et_pin_no.requestFocus();
                    } else if (pin_no.length() < 4) {
                        et_pin_no.setError("PIN length should be min 4 digits");
                        et_pin_no.requestFocus();
                    } else if (!cb_accept.isChecked()) {
                        dialogsUtil.alertDialog("Please accept the payment !");
                    } else {
                        prepareModel(Constants.DepositProcCode);
                    }
                }
            }
        });
    }

    public void prepareModel(final String processingCode) {
        txnModel = new TxnModel();
        stan = Utility.padLeft(getStan(), 6,
                "0");
        rrn = Utility.getYear().substring(3) + Utility.padLeft(Utility.getDayofYear(), 3, "0") +
                Utility.padLeft(Utility.getHours(), 2, "0")
                + stan;
        txnModel.setRrn(rrn);
        txnModel.setStan(stan);
        txnModel.setAccNo(getBundle.getString("account_no"));
        txnModel.setLocAccNo(getBundle.getString("product_code") + "/" + getBundle.getString("account_no"));
        txnModel.setTxndate(Utility.getCurrentTimeStamp());
        txnModel.setMicroatmid(String.valueOf(GlobalModel.microatmid));
        txnModel.setBcid(GlobalModel.bcid);
        txnModel.setBranch_id(GlobalModel.branchid);
        txnModel.setTxnserviceid(Constants.txnserviceid);
        txnModel.setTxnsubserviceid(Constants.txnsubserviceid);
        txnModel.setProcessingcode(processingCode);
        txnModel.setProductcode(getBundle.getString("product_code"));
        txnModel.setCustName(tv_account_name.getText().toString().trim());
        if ("TXN_LOAN".equals(Txn_Fr.calledFrom)
                && processingCode.equals(Constants.DepositProcCode)) {
            txnModel.setTxnType("dr");
            txnModel.setAvlbalance(getBundle.getString("getAvlbalance"));
            txnModel.setLedgbalance(getBundle.getString("getLedgbalance"));
            txnModel.setAmount(entered_amount);
            txnModel.setPinNo(pin_no);
            txnModel.setRequestFrom("loan");
        } else if ("TXN_SAVING".equals(Txn_Fr.calledFrom)
                && processingCode.equals(Constants.DepositProcCode)) {
            txnModel.setTxnType("cr");
            txnModel.setAvlbalance(getBundle.getString("getAvlbalance"));
            txnModel.setLedgbalance("0");
            txnModel.setAmount(entered_amount);
            txnModel.setPinNo(pin_no);
            txnModel.setRequestFrom("saving");
        } else if ("TXN_AADHAAR_SEEDING".equals(AadhaarMobile_Fr.calledFrom)
                && processingCode.equals(Constants.AadhaarSeedingProcCode)) {
            txnModel.setTxnType("nf");
            txnModel.setCustno(aadhaar_no);
            txnModel.setCustRefNo(aadhaar_no);
            txnModel.setLedgbalance("0");
            txnModel.setAmount(aadhaar_no);
            txnModel.setRequestFrom("aadhaar_seeding");
        } else if ("TXN_MOBILE_SEEDING".equals(AadhaarMobile_Fr.calledFrom)
                && processingCode.equals(Constants.MobileSeedingProcCode)) {
            txnModel.setTxnType("nf");
            txnModel.setCustno(mobile_no);
            txnModel.setCustRefNo(mobile_no);
            txnModel.setLedgbalance("0");
            txnModel.setAmount(mobile_no);
            txnModel.setRequestFrom("mobile_seeding");
        } else {
            txnModel.setTxnType("nf");
            txnModel.setCustno(pan_no);
            txnModel.setCustRefNo(pan_no);
            txnModel.setLedgbalance("0");
            txnModel.setAmount(pan_no);
            txnModel.setRequestFrom("pan_update");
        }

        boolean inserted = dbFunctions.insertTxnInDatabase(txnModel);
        if (!inserted) {
            Log.i(TAG, "Unable to insert in DB");
        }

        transactionBO = new TransactionBO(getActivity());
        transactionBO.transactionRequest(txnModel);
        transactionBO.setOnTaskFinishedEvent(new TransactionBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished() {
                Fragment printPreview = new PrintPreview_Fr();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                boolean updateInDB;
                if (transactionBO.txnResponseModel.getStatus() == Masters.Status.FAILURE) {
                    tv_account_details_response.setVisibility(View.VISIBLE);
                    tv_account_details_response.setText(transactionBO.txnResponseModel.getStatusDescription());
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else if (("TXN_LOAN".equals(Txn_Fr.calledFrom)
                        || "TXN_SAVING".equals(Txn_Fr.calledFrom))
                        && (processingCode.equals(Constants.DepositProcCode))) {
                    if ("TXN_LOAN".equals(Txn_Fr.calledFrom)) {
                        if (tv_account_name.getText().toString().trim().length() > 0) {
                            calledFrom = "LOAN_PREVIEW";
                            setPreviewData.putString("getCustName", getBundle.getString("getCustName"));
                            setPreviewData.putString("account_no", getBundle.getString("account_no"));
                            setPreviewData.putString("product_code", getBundle.getString("product_code"));
                            setPreviewData.putString("entered_amount", entered_amount);
                            setPreviewData.putString("getAvlbalance", transactionBO.txnModel.getAvlbalance());
                            setPreviewData.putString("getLedgbalance", transactionBO.txnModel.getLedgbalance());
                            setPreviewData.putString("getRrn", transactionBO.txnModel.getRrn());

                            updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + getBundle.getString("getCustName"),
                                    "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                    "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                    "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                            if (!updateInDB) {
                                Log.i(TAG, "Unable to update in DB");
                            }

                            if (dialog != null) {
                                dialog.dismiss();
                            }

                            if (transactionBO.txnResponseModel.getStatus() == Masters.Status.SUCCESS) {
                                ft.replace(R.id.content_frame, printPreview);
                                printPreview.setArguments(setPreviewData);
                            }
                        } else {
                            tv_account_details_response.setVisibility(View.VISIBLE);
                            tv_account_details_response.setText(R.string.account_details_not_found);
                        }
                    } else {
                        if (tv_account_name.getText().toString().trim().length() > 0) {
                            calledFrom = "SAVING_PREVIEW";
                            setPreviewData.putString("getCustName", "" + getBundle.getString("getCustName"));
                            setPreviewData.putString("account_no", getBundle.getString("account_no"));
                            setPreviewData.putString("product_code", getBundle.getString("product_code"));
                            setPreviewData.putString("entered_amount", "" + entered_amount);
                            setPreviewData.putString("getAvlbalance", transactionBO.txnModel.getAvlbalance());
                            setPreviewData.putString("getLedgbalance", transactionBO.txnModel.getLedgbalance());
                            setPreviewData.putString("getRrn", transactionBO.txnModel.getRrn());

                            updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + getBundle.getString("getCustName"),
                                    "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                    "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                    "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                            if (!updateInDB) {
                                Log.i(TAG, "Unable to update in DB");
                            }

                            if (dialog != null) {
                                dialog.dismiss();
                            }

                            if (transactionBO.txnResponseModel.getStatus() == Masters.Status.SUCCESS) {
                                ft.replace(R.id.content_frame, printPreview);
                                printPreview.setArguments(setPreviewData);
                            }
                        } else {
                            tv_account_details_response.setVisibility(View.VISIBLE);
                            tv_account_details_response.setText(R.string.account_details_not_found);
                        }
                    }
                } else if ("TXN_AADHAAR_SEEDING".equals(AadhaarMobile_Fr.calledFrom)) {
                    if (tv_account_name.getText().toString().trim().length() > 0) {
                        calledFrom = "AADHAAR_SEEDING_PREVIEW";
                        setPreviewData.putString("getCustName", "" + getBundle.getString("getCustName"));
                        setPreviewData.putString("account_no", getBundle.getString("account_no"));
                        setPreviewData.putString("product_code", getBundle.getString("product_code"));
                        setPreviewData.putString("aadhaar_no", aadhaar_no);
                        setPreviewData.putString("getAvlbalance", "" + getBundle.getString("getAvlbalance"));
                        setPreviewData.putString("getRrn", transactionBO.txnModel.getRrn());

                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + getBundle.getString("getCustName"),
                                "" + getBundle.getString("getAvlbalance"), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            Log.i(TAG, "Unable to update in DB");
                        }

                        if (transactionBO.txnResponseModel.getStatus() == Masters.Status.SUCCESS) {
                            ft.replace(R.id.content_frame, printPreview);
                            printPreview.setArguments(setPreviewData);
                        }
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else if ("TXN_MOBILE_SEEDING".equals(AadhaarMobile_Fr.calledFrom)) {
                    if (tv_account_name.getText().toString().trim().length() > 0) {
                        calledFrom = "MOBILE_SEEDING_PREVIEW";
                        setPreviewData.putString("getCustName", "" + getBundle.getString("getCustName"));
                        setPreviewData.putString("account_no", getBundle.getString("account_no"));
                        setPreviewData.putString("product_code", getBundle.getString("product_code"));
                        setPreviewData.putString("mobile_no", mobile_no);
                        setPreviewData.putString("getAvlbalance", "" + getBundle.getString("getAvlbalance"));
                        setPreviewData.putString("getLedgbalance", "" + getBundle.getString("getLedgbalance"));
                        setPreviewData.putString("getRrn", transactionBO.txnModel.getRrn());

                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + getBundle.getString("getCustName"),
                                "" + getBundle.getString("getAvlbalance"), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            Log.i(TAG, "Unable to update in DB");
                        }

                        if (transactionBO.txnResponseModel.getStatus() == Masters.Status.SUCCESS) {
                            ft.replace(R.id.content_frame, printPreview);
                            printPreview.setArguments(setPreviewData);
                        }
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else {
                    if (tv_account_name.getText().toString().trim().length() > 0) {
                        calledFrom = "PAN_UPDATE_PREVIEW";
                        setPreviewData.putString("getCustName", "" + getBundle.getString("getCustName"));
                        setPreviewData.putString("account_no", getBundle.getString("account_no"));
                        setPreviewData.putString("product_code", getBundle.getString("product_code"));
                        setPreviewData.putString("pan_no", pan_no);
                        setPreviewData.putString("getAvlbalance", "" + getBundle.getString("getAvlbalance"));
                        setPreviewData.putString("getLedgbalance", "" + getBundle.getString("getLedgbalance"));
                        setPreviewData.putString("getRrn", transactionBO.txnModel.getRrn());

                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + getBundle.getString("getCustName"),
                                "" + getBundle.getString("getAvlbalance"), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            Log.i(TAG, "Unable to update in DB");
                        }

                        if (transactionBO.txnResponseModel.getStatus() == Masters.Status.SUCCESS) {
                            ft.replace(R.id.content_frame, printPreview);
                            printPreview.setArguments(setPreviewData);
                        }
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private String getStan() {
        String stan = dbFunctions.getStan();
        if (stan.equals("0") || stan.equals("")) {
            stan = String.valueOf(Integer.parseInt(stan) + 1);
            dbFunctions.updateStan(stan);
        } else {
            stan = String.valueOf(Integer.parseInt(stan) + 1);
            dbFunctions.updateStan(stan);
        }
        return stan;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    showDialog(str);
                default:
                    break;
            }
        }
    };

    public void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    public class VerifyTempleAsync extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(R.string.bluetooth);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage(getString(R.string.place_your_finger));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            String fpsData = dbFunctions.getFpsData(String.valueOf(GlobalModel.bcid));
            try {
                brecentminituaedata = HexString.hexToBuffer(fpsData);
                try {
                    OutputStream outSt = BluetoothComm.mosOut;
                    InputStream inSt = BluetoothComm.misIn;
                    if (outSt != null && inSt != null) {
                        fps = new FPS(LoginActivity.setupInstance, outSt, inSt);
                    } else {
                        handler.obtainMessage(MESSAGE_BOX, "Leopard Not Activated...").sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iRetVal = fps.iFpsVerifyMinutiae(brecentminituaedata, new FpsConfig(1, (byte) 0x0f));
                Log.i(TAG, "iRetVal-->" + String.valueOf(iRetVal));
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                Log.i(TAG, "iRetVal-->" + String.valueOf(iRetVal));
                e.printStackTrace();
            }
            return iRetVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (iRetVal == DEVICE_NOTCONNECTED) {
                handler.obtainMessage(DEVICE_NOTCONNECTED, "Device not connected").sendToTarget();
            } else if (iRetVal == FPS.SUCCESS) {
                blVerifyfinger = true;
                prepareModel(Constants.DepositProcCode);
            } else if (iRetVal == FPS.FPS_INACTIVE_PERIPHERAL) {
                handler.obtainMessage(MESSAGE_BOX, "Peripheral is inactive").sendToTarget();
            } else if (iRetVal == FPS.TIME_OUT) {
                handler.obtainMessage(MESSAGE_BOX, "Capture finger time out").sendToTarget();
            } else if (iRetVal == FPS.FPS_ILLEGAL_LIBRARY) {
                handler.obtainMessage(MESSAGE_BOX, "Illegal library").sendToTarget();
            } else if (iRetVal == FPS.FAILURE) {
                handler.obtainMessage(MESSAGE_BOX, "Captured template verification is failed,\nWrong finger placed").sendToTarget();
            } else if (iRetVal == FPS.PARAMETER_ERROR) {
                handler.obtainMessage(MESSAGE_BOX, "Parameter error").sendToTarget();
            } else if (iRetVal == FPS.FPS_INVALID_DEVICE_ID) {
                handler.obtainMessage(MESSAGE_BOX, "Library is in demo version").sendToTarget();
            } else {
                handler.obtainMessage(MESSAGE_BOX, "Invalid Credentials").sendToTarget();
            }
            super.onPostExecute(result);
        }
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
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : " + tv_account_name.getText().toString().trim() + "\n");
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                        + getBundle.getString("product_code").trim() + "/" + getBundle.getString("account_no").trim() + "\n");
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                        byteToRupeeConversion("Balance   : ", "" + avlBalance.trim()));
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");

                if ("CD".equals(getBundle.getString("product_code"))
                        || "DDS".equals(getBundle.getString("product_code"))
                        || "SB".equals(getBundle.getString("product_code"))) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "");
                } else {
                    if (getBundle.getString("getLedgbalance") != null
                            && !"".equals(getBundle.getString("getLedgbalance"))
                            && !"0".equals(getBundle.getString("getLedgbalance"))) {
                        if (getBundle.getString("getLedgbalance").contains("-")) {
                            ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                    byteToRupeeConversion("Overdue   : ", "" + getBundle.getString("getLedgbalance").substring(1)));
                        } else {
                            ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                    byteToRupeeConversion("Adv Bal   : ", "" + getBundle.getString("getLedgbalance").trim()));
                        }
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                    } else {
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "");
                    }
                }

                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : " + getBundle.getString("getRrn").trim() + "\n");
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname.trim() + "\n");
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
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.print);
            progressDialog.setMessage("Printing in progress...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
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
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : " + tv_account_name.getText().toString().trim() + "\n");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                        + getBundle.getString("product_code").trim() + "/" + getBundle.getString("account_no").trim() + "\n");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                        byteToRupeeConversion("Balance   : ", "" + avlBalance));
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");

                if ("CD".equals(getBundle.getString("product_code"))
                        || "DDS".equals(getBundle.getString("product_code"))
                        || "SB".equals(getBundle.getString("product_code"))) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "");
                } else {
                    if (getBundle.getString("getLedgbalance") != null
                            && !"".equals(getBundle.getString("getLedgbalance"))
                            && !"0".equals(getBundle.getString("getLedgbalance"))) {
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                byteToRupeeConversion("Overdue   : ", "" + getBundle.getString("getLedgbalance").trim()));
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                    } else {
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "");
                    }
                }

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : " + getBundle.getString("getRrn").trim() + "\n");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : " + GlobalModel.bcid + "/" + agentname + "\n");
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
        Fragment txn;
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        txn = new Txn_Fr();
        fragmentTransaction.replace(R.id.content_frame, txn);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }
}
