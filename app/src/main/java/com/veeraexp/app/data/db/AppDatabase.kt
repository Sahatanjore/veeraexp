package com.veeraexp.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.veeraexp.app.data.dao.BudgetDao
import com.veeraexp.app.data.dao.CategoryDao
import com.veeraexp.app.data.dao.GoalDao
import com.veeraexp.app.data.dao.SettingsDao
import com.veeraexp.app.data.dao.TransactionDao
import com.veeraexp.app.data.entity.AppSettings
import com.veeraexp.app.data.entity.Budget
import com.veeraexp.app.data.entity.Category
import com.veeraexp.app.data.entity.InvestmentGoal
import com.veeraexp.app.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Transaction::class,
        Category::class,
        InvestmentGoal::class,
        Budget::class,
        AppSettings::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun budgetDao(): BudgetDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "veeraexp.db"
                )
                    // Migrations must be added explicitly as the schema evolves —
                    // deliberately NOT using fallbackToDestructiveMigration()
                    // so a schema change can never silently wipe user data.
                    .build()
                    .also { db ->
                        INSTANCE = db
                        CoroutineScope(Dispatchers.IO).launch { seedDefaultsIfEmpty(context, db) }
                    }
            }

        private suspend fun seedDefaultsIfEmpty(context: Context, db: AppDatabase) {
            if (db.categoryDao().count() == 0) {
                db.categoryDao().insertAll(DefaultCategories.build())
            }
            if (db.settingsDao().get() == null) {
                db.settingsDao().upsert(AppSettings())
            }
        }
    }
}
