package com.example.a2374820062_leanhhieu_30.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRate {

    @SerializedName("base_code")
    private String base;

    @SerializedName("conversion_rates")
    private Map<String, Double> rates;

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
