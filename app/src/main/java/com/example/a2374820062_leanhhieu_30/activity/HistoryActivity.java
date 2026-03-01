package com.example.a2374820062_leanhhieu_30.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2374820062_leanhhieu_30.AppDatabase;
import com.example.a2374820062_leanhhieu_30.R;
import com.example.a2374820062_leanhhieu_30.adapter.HistoryAdapter;
import com.example.a2374820062_leanhhieu_30.model.ConversionHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private AppDatabase database;
    private List<ConversionHistory> historyList;
    private TextView tvEmptyHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_history);

        initViews();
        setupDatabase();
        setupRecyclerView();
        loadHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        Button btnClear = findViewById(R.id.btnClearHistory);
        Button btnBack = findViewById(R.id.btnBack);

        btnClear.setOnClickListener(v -> clearHistory());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadHistory() {
        new Thread(() -> {
            List<ConversionHistory> histories = database.historyDao().getAll();
            runOnUiThread(() -> {
                historyList.clear();
                historyList.addAll(histories);
                adapter.notifyDataSetChanged();

                if (historyList.isEmpty()) {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyHistory.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    private void clearHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử giao dịch?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(() -> {
                            database.historyDao().deleteAll();
                            runOnUiThread(() -> {
                                loadHistory();
                                new AlertDialog.Builder(HistoryActivity.this)
                                        .setMessage("Đã xóa toàn bộ lịch sử giao dịch")
                                        .setPositiveButton("OK", null)
                                        .show();
                            });
                        }).start();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }
}