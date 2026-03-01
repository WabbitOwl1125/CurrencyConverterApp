package com.example.a2374820062_leanhhieu_30.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.a2374820062_leanhhieu_30.AppDatabase;
import com.example.a2374820062_leanhhieu_30.R;
import com.example.a2374820062_leanhhieu_30.api.ApiClient;
import com.example.a2374820062_leanhhieu_30.api.ExchangeRateAPI;
import com.example.a2374820062_leanhhieu_30.model.ConversionHistory;
import com.example.a2374820062_leanhhieu_30.model.Currency;
import com.example.a2374820062_leanhhieu_30.model.ExchangeRate;
import com.example.a2374820062_leanhhieu_30.model.ExchangeRateHistory;
import com.example.a2374820062_leanhhieu_30.model.FavoriteSetting;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etAmount;
    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private Button btnConvert, btnHistory;
    private CardView cardResult;
    private TextView tvResult, tvConversionRate;
    private Button btnChart, btnFavorites;
    private List<Currency> currencyList;
    private ArrayAdapter<Currency> currencyAdapter;
    private ExchangeRateAPI apiService;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        initViews();
        setupCurrencyList();
        setupSpinners();
        setupApiService();
        setupDatabase();
        setupEventListeners();
        loadDefaultFavorite();
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency);
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency);
        btnConvert = findViewById(R.id.btnConvert);
        btnHistory = findViewById(R.id.btnHistory);
        cardResult = findViewById(R.id.cardResult);
        tvResult = findViewById(R.id.tvResult);
        tvConversionRate = findViewById(R.id.tvConversionRate);
        btnChart = findViewById(R.id.btnChart); // Cần thêm button trong layout
        btnFavorites = findViewById(R.id.btnFavorites); // Cần thêm button trong layout
    }

    private void setupCurrencyList() {
        currencyList = new ArrayList<>();
        currencyList.add(new Currency("USD", "Đô la Mỹ"));
        currencyList.add(new Currency("EUR", "Euro"));
        currencyList.add(new Currency("VND", "Đồng Việt Nam"));
        currencyList.add(new Currency("JPY", "Yên Nhật"));
        currencyList.add(new Currency("GBP", "Bảng Anh"));
        currencyList.add(new Currency("CAD", "Đô la Canada"));
        currencyList.add(new Currency("AUD", "Đô la Úc"));
        currencyList.add(new Currency("CNY", "Nhân dân tệ"));
        currencyList.add(new Currency("KRW", "Won Hàn Quốc"));
        currencyList.add(new Currency("SGD", "Đô la Singapore"));
    }

    private void setupSpinners() {
        currencyAdapter = new ArrayAdapter<Currency>(this,
                android.R.layout.simple_spinner_item, currencyList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.parseColor("#184332"));
                textView.setTextSize(16);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.parseColor("#184332"));
                textView.setTextSize(16);
                textView.setPadding(20, 20, 20, 20);
                return view;
            }
        };

        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromCurrency.setAdapter(currencyAdapter);
        spinnerToCurrency.setAdapter(currencyAdapter);

        spinnerFromCurrency.setSelection(0);
        spinnerToCurrency.setSelection(2);
    }

    private void setupApiService() {
        apiService = ApiClient.getApiService();
    }

    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void setupEventListeners() {
        btnConvert.setOnClickListener(v -> convertCurrency());
        btnHistory.setOnClickListener(v -> showHistory());
        btnChart.setOnClickListener(v -> showChart());
        btnFavorites.setOnClickListener(v -> showFavorites());
    }

    private void convertCurrency() {
        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            showError("Vui lòng nhập số tiền cần chuyển đổi");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showError("Số tiền phải lớn hơn 0");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Số tiền không hợp lệ");
            return;
        }

        Currency fromCurrency = (Currency) spinnerFromCurrency.getSelectedItem();
        Currency toCurrency = (Currency) spinnerToCurrency.getSelectedItem();

        if (fromCurrency.getCode().equals(toCurrency.getCode())) {
            showError("Hai loại tiền tệ phải khác nhau");
            return;
        }

        fetchExchangeRate(fromCurrency.getCode(), toCurrency.getCode(), amount);
    }

    private void fetchExchangeRate(String fromCurrency, String toCurrency, double amount) {
        showLoading();

        Call<ExchangeRate> call = apiService.getLatestRates(fromCurrency);
        call.enqueue(new Callback<ExchangeRate>() {
            @Override
            public void onResponse(Call<ExchangeRate> call, Response<ExchangeRate> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRate exchangeRate = response.body();
                    Map<String, Double> rates = exchangeRate.getRates();

                    if (rates != null && rates.containsKey(toCurrency)) {
                        double rate = rates.get(toCurrency);
                        double convertedAmount = amount * rate;

                        showResult(convertedAmount, rate, toCurrency);
                        saveConversionHistory(amount, fromCurrency, toCurrency, convertedAmount, rate);

                        saveExchangeRateHistory(fromCurrency, toCurrency, rate);

                    } else {
                        showError("Không tìm thấy tỷ giá cho loại tiền tệ này");
                    }
                } else {
                    showError("Không thể lấy tỷ giá. Vui lòng thử lại");
                }
            }

            @Override
            public void onFailure(Call<ExchangeRate> call, Throwable t) {
                hideLoading();
                showError("Lỗi kết nối: " + t.getMessage());
                useFallbackExchangeRate(fromCurrency, toCurrency, amount);
            }
        });
    }


    private void useFallbackExchangeRate(String fromCurrency, String toCurrency, double amount) {
        double rate = getFallbackRate(fromCurrency, toCurrency);
        double convertedAmount = amount * rate;

        showResult(convertedAmount, rate, toCurrency);
        saveConversionHistory(amount, fromCurrency, toCurrency, convertedAmount, rate);

        // THÊM DÒNG NÀY Ở ĐÂY NỮA - cho trường hợp dùng tỷ giá fallback
        saveExchangeRateHistory(fromCurrency, toCurrency, rate);

        showError("Đang sử dụng tỷ giá demo do lỗi kết nối");
    }

    private double getFallbackRate(String fromCurrency, String toCurrency) {
        Map<String, Double> fallbackRates = new HashMap<>();

        // ==== TỶ GIÁ GỐC TỪ USD (31/12/2025) ====
        fallbackRates.put("USD_VND", 25350.0);
        fallbackRates.put("USD_EUR", 0.89);
        fallbackRates.put("USD_JPY", 148.0);
        fallbackRates.put("USD_GBP", 0.76);
        fallbackRates.put("USD_CAD", 1.32);
        fallbackRates.put("USD_AUD", 1.47);
        fallbackRates.put("USD_CNY", 7.15);
        fallbackRates.put("USD_KRW", 1320.0);
        fallbackRates.put("USD_SGD", 1.33);

        // ==== CHIỀU NGƯỢC LẠI (TỪ CÁC ĐỒNG TIỀN VỀ USD) ====
        // Tính toán chính xác từ tỷ giá gốc
        fallbackRates.put("VND_USD", 1.0 / 25350.0);     // ~0.00003945
        fallbackRates.put("EUR_USD", 1.0 / 0.89);        // ~1.1236
        fallbackRates.put("JPY_USD", 1.0 / 148.0);       // ~0.006756
        fallbackRates.put("GBP_USD", 1.0 / 0.76);        // ~1.3158
        fallbackRates.put("CAD_USD", 1.0 / 1.32);        // ~0.7576
        fallbackRates.put("AUD_USD", 1.0 / 1.47);        // ~0.6803
        fallbackRates.put("CNY_USD", 1.0 / 7.15);        // ~0.1399
        fallbackRates.put("KRW_USD", 1.0 / 1320.0);      // ~0.0007576
        fallbackRates.put("SGD_USD", 1.0 / 1.33);        // ~0.7519

        // ==== TỶ GIÁ CHÉO GIỮA CÁC ĐỒNG TIỀN (không qua USD) ====
        // Công thức: (A/B) = (A/USD) ÷ (B/USD)

        // EUR sang các đồng tiền khác
        fallbackRates.put("EUR_VND", 25350.0 / 0.89);    // ~28,483
        fallbackRates.put("EUR_JPY", 148.0 / 0.89);      // ~166.29
        fallbackRates.put("EUR_GBP", 0.76 / 0.89);       // ~0.8539
        fallbackRates.put("EUR_CAD", 1.32 / 0.89);       // ~1.4831
        fallbackRates.put("EUR_AUD", 1.47 / 0.89);       // ~1.6517
        fallbackRates.put("EUR_CNY", 7.15 / 0.89);       // ~8.0337
        fallbackRates.put("EUR_KRW", 1320.0 / 0.89);     // ~1,483.15
        fallbackRates.put("EUR_SGD", 1.33 / 0.89);       // ~1.4944

        // GBP sang các đồng tiền khác
        fallbackRates.put("GBP_VND", 25350.0 / 0.76);    // ~33,355
        fallbackRates.put("GBP_EUR", 0.89 / 0.76);       // ~1.1711
        fallbackRates.put("GBP_JPY", 148.0 / 0.76);      // ~194.74
        fallbackRates.put("GBP_CAD", 1.32 / 0.76);       // ~1.7368
        fallbackRates.put("GBP_AUD", 1.47 / 0.76);       // ~1.9342
        fallbackRates.put("GBP_CNY", 7.15 / 0.76);       // ~9.4079
        fallbackRates.put("GBP_KRW", 1320.0 / 0.76);     // ~1,736.84
        fallbackRates.put("GBP_SGD", 1.33 / 0.76);       // ~1.7500

        // JPY sang các đồng tiền khác
        fallbackRates.put("JPY_VND", 25350.0 / 148.0);   // ~171.28
        fallbackRates.put("JPY_EUR", 0.89 / 148.0);      // ~0.006014
        fallbackRates.put("JPY_GBP", 0.76 / 148.0);      // ~0.005135
        fallbackRates.put("JPY_CAD", 1.32 / 148.0);      // ~0.008919
        fallbackRates.put("JPY_AUD", 1.47 / 148.0);      // ~0.009932
        fallbackRates.put("JPY_CNY", 7.15 / 148.0);      // ~0.04831
        fallbackRates.put("JPY_KRW", 1320.0 / 148.0);    // ~8.9189
        fallbackRates.put("JPY_SGD", 1.33 / 148.0);      // ~0.008986

        // ==== CÁC CẶP TIỀN KHÁC ====

        // VND sang các đồng tiền
        fallbackRates.put("VND_EUR", 0.89 / 25350.0);    // ~0.0000351
        fallbackRates.put("VND_JPY", 148.0 / 25350.0);   // ~0.005838
        fallbackRates.put("VND_GBP", 0.76 / 25350.0);    // ~0.00002998
        fallbackRates.put("VND_CAD", 1.32 / 25350.0);    // ~0.0000521
        fallbackRates.put("VND_AUD", 1.47 / 25350.0);    // ~0.0000580
        fallbackRates.put("VND_CNY", 7.15 / 25350.0);    // ~0.000282
        fallbackRates.put("VND_KRW", 1320.0 / 25350.0);  // ~0.05207
        fallbackRates.put("VND_SGD", 1.33 / 25350.0);    // ~0.0000525

        // AUD sang các đồng tiền
        fallbackRates.put("AUD_VND", 25350.0 / 1.47);    // ~17,245
        fallbackRates.put("AUD_EUR", 0.89 / 1.47);       // ~0.6054
        fallbackRates.put("AUD_JPY", 148.0 / 1.47);      // ~100.68
        fallbackRates.put("AUD_GBP", 0.76 / 1.47);       // ~0.5170
        fallbackRates.put("AUD_CAD", 1.32 / 1.47);       // ~0.8980
        fallbackRates.put("AUD_CNY", 7.15 / 1.47);       // ~4.8639
        fallbackRates.put("AUD_KRW", 1320.0 / 1.47);     // ~897.96
        fallbackRates.put("AUD_SGD", 1.33 / 1.47);       // ~0.9048

        // CAD sang các đồng tiền
        fallbackRates.put("CAD_VND", 25350.0 / 1.32);    // ~19,205
        fallbackRates.put("CAD_EUR", 0.89 / 1.32);       // ~0.6742
        fallbackRates.put("CAD_JPY", 148.0 / 1.32);      // ~112.12
        fallbackRates.put("CAD_GBP", 0.76 / 1.32);       // ~0.5758
        fallbackRates.put("CAD_AUD", 1.47 / 1.32);       // ~1.1136
        fallbackRates.put("CAD_CNY", 7.15 / 1.32);       // ~5.4167
        fallbackRates.put("CAD_KRW", 1320.0 / 1.32);     // ~1,000.0
        fallbackRates.put("CAD_SGD", 1.33 / 1.32);       // ~1.0076

        // CNY sang các đồng tiền
        fallbackRates.put("CNY_VND", 25350.0 / 7.15);    // ~3,545.5
        fallbackRates.put("CNY_EUR", 0.89 / 7.15);       // ~0.1245
        fallbackRates.put("CNY_JPY", 148.0 / 7.15);      // ~20.699
        fallbackRates.put("CNY_GBP", 0.76 / 7.15);       // ~0.1063
        fallbackRates.put("CNY_CAD", 1.32 / 7.15);       // ~0.1846
        fallbackRates.put("CNY_AUD", 1.47 / 7.15);       // ~0.2056
        fallbackRates.put("CNY_KRW", 1320.0 / 7.15);     // ~184.62
        fallbackRates.put("CNY_SGD", 1.33 / 7.15);       // ~0.1860

        // KRW sang các đồng tiền
        fallbackRates.put("KRW_VND", 25350.0 / 1320.0);  // ~19.205
        fallbackRates.put("KRW_EUR", 0.89 / 1320.0);     // ~0.0006742
        fallbackRates.put("KRW_JPY", 148.0 / 1320.0);    // ~0.11212
        fallbackRates.put("KRW_GBP", 0.76 / 1320.0);     // ~0.0005758
        fallbackRates.put("KRW_CAD", 1.32 / 1320.0);     // ~0.001000
        fallbackRates.put("KRW_AUD", 1.47 / 1320.0);     // ~0.0011136
        fallbackRates.put("KRW_CNY", 7.15 / 1320.0);     // ~0.0054167
        fallbackRates.put("KRW_SGD", 1.33 / 1320.0);     // ~0.0010076

        // SGD sang các đồng tiền
        fallbackRates.put("SGD_VND", 25350.0 / 1.33);    // ~19,060
        fallbackRates.put("SGD_EUR", 0.89 / 1.33);       // ~0.6692
        fallbackRates.put("SGD_JPY", 148.0 / 1.33);      // ~111.28
        fallbackRates.put("SGD_GBP", 0.76 / 1.33);       // ~0.5714
        fallbackRates.put("SGD_CAD", 1.32 / 1.33);       // ~0.9925
        fallbackRates.put("SGD_AUD", 1.47 / 1.33);       // ~1.1053
        fallbackRates.put("SGD_CNY", 7.15 / 1.33);       // ~5.3759
        fallbackRates.put("SGD_KRW", 1320.0 / 1.33);     // ~992.48

        // ==== TRƯỜNG HỢP MẶC ĐỊNH (nếu không tìm thấy) ====
        String key = fromCurrency + "_" + toCurrency;
        Double rate = fallbackRates.get(key);

        // Nếu không tìm thấy direct rate, thử tính toán qua USD
        if (rate == null) {
            Double usdToFrom = fallbackRates.get("USD_" + fromCurrency);
            Double usdToTo = fallbackRates.get("USD_" + toCurrency);

            if (usdToFrom != null && usdToTo != null) {
                // Công thức: A/B = (A/USD) ÷ (B/USD)
                rate = usdToTo / usdToFrom;
            }
        }

        return rate != null ? rate : 1.0;  // Trả về 1.0 nếu không tính được
    }

    private void showResult(double convertedAmount, double rate, String toCurrency) {
        Currency fromCurrencyObj = (Currency) spinnerFromCurrency.getSelectedItem();
        Currency toCurrencyObj = (Currency) spinnerToCurrency.getSelectedItem();

        String resultText = String.format("%,.2f %s", convertedAmount, toCurrency);
        String rateText = String.format("Tỷ giá: 1 %s = %,.4f %s",
                fromCurrencyObj.getCode(), rate, toCurrencyObj.getCode());

        tvResult.setText(resultText);
        tvConversionRate.setText(rateText);
        cardResult.setVisibility(View.VISIBLE);
    }

    private void saveConversionHistory(double amount, String fromCurrency,
                                       String toCurrency, double convertedAmount, double rate) {
        new Thread(() -> {
            try {
                ConversionHistory history = new ConversionHistory(
                        amount, fromCurrency, toCurrency, convertedAmount, rate, new Date()
                );
                database.historyDao().insert(history);

                int count = database.historyDao().getCount();
                System.out.println("Đã lưu lịch sử. Tổng số bản ghi: " + count);
            } catch (Exception e) {
                System.err.println("Lỗi khi lưu lịch sử: " + e.getMessage());
            }
        }).start();
    }

    private void showHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void showError(String message) {
        Snackbar.make(btnConvert, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.parseColor("#E74C3C"))
                .setTextColor(Color.WHITE)
                .show();
    }

    private void showLoading() {
        btnConvert.setText("Đang chuyển đổi...");
        btnConvert.setEnabled(false);
    }

    private void hideLoading() {
        btnConvert.setText("Chuyển Đổi");
        btnConvert.setEnabled(true);
    }

    private void showChart() {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    private void showFavorites() {
        Intent intent = new Intent(this, FavoriteSettingsActivity.class);
        startActivity(intent);
    }

    // Lưu lịch sử tỷ giá vào database
    private void saveExchangeRateHistory(String fromCurrency, String toCurrency, double rate) {
        new Thread(() -> {
            try {
                ExchangeRateHistory history = new ExchangeRateHistory(fromCurrency, toCurrency, rate);
                database.exchangeRateHistoryDao().insert(history);

                // Xóa dữ liệu cũ (giữ 30 ngày)
                long cutoffTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
                database.exchangeRateHistoryDao().deleteOldRates(cutoffTime);

                System.out.println("Đã lưu lịch sử tỷ giá: " + fromCurrency + "->" + toCurrency + " = " + rate);
            } catch (Exception e) {
                System.err.println("Lỗi khi lưu lịch sử tỷ giá: " + e.getMessage());
            }
        }).start();
    }

    // Tải cài đặt mặc định khi khởi động app
    private void loadDefaultFavorite() {
        new Thread(() -> {
            try {
                FavoriteSetting defaultSetting = database.favoriteSettingDao().getDefault();
                if (defaultSetting != null) {
                    runOnUiThread(() -> {
                        // Tự động chọn currency theo favorite
                        for (int i = 0; i < currencyList.size(); i++) {
                            if (currencyList.get(i).getCode().equals(defaultSetting.getFromCurrency())) {
                                spinnerFromCurrency.setSelection(i);
                                break;
                            }
                        }
                        for (int i = 0; i < currencyList.size(); i++) {
                            if (currencyList.get(i).getCode().equals(defaultSetting.getToCurrency())) {
                                spinnerToCurrency.setSelection(i);
                                break;
                            }
                        }
                        System.out.println("Đã tải cài đặt mặc định: " + defaultSetting.getSettingName());
                    });
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải cài đặt mặc định: " + e.getMessage());
            }
        }).start();
    }
}