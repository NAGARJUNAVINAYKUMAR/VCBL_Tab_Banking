package com.vcbl.tabbanking.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.ImageAdapter;
import com.vcbl.tabbanking.interactors.DataSyncBO;
import com.vcbl.tabbanking.interactors.VersionUpgradeBO;
import com.vcbl.tabbanking.models.DataSyncModel;
import com.vcbl.tabbanking.models.VersionUpgradeModel;
import com.vcbl.tabbanking.protobuff.DataSyncResp;
import com.vcbl.tabbanking.protobuff.Masters;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.HardwareUtil;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class Services_Fr extends Fragment {

    private static final String TAG = "Services_Fr--> ";
    GridView grid;
    private static String[] module_name = {
            "Aadhaar Seeding",
            "Mobile Seeding",
            "PAN Update",
            "Sync",
            "Version Upgrade"
    };
    private static int[] module_image = {
            R.drawable.ic_aadhar,
            R.drawable.ic_mobile_seeding_192x192,
            R.drawable.ic_pan_card_192x192,
            R.drawable.ic_sync,
            R.drawable.remote_upgrade
    };
    static String calledFrom;
    DataSyncBO dataSyncBO;
    VersionUpgradeBO versionUpgradeBO;
    int versionCode, ftpPort;
    String versionName, ftpPath, fileName, ftpUserID, ftpPassword;
    ProgressDialog progressDialog;
    DialogsUtil dialogsUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        setTitle(R.string.service);

        dialogsUtil = new DialogsUtil(getActivity());

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
                    frag = new AadhaarMobile_Fr();
                    calledFrom = "AADHAAR_SEEDING";
                    ft.replace(R.id.content_frame, frag);
                    setSubTitle(R.string.aadhaar_seeding);
                } else if (position == 1) {
                    frag = new AadhaarMobile_Fr();
                    calledFrom = "MOBILE_SEEDING";
                    ft.replace(R.id.content_frame, frag);
                    setSubTitle(R.string.mobile_seeding);
                } else if (position == 2) {
                    frag = new AadhaarMobile_Fr();
                    calledFrom = "PAN_UPDATE";
                    ft.replace(R.id.content_frame, frag);
                    setSubTitle(R.string.pan_update);
                } else if (position == 3) {
                    calledFrom = "SYNC";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.sync);
                    builder.setIcon(R.drawable.app_logo);
                    builder.setMessage("After finishing the sync process,\napplication will be logged out...");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            prepareModel();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (position == 4) {
                    calledFrom = "VERSION_UPGRADE";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.version_upgrade);
                    builder.setIcon(R.drawable.app_logo);
                    builder.setMessage("Download will take several minutes\nwould you like to continue ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            versionUpgrade();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }

    private void versionUpgrade() {
        VersionUpgradeModel upgradeModel = new VersionUpgradeModel();
        upgradeModel.setAppVersion(versionName);
        upgradeModel.setMicroAtmID(HardwareUtil.getMacId());
        upgradeModel.setLastUpdateDate(Utility.getCurrentTimeStamp());
        versionUpgradeBO = new VersionUpgradeBO(getActivity());
        versionUpgradeBO.versionUpgradeRequest(upgradeModel);
        versionUpgradeBO.setOnTaskFinishedEvent(new VersionUpgradeBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(VersionUpgradeModel upgradeResp) {

                if (upgradeResp.getFtpPath() != null
                        && !"".equals(upgradeResp.getFtpPath())
                        && upgradeResp.getFtpPath().length() > 0) {
                    ftpPath = upgradeResp.getFtpPath();
                    Log.i(TAG, "ftpPath-->" + ftpPath);
                }

                if (upgradeResp.getFileName() != null
                        && !"".equals(upgradeResp.getFileName())
                        && upgradeResp.getFileName().length() > 0) {
                    fileName = upgradeResp.getFileName();
                    Log.i(TAG, "fileName-->" + fileName);
                }

                if (upgradeResp.getFtpUserID() != null
                        && !"".equals(upgradeResp.getFtpUserID())
                        && upgradeResp.getFtpUserID().length() > 0) {
                    ftpUserID = upgradeResp.getFtpUserID();
                    Log.i(TAG, "ftpUserID-->" + ftpUserID);
                }

                if (upgradeResp.getFtpPassword() != null
                        && !"".equals(upgradeResp.getFtpPassword())
                        && upgradeResp.getFtpPassword().length() > 0) {
                    ftpPassword = upgradeResp.getFtpPassword();
                    Log.i(TAG, "ftpPassword-->" + ftpPassword);
                }

                if (upgradeResp.getFtpPort() > 0) {
                    ftpPort = upgradeResp.getFtpPort();
                    Log.i(TAG, "getFtpPort-->" + String.valueOf(ftpPort));
                }

                Log.i(TAG, "versionCode-->" + versionCode);
                Log.i(TAG, "versionName-->" + versionName);
                new VersionUpgrade().execute(ftpPath + "/" + fileName);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void prepareModel() {
        DataSyncModel syncModel = new DataSyncModel();
        syncModel.setMicroAtmId(HardwareUtil.getMacId());
        syncModel.setTimeStamp(Utility.getCurrentTimeStamp());
        syncModel.setAppVersion(Constants.APP_VERSION);
        dataSyncBO = new DataSyncBO(getActivity());
        dataSyncBO.dataSyncRequest(syncModel);
        dataSyncBO.setOnTaskFinishedEvent(new DataSyncBO.OnTaskExecutionFinished() {
            @Override
            public void onTaskFinished(DataSyncResp.DataSyncResponse syncResponse) {
                if (syncResponse.getStatus() == Masters.Status.SUCCESS) {
                    Storage storage = new Storage(getActivity());
                    storage.saveSecure(Constants.SYNC_PROCESS, "Y");
                    Toasty.success(getActivity(), "Masters Data Synced!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    dialogsUtil.alertDialog("Data sync failed !");
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class VersionUpgrade extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIcon(R.drawable.app_logo);
            progressDialog.setTitle(R.string.downloading_file);
            progressDialog.setMessage("Please wait\nFile is downloading");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                String PATH = "/mnt/sdcard/Download/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "tabbanking.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                downloadAndSaveFile(ftpPath, ftpPort, ftpUserID, ftpPassword, fileName, outputFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/tabbanking.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                getActivity().startActivity(intent);
            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean downloadAndSaveFile(String server, int portNumber,
                                        String user, String password, String filename, File localFile)
            throws IOException {
        FTPClient ftp = null;

        try {
            ftp = new FTPClient();
            try {
                ftp.connect(server, portNumber);
                // ftp.setSSLSocketFactory(s.);
                ftp.login(user, password);
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
            }

            OutputStream outputStream = null;
            boolean success = false;
            try {
                //outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                try {
                    //ftp.setSSLSocketFactory(s);
                    ftp.download(filename, localFile);
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                } catch (FTPDataTransferException e) {
                    e.printStackTrace();
                } catch (FTPAbortedException e) {
                    e.printStackTrace();
                }
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            return success;
        } finally {
            if (ftp != null) {
                try {
                    ftp.logout();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                }
                try {
                    ftp.disconnect(true);
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(resource);
    }

    public void setSubTitle(int resource) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(resource);
    }
}
