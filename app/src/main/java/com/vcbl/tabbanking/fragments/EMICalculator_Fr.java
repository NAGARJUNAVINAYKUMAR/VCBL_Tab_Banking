package com.vcbl.tabbanking.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.vcbl.tabbanking.R;

public class EMICalculator_Fr extends Fragment {

    View view;
    CardView cardView;
    LinearLayout ll_calc_monthly_emi, ll_tot_amount_with_interest, ll_flat_interest_rate_pa, ll_flat_interest_rate_pm,
            ll_tot_interest_amount, ll_yearly_interest_amount;
    AppCompatButton btn_clear_details, btn_calculate;
    AppCompatEditText et_loan_amount, et_loan_tenure, et_loan_interest_rate, et_monthly_emi,
            et_total_amt_plus_interest, et_total_interest_amt, et_yearly_interest_amt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.emi_calc, container, false);

        loadUiComponents();

        clearFields();

        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st1 = et_loan_amount.getText().toString();
                String st2 = et_loan_interest_rate.getText().toString();
                String st3 = et_loan_tenure.getText().toString();

                if (TextUtils.isEmpty(st1)) {
                    et_loan_amount.setError("Enter Principle Amount");
                    et_loan_amount.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(st2)) {
                    et_loan_interest_rate.setError("Enter Interest Rate");
                    et_loan_interest_rate.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(st3)) {
                    et_loan_tenure.setError("Enter Years");
                    et_loan_tenure.requestFocus();
                    return;
                }

                float p = Float.parseFloat(st1);
                float i = Float.parseFloat(st2);
                float y = Float.parseFloat(st3);

                float Principal = calPric(p);

                float Rate = calInt(i);

                float Months = calMonth(y);

                float Dvdnt = calDvdnt(Rate, Months);

                float FD = calFinalDvdnt(Principal, Rate, Dvdnt);

                float D = calDivider(Dvdnt);

                float emi = calEmi(FD, D);

                float TA = calTa(emi, Months);

                et_total_amt_plus_interest.setText(String.valueOf(TA));

                float ti = calTotalInt(TA, Principal);

                float yi = calYearlyInt(ti, Months);
                et_total_interest_amt.setText(String.valueOf(ti));
                et_monthly_emi.setText(String.valueOf(emi));
                et_yearly_interest_amt.setText(String.valueOf(yi));
            }
        });

        btn_clear_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        return view;
    }

    private void clearFields() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_enter);
        cardView.setAnimation(animation);

        et_loan_amount.setText("");
        et_loan_amount.setHint("Enter Principle Amount");
        et_loan_amount.requestFocus();

        et_loan_interest_rate.setText("");
        et_loan_interest_rate.setHint("Enter Interest Rate");

        et_loan_tenure.setText("");
        et_loan_tenure.setHint("Enter Years");

        //ll_flat_interest_rate_pa.setVisibility(View.GONE);
        //ll_flat_interest_rate_pm.setVisibility(View.GONE);

        et_monthly_emi.setText("");
        et_monthly_emi.setHint("8698.84");

        et_total_amt_plus_interest.setText("");
        et_total_amt_plus_interest.setHint("104386.08");


        et_total_interest_amt.setText("");
        et_total_interest_amt.setHint("4386.08");

        et_yearly_interest_amt.setText("");
        et_yearly_interest_amt.setHint("4386.08");
    }

    private void loadUiComponents() {
        cardView = view.findViewById(R.id.cardView);

        et_loan_amount = view.findViewById(R.id.et_loan_amount);
        et_loan_tenure = view.findViewById(R.id.et_loan_tenure);
        et_loan_interest_rate = view.findViewById(R.id.et_loan_interest_rate);

        ll_calc_monthly_emi = view.findViewById(R.id.ll_calc_monthly_emi);
        ll_tot_amount_with_interest = view.findViewById(R.id.ll_tot_amount_with_interest);
        ll_flat_interest_rate_pa = view.findViewById(R.id.ll_flat_interest_rate_pa);
        ll_flat_interest_rate_pm = view.findViewById(R.id.ll_flat_interest_rate_pm);
        ll_tot_interest_amount = view.findViewById(R.id.ll_tot_interest_amount);
        ll_yearly_interest_amount = view.findViewById(R.id.ll_yearly_interest_amount);

        et_monthly_emi = view.findViewById(R.id.et_monthly_emi);
        et_total_amt_plus_interest = view.findViewById(R.id.et_total_amt_plus_interest);
        et_total_interest_amt = view.findViewById(R.id.et_total_interest_amt);
        et_yearly_interest_amt = view.findViewById(R.id.et_yearly_interest_amt);

        btn_clear_details = view.findViewById(R.id.btn_clear_details);
        btn_calculate = view.findViewById(R.id.btn_calculate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public float calPric(float p) {
        return (float) (p);
    }

    public float calInt(float i) {
        return (float) (i / 12 / 100);
    }

    public float calMonth(float y) {
        return (float) (y);
    }

    public float calDvdnt(float Rate, float Months) {
        return (float) (Math.pow(1 + Rate, Months));

    }

    public float calFinalDvdnt(float Principal, float Rate, float Dvdnt) {
        return (float) (Principal * Rate * Dvdnt);
    }

    public float calDivider(float Dvdnt) {
        return (float) (Dvdnt - 1);
    }

    public float calEmi(float FD, Float D) {
        return (float) (FD / D);
    }

    public float calTa(float emi, Float Months) {
        return (float) (emi * Months);
    }

    public float calTotalInt(float TA, float Principal) {
        return (float) (TA - Principal);
    }

    public float calYearlyInt(float ti, float months) {
        return (float) (ti * 12) / months;
    }
}
