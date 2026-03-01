package com.example.a2374820062_leanhhieu_30.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/be7ec441ac1cfd73c57a8ca0/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // TẠO HTTP LOGGING INTERCEPTOR
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // HIỂN THỊ TOÀN BỘ REQUEST/RESPONSE

            // TẠO OKHTTP CLIENT VỚI INTERCEPTOR
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)  // THÊM INTERCEPTOR ĐỂ LOG
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // THÊM CLIENT VÀO RETROFIT
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ExchangeRateAPI getApiService() {
        return getClient().create(ExchangeRateAPI.class);
    }
}