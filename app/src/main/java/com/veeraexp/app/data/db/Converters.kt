package com.veeraexp.app.data.db

import androidx.room.TypeConverter
import com.veeraexp.app.data.entity.CategoryType
import com.veeraexp.app.data.entity.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)
}
