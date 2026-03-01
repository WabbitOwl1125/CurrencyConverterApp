package com.example.a2374820062_leanhhieu_30.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_settings")
public class FavoriteSetting {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fromCurrency;
    private String toCurrency;
    private String settingName;
    private boolean isDefault;
    private long createdAt;

    public FavoriteSetting(String fromCurrency, String toCurrency, String settingName, boolean isDefault) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.settingName = settingName;
        this.isDefault = isDefault;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public String getSettingName() { return settingName; }
    public void setSettingName(String settingName) { this.settingName = settingName; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}