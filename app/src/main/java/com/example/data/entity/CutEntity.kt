package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuts")
data class CutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cutNumber: String,       // رقم القصة (e.g. ق-101)
    val modelName: String,       // اسم الموديل (e.g. تيشيرت بولو صيفي)
    val fabricType: String,      // نوع القماش / الخامة (e.g. قطن سينجل جيرسي)
    val color: String,           // اللون (e.g. كحلي)
    val season: String = "",     // الموسم (e.g. صيف 2026)
    val targetPieces: Int = 0,   // إجمالي قطع القصة
    val notes: String = "",      // ملاحظات
    val createdAt: Long = System.currentTimeMillis()
)
