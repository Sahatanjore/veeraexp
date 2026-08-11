package com.veeraexp.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategoryType { INCOME, EXPENSE }

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val iconKey: String,        // maps to a drawable/icon resource name
    val colorHex: String,       // e.g. "#FF5722"
    val isDefault: Boolean = false,
    val isDeleted: Boolean = false
)
