package com.example.a2374820062_leanhhieu_30.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2374820062_leanhhieu_30.R;
import com.example.a2374820062_leanhhieu_30.model.ConversionHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<ConversionHistory> historyList;

    public HistoryAdapter(List<ConversionHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversionHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<ConversionHistory> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAmount, tvConversion, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvConversion = itemView.findViewById(R.id.tvConversion);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(ConversionHistory history) {
            String amountText = String.format(Locale.getDefault(), "%,.2f %s",
                    history.getAmount(), history.getFromCurrency());
            String conversionText = String.format(Locale.getDefault(), "→ %,.2f %s",
                    history.getConvertedAmount(), history.getToCurrency());

            tvAmount.setText(amountText);
            tvConversion.setText(conversionText);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateString = sdf.format(history.getTimestamp());
            tvDate.setText(dateString);
        }
    }
}