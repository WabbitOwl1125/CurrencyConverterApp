package com.example.a2374820062_leanhhieu_30.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.a2374820062_leanhhieu_30.model.ExchangeRateHistory;

import java.util.List;

@Dao
public interface ExchangeRateHistoryDao {
    @Query("SELECT * FROM exchange_rate_history WHERE fromCurrency = :fromCurrency AND toCurrency = :toCurrency AND timestamp >= :startTime ORDER BY timestamp ASC")
    List<ExchangeRateHistory> getRatesInPeriod(String fromCurrency, String toCurrency, long startTime);

    @Query("SELECT * FROM exchange_rate_history WHERE fromCurrency = :fromCurrency AND toCurrency = :toCurrency ORDER BY timestamp DESC LIMIT :limit")
    List<ExchangeRateHistory> getRecentRates(String fromCurrency, String toCurrency, int limit);

    @Insert
    void insert(ExchangeRateHistory history);

    @Query("DELETE FROM exchange_rate_history WHERE timestamp < :cutoffTime")
    void deleteOldRates(long cutoffTime);
}