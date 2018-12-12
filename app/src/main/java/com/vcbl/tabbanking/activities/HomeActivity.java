package com.vcbl.tabbanking.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.application.GlobalPool;
import com.vcbl.tabbanking.bluetooth.BluetoothComm;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.fragments.AadhaarMobile_Fr;
import com.vcbl.tabbanking.fragments.AadhaarTxns_Fr;
import com.vcbl.tabbanking.fragments.Accounts_Fr;
import com.vcbl.tabbanking.fragments.AddressDetails_Fr;
import com.vcbl.tabbanking.fragments.BCEnrollment_Fr;
import com.vcbl.tabbanking.fragments.DailyTxnDetails_Fr;
import com.vcbl.tabbanking.fragments.DayPrints_Fr;
import com.vcbl.tabbanking.fragments.DetailedTxnReports_Fr;
import com.vcbl.tabbanking.fragments.EMICalculator_Fr;
import com.vcbl.tabbanking.fragments.GuardianDetails_Fr;
import com.vcbl.tabbanking.fragments.Home_Fr;
import com.vcbl.tabbanking.fragments.Introducer_Fr;
import com.vcbl.tabbanking.fragments.LoanSaving_Fr;
import com.vcbl.tabbanking.fragments.SmartCard_Fr;
import com.vcbl.tabbanking.fragments.TxnRupay_Fr;
import com.vcbl.tabbanking.fragments.NomineeDetails_Fr;
import com.vcbl.tabbanking.fragments.OtherDetails_Fr;
import com.vcbl.tabbanking.fragments.NewCustomer_Fr;
import com.vcbl.tabbanking.fragments.PhotoUpload_Fr;
import com.vcbl.tabbanking.fragments.PrintPreview_Fr;
import com.vcbl.tabbanking.fragments.Reports_Fr;
import com.vcbl.tabbanking.fragments.RupayCard_Fr;
import com.vcbl.tabbanking.fragments.Services_Fr;
import com.vcbl.tabbanking.fragments.Signature_Fr;
import com.vcbl.tabbanking.fragments.Staff_Fr;
import com.vcbl.tabbanking.fragments.Transaction;
import com.vcbl.tabbanking.fragments.TxnAadhar_Fr;
import com.vcbl.tabbanking.fragments.TxnCancel_Fr;
import com.vcbl.tabbanking.fragments.TxnSummary_Fr;
import com.vcbl.tabbanking.fragments.Txn_Fr;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.utils.DialogsUtil;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity-->";
    Context context = this;
    Toolbar toolbar;
    Storage storage;
    DialogsUtil dialogsUtil;
    private GlobalPool globalPool;
    int timeout;
    Handler hl_timeout = new Handler();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        //| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Log.i(TAG, "--------Entered---------");

        timeout = 60000 * 15;
        hl_timeout.postDelayed(DoOnTimeOut, timeout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        TextView tv_nav_header_branch_name = view.findViewById(R.id.tv_nav_header_branch_name);
        TextView tv_nav_header_agent_name = view.findViewById(R.id.tv_nav_header_agent_name);
        DbFunctions dbFunctions = DbFunctions.getInstance(getApplicationContext());
        tv_nav_header_branch_name.setText(dbFunctions.getBranchName(String.valueOf(GlobalModel.branchid)));
        tv_nav_header_agent_name.setText(dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid)));
        displaySelectedScreen(R.id.nav_home);

        objInit();
    }

    private void objInit() {
        storage = new Storage(getApplicationContext());
        globalPool = ((GlobalPool) getApplicationContext());
        dialogsUtil = new DialogsUtil(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_check_bt_conn) {
            if ("YES".equals(storage.getValue(Constants.BT_SERVICE))) {
                try {
                    //if (globalPool.connection) {
                        globalPool.closeConn();
                    //}
                    BluetoothComm.misIn = null;
                    BluetoothComm.mosOut = null;
                    new connSocketTask().execute(storage.getValue(Constants.BT_ADDRESS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dialogsUtil.alertDialog("Printer services required");
            }
            return true;
        } else if (id == R.id.action_configure_bt) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.app_logo);
            builder.setMessage(R.string.configure_bt);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    //if (globalPool.connection) {
                        globalPool.closeConn();
                    //}
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else if (id == R.id.action_select_bt_device) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.app_logo);
            builder.setMessage("Select/Change bluetooth device");
            builder.setCancelable(false);
            builder.setPositiveButton("Leopard", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    mainOrLogin("LEOPARD");
                }
            });
            builder.setNegativeButton("Other", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mainOrLogin("OTHER");
                }
            });
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        //calling the method displaySelectedScreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.nav_home:
                fragment = new Home_Fr();
                toolbar.setTitle(R.string.app_name);
                break;
            case R.id.nav_transaction:
                fragment = new Transaction();
                toolbar.setTitle(R.string.transaction);
                break;
            case R.id.nav_accounts:
                fragment = new Accounts_Fr();
                toolbar.setTitle(R.string.accounts);
                break;
            case R.id.nav_services:
                fragment = new Services_Fr();
                toolbar.setTitle(R.string.service);
                break;
            case R.id.nav_reports:
                fragment = new Reports_Fr();
                toolbar.setTitle(R.string.reports);
                break;
            case R.id.nav_emi_calculator:
                fragment = new EMICalculator_Fr();
                toolbar.setTitle(R.string.emi_calculator);
                break;
            case R.id.nav_reprint:
                fragment = new DayPrints_Fr();
                toolbar.setTitle(R.string.reprint);
                break;
            case R.id.nav_logout:
                dlgExit();
                break;
        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } /*else {
            super.onBackPressed();
        }*/
        Fragment fragmentFrame = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        // transactions, card-1
        if (fragmentFrame instanceof Transaction) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Home_Fr()).commit();
        } else if (fragmentFrame instanceof Txn_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Transaction()).commit();
//        } else if (fragmentFrame instanceof Txn_Fr) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new DirectDeposits_Fr()).commit();
        } else if (fragmentFrame instanceof LoanSaving_Fr) {
            if ("4".equals(LoanSaving_Fr.loanSaving)
                    || "5".equals(LoanSaving_Fr.loanSaving)
                    || "7".equals(LoanSaving_Fr.loanSaving)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AadhaarMobile_Fr()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Txn_Fr()).commit();
            }
        } else if (fragmentFrame instanceof PrintPreview_Fr) {
            if ("AADHAAR_PREVIEW_DONE".equals(PrintPreview_Fr.calledFrom)
                    || "MOBILE_PREVIEW_DONE".equals(PrintPreview_Fr.calledFrom)
                    || "PAN_PREVIEW_DONE".equals(PrintPreview_Fr.calledFrom)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AadhaarMobile_Fr()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Txn_Fr()).commit();
            }
        } else if (fragmentFrame instanceof TxnCancel_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Transaction()).commit();

        } else if (fragmentFrame instanceof AadhaarTxns_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Transaction()).commit();
        } else if (fragmentFrame instanceof TxnAadhar_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AadhaarTxns_Fr()).commit();

        } else if (fragmentFrame instanceof RupayCard_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Transaction()).commit();
        } else if (fragmentFrame instanceof TxnRupay_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RupayCard_Fr()).commit();

        } else if (fragmentFrame instanceof SmartCard_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Transaction()).commit();

        // accounts, card-2
        } else if (fragmentFrame instanceof Accounts_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Home_Fr()).commit();
        } else if (fragmentFrame instanceof Staff_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Accounts_Fr()).commit();
        } else if (fragmentFrame instanceof BCEnrollment_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Staff_Fr()).commit();
        } else if (fragmentFrame instanceof NewCustomer_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Accounts_Fr()).commit();
        } else if (fragmentFrame instanceof AddressDetails_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new NewCustomer_Fr()).commit();
        } else if (fragmentFrame instanceof GuardianDetails_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddressDetails_Fr()).commit();
        } else if (fragmentFrame instanceof OtherDetails_Fr) {
            if ((Integer.parseInt(storage.getValue(Constants.CUSTOMER_AGE))) < 18) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GuardianDetails_Fr()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddressDetails_Fr()).commit();
            }
        } else if (fragmentFrame instanceof NomineeDetails_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new OtherDetails_Fr()).commit();
        } else if (fragmentFrame instanceof PhotoUpload_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new NomineeDetails_Fr()).commit();
        } else if (fragmentFrame instanceof Signature_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PhotoUpload_Fr()).commit();
        } else if (fragmentFrame instanceof Introducer_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Signature_Fr()).commit();

        // services, card-3
        } else if (fragmentFrame instanceof Services_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Home_Fr()).commit();
        } else if (fragmentFrame instanceof AadhaarMobile_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Services_Fr()).commit();

        // reports, card-4
        } else if (fragmentFrame instanceof Reports_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Home_Fr()).commit();
        } else if (fragmentFrame instanceof TxnSummary_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Reports_Fr()).commit();
        } else if (fragmentFrame instanceof DailyTxnDetails_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Reports_Fr()).commit();
        } else if (fragmentFrame instanceof DetailedTxnReports_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Reports_Fr()).commit();

        // emi_calculator, card-5
        } else if (fragmentFrame instanceof EMICalculator_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Home_Fr()).commit();

        // reprint, card-6
        } else if (fragmentFrame instanceof DayPrints_Fr) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Home_Fr()).commit();

        // default, logout
        } else if (fragmentFrame instanceof Home_Fr) {
            dlgExit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //Remove any previous callback
        try {
            hl_timeout.removeCallbacks(DoOnTimeOut);
            hl_timeout.postDelayed(DoOnTimeOut, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            hl_timeout.removeCallbacks(DoOnTimeOut);
            hl_timeout.postDelayed(DoOnTimeOut, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mainOrLogin(String deviceType) {
        storage.saveSecure(Constants.DEVICE_TYPE, deviceType);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void dlgExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.app_logo);
        builder.setMessage("Do you want to logout ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    Thread DoOnTimeOut = new Thread() {
        public void run() {
            try {
                if (!((Activity) context).isFinishing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.app_logo);
                    builder.setMessage("Session timeout, Please login again !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //DoOnTimeOut.stop();
                            dialog.cancel();
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    private class connSocketTask extends AsyncTask<String, String, Integer> {

        private static final int CONN_FAIL = 0x01;
        private static final int CONN_SUCCESS = 0x02;
        //private static final int miWATI_TIME = 15;
        //private static final int miSLEEP_TIME = 150;

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle(R.string.bluetooth);
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setMessage(getString(R.string.actMain_msg_device_connecting));
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            if (globalPool.createConn(arg0[0])) {
                return CONN_SUCCESS;
            } else {
                return CONN_FAIL;
            }
        }

        @Override
        public void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (CONN_SUCCESS == result) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toasty.success(getApplicationContext(), getString(R.string.actMain_msg_device_connect_succes), Toast.LENGTH_SHORT).show();
            } else {
                Toasty.error(getApplicationContext(), "Connection failed !", Toast.LENGTH_SHORT, true).show();
            }
        }
    }
}
