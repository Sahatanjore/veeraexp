package com.veeraexp.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table (id is always 1) holding settings that must survive
 * app restarts and aren't tied to Android SharedPreferences.
 * Opening balance lives here — deliberately separate from the
 * transactions table so it can never be double-counted as income.
 */
@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val openingBalance: Double = 0.0,
    val themeKey: String = "midnight",
    val darkModeMode: String = "system", // "system" | "light" | "dark"
    val languageCode: String = "en",     // "en" | "ta"
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val floatingQuickAddEnabled: Boolean = false,
    val appLockEnabled: Boolean = false,
    val pinHash: String? = null
)
