package com.vcbl.tabbanking.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.TransactionBO;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.NumericKeyBoardTransformation;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class Txn_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Txn_Fr-->";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header, tv_account_details_response;
    AppCompatSpinner spinner_product_name;
    AppCompatEditText et_account_no;
    AppCompatButton btn_clear_details, btn_get_details;
    DbFunctions dbFunctions;
    int productId;
    String rrn, stan, product_code, account_no, avlBalance, overdue, outStanding;
    TxnModel txnModel;
    TransactionBO transactionBO;
    public static String calledFrom;
    Storage storage;
    DialogsUtil dialogsUtil;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.txn, container, false);

        setTitle(R.string.direct_deposits);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        loadUiComponents();

        objInit();

        clearFields();

        spinner_product_name.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> productList;
        if ("LOAN".equals(Transaction.calledFrom)) {
            setSubTitle(R.string.loan);
            tv_main_header.setText(R.string.loan);
            productList = dbFunctions.getProductForLoan();
        } else if ("SAVING".equals(Transaction.calledFrom)) {
            setSubTitle(R.string.saving);
            tv_main_header.setText(R.string.saving);
            productList = dbFunctions.getProductForSaving();
        } else if ("LOAN_HISTORY".equals(Transaction.calledFrom)) {
            setSubTitle(R.string.customer_loan_history);
            tv_main_header.setText(R.string.customer_loan_history);
            productList = dbFunctions.getProductForLoan();
        } else {
            setSubTitle(R.string.balance_enquiry);
            tv_main_header.setText(R.string.balance_enquiry);
            productList = dbFunctions.getProductDetails();
        }
        assert productList != null;
        ArrayAdapter<SpinnerList> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, productList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product_name.setAdapter(arrayAdapter);

        btn_get_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account_no = et_account_no.getText().toString().trim();
                if (productId == 0) {
                    dialogsUtil.alertDialog("Select product code");
                } else if (account_no.isEmpty()) {
                    et_account_no.setError("Enter account no.");
                    et_account_no.requestFocus();
                } else {
                    /*padded_account_no = Utility.padRight(product_code, 8, " ");
                    padded_account_no = padded_account_no + Utility.padLeft(account_no, 16, "0");
                    padded_account_no = Utility.padRight(padded_account_no, 32, "0");*/
                    //calledFrom = "";
                    if ("LOAN".equals(Transaction.calledFrom)
                            || "SAVING".equals(Transaction.calledFrom)
                            || "BAL_ENQ".equals(Transaction.calledFrom)) {
                        prepareModel(Constants.BalanceEnqryProcCode);
                    } else {
                        prepareModel(Constants.LoanHistoryProcCode);
                    }
                }
            }
        });

        btn_clear_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        return view;
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        tv_main_header = view.findViewById(R.id.tv_main_header);

        spinner_product_name = view.findViewById(R.id.spinner_product_name);

        et_account_no = view.findViewById(R.id.et_account_no);

        btn_get_details = view.findViewById(R.id.btn_get_details);
        btn_clear_details = view.findViewById(R.id.btn_clear_details);

        tv_account_details_response = view.findViewById(R.id.tv_account_details_response);
    }

    private void clearFields() {
        spinner_product_name.setSelection(0);

        et_account_no.setText("");
        et_account_no.setHint(R.string.account_no);
        et_account_no.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et_account_no.setTransformationMethod(new NumericKeyBoardTransformation());

        tv_account_details_response.setVisibility(View.GONE);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        productId = adapterView.getSelectedItemPosition();
        product_code = String.valueOf(adapterView.getSelectedItem());
        Log.i(TAG, "productId--->" + adapterView.getSelectedItemPosition());
        Log.i(TAG, "product_code--->" + String.valueOf(adapterView.getSelectedItem()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void prepareModel(final String processingCode) {
        txnModel = new TxnModel();
        stan = Utility.padLeft(getStan(), 6, "0");
        rrn = Utility.getYear().substring(3)
                + Utility.padLeft(Utility.getDayofYear(), 3, "0")
                + Utility.padLeft(Utility.getHours(), 2, "0")
                + stan;
        txnModel.setRrn(rrn);
        txnModel.setStan(stan);
        txnModel.setAccNo(account_no);
        txnModel.setLocAccNo(product_code + "/" + account_no);
        txnModel.setTxndate(Utility.getCurrentTimeStamp());
        txnModel.setMicroatmid(String.valueOf(GlobalModel.microatmid));
        txnModel.setBcid(GlobalModel.bcid);
        txnModel.setBranch_id(GlobalModel.branchid);
        txnModel.setTxnserviceid(Constants.txnserviceid);
        txnModel.setTxnsubserviceid(Constants.txnsubserviceid);
        txnModel.setProcessingcode(processingCode);
        txnModel.setProductcode(product_code);
        txnModel.setAmount("");
        if (processingCode.equals(Constants.BalanceEnqryProcCode)) {
            if ("LOAN".equals(Transaction.calledFrom)) {
                txnModel.setTxnType("dr");
                txnModel.setRequestFrom("loan");
            } else if ("SAVING".equals(Transaction.calledFrom)) {
                txnModel.setTxnType("cr");
                txnModel.setRequestFrom("saving");
            } else {
                if ("CD".equals(product_code)
                        || "DDS".equals(product_code)
                        || "SB".equals(product_code)) {
                    txnModel.setTxnType("cr");
                } else {
                    txnModel.setTxnType("");
                }
                txnModel.setRequestFrom("bal_enq");
            }
        } else {
            txnModel.setTxnType("nf");
            txnModel.setRequestFrom("loanHistory");
        }

        boolean insertInDB = dbFunctions.insertTxnInDatabase(txnModel);
        if (!insertInDB) {
            //Toasty.warning(Objects.requireNonNull(getActivity()), "Unable to insert in DB", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Unable to insert in DB");
        }

        AadhaarMobile_Fr.calledFrom = "";

        transactionBO = new TransactionBO(getActivity());
        transactionBO.transactionRequest(txnModel);
        transactionBO.setOnTaskFinishedEvent(new TransactionBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished() {
                Fragment loanSaving = new LoanSaving_Fr();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle setBundle = new Bundle();
                boolean updateInDB;
                if (transactionBO.txnResponseModel.getStatus() == Masters.Status.FAILURE) {
                    tv_account_details_response.setVisibility(View.VISIBLE);
                    tv_account_details_response.setText(transactionBO.txnResponseModel.getStatusDescription());
                } else if ("LOAN".equals(Transaction.calledFrom)
                        && processingCode.equals(Constants.BalanceEnqryProcCode)) {
                    if (transactionBO.txnModel.getCustName() != null
                            && !"".equals(transactionBO.txnModel.getCustName())
                            && transactionBO.txnModel.getCustName().length() > 0) {
                        calledFrom = "TXN_LOAN";
                        // loan data in global model class
                        EnrollmentData.getTxnDataModel().setAccountNo(account_no);
                        EnrollmentData.getTxnDataModel().setProductCode(product_code);
                        EnrollmentData.getTxnDataModel().setCustName(transactionBO.txnModel.getCustName());

                        setBundle.putString("account_no", account_no);
                        setBundle.putString("product_code", product_code);
                        setBundle.putString("getCustName", "" + transactionBO.txnModel.getCustName());
                        if (transactionBO.txnModel.getAvlbalance() != null
                                && !"".equals(transactionBO.txnModel.getAvlbalance())
                                && transactionBO.txnModel.getAvlbalance().length() > 0) {
                            avlBalance = transactionBO.txnModel.getAvlbalance();
                            setBundle.putString("getAvlbalance", avlBalance);
                            EnrollmentData.getTxnDataModel().setAvlBalance(transactionBO.txnModel.getAvlbalance());
                        } else {
                            avlBalance = "";
                            setBundle.putString("getAvlbalance", "");
                            EnrollmentData.getTxnDataModel().setAvlBalance("");
                        }
                        if (transactionBO.txnModel.getLedgbalance() != null
                                && !"".equals(transactionBO.txnModel.getLedgbalance())
                                && transactionBO.txnModel.getLedgbalance().length() > 0) {
                            overdue = transactionBO.txnModel.getLedgbalance();
                            setBundle.putString("getLedgbalance", overdue);
                            EnrollmentData.getTxnDataModel().setLedgBalance(transactionBO.txnModel.getLedgbalance());
                        } else {
                            overdue = "";
                            setBundle.putString("getLedgbalance", "");
                            EnrollmentData.getTxnDataModel().setLedgBalance("");
                        }
                        if (transactionBO.txnModel.getAccountStatusDesc() != null
                                && !"".equals(transactionBO.txnModel.getAccountStatusDesc())
                                && transactionBO.txnModel.getAccountStatusDesc().length() > 0) {
                            setBundle.putString("getAccountStatusDesc", transactionBO.txnModel.getAccountStatusDesc());
                            EnrollmentData.getTxnDataModel().setAccountStatusDesc(transactionBO.txnModel.getAccountStatusDesc());
                        } else {
                            setBundle.putString("getAccountStatusDesc", "");
                            EnrollmentData.getTxnDataModel().setAccountStatusDesc("");
                        }
                        if (transactionBO.txnModel.getAccountTypeDesc() != null
                                && !"".equals(transactionBO.txnModel.getAccountTypeDesc())
                                && transactionBO.txnModel.getAccountTypeDesc().length() > 0) {
                            setBundle.putString("getAccountTypeDesc", transactionBO.txnModel.getAccountTypeDesc());
                            EnrollmentData.getTxnDataModel().setAccountTypeDesc(transactionBO.txnModel.getAccountTypeDesc());
                        } else {
                            setBundle.putString("getAccountTypeDesc", "");
                            EnrollmentData.getTxnDataModel().setAccountTypeDesc("");
                        }
                        setBundle.putString("getAccountType", String.valueOf(transactionBO.txnModel.getAccountType()));
                        EnrollmentData.getTxnDataModel().setAccountType(transactionBO.txnModel.getAccountType());
                        setBundle.putString("getAccountStatus", String.valueOf(transactionBO.txnModel.getAccountStatus()));
                        EnrollmentData.getTxnDataModel().setAccountStatus(transactionBO.txnModel.getAccountStatus());
                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + transactionBO.txnModel.getCustName(),
                                "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            Toasty.warning(getActivity(), "Unable to update in DB", Toast.LENGTH_SHORT).show();
                        }
                        ft.replace(R.id.content_frame, loanSaving);
                        loanSaving.setArguments(setBundle);
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else if ("SAVING".equals(Transaction.calledFrom)
                        && processingCode.equals(Constants.BalanceEnqryProcCode)) {
                    if (transactionBO.txnModel.getCustName() != null
                            && !"".equals(transactionBO.txnModel.getCustName())
                            && transactionBO.txnModel.getCustName().length() > 0) {
                        calledFrom = "TXN_SAVING";
                        setBundle.putString("account_no", account_no);
                        setBundle.putString("product_code", product_code);
                        setBundle.putString("getCustName", "" + transactionBO.txnModel.getCustName());
                        if (transactionBO.txnModel.getAvlbalance() != null
                                && !"".equals(transactionBO.txnModel.getAvlbalance())
                                && transactionBO.txnModel.getAvlbalance().length() > 0) {
                            avlBalance = transactionBO.txnModel.getAvlbalance();
                            setBundle.putString("getAvlbalance", avlBalance);
                        } else {
                            avlBalance = "";
                            setBundle.putString("getAvlbalance", "");
                        }
                        if (transactionBO.txnModel.getLedgbalance() != null
                                && !"".equals(transactionBO.txnModel.getLedgbalance())
                                && transactionBO.txnModel.getLedgbalance().length() > 0) {
                            overdue = transactionBO.txnModel.getLedgbalance();
                            //setBundle.putString("getLedgbalance", overdue);
                        } else {
                            overdue = "";
                            //setBundle.putString("getLedgbalance", "");
                        }
                        if (transactionBO.txnModel.getAccountStatusDesc() != null
                                && !"".equals(transactionBO.txnModel.getAccountStatusDesc())
                                && transactionBO.txnModel.getAccountStatusDesc().length() > 0) {
                            setBundle.putString("getAccountStatusDesc", transactionBO.txnModel.getAccountStatusDesc());
                        } else {
                            setBundle.putString("getAccountStatusDesc", "");
                        }
                        if (transactionBO.txnModel.getAccountTypeDesc() != null
                                && !"".equals(transactionBO.txnModel.getAccountTypeDesc())
                                && transactionBO.txnModel.getAccountTypeDesc().length() > 0) {
                            setBundle.putString("getAccountTypeDesc", transactionBO.txnModel.getAccountTypeDesc());
                        } else {
                            setBundle.putString("getAccountTypeDesc", "");
                        }
                        setBundle.putString("getAccountType", String.valueOf(transactionBO.txnModel.getAccountType()));
                        setBundle.putString("getAccountStatus", String.valueOf(transactionBO.txnModel.getAccountStatus()));
                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + transactionBO.txnModel.getCustName(),
                                "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            Toasty.warning(getActivity(), "Unable to update in DB", Toast.LENGTH_SHORT).show();
                        }
                        ft.replace(R.id.content_frame, loanSaving);
                        loanSaving.setArguments(setBundle);
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else if ("LOAN_HISTORY".equals(Transaction.calledFrom)
                        && processingCode.equals(Constants.LoanHistoryProcCode)) {
                    if (transactionBO.loanHistoryModel.getCustName() != null
                            && !"".equals(transactionBO.loanHistoryModel.getCustName())
                            && transactionBO.loanHistoryModel.getCustName().length() > 0) {
                        calledFrom = "TXN_LOAN_HISTORY";
                        setBundle.putString("account_no", account_no);
                        setBundle.putString("product_code", product_code);
                        setBundle.putString("getBranchcode", transactionBO.loanHistoryModel.getBranchcode());
                        setBundle.putString("getAccountNom", transactionBO.loanHistoryModel.getAccountNom());
                        setBundle.putString("getCustName", transactionBO.loanHistoryModel.getCustName());
                        setBundle.putString("getScanctiondt", Utility.getDate(transactionBO.loanHistoryModel.getScanctiondt()));
                        setBundle.putString("getScanctionAmount", transactionBO.loanHistoryModel.getScanctionAmount());
                        setBundle.putString("getInstallmentAmt", transactionBO.loanHistoryModel.getInstallmentAmt());
                        setBundle.putString("getExpirydate", Utility.getDate(transactionBO.loanHistoryModel.getExpirydate()));
                        setBundle.putString("getNoofinstallments", String.valueOf(transactionBO.loanHistoryModel.getNoofinstallments()));
                        if (transactionBO.loanHistoryModel.getOutstandingBal() != null
                                && !"".equals(transactionBO.loanHistoryModel.getOutstandingBal())
                                && transactionBO.loanHistoryModel.getOutstandingBal().length() > 0) {
                            if (transactionBO.loanHistoryModel.getOutstandingBal().contains("-"))
                                outStanding = (transactionBO.loanHistoryModel.getOutstandingBal()).substring(1);
                            else
                                outStanding = (transactionBO.loanHistoryModel.getOutstandingBal());
                            setBundle.putString("getOutstandingBal", "" + outStanding);
                        } else {
                            setBundle.putString("getOutstandingBal", "");
                        }
                        if (transactionBO.loanHistoryModel.getOverdue() != null
                                && !"".equals(transactionBO.loanHistoryModel.getOverdue())
                                && transactionBO.loanHistoryModel.getOverdue().length() > 0) {
                            if (transactionBO.loanHistoryModel.getOutstandingBal().contains("-"))
                                overdue = (transactionBO.loanHistoryModel.getOverdue()).substring(1);
                            else
                                overdue = (transactionBO.loanHistoryModel.getOverdue());
                            setBundle.putString("getOverdue", "" + overdue);
                        } else {
                            setBundle.putString("getOverdue", "");
                        }
                        setBundle.putString("getReserve1", String.valueOf(Float.parseFloat(transactionBO.loanHistoryModel.getReserve1())) + "%");
                        setBundle.putString("getReserve2", transactionBO.loanHistoryModel.getReserve2());
                        setBundle.putString("getReserve3", transactionBO.loanHistoryModel.getReserve3());
                        setBundle.putString("getTxndate", Utility.getDate(transactionBO.loanHistoryModel.getTxndate()));
                        setBundle.putString("getPendingInstlmnts", String.valueOf(transactionBO.loanHistoryModel.getPendingInstlmnts()));
                        if (transactionBO.loanHistoryModel.getAccountStatusDesc() != null
                                && !"".equals(transactionBO.loanHistoryModel.getAccountStatusDesc())
                                && transactionBO.loanHistoryModel.getAccountStatusDesc().length() > 0) {
                            setBundle.putString("getAccountStatusDesc", transactionBO.loanHistoryModel.getAccountStatusDesc());
                        } else {
                            setBundle.putString("getAccountStatusDesc", "");
                        }
                        if (transactionBO.loanHistoryModel.getAccountTypeDesc() != null
                                && !"".equals(transactionBO.loanHistoryModel.getAccountTypeDesc())
                                && transactionBO.loanHistoryModel.getAccountTypeDesc().length() > 0) {
                            setBundle.putString("getAccountTypeDesc", transactionBO.loanHistoryModel.getAccountTypeDesc());
                        } else {
                            setBundle.putString("getAccountTypeDesc", "");
                        }
                        setBundle.putString("getAccountType", String.valueOf(transactionBO.loanHistoryModel.getAccountType()));
                        setBundle.putString("getAccountStatus", String.valueOf(transactionBO.loanHistoryModel.getAccountStatus()));
                        ft.replace(R.id.content_frame, loanSaving);
                        loanSaving.setArguments(setBundle);
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else {
                    if (transactionBO.txnModel.getCustName() != null
                            && !"".equals(transactionBO.txnModel.getCustName())
                            && transactionBO.txnModel.getCustName().length() > 0) {
                        calledFrom = "TXN_BAL_ENQ";
                        setBundle.putString("account_no", account_no.trim());
                        setBundle.putString("product_code", product_code.trim());
                        setBundle.putString("getCustName", "" + transactionBO.txnModel.getCustName());
                        if (transactionBO.txnModel.getAvlbalance() != null
                                && !"".equals(transactionBO.txnModel.getAvlbalance())
                                && transactionBO.txnModel.getAvlbalance().length() > 0) {
                            avlBalance = transactionBO.txnModel.getAvlbalance();
                            setBundle.putString("getAvlbalance", avlBalance);
                        } else {
                            avlBalance = "";
                            setBundle.putString("getAvlbalance", "");
                        }
                        if (transactionBO.txnModel.getLedgbalance() != null
                                && !"".equals(transactionBO.txnModel.getLedgbalance())
                                && transactionBO.txnModel.getLedgbalance().length() > 0) {
                            overdue = transactionBO.txnModel.getLedgbalance();
                            setBundle.putString("getLedgbalance", overdue.trim());
                        } else {
                            overdue = "";
                            setBundle.putString("getLedgbalance", "");
                        }
                        if (transactionBO.txnModel.getAccountStatusDesc() != null
                                && !"".equals(transactionBO.txnModel.getAccountStatusDesc())
                                && transactionBO.txnModel.getAccountStatusDesc().length() > 0) {
                            setBundle.putString("getAccountStatusDesc", transactionBO.txnModel.getAccountStatusDesc());
                        } else {
                            setBundle.putString("getAccountStatusDesc", "");
                        }
                        if (transactionBO.txnModel.getAccountTypeDesc() != null
                                && !"".equals(transactionBO.txnModel.getAccountTypeDesc())
                                && transactionBO.txnModel.getAccountTypeDesc().length() > 0) {
                            setBundle.putString("getAccountTypeDesc", transactionBO.txnModel.getAccountTypeDesc());
                        } else {
                            setBundle.putString("getAccountTypeDesc", "");
                        }
                        setBundle.putString("getAccountType", String.valueOf(transactionBO.txnModel.getAccountType()));
                        setBundle.putString("getAccountStatus", String.valueOf(transactionBO.txnModel.getAccountStatus()));
                        setBundle.putString("getRrn", transactionBO.txnModel.getRrn());
                        ft.replace(R.id.content_frame, loanSaving);
                        loanSaving.setArguments(setBundle);
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

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }
}
