package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_audits")
data class AuditEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cutNumber: String,
    val modelName: String,
    val bookBalance: Int,       // الرصيد الدفتري
    val actualBalance: Int,     // الرصيد الفعلي
    val difference: Int,        // الفرق = الفعلي - الدفتري
    val auditDate: String,      // تاريخ الجرد
    val auditorName: String,    // القائم بالجرد
    val notes: String = "",     // ملاحظات
    val isApproved: Boolean = false // حالة الاعتماد
)
