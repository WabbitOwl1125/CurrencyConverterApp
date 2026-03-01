package com.example.a2374820062_leanhhieu_30.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "exchange_rate_history")
public class ExchangeRateHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fromCurrency;
    private String toCurrency;
    private double rate;
    private long timestamp;

    public ExchangeRateHistory(String fromCurrency, String toCurrency, double rate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Date getDate() { return new Date(timestamp); }
}