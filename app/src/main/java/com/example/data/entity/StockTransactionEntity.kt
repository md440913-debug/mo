package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_transactions")
data class StockTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cutNumber: String,            // رقم القصة
    val modelName: String,            // اسم الموديل
    val transactionType: String,      // IN (إدخال وارد) أو OUT (إخراج منصرف)
    val category: String,             // السبب/المرحلة
    val quantityPieces: Int,          // عدد القطع
    val quantityRolls: Int = 0,       // عدد الأثواب / الرولات
    val weightKg: Double = 0.0,       // الوزن كجم
    val destination: String = "",     // جهة الصرف أو التوريد (ورشة، خط خياطة، مقصدار)
    val responsiblePerson: String = "",// المسؤول / أمين المخزن
    val receiverName: String = "",    // اسم المستلم
    val documentNumber: String = "",  // رقم المستند / إذن الصرف أو الإضافة
    val stage: String = "",           // المرحلة (قص، خياطة، طباعة، تطريز، تجهيز، تشطيب، أخرى)
    val fabricType: String = "",      // نوع القماش
    val color: String = "",           // اللون
    val dateStr: String,              // التاريخ (YYYY-MM-DD)
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""            // ملاحظات
) {
    val isIncoming: Boolean
        get() = transactionType.equals("IN", ignoreCase = true)
}
