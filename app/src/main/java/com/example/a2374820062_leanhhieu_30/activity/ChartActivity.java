package com.example.a2374820062_leanhhieu_30.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a2374820062_leanhhieu_30.AppDatabase;
import com.example.a2374820062_leanhhieu_30.R;
import com.example.a2374820062_leanhhieu_30.model.Currency;
import com.example.a2374820062_leanhhieu_30.model.ExchangeRateHistory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    private LineChart chart;
    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private Button btnShowChart, btnBack;
    private TextView tvChartTitle;
    private AppDatabase database;
    private List<Currency> currencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_chart);

        initViews();
        setupCurrencyList();
        setupSpinners();
        setupDatabase();
        setupChart();
        setupEventListeners();

        // Tự động load biểu đồ khi mở activity
        loadChartData();
    }

    private void initViews() {
        chart = findViewById(R.id.chart);
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrencyChart);
        spinnerToCurrency = findViewById(R.id.spinnerToCurrencyChart);
        btnShowChart = findViewById(R.id.btnShowChart);
        btnBack = findViewById(R.id.btnBackChart);
        tvChartTitle = findViewById(R.id.tvChartTitle);
    }

    private void setupCurrencyList() {
        currencyList = new ArrayList<>();
        currencyList.add(new Currency("USD", "Đô la Mỹ"));
        currencyList.add(new Currency("EUR", "Euro"));
        currencyList.add(new Currency("VND", "Đồng Việt Nam"));
        currencyList.add(new Currency("JPY", "Yên Nhật"));
        currencyList.add(new Currency("GBP", "Bảng Anh"));
        currencyList.add(new Currency("CAD", "Đô la Canada"));
    }

    private void setupSpinners() {
        ArrayAdapter<Currency> adapter = new ArrayAdapter<Currency>(this,
                android.R.layout.simple_spinner_item, currencyList) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#184332"));
                return textView;
            }

            @Override
            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#184332"));
                textView.setPadding(20, 20, 20, 20);
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromCurrency.setAdapter(adapter);
        spinnerToCurrency.setAdapter(adapter);

        // Mặc định chọn USD -> VND
        spinnerFromCurrency.setSelection(0);
        spinnerToCurrency.setSelection(2);
    }

    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void setupChart() {
        // Cấu hình biểu đồ cơ bản
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.WHITE);

        // Cấu hình trục X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#184332"));
        xAxis.setDrawGridLines(false);

        // Cấu hình trục Y
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#184332"));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(0.1f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getLegend().setEnabled(false);
    }

    private void setupEventListeners() {
        btnShowChart.setOnClickListener(v -> loadChartData());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadChartData() {
        Currency fromCurrency = (Currency) spinnerFromCurrency.getSelectedItem();
        Currency toCurrency = (Currency) spinnerToCurrency.getSelectedItem();

        new Thread(() -> {
            try {
                // Lấy dữ liệu 7 ngày gần nhất (dữ liệu mẫu nếu chưa có thật)
                List<ExchangeRateHistory> rates = getSampleData(fromCurrency.getCode(), toCurrency.getCode());

                // Hoặc lấy từ database nếu có
                // List<ExchangeRateHistory> rates = database.exchangeRateHistoryDao()
                //         .getRecentRates(fromCurrency.getCode(), toCurrency.getCode(), 7);

                runOnUiThread(() -> updateChart(rates, fromCurrency, toCurrency));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvChartTitle.setText("Lỗi khi tải dữ liệu biểu đồ");
                    chart.clear();
                });
            }
        }).start();
    }

    // Dữ liệu mẫu để test
    private List<ExchangeRateHistory> getSampleData(String fromCurrency, String toCurrency) {
        List<ExchangeRateHistory> sampleData = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000L;

        // Tỷ giá mẫu cho USD -> VND
        double[] sampleRates = {23000.0, 23100.0, 23050.0, 23200.0, 23150.0, 23300.0, 23250.0};

        for (int i = 6; i >= 0; i--) {
            ExchangeRateHistory history = new ExchangeRateHistory(
                    fromCurrency,
                    toCurrency,
                    sampleRates[i]
            );
            history.setTimestamp(currentTime - (i * oneDay));
            sampleData.add(history);
        }

        return sampleData;
    }

    private void updateChart(List<ExchangeRateHistory> rates, Currency from, Currency to) {
        if (rates == null || rates.isEmpty()) {
            tvChartTitle.setText("Không có dữ liệu cho " + from.getCode() + "/" + to.getCode());
            chart.clear();
            return;
        }

        // Tạo dữ liệu cho biểu đồ
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            ExchangeRateHistory rate = rates.get(i);
            entries.add(new Entry(i, (float) rate.getRate()));
        }

        // Tạo dataset
        LineDataSet dataSet = new LineDataSet(entries, "Tỷ giá " + from.getCode() + "/" + to.getCode());
        dataSet.setColor(Color.parseColor("#2ECC71"));
        dataSet.setCircleColor(Color.parseColor("#27AE60"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#184332"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Cấu hình trục X với ngày tháng
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < rates.size()) {
                    int index = (int) value;
                    if (index < rates.size()) {
                        return mFormat.format(new Date(rates.get(index).getTimestamp()));
                    }
                }
                return "";
            }
        });

        // Đặt dữ liệu vào biểu đồ
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // Refresh biểu đồ

        // Cập nhật tiêu đề
        tvChartTitle.setText("Xu hướng tỷ giá " + from.getCode() + "/" + to.getCode() + " (7 ngày)");
    }
}