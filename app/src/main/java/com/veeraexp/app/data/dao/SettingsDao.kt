package com.veeraexp.app.data.dao

import androidx.room.*
import com.veeraexp.app.data.entity.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: AppSettings)

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun observe(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun get(): AppSettings?

    @Query("UPDATE app_settings SET openingBalance = :amount WHERE id = 1")
    suspend fun setOpeningBalance(amount: Double)
}
