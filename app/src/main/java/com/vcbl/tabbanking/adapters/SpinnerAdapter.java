package com.vcbl.tabbanking.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.models.SpinnerList;

import java.util.ArrayList;

/**
 * Created by ecosoft2 on 02-Mar-18.
 */

public class SpinnerAdapter extends ArrayAdapter<SpinnerList> {

    private Context mContext;
    private ArrayList<SpinnerList> listState;

    public SpinnerAdapter(Context context, int resource, ArrayList<SpinnerList> spinnerLists) {
        super(context, resource, spinnerLists);
        this.mContext = context;
        this.listState = spinnerLists;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @SuppressLint("InflateParams")
    private View getCustomView(final int position, View convertView,
                               ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.tv_spinner);
            holder.mCheckBox = convertView.findViewById(R.id.cb_spinner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getName());

        // To check weather checked event fire from getview() or user input
        holder.mCheckBox.setChecked(listState.get(position).isSelected());

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private AppCompatTextView mTextView;
        private AppCompatCheckBox mCheckBox;
    }
}
