package com.example.a2374820062_leanhhieu_30.api;

import com.example.a2374820062_leanhhieu_30.model.ExchangeRate;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateAPI {
    @GET("latest/{base}")
    Call<ExchangeRate> getLatestRates(@Path("base") String baseCurrency);
}