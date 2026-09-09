package com.example.data.repository

import com.example.data.dao.AuditDao
import com.example.data.dao.CutDao
import com.example.data.dao.StockTransactionDao
import com.example.data.entity.AuditEntity
import com.example.data.entity.CutEntity
import com.example.data.entity.StockTransactionEntity
import com.example.data.model.CutBalance
import com.example.data.model.WarehouseStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventoryRepository(
    private val cutDao: CutDao,
    private val transactionDao: StockTransactionDao,
    private val auditDao: AuditDao
) {
    val allCuts: Flow<List<CutEntity>> = cutDao.getAllCuts()
    val allTransactions: Flow<List<StockTransactionEntity>> = transactionDao.getAllTransactions()
    val allAudits: Flow<List<AuditEntity>> = auditDao.getAllAudits()

    // Dynamically calculate balances by combining cuts and transactions
    val cutsWithBalances: Flow<List<CutBalance>> = combine(allCuts, allTransactions) { cuts, transactions ->
        cuts.map { cut ->
            val cutTrans = transactions.filter { it.cutNumber.trim().equals(cut.cutNumber.trim(), ignoreCase = true) }
            val inPieces = cutTrans.filter { it.isIncoming }.sumOf { it.quantityPieces }
            val outPieces = cutTrans.filter { !it.isIncoming }.sumOf { it.quantityPieces }
            val inRolls = cutTrans.filter { it.isIncoming }.sumOf { it.quantityRolls }
            val outRolls = cutTrans.filter { !it.isIncoming }.sumOf { it.quantityRolls }

            CutBalance(
                cut = cut,
                totalInPieces = inPieces,
                totalOutPieces = outPieces,
                remainingPieces = inPieces - outPieces,
                totalInRolls = inRolls,
                totalOutRolls = outRolls,
                remainingRolls = inRolls - outRolls,
                transactionsCount = cutTrans.size
            )
        }
    }

    // Dynamic overall statistics for KPI cards & Dashboard
    val warehouseStats: Flow<WarehouseStats> = combine(allCuts, allTransactions) { cuts, transactions ->
        val inPieces = transactions.filter { it.isIncoming }.sumOf { it.quantityPieces }
        val outPieces = transactions.filter { !it.isIncoming }.sumOf { it.quantityPieces }
        val inRolls = transactions.filter { it.isIncoming }.sumOf { it.quantityRolls }
        val outRolls = transactions.filter { !it.isIncoming }.sumOf { it.quantityRolls }

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val thisMonthStr = SimpleDateFormat("yyyy-MM", Locale.ENGLISH).format(Date())

        val todayIn = transactions.filter { it.isIncoming && it.dateStr.startsWith(todayStr) }.sumOf { it.quantityPieces }
        val todayOut = transactions.filter { !it.isIncoming && it.dateStr.startsWith(todayStr) }.sumOf { it.quantityPieces }

        val monthIn = transactions.filter { it.isIncoming && it.dateStr.startsWith(thisMonthStr) }.sumOf { it.quantityPieces }
        val monthOut = transactions.filter { !it.isIncoming && it.dateStr.startsWith(thisMonthStr) }.sumOf { it.quantityPieces }

        // Cuts classification
        var availableCount = 0
        var fullDispatchedCount = 0
        var partialDispatchedCount = 0
        var noMovementCount = 0

        cuts.forEach { cut ->
            val cutTrans = transactions.filter { it.cutNumber.trim().equals(cut.cutNumber.trim(), ignoreCase = true) }
            val inP = cutTrans.filter { it.isIncoming }.sumOf { it.quantityPieces }
            val outP = cutTrans.filter { !it.isIncoming }.sumOf { it.quantityPieces }
            val rem = inP - outP

            when {
                inP == 0 -> noMovementCount++
                rem == 0 -> fullDispatchedCount++
                outP == 0 -> availableCount++
                rem > 0 -> {
                    availableCount++
                    partialDispatchedCount++
                }
            }
        }

        WarehouseStats(
            totalCutsCount = cuts.size,
            totalInPieces = inPieces,
            totalOutPieces = outPieces,
            currentStockBalance = inPieces - outPieces,
            totalInRolls = inRolls,
            totalOutRolls = outRolls,
            currentRollsBalance = inRolls - outRolls,
            availableCutsCount = availableCount,
            fullyDispatchedCutsCount = fullDispatchedCount,
            partiallyDispatchedCutsCount = partialDispatchedCount,
            noMovementCutsCount = noMovementCount,
            todayInPieces = todayIn,
            todayOutPieces = todayOut,
            thisMonthInPieces = monthIn,
            thisMonthOutPieces = monthOut,
            totalTransactionsCount = transactions.size
        )
    }

    suspend fun getCutAvailableBalance(cutNumber: String): Int {
        val inPieces = transactionDao.getTotalInPiecesForCut(cutNumber.trim())
        val outPieces = transactionDao.getTotalOutPiecesForCut(cutNumber.trim())
        return inPieces - outPieces
    }

    suspend fun insertCut(cut: CutEntity): Long = cutDao.insertCut(cut)
    suspend fun updateCut(cut: CutEntity) = cutDao.updateCut(cut)
    suspend fun deleteCut(cut: CutEntity) = cutDao.deleteCut(cut)
    suspend fun deleteCutById(id: Long) = cutDao.deleteCutById(id)
    suspend fun getCutByNumber(cutNumber: String): CutEntity? = cutDao.getCutByNumber(cutNumber.trim())

    suspend fun insertTransaction(transaction: StockTransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: StockTransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: StockTransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) = transactionDao.deleteTransactionById(id)

    suspend fun insertCuts(cuts: List<CutEntity>) = cutDao.insertCuts(cuts)
    suspend fun insertTransactions(transactions: List<StockTransactionEntity>) =
        transactionDao.insertTransactions(transactions)

    suspend fun insertAudit(audit: AuditEntity) = auditDao.insertAudit(audit)
    suspend fun updateAudit(audit: AuditEntity) = auditDao.updateAudit(audit)
    suspend fun deleteAudit(audit: AuditEntity) = auditDao.deleteAudit(audit)

    suspend fun getCutsCount(): Int = cutDao.getCutsCount()

    // Pre-populate with realistic Arabic garment cutting warehouse data if empty
    suspend fun seedSampleDataIfEmpty() {
        if (cutDao.getCutsCount() == 0) {
            val sampleCuts = listOf(
                CutEntity(
                    cutNumber = "1001",
                    modelName = "تيشيرت بولو شبابي كلاسيك",
                    fabricType = "قطن بيكيه 100%",
                    color = "كحلي ملكي",
                    season = "صيف 2026",
                    targetPieces = 1200,
                    notes = "تجهيز تطريز الصدر"
                ),
                CutEntity(
                    cutNumber = "1002",
                    modelName = "بنطلون جبردين كاجوال سليم",
                    fabricType = "جبردين ليكرا تركي",
                    color = "بيج غامق",
                    season = "صيف 2026",
                    targetPieces = 800,
                    notes = "مراحل خياطة وجيوب جانبية"
                ),
                CutEntity(
                    cutNumber = "1003",
                    modelName = "هودي رياضي كابيشو بجيوب",
                    fabricType = "ميلتون قطن مبطن",
                    color = "رمادي ميلانج",
                    season = "شتاء 2026",
                    targetPieces = 1500,
                    notes = "طباعة حرارية ديجيتال"
                ),
                CutEntity(
                    cutNumber = "1004",
                    modelName = "قميص كتان أوفر سايز",
                    fabricType = "كتان مغسول ناعم",
                    color = "أبيض سكري",
                    season = "صيف 2026",
                    targetPieces = 950,
                    notes = "أزرار صدف طبيعي"
                ),
                CutEntity(
                    cutNumber = "1005",
                    modelName = "سويت شيرت راوند مطبوع",
                    fabricType = "ميلتون وبري تقيل",
                    color = "أسود فاحم",
                    season = "شتاء 2026",
                    targetPieces = 600,
                    notes = "طلبية خاصة للتوزيع"
                )
            )
            cutDao.insertCuts(sampleCuts)

            val sampleTransactions = listOf(
                StockTransactionEntity(
                    cutNumber = "1001",
                    modelName = "تيشيرت بولو شبابي كلاسيك",
                    transactionType = "IN",
                    category = "استلام من صالة القص",
                    quantityPieces = 1000,
                    quantityRolls = 14,
                    weightKg = 310.5,
                    destination = "صالة القص الرئيسية",
                    responsiblePerson = "أحمد رضوان (أمين مخزن)",
                    receiverName = "محمود حسن",
                    documentNumber = "وارد-001/2026",
                    stage = "قص",
                    fabricType = "قطن بيكيه 100%",
                    color = "كحلي ملكي",
                    dateStr = "2026-09-01",
                    notes = "الفرشة مطابقة للماركر وعدد الطبقات 100"
                ),
                StockTransactionEntity(
                    cutNumber = "1001",
                    modelName = "تيشيرت بولو شبابي كلاسيك",
                    transactionType = "OUT",
                    category = "صرف لمرحلة التطريز",
                    quantityPieces = 300,
                    quantityRolls = 4,
                    weightKg = 90.0,
                    destination = "مغسلة ومطرزات الإخوة",
                    responsiblePerson = "أحمد رضوان",
                    receiverName = "إبراهيم خليل (مشرف التطريز)",
                    documentNumber = "صرف-012/2026",
                    stage = "تطريز",
                    fabricType = "قطن بيكيه 100%",
                    color = "كحلي ملكي",
                    dateStr = "2026-09-03",
                    notes = "صرف أجزاء الصدر الأمامي فقط"
                ),
                StockTransactionEntity(
                    cutNumber = "1001",
                    modelName = "تيشيرت بولو شبابي كلاسيك",
                    transactionType = "OUT",
                    category = "صرف لخط التجميع والخياطة",
                    quantityPieces = 200,
                    quantityRolls = 3,
                    weightKg = 60.0,
                    destination = "خط إنتاج رقم 1",
                    responsiblePerson = "محمود السيد",
                    receiverName = "أ. كمال متولي",
                    documentNumber = "صرف-015/2026",
                    stage = "خياطة",
                    fabricType = "قطن بيكيه 100%",
                    color = "كحلي ملكي",
                    dateStr = "2026-09-05",
                    notes = "تسليم مراحل التجميع"
                ),
                StockTransactionEntity(
                    cutNumber = "1002",
                    modelName = "بنطلون جبردين كاجوال سليم",
                    transactionType = "IN",
                    category = "استلام من صالة القص",
                    quantityPieces = 800,
                    quantityRolls = 18,
                    weightKg = 420.0,
                    destination = "صالة القص",
                    responsiblePerson = "أحمد رضوان",
                    receiverName = "أحمد رضوان",
                    documentNumber = "وارد-002/2026",
                    stage = "قص",
                    fabricType = "جبردين ليكرا تركي",
                    color = "بيج غامق",
                    dateStr = "2026-09-02",
                    notes = "استلام كامل الفرشة"
                ),
                StockTransactionEntity(
                    cutNumber = "1002",
                    modelName = "بنطلون جبردين كاجوال سليم",
                    transactionType = "OUT",
                    category = "صرف لورشة البنطلون",
                    quantityPieces = 350,
                    quantityRolls = 8,
                    weightKg = 185.0,
                    destination = "ورشة الفتح للجينز",
                    responsiblePerson = "محمود السيد",
                    receiverName = "المعلم فوزي",
                    documentNumber = "صرف-018/2026",
                    stage = "خياطة",
                    fabricType = "جبردين ليكرا تركي",
                    color = "بيج غامق",
                    dateStr = "2026-09-06",
                    notes = "شامل الجيوب والبطانات"
                ),
                StockTransactionEntity(
                    cutNumber = "1003",
                    modelName = "هودي رياضي كابيشو بجيوب",
                    transactionType = "IN",
                    category = "استلام من صالة القص",
                    quantityPieces = 1500,
                    quantityRolls = 25,
                    weightKg = 680.0,
                    destination = "مقصدار كريم حسن",
                    responsiblePerson = "أحمد رضوان",
                    receiverName = "أحمد رضوان",
                    documentNumber = "وارد-003/2026",
                    stage = "قص",
                    fabricType = "ميلتون قطن مبطن",
                    color = "رمادي ميلانج",
                    dateStr = "2026-09-04",
                    notes = "استلام أجزاء الهودي والريب والجيوب"
                ),
                StockTransactionEntity(
                    cutNumber = "1003",
                    modelName = "هودي رياضي كابيشو بجيوب",
                    transactionType = "OUT",
                    category = "صرف لمرحلة الطباعة",
                    quantityPieces = 600,
                    quantityRolls = 10,
                    weightKg = 270.0,
                    destination = "مطبعة الدلتا ديجيتال",
                    responsiblePerson = "أحمد رضوان",
                    receiverName = "كابتن وحيد",
                    documentNumber = "صرف-022/2026",
                    stage = "طباعة",
                    fabricType = "ميلتون قطن مبطن",
                    color = "رمادي ميلانج",
                    dateStr = "2026-09-07",
                    notes = "صدر الهودي للطباعة الديجيتال"
                ),
                StockTransactionEntity(
                    cutNumber = "1004",
                    modelName = "قميص كتان أوفر سايز",
                    transactionType = "IN",
                    category = "استلام من صالة القص",
                    quantityPieces = 950,
                    quantityRolls = 12,
                    weightKg = 240.0,
                    destination = "صالة القص",
                    responsiblePerson = "أحمد رضوان",
                    receiverName = "أحمد رضوان",
                    documentNumber = "وارد-004/2026",
                    stage = "قص",
                    fabricType = "كتان مغسول ناعم",
                    color = "أبيض سكري",
                    dateStr = "2026-09-08",
                    notes = "قماش كتان معالج"
                ),
                StockTransactionEntity(
                    cutNumber = "1005",
                    modelName = "سويت شيرت راوند مطبوع",
                    transactionType = "IN",
                    category = "استلام من صالة القص",
                    quantityPieces = 600,
                    quantityRolls = 9,
                    weightKg = 260.0,
                    destination = "صالة القص",
                    responsiblePerson = "أحمد رضوان",
                    receiverName = "أحمد رضوان",
                    documentNumber = "وارد-005/2026",
                    stage = "قص",
                    fabricType = "ميلتون وبري تقيل",
                    color = "أسود فاحم",
                    dateStr = "2026-09-09",
                    notes = "جاهز للصرف المباشر"
                )
            )
            transactionDao.insertTransactions(sampleTransactions)

            // Sample Audits
            val sampleAudits = listOf(
                AuditEntity(
                    cutNumber = "1001",
                    modelName = "تيشيرت بولو شبابي كلاسيك",
                    bookBalance = 500,
                    actualBalance = 500,
                    difference = 0,
                    auditDate = "2026-09-08",
                    auditorName = "لجنة الجرد السنوي",
                    notes = "مطابق دفترياً وفعلياً بالكامل",
                    isApproved = true
                ),
                AuditEntity(
                    cutNumber = "1002",
                    modelName = "بنطلون جبردين كاجوال سليم",
                    bookBalance = 450,
                    actualBalance = 448,
                    difference = -2,
                    auditDate = "2026-09-08",
                    auditorName = "لجنة الجرد",
                    notes = "عجز قطعتين في مرحلة الفرز",
                    isApproved = false
                )
            )
            sampleAudits.forEach { auditDao.insertAudit(it) }
        }
    }
}
