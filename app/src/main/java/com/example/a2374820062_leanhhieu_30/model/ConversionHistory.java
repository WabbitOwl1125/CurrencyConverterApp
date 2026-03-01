package com.example.a2374820062_leanhhieu_30.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.a2374820062_leanhhieu_30.util.DateConverter;

import java.util.Date;

@Entity(tableName = "conversion_history")
@TypeConverters({DateConverter.class})
public class ConversionHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private String fromCurrency;
    private String toCurrency;
    private double convertedAmount;
    private double rate;
    private Date timestamp;

    public ConversionHistory(double amount, String fromCurrency, String toCurrency,
                             double convertedAmount, double rate, Date timestamp) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.convertedAmount = convertedAmount;
        this.rate = rate; // THÊM DÒNG NÀY
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public double getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(double convertedAmount) { this.convertedAmount = convertedAmount; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}