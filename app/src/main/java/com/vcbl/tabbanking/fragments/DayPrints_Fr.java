package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.leopard.api.Printer;
import com.prowesspride.api.Printer_GEN;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.activities.LoginActivity;
import com.vcbl.tabbanking.adapters.ReprintAdapter;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.interactors.DetailedTxnBO;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.models.ReportSearchModel;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.Conversions;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DayPrints_Fr extends Fragment {

    private static final String TAG = "DayPrints_Fr-->";
    View view;
    AppCompatTextView tv_main_header, tv_reports_not_found;
    RecyclerView recyclerView;
    //Bundle getBundle = null;
    private static Printer ptr;
    public static Printer_GEN ptrGen;
    private final static int MESSAGE_BOX = 1;
    private int iRetVal;
    private ProgressDialog progressDialog;
    private static final int DEVICE_NOTCONNECTED = -100;
    private String branchname, agentName, bal_split = "";
    RecyclerItem recyclerItem;
    int itemPosition;
    DbFunctions dbFunctions;
    Storage storage;
    List<RecyclerItem> list;
    DialogsUtil dialogsUtil;
    DetailedTxnBO detailedTxnBO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.daily_txn_details, container, false);

        loadUiComponents();

        objInit();

        if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
            printerObjInit();
        }

        if (list != null) {
            list.clear();
        }

        branchname = dbFunctions.getBranchName(String.valueOf(GlobalModel.branchid));
        agentName = dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid));

        prepareModel();

        return view;
    }

    private void prepareModel() {
        ReportSearchModel searchModel = new ReportSearchModel();
        searchModel.setFromDate(Utility.getCurrentTimeStamp());
        searchModel.setToDate(Utility.getCurrentTimeStamp());
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
                if (list != null) {
                    list.clear();
                }
                if (searchModel.getList() == null
                        || searchModel.getList().size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    tv_reports_not_found.setVisibility(View.VISIBLE);
                    tv_reports_not_found.setText(R.string.no_reports_found);
                } else {
                    list = searchModel.getList();
                    recyclerView.setAdapter(new ReprintAdapter(list, new ReprintAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final RecyclerItem item, final int position) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.app_name);
                            builder.setIcon(R.drawable.app_logo);
                            builder.setMessage("Need duplicate print ?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int id) {
                                    //recyclerItem = item;
                                    itemPosition = position;
                                    recyclerItem = list.get(position);
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
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }, getActivity()));
                }
            }
        });
    }

    private void loadUiComponents() {
        tv_main_header = view.findViewById(R.id.tv_main_header);
        tv_main_header.setText(R.string.reprint);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        tv_reports_not_found = view.findViewById(R.id.tv_reports_not_found);
    }

    private void objInit() {
        dbFunctions = DbFunctions.getInstance(getActivity());
        storage = new Storage(getActivity());
        dialogsUtil = new DialogsUtil(getActivity());
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

    private void clearFields() {
        tv_main_header.setText(R.string.reprint);
        tv_reports_not_found.setVisibility(View.GONE);
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
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "    DUPLICATE RECEIPT");
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");
                ptr.iBmpPrint(getActivity(), R.drawable.logo3);
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Branch    : " + branchname.trim());
                Character ch = '-';
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);

                if (recyclerItem.getDataTime() != null
                        && !"".equals(recyclerItem.getDataTime())
                        && recyclerItem.getDataTime().length() > 0) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Date:"
                            + recyclerItem.getDataTime().trim());
                }

                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);

                if (recyclerItem.getCustName() != null
                        && !"".equals(recyclerItem.getCustName())
                        && recyclerItem.getCustName().length() > 0) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "\n" + "Customer  : "
                            + recyclerItem.getCustName().trim() + "\n");
                }

                if (recyclerItem.getProductCode() != null
                        && recyclerItem.getProductCode().length() > 0
                        && recyclerItem.getAccountNo() != null
                        && recyclerItem.getAccountNo().length() > 0) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Acc. No   : "
                            + recyclerItem.getProductCode() + "/" + recyclerItem.getAccountNo() + "\n");
                }

                if (recyclerItem.getAmount() != null
                        && !"".equals(recyclerItem.getAmount())
                        && recyclerItem.getAmount().length() > 0) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                            byteToRupeeConversion("Amount    : ", recyclerItem.getAmount()));
                }

                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");

                if (recyclerItem.getAvblBalance() != null
                        && !"".equals(recyclerItem.getAvblBalance())
                        && recyclerItem.getAvblBalance().length() > 0) {
                    if (recyclerItem.getAvblBalance().contains("-")) {
                        bal_split = recyclerItem.getAvblBalance().substring(1);
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                byteToRupeeConversion("Balance   : ", bal_split));
                    } else {
                        bal_split = recyclerItem.getAvblBalance();
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                byteToRupeeConversion("Balance   : ", bal_split));
                    }
                }

                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");

                if (recyclerItem.getOverdue() != null
                        && !"".equals(recyclerItem.getOverdue())
                        && recyclerItem.getOverdue().length() > 0) {
                    if (recyclerItem.getOverdue().contains("-")) {
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                byteToRupeeConversion("Overdue   : ", recyclerItem.getOverdue().substring(1)));
                    } else {
                        ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL,
                                byteToRupeeConversion("Adv Bal   : ", recyclerItem.getOverdue()));
                    }
                }

                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "            ");

                if (recyclerItem.getServerRRN() != null
                        && !"".equals(recyclerItem.getServerRRN())
                        && recyclerItem.getServerRRN().length() > 0) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Ref No.   : "
                            + recyclerItem.getServerRRN() + "\n");
                }

                if (recyclerItem.getAgentId() != null
                        && !"".equals(recyclerItem.getAgentId())
                        && recyclerItem.getAgentId().length() > 0) {
                    ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "Agent ID  : "
                            + recyclerItem.getAgentId() + "/" + agentName + "\n");
                }

                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);
                ptr.iPrinterAddData(Printer.PR_FONTLARGENORMAL, "       Thank you");
                ptr.iPrinterAddLine(Printer.PR_FONTLARGENORMAL, ch);

                ptr.iPaperFeed();
                //SystemClock.sleep(100);
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
                //handler.obtainMessage(MESSAGE_BOX, "Print Success").sendToTarget();
                Log.i(TAG, "Print Success");
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
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "    DUPLICATE RECEIPT");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");
                ptrGen.iBmpPrint(getActivity(), R.drawable.logo3);

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Branch    : " + branchname.trim());
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");

                if (recyclerItem.getDataTime() != null
                        && !"".equals(recyclerItem.getDataTime())
                        && recyclerItem.getDataTime().length() > 0) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Date:"
                            + recyclerItem.getDataTime().trim());
                }

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");

                if (recyclerItem.getCustName() != null
                        && !"".equals(recyclerItem.getCustName())
                        && recyclerItem.getCustName().length() > 0) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "\n" + "Customer  : "
                            + recyclerItem.getCustName().trim() + "\n");
                }

                if (recyclerItem.getProductCode() != null
                        && recyclerItem.getProductCode().length() > 0
                        && recyclerItem.getAccountNo() != null
                        && recyclerItem.getAccountNo().length() > 0) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Acc. No   : "
                            + recyclerItem.getProductCode() + "/" + recyclerItem.getAccountNo() + "\n");
                }

                if (recyclerItem.getAmount() != null
                        && !"".equals(recyclerItem.getAmount())
                        && recyclerItem.getAmount().length() > 0) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                            byteToRupeeConversion("Amount    : ", recyclerItem.getAmount()));
                }

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");

                if (recyclerItem.getAvblBalance() != null
                        && !"".equals(recyclerItem.getAvblBalance())
                        && recyclerItem.getAvblBalance().length() > 0) {
                    if (recyclerItem.getAvblBalance().contains("-")) {
                        bal_split = recyclerItem.getAvblBalance().substring(1);
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                byteToRupeeConversion("Balance   : ", bal_split));
                    } else {
                        bal_split = recyclerItem.getAvblBalance();
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                byteToRupeeConversion("Balance   : ", bal_split));
                    }
                }

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");

                if (recyclerItem.getOverdue() != null
                        && !"".equals(recyclerItem.getOverdue())
                        && recyclerItem.getOverdue().length() > 0) {
                    if (recyclerItem.getOverdue().contains("-")) {
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                byteToRupeeConversion("Overdue   : ", recyclerItem.getOverdue().substring(1)));
                    } else {
                        ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL,
                                byteToRupeeConversion("Adv Bal   : ", recyclerItem.getOverdue()));
                    }
                }

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "            ");

                if (recyclerItem.getServerRRN() != null
                        && !"".equals(recyclerItem.getServerRRN())
                        && recyclerItem.getServerRRN().length() > 0) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Ref No.   : "
                            + recyclerItem.getServerRRN() + "\n");
                }

                if (recyclerItem.getAgentId() != null
                        && !"".equals(recyclerItem.getAgentId())
                        && recyclerItem.getAgentId().length() > 0) {
                    ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "Agent ID  : "
                            + recyclerItem.getAgentId() + "/" + agentName + "\n");
                }

                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "       Thank you");
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, "------------------------");

                //ptrGen.iPaperFeed();
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
                //printToBack();
                Log.i(TAG, "Print Success");
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
}
