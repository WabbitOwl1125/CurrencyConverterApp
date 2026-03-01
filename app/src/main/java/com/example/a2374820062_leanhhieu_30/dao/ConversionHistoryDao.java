package com.example.a2374820062_leanhhieu_30.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.a2374820062_leanhhieu_30.model.ConversionHistory;

import java.util.List;

@Dao
public interface ConversionHistoryDao {
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    List<ConversionHistory> getAll();

    @Insert
    void insert(ConversionHistory history);

    @Query("DELETE FROM conversion_history")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM conversion_history")
    int getCount();
}