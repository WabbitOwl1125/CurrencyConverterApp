package com.example.a2374820062_leanhhieu_30.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.a2374820062_leanhhieu_30.model.FavoriteSetting;

import java.util.List;

@Dao
public interface FavoriteSettingDao {
    @Query("SELECT * FROM favorite_settings ORDER BY isDefault DESC, createdAt DESC")
    List<FavoriteSetting> getAll();

    @Query("SELECT * FROM favorite_settings WHERE isDefault = 1 LIMIT 1")
    FavoriteSetting getDefault();

    @Query("SELECT * FROM favorite_settings WHERE fromCurrency = :fromCurrency AND toCurrency = :toCurrency LIMIT 1")
    FavoriteSetting getByCurrencies(String fromCurrency, String toCurrency);

    @Insert
    void insert(FavoriteSetting setting);

    @Update
    void update(FavoriteSetting setting);

    @Delete
    void delete(FavoriteSetting setting);

    @Query("DELETE FROM favorite_settings WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE favorite_settings SET isDefault = 0")
    void clearDefault();
}