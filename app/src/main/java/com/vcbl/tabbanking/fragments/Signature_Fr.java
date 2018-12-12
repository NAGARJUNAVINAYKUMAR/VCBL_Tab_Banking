package com.vcbl.tabbanking.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.kyanogen.signatureview.SignatureView;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.utils.DialogsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Signature_Fr extends Fragment {

    private static final String TAG = "Signature_Fr-->";
    View view;
    CardView cardView;
    private SignatureView signatureView;
    AppCompatButton btn_clear_details, btn_next;
    DialogsUtil dialogsUtil;
    String signData;
    byte[] signByteArray;
    private String signatureFlag = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signature, container, false);

        dialogsUtil = new DialogsUtil(getActivity());

        cardView = view.findViewById(R.id.cardView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        signatureView = view.findViewById(R.id.signature_view);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_next = view.findViewById(R.id.btn_next);

        int colorPrimary = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        signatureView.setPenColor(colorPrimary);

        btn_clear_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(signatureFlag)) {
                    dialogsUtil.alertDialog("Please draw your signature");
                } else if (!saveSignature()) {
                    Toasty.warning(getActivity(), "Unable to save the image", Toast.LENGTH_SHORT).show();
                    signatureFlag = "0";
                } else {
                    Fragment fragment = new Introducer_Fr();
                    assert getFragmentManager() != null;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        return view;
    }

    private boolean saveSignature() {
        boolean saved = false;
        File directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(directory, System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        Bitmap bitmap = signatureView.getSignatureBitmap();
        try {
            out = new FileOutputStream(file);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                EnrollmentData.getPersonalInfo().setSignData(bitmapToString(bitmap));
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                    if (bitmap != null) {
                        saved = true;

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        signByteArray = stream.toByteArray();
                        Log.i(TAG, "signByteArray--> " + signByteArray.toString());

                        signatureFlag = "1";

                        Toasty.success(getActivity(), "Signature saved successfully", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            new MyMediaScanner(getActivity(), file);
                        } else {
                            ArrayList<String> toBeScanned = new ArrayList<>();
                            toBeScanned.add(file.getAbsolutePath());
                            String[] toBeScannedStr = new String[toBeScanned.size()];
                            toBeScannedStr = toBeScanned.toArray(toBeScannedStr);
                            MediaScannerConnection.scanFile(getActivity(), toBeScannedStr, null, null);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }

    private class MyMediaScanner implements
            MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mSC;
        private File file;

        MyMediaScanner(Context context, File file) {
            this.file = file;
            mSC = new MediaScannerConnection(context, this);
            mSC.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mSC.scanFile(file.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mSC.disconnect();
        }
    }

    public byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }

    public Bitmap getBitmap(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        signData = Base64.encodeToString(b, Base64.DEFAULT);
        return signData;
    }
}
