package com.vcbl.tabbanking.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import java.util.List;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.database.DbFunctions;
import com.vcbl.tabbanking.fragments.DetailedTxnReports_Fr;
import com.vcbl.tabbanking.modelclasses.RecyclerItem;
import com.vcbl.tabbanking.models.GlobalModel;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<RecyclerItem> recyclerItems;
    private Context context;
    private static int currentPosition = 0;

    public RecyclerAdapter(List<RecyclerItem> recyclerItems, Context context) {
        this.recyclerItems = recyclerItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        RecyclerItem recyclerItem = recyclerItems.get(position);
        DbFunctions dbFunctions = DbFunctions.getInstance(context);
        String branchname = dbFunctions.getBranchName(String.valueOf(GlobalModel.branchid));
        String agentName = dbFunctions.getNameByAgentID(String.valueOf(GlobalModel.bcid));

        if (recyclerItem.getServerRRN() != null) {
            holder.tv_sl_no.setText(recyclerItem.getServerRRN());
            holder.dlg_ref_no.setText(recyclerItem.getServerRRN());
        } else {
            holder.tv_sl_no.setText("");
            holder.dlg_ref_no.setText("");
        }

        if (recyclerItem.getAccountNo() != null) {
            holder.tv_productCode_accNo.setText(recyclerItem.getProductCode() + "/" + recyclerItem.getAccountNo());
            holder.dlg_account_no.setText(recyclerItem.getProductCode() + "/" + recyclerItem.getAccountNo());
        } else {
            holder.tv_productCode_accNo.setText("");
            holder.dlg_account_no.setText("");
        }

        if (recyclerItem.getAmount() != null) {
            holder.tv_amount.setText(context.getResources().getString(R.string.Rs)  + recyclerItem.getAmount());
            holder.dlg_amount.setText(context.getResources().getString(R.string.Rs) + recyclerItem.getAmount());
        } else {
            holder.tv_amount.setText("");
            holder.dlg_amount.setText("");
        }

        // below details
        if (branchname != null || GlobalModel.branchid > 0) {
            holder.tv_branch_id_name.setText(String.valueOf(GlobalModel.branchid) + "/" + branchname);
        } else {
            holder.tv_branch_id_name.setText("");
        }

        if (recyclerItem.getDataTime() != null) {
            holder.dlg_datetime.setText(recyclerItem.getDataTime());
        } else {
            holder.dlg_datetime.setText("");
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

        if (recyclerItem.getCustName() != null
                && !"".equals(recyclerItem.getCustName())
                && recyclerItem.getCustName().length() > 0) {
            holder.dlg_customer_name.setText(recyclerItem.getCustName());
        } else {
            holder.ll_customer_name.setVisibility(View.GONE);
            holder.dlg_customer_name.setText("");
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
        }

        if ("DTR".equals(DetailedTxnReports_Fr.reportsFlag)) {
            if (recyclerItem.getCustno() != null && recyclerItem.getCustno() != null) {
                holder.dlg_agent_id.setText(recyclerItem.getAgentId() + "/" + recyclerItem.getCustno());
            } else {
                holder.dlg_agent_id.setText("");
            }
        } else {
            if (recyclerItem.getAgentId() != null || agentName != null) {
                holder.dlg_agent_id.setText(recyclerItem.getAgentId() + "/" + agentName);
            } else {
                holder.dlg_agent_id.setText("");
            }
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
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_expand_collapse, layout_animation, ll_overdue, ll_customer_name;
        AppCompatTextView tv_sl_no, tv_productCode_accNo, tv_amount, tv_branch_id_name,
                dlg_datetime, dlg_customer_name, dlg_account_no, tv_balance, tv_overdue_label, tv_overdue,
                dlg_amount, dlg_ref_no, dlg_agent_id;

        ViewHolder(View itemView) {
            super(itemView);
            tv_sl_no = itemView.findViewById(R.id.tv_sl_no);
            tv_productCode_accNo = itemView.findViewById(R.id.tv_productCode_accNo);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_branch_id_name = itemView.findViewById(R.id.tv_branch_id_name);

            layout_animation = itemView.findViewById(R.id.layout_animation);
            ll_expand_collapse = itemView.findViewById(R.id.ll_expand_collapse);

            dlg_datetime = itemView.findViewById(R.id.dlg_datetime);
            dlg_customer_name = itemView.findViewById(R.id.dlg_customer_name);
            dlg_account_no = itemView.findViewById(R.id.dlg_account_no);
            tv_balance = itemView.findViewById(R.id.tv_balance);
            ll_overdue = itemView.findViewById(R.id.ll_overdue);
            ll_customer_name = itemView.findViewById(R.id.ll_customer_name);
            tv_overdue_label = itemView.findViewById(R.id.tv_overdue_label);
            tv_overdue = itemView.findViewById(R.id.tv_overdue);
            dlg_amount = itemView.findViewById(R.id.dlg_amount);
            dlg_ref_no = itemView.findViewById(R.id.dlg_ref_no);
            dlg_agent_id = itemView.findViewById(R.id.dlg_agent_id);
        }
    }
}
