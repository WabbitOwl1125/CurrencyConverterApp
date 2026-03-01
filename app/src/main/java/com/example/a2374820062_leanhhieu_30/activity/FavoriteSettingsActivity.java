package com.example.a2374820062_leanhhieu_30.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.a2374820062_leanhhieu_30.AppDatabase;
import com.example.a2374820062_leanhhieu_30.R;
import com.example.a2374820062_leanhhieu_30.model.Currency;
import com.example.a2374820062_leanhhieu_30.model.FavoriteSetting;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSettingsActivity extends AppCompatActivity {

    private LinearLayout layoutFavorites;
    private Button btnAddFavorite, btnBack;
    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private AppDatabase database;
    private List<Currency> currencyList;
    private ArrayAdapter<Currency> currencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_favorite_settings);

        initViews();
        setupCurrencyList();
        setupSpinners();
        setupDatabase();
        loadFavorites();
        setupEventListeners();
    }

    private void initViews() {
        layoutFavorites = findViewById(R.id.layoutFavorites);
        btnAddFavorite = findViewById(R.id.btnAddFavorite);
        btnBack = findViewById(R.id.btnBackFavorites);
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrencyFav);
        spinnerToCurrency = findViewById(R.id.spinnerToCurrencyFav);
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
    }

    private void setupSpinners() {
        currencyAdapter = new ArrayAdapter<Currency>(this,
                android.R.layout.simple_spinner_item, currencyList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.parseColor("#184332"));
                textView.setTextSize(16);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
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

        // Mặc định chọn USD -> VND
        spinnerFromCurrency.setSelection(0);
        spinnerToCurrency.setSelection(2);
    }

    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
    }

    private void setupEventListeners() {
        btnAddFavorite.setOnClickListener(v -> showAddFavoriteDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void showAddFavoriteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_favorite, null);

        EditText etName = dialogView.findViewById(R.id.etFavoriteName);
        Spinner spinnerFrom = dialogView.findViewById(R.id.spinnerFromCurrencyDialog);
        Spinner spinnerTo = dialogView.findViewById(R.id.spinnerToCurrencyDialog);

        // Setup spinners cho dialog
        ArrayAdapter<Currency> dialogAdapter = new ArrayAdapter<Currency>(this,
                android.R.layout.simple_spinner_item, currencyList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#184332"));
                return textView;
            }
        };
        dialogAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(dialogAdapter);
        spinnerTo.setAdapter(dialogAdapter);

        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(2);

        builder.setView(dialogView)
                .setTitle("Thêm cài đặt ưa thích")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    Currency from = (Currency) spinnerFrom.getSelectedItem();
                    Currency to = (Currency) spinnerTo.getSelectedItem();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên cài đặt", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (from.getCode().equals(to.getCode())) {
                        Toast.makeText(this, "Hai loại tiền tệ phải khác nhau", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    saveFavoriteSetting(name, from.getCode(), to.getCode());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveFavoriteSetting(String name, String fromCurrency, String toCurrency) {
        new Thread(() -> {
            try {
                // Kiểm tra xem đã tồn tại chưa
                FavoriteSetting existing = database.favoriteSettingDao()
                        .getByCurrencies(fromCurrency, toCurrency);

                if (existing != null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Cài đặt này đã tồn tại", Toast.LENGTH_SHORT).show());
                    return;
                }

                FavoriteSetting setting = new FavoriteSetting(fromCurrency, toCurrency, name, false);
                database.favoriteSettingDao().insert(setting);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã thêm cài đặt ưa thích", Toast.LENGTH_SHORT).show();
                    loadFavorites();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi khi thêm cài đặt", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadFavorites() {
        new Thread(() -> {
            try {
                List<FavoriteSetting> favorites = database.favoriteSettingDao().getAll();
                runOnUiThread(() -> displayFavorites(favorites));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi khi tải cài đặt", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayFavorites(List<FavoriteSetting> favorites) {
        layoutFavorites.removeAllViews();

        if (favorites.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có cài đặt ưa thích nào");
            tvEmpty.setTextColor(Color.parseColor("#687280"));
            tvEmpty.setTextSize(16);
            tvEmpty.setGravity(View.TEXT_ALIGNMENT_CENTER);
            tvEmpty.setPadding(0, 100, 0, 0);
            layoutFavorites.addView(tvEmpty);
            return;
        }

        for (FavoriteSetting favorite : favorites) {
            CardView cardView = createFavoriteCard(favorite);
            layoutFavorites.addView(cardView);
        }
    }

    private CardView createFavoriteCard(FavoriteSetting favorite) {
        CardView cardView = (CardView) LayoutInflater.from(this)
                .inflate(R.layout.item_favorite_setting, layoutFavorites, false);

        TextView tvName = cardView.findViewById(R.id.tvFavoriteName);
        TextView tvCurrencies = cardView.findViewById(R.id.tvFavoriteCurrencies);
        Button btnUse = cardView.findViewById(R.id.btnUseFavorite);
        Button btnDelete = cardView.findViewById(R.id.btnDeleteFavorite);

        tvName.setText(favorite.getSettingName());
        if (favorite.isDefault()) {
            tvName.setText(favorite.getSettingName() + " ⭐");
        }

        tvCurrencies.setText(favorite.getFromCurrency() + " → " + favorite.getToCurrency());

        btnUse.setOnClickListener(v -> useFavorite(favorite));
        btnDelete.setOnClickListener(v -> deleteFavorite(favorite));

        return cardView;
    }

    private void useFavorite(FavoriteSetting favorite) {
        // Trả về kết quả cho MainActivity
        Toast.makeText(this, "Đã chọn: " + favorite.getSettingName(), Toast.LENGTH_SHORT).show();

        // Có thể implement để tự động chuyển về MainActivity với cài đặt này
        // Intent intent = new Intent();
        // intent.putExtra("fromCurrency", favorite.getFromCurrency());
        // intent.putExtra("toCurrency", favorite.getToCurrency());
        // setResult(RESULT_OK, intent);
        // finish();
    }

    private void deleteFavorite(FavoriteSetting favorite) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa cài đặt")
                .setMessage("Bạn có chắc muốn xóa '" + favorite.getSettingName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        database.favoriteSettingDao().delete(favorite);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Đã xóa cài đặt", Toast.LENGTH_SHORT).show();
                            loadFavorites();
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}