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
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.SpinnerList;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.NumericKeyBoardTransformation;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AadhaarMobile_Fr extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AadhaarMobile_Fr-->";
    View view;
    CardView cardView;
    AppCompatTextView tv_main_header, tv_account_details_response;
    AppCompatSpinner spinner_product_name;
    AppCompatEditText et_account_no;
    AppCompatButton btn_clear_details, btn_get_details;
    DbFunctions dbFunctions;
    String rrn, stan, product_code, account_no, avlBalance, overdue;
    TxnModel txnModel;
    TransactionBO transactionBO;
    public static String calledFrom;
    Storage storage;
    DialogsUtil dialogsUtil;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.txn, container, false);

        setTitle(R.string.service);

        loadUiComponents();

        clearFields();

        objInit();

        spinner_product_name.setOnItemSelectedListener(this);
        ArrayList<SpinnerList> productList;
        if ("AADHAAR_SEEDING".equals(Services_Fr.calledFrom)) {
            setSubTitle(R.string.aadhaar_seeding);
            tv_main_header.setText(R.string.aadhaar_seeding);
        } else if ("MOBILE_SEEDING".equals(Services_Fr.calledFrom)) {
            setSubTitle(R.string.mobile_seeding);
            tv_main_header.setText(R.string.mobile_seeding);
        } else {
            setSubTitle(R.string.pan_update);
            tv_main_header.setText(R.string.pan_update);
        }

        //assert productList != null;
        productList = dbFunctions.getProductDetails();
        final ArrayAdapter<SpinnerList> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, productList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product_name.setAdapter(arrayAdapter);

        btn_get_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account_no = et_account_no.getText().toString().trim();
                if (product_code.equals("-- Select Code --")) {
                    dialogsUtil.alertDialog("Select product code");
                    spinner_product_name.requestFocus();
                } else if (account_no.isEmpty()) {
                    et_account_no.setError("Enter account no.");
                    et_account_no.requestFocus();
                } else {
                    prepareModel(Constants.BalanceEnqryProcCode);
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

        tv_main_header = view.findViewById(R.id.tv_main_header);

        spinner_product_name = view.findViewById(R.id.spinner_product_name);

        et_account_no = view.findViewById(R.id.et_account_no);

        btn_get_details = view.findViewById(R.id.btn_get_details);
        btn_clear_details = view.findViewById(R.id.btn_clear_details);

        tv_account_details_response = view.findViewById(R.id.tv_account_details_response);
    }

    private void clearFields() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

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
        product_code = String.valueOf(adapterView.getSelectedItem());
        Log.i(TAG, "SelectedItem--->" + adapterView.getSelectedItem());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
        txnModel.setCustno("");
        txnModel.setAmount("");
        if ("AADHAAR_SEEDING".equals(Services_Fr.calledFrom)) {
            txnModel.setTxnType("nf");
            txnModel.setRequestFrom("aadhaar_seeding");
        } else if ("MOBILE_SEEDING".equals(Services_Fr.calledFrom)) {
            txnModel.setTxnType("nf");
            txnModel.setRequestFrom("mobile_seeding");
        } else {
            txnModel.setTxnType("nf");
            txnModel.setRequestFrom("pan_update");
        }

        boolean insertInDB = dbFunctions.insertTxnInDatabase(txnModel);
        if (!insertInDB) {
            Toasty.warning(getActivity(), "Unable to insert in DB", Toast.LENGTH_SHORT).show();
        }

        Txn_Fr.calledFrom = "";

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
                if ("AADHAAR_SEEDING".equals(Services_Fr.calledFrom)) {
                    if (!"".equals(transactionBO.txnModel.getCustName())
                            && transactionBO.txnModel.getCustName().length() > 0) {
                        calledFrom = "TXN_AADHAAR_SEEDING";
                        storage.saveSecure(Constants.FRAGMENT_NAVIGATION, "TXN_AADHAAR_SEEDING");
                        setBundle.putString("account_no", account_no);
                        setBundle.putString("product_code", product_code);
                        setBundle.putString("getCustName", "" + transactionBO.txnModel.getCustName());
                        if (transactionBO.txnModel.getAvlbalance() != null
                                && !"".equals(transactionBO.txnModel.getAvlbalance())
                                && transactionBO.txnModel.getAvlbalance().length() > 0) {
                            avlBalance = transactionBO.txnModel.getAvlbalance();
                            avlBalance = avlBalance.substring(1);
                            setBundle.putString("getAvlbalance", "" + avlBalance);
                        } else {
                            setBundle.putString("getAvlbalance", "");
                        }
                        if (transactionBO.txnModel.getLedgbalance() != null
                                && !"".equals(transactionBO.txnModel.getLedgbalance())
                                && transactionBO.txnModel.getLedgbalance().length() > 0) {
                            overdue = transactionBO.txnModel.getLedgbalance();
                            overdue = overdue.substring(1);
                            setBundle.putString("getLedgbalance", "" + overdue);
                        } else {
                            setBundle.putString("getLedgbalance", "");
                        }
                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + transactionBO.txnModel.getCustName(),
                                "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            //Toasty.warning(getActivity(), "Unable to update in DB", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Unable to update in DB");
                        }
                        ft.replace(R.id.content_frame, loanSaving);
                        loanSaving.setArguments(setBundle);
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else if ("MOBILE_SEEDING".equals(Services_Fr.calledFrom)) {
                    if (!"".equals(transactionBO.txnModel.getCustName())
                            && transactionBO.txnModel.getCustName().length() > 0) {
                        calledFrom = "TXN_MOBILE_SEEDING";
                        storage.saveSecure(Constants.FRAGMENT_NAVIGATION, "TXN_MOBILE_SEEDING");
                        setBundle.putString("account_no", account_no);
                        setBundle.putString("product_code", product_code);
                        setBundle.putString("getCustName", "" + transactionBO.txnModel.getCustName());
                        if (transactionBO.txnModel.getAvlbalance() != null
                                && !"".equals(transactionBO.txnModel.getAvlbalance())
                                && transactionBO.txnModel.getAvlbalance().length() > 0) {
                            avlBalance = transactionBO.txnModel.getAvlbalance();
                            avlBalance = avlBalance.substring(1);
                            setBundle.putString("getAvlbalance", "" + avlBalance);
                        } else {
                            setBundle.putString("getAvlbalance", "");
                        }
                        if (transactionBO.txnModel.getLedgbalance() != null
                                && !"".equals(transactionBO.txnModel.getLedgbalance())
                                && transactionBO.txnModel.getLedgbalance().length() > 0) {
                            overdue = transactionBO.txnModel.getLedgbalance();
                            overdue = overdue.substring(1);
                            setBundle.putString("getLedgbalance", "" + overdue);
                        } else {
                            setBundle.putString("getLedgbalance", "");
                        }
                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + transactionBO.txnModel.getCustName(),
                                "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            //Toasty.warning(getActivity(), "Unable to update in DB", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Unable to update in DB");
                        }
                        ft.replace(R.id.content_frame, loanSaving);
                        loanSaving.setArguments(setBundle);
                    } else {
                        tv_account_details_response.setVisibility(View.VISIBLE);
                        tv_account_details_response.setText(R.string.account_details_not_found);
                    }
                } else {
                    if (!"".equals(transactionBO.txnModel.getCustName())
                            && transactionBO.txnModel.getCustName().length() > 0) {
                        calledFrom = "TXN_PAN_UPDATE";
                        storage.saveSecure(Constants.FRAGMENT_NAVIGATION, "TXN_PAN_UPDATE");
                        setBundle.putString("account_no", account_no);
                        setBundle.putString("product_code", product_code);
                        setBundle.putString("getCustName", "" + transactionBO.txnModel.getCustName());
                        if (transactionBO.txnModel.getAvlbalance() != null
                                && !"".equals(transactionBO.txnModel.getAvlbalance())
                                && transactionBO.txnModel.getAvlbalance().length() > 0) {
                            avlBalance = transactionBO.txnModel.getAvlbalance();
                            avlBalance = avlBalance.substring(1);
                            setBundle.putString("getAvlbalance", "" + avlBalance);
                        } else {
                            setBundle.putString("getAvlbalance", "");
                        }
                        if (transactionBO.txnModel.getLedgbalance() != null
                                && !"".equals(transactionBO.txnModel.getLedgbalance())
                                && transactionBO.txnModel.getLedgbalance().length() > 0) {
                            overdue = transactionBO.txnModel.getLedgbalance();
                            overdue = overdue.substring(1);
                            setBundle.putString("getLedgbalance", "" + overdue);
                        } else {
                            setBundle.putString("getLedgbalance", "");
                        }
                        updateInDB = dbFunctions.updateTxnInDatabase("" + rrn, "" + transactionBO.txnModel.getCustName(),
                                "" + transactionBO.txnModel.getAvlbalance(), "" + transactionBO.txnModel.getRrn(),
                                "" + transactionBO.txnModel.getTxnStatus(), "" + transactionBO.txnModel.getResponsecode(),
                                "" + transactionBO.txnModel.getLedgbalance(), "" + txnModel.getTableName());
                        if (!updateInDB) {
                            Log.i(TAG, "Unable to update in DB");
                        }
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
