package com.example.a2374820062_leanhhieu_30;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.a2374820062_leanhhieu_30.dao.ConversionHistoryDao;
import com.example.a2374820062_leanhhieu_30.dao.ExchangeRateHistoryDao;
import com.example.a2374820062_leanhhieu_30.dao.FavoriteSettingDao;
import com.example.a2374820062_leanhhieu_30.model.ConversionHistory;
import com.example.a2374820062_leanhhieu_30.model.ExchangeRateHistory;
import com.example.a2374820062_leanhhieu_30.model.FavoriteSetting;
import com.example.a2374820062_leanhhieu_30.util.DateConverter;

@Database(entities = {
        ConversionHistory.class,
        FavoriteSetting.class,
        ExchangeRateHistory.class
}, version = 3, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ConversionHistoryDao historyDao();
    public abstract FavoriteSettingDao favoriteSettingDao();
    public abstract ExchangeRateHistoryDao exchangeRateHistoryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "currency_converter.db"
                            ).fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}