package com.vcbl.tabbanking.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;

import java.util.List;

public class ReprintAdapter extends RecyclerView.Adapter<ReprintAdapter.ViewHolder> {

    private List<RecyclerItem> recyclerItems;
    private Context context;
    private RecyclerItem recyclerItem;
    private static int currentPosition = 0;

    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(RecyclerItem item, int position);
    }

    public ReprintAdapter(List<RecyclerItem> recyclerItems, OnItemClickListener onItemClickListener, Context context1) {
        this.recyclerItems = recyclerItems;
        this.onItemClickListener = onItemClickListener;
        this.context = context1;
    }

    @NonNull
    @Override
    public ReprintAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reprint_item, parent, false);
        return new ReprintAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReprintAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        recyclerItem = recyclerItems.get(position);
        DbFunctions dbFunctions = DbFunctions.getInstance(context);
        String branchname = dbFunctions.getBranchName(String.valueOf(GlobalModel.branchid));
        String agentName = dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid));

        if (recyclerItem.getServerRRN() != null) {
            holder.tv_header_server_rrn.setText(recyclerItem.getServerRRN());
            holder.tv_ref_no.setText(recyclerItem.getServerRRN());
        } else {
            holder.tv_header_server_rrn.setText("");
            holder.tv_ref_no.setText("");
            holder.ll_ref_no.setVisibility(View.GONE);
        }

        if (recyclerItem.getAccountNo() != null) {
            holder.tv_header_account_no.setText(recyclerItem.getProductCode() + "/" + recyclerItem.getAccountNo());
            holder.tv_account_no.setText(recyclerItem.getProductCode() + "/" + recyclerItem.getAccountNo());
        } else {
            holder.tv_header_account_no.setText("");
            holder.tv_account_no.setText("");
            holder.ll_account_no.setVisibility(View.GONE);
        }

        if (recyclerItem.getAmount() != null) {
            holder.tv_header_amount.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getAmount());
            holder.tv_amount.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getAmount());
        } else {
            holder.tv_header_amount.setText("");
            holder.tv_amount.setText("");
            holder.ll_amount.setVisibility(View.GONE);
        }

        if (branchname != null || GlobalModel.branchid > 0) {
            holder.tv_branch_id_name.setText(String.valueOf(GlobalModel.branchid) + "/" + branchname);
        } else {
            holder.tv_branch_id_name.setText("");
            holder.ll_branch_id_name.setVisibility(View.GONE);
        }

        if (recyclerItem.getDataTime() != null) {
            holder.tv_date_time.setText(recyclerItem.getDataTime());
        } else {
            holder.tv_date_time.setText("");
            holder.ll_date_time.setVisibility(View.GONE);
        }

        if (recyclerItem.getCustName() != null
                && !"".equals(recyclerItem.getCustName())
                && recyclerItem.getCustName().length() > 0) {
            holder.tv_customer_name.setText(recyclerItem.getCustName());
        } else {
            holder.tv_customer_name.setText("");
            holder.ll_customer_name.setVisibility(View.GONE);
        }

        if (recyclerItem.getAvblBalance() != null
                && !"".equals(recyclerItem.getAvblBalance())) {
            if (recyclerItem.getAvblBalance().contains("-")) {
                holder.tv_balance.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getAvblBalance().substring(1));
            } else {
                holder.tv_balance.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getAvblBalance());
            }
        } else {
            holder.tv_balance.setText("");
            holder.ll_balance.setVisibility(View.GONE);
        }

        if (recyclerItem.getOverdue() != null
                && !"".equals(recyclerItem.getOverdue())
                && !"0".equals(recyclerItem.getOverdue())) {
            if (recyclerItem.getOverdue().contains("-")) {
                holder.tv_overdue.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getOverdue().substring(1));
            } else {
                holder.tv_overdue_label.setText(context.getResources().getString(R.string.advanced_balance));
                holder.tv_overdue.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getOverdue());
            }
        } else {
            holder.tv_overdue.setText("");
            holder.ll_overdue.setVisibility(View.GONE);
        }

        if (recyclerItem.getCustno() != null && recyclerItem.getCustno() != null) {
            holder.tv_agent_name_id.setText(recyclerItem.getAgentId() + "/" + recyclerItem.getCustno());
        } else {
            holder.tv_agent_name_id.setText("");
            holder.ll_agent_name_id.setVisibility(View.GONE);
        }

        holder.ll_expand_collapse.setVisibility(View.GONE);
        if (currentPosition == position) {
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
            holder.ll_expand_collapse.setVisibility(View.VISIBLE);
            holder.ll_expand_collapse.startAnimation(slideDown);
        }

        holder.layout_animation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = position;
                notifyDataSetChanged();
            }
        });

        holder.iv_header_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(recyclerItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout_animation, ll_expand_collapse, ll_branch_id_name, ll_account_no, ll_date_time, ll_customer_name,
                ll_balance, ll_overdue, ll_amount, ll_ref_no, ll_agent_name_id;
        AppCompatImageView iv_header_print;
        AppCompatTextView tv_header_server_rrn, tv_header_account_no, tv_header_amount, tv_branch_id_name,
                tv_account_no, tv_date_time, tv_customer_name, tv_balance, tv_overdue_label, tv_overdue, tv_amount,
                tv_ref_no, tv_agent_name_id;

        ViewHolder(View itemView) {
            super(itemView);
            layout_animation = itemView.findViewById(R.id.layout_animation);
            ll_expand_collapse = itemView.findViewById(R.id.ll_expand_collapse);

            ll_branch_id_name = itemView.findViewById(R.id.ll_branch_id_name);
            ll_account_no = itemView.findViewById(R.id.ll_account_no);
            ll_date_time = itemView.findViewById(R.id.ll_date_time);
            ll_customer_name = itemView.findViewById(R.id.ll_customer_name);
            ll_balance = itemView.findViewById(R.id.ll_balance);
            ll_overdue = itemView.findViewById(R.id.ll_overdue);
            ll_amount = itemView.findViewById(R.id.ll_amount);
            ll_ref_no = itemView.findViewById(R.id.ll_ref_no);
            ll_agent_name_id = itemView.findViewById(R.id.ll_agent_name_id);

            iv_header_print = itemView.findViewById(R.id.iv_header_print);

            tv_header_server_rrn = itemView.findViewById(R.id.tv_header_server_rrn);
            tv_header_account_no = itemView.findViewById(R.id.tv_header_account_no);
            tv_header_amount = itemView.findViewById(R.id.tv_header_amount);
            tv_branch_id_name = itemView.findViewById(R.id.tv_branch_id_name);
            tv_account_no = itemView.findViewById(R.id.tv_account_no);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);
            tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            tv_balance = itemView.findViewById(R.id.tv_balance);
            tv_overdue_label = itemView.findViewById(R.id.tv_overdue_label);
            tv_overdue = itemView.findViewById(R.id.tv_overdue);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_ref_no = itemView.findViewById(R.id.tv_ref_no);
            tv_agent_name_id = itemView.findViewById(R.id.tv_agent_name_id);
        }
    }
}
