package com.example.data.model

import com.example.data.entity.CutEntity

data class CutBalance(
    val cut: CutEntity,
    val totalInPieces: Int,
    val totalOutPieces: Int,
    val remainingPieces: Int,
    val totalInRolls: Int,
    val totalOutRolls: Int,
    val remainingRolls: Int,
    val transactionsCount: Int
) {
    val completionPercentage: Int
        get() = if (totalInPieces > 0) {
            ((totalOutPieces.toDouble() / totalInPieces) * 100).toInt().coerceIn(0, 100)
        } else 0

    val statusText: String
        get() = when {
            totalInPieces == 0 -> "لا توجد حركة"
            remainingPieces == 0 -> "منصرف بالكامل"
            remainingPieces < 0 -> "عجز بالسالب ($remainingPieces)"
            totalOutPieces == 0 -> "متوفر بالكامل"
            else -> "صرف جزئي"
        }
}

data class WarehouseStats(
    val totalCutsCount: Int = 0,
    val totalInPieces: Int = 0,
    val totalOutPieces: Int = 0,
    val currentStockBalance: Int = 0,
    val totalInRolls: Int = 0,
    val totalOutRolls: Int = 0,
    val currentRollsBalance: Int = 0,
    val availableCutsCount: Int = 0,
    val fullyDispatchedCutsCount: Int = 0,
    val partiallyDispatchedCutsCount: Int = 0,
    val noMovementCutsCount: Int = 0,
    val todayInPieces: Int = 0,
    val todayOutPieces: Int = 0,
    val thisMonthInPieces: Int = 0,
    val thisMonthOutPieces: Int = 0,
    val totalTransactionsCount: Int = 0
)

data class ImportPreviewRow(
    val rowIndex: Int,
    val cutNumber: String,
    val modelName: String,
    val type: String,
    val stage: String,
    val pieces: Int,
    val rolls: Int,
    val weight: Double,
    val destination: String,
    val responsible: String,
    val dateStr: String,
    val docNumber: String,
    val notes: String,
    val isValid: Boolean,
    val errorMessage: String = ""
)
