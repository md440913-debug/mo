package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.AuditEntity
import com.example.data.entity.CutEntity
import com.example.data.entity.StockTransactionEntity
import com.example.data.model.CutBalance
import com.example.data.model.ImportPreviewRow
import com.example.data.model.WarehouseStats
import com.example.data.repository.InventoryRepository
import com.example.utils.ExcelHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String, val iconName: String) {
    DASHBOARD("الرئيسية", "home"),
    CUTS("دليل القصات", "inventory"),
    TRANSACTIONS("حركات المخزن", "swap_vert"),
    ADD_INCOMING("إضافة وارد", "add_circle"),
    ADD_OUTGOING("إضافة صرف", "remove_circle"),
    REPORTS("التقارير", "assessment"),
    AUDIT("الجرد", "fact_check"),
    EXCEL_SHEET("شيت المخزن", "table_chart"),
    SETTINGS("الإعدادات", "settings")
}

enum class DateRangeFilter(val label: String) {
    ALL("كل الفترات"),
    TODAY("اليوم"),
    THIS_WEEK("هذا الأسبوع"),
    THIS_MONTH("هذا الشهر")
}

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _dateRangeFilter = MutableStateFlow(DateRangeFilter.ALL)
    val dateRangeFilter: StateFlow<DateRangeFilter> = _dateRangeFilter.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Import Preview State
    private val _importPreviewRows = MutableStateFlow<List<ImportPreviewRow>?>(null)
    val importPreviewRows: StateFlow<List<ImportPreviewRow>?> = _importPreviewRows.asStateFlow()

    val cuts: StateFlow<List<CutEntity>> = repository.allCuts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cutsBalances: StateFlow<List<CutBalance>> = repository.cutsWithBalances.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTransactions: StateFlow<List<StockTransactionEntity>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAudits: StateFlow<List<AuditEntity>> = repository.allAudits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<WarehouseStats> = repository.warehouseStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WarehouseStats()
    )

    // Filtered transactions for search & date filter
    val filteredTransactions: StateFlow<List<StockTransactionEntity>> = combine(
        repository.allTransactions,
        _searchQuery,
        _dateRangeFilter
    ) { list, query, dateRange ->
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val thisMonthStr = SimpleDateFormat("yyyy-MM", Locale.ENGLISH).format(Date())

        list.filter { item ->
            val matchesDate = when (dateRange) {
                DateRangeFilter.ALL -> true
                DateRangeFilter.TODAY -> item.dateStr.startsWith(todayStr)
                DateRangeFilter.THIS_WEEK -> true
                DateRangeFilter.THIS_MONTH -> item.dateStr.startsWith(thisMonthStr)
            }
            val matchesQuery = if (query.isBlank()) true else {
                item.cutNumber.contains(query, ignoreCase = true) ||
                        item.modelName.contains(query, ignoreCase = true) ||
                        item.category.contains(query, ignoreCase = true) ||
                        item.destination.contains(query, ignoreCase = true) ||
                        item.responsiblePerson.contains(query, ignoreCase = true) ||
                        item.documentNumber.contains(query, ignoreCase = true) ||
                        item.dateStr.contains(query, ignoreCase = true)
            }
            matchesDate && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered cuts balances for search
    val filteredCutsBalances: StateFlow<List<CutBalance>> = combine(
        repository.cutsWithBalances,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list else {
            list.filter { item ->
                item.cut.cutNumber.contains(query, ignoreCase = true) ||
                        item.cut.modelName.contains(query, ignoreCase = true) ||
                        item.cut.fabricType.contains(query, ignoreCase = true) ||
                        item.cut.color.contains(query, ignoreCase = true) ||
                        item.cut.season.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDateRangeFilter(filter: DateRangeFilter) {
        _dateRangeFilter.value = filter
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun addCut(
        cutNumber: String,
        modelName: String,
        fabricType: String,
        color: String,
        season: String,
        targetPieces: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val cut = CutEntity(
                cutNumber = cutNumber.trim(),
                modelName = modelName.trim(),
                fabricType = fabricType.trim(),
                color = color.trim(),
                season = season.trim(),
                targetPieces = targetPieces,
                notes = notes.trim()
            )
            repository.insertCut(cut)
            _userMessage.value = "تم تسجيل القصة ($cutNumber) بنجاح"
        }
    }

    fun updateCut(cut: CutEntity) {
        viewModelScope.launch {
            repository.updateCut(cut)
            _userMessage.value = "تم تحديث بيانات القصة بنجاح"
        }
    }

    fun deleteCut(cut: CutEntity) {
        viewModelScope.launch {
            repository.deleteCut(cut)
            _userMessage.value = "تم حذف القصة (${cut.cutNumber})"
        }
    }

    /**
     * Records an Incoming (وارد) movement.
     * Increases inventory balance automatically.
     */
    fun addIncomingTransaction(
        cutNumber: String,
        modelName: String,
        fabricType: String,
        color: String,
        pieces: Int,
        rolls: Int,
        weightKg: Double,
        source: String,
        responsible: String,
        dateStr: String,
        docNumber: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (pieces <= 0) {
                _errorMessage.value = "يجب أن تكون كمية الوارد أكبر من صفر"
                return@launch
            }

            val date = if (dateStr.isBlank()) {
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
            } else dateStr

            val resolvedModel = if (modelName.isBlank()) {
                repository.getCutByNumber(cutNumber)?.modelName ?: "بدون موديل"
            } else modelName

            val transaction = StockTransactionEntity(
                cutNumber = cutNumber.trim(),
                modelName = resolvedModel.trim(),
                transactionType = "IN",
                category = "استلام وارد",
                stage = "قص",
                fabricType = fabricType.trim(),
                color = color.trim(),
                quantityPieces = pieces,
                quantityRolls = rolls,
                weightKg = weightKg,
                destination = source.trim(),
                responsiblePerson = responsible.trim(),
                documentNumber = docNumber.trim(),
                dateStr = date,
                notes = notes.trim()
            )
            repository.insertTransaction(transaction)

            // If cut doesn't exist yet, automatically add it to cuts catalog!
            val existingCut = repository.getCutByNumber(cutNumber)
            if (existingCut == null) {
                repository.insertCut(
                    CutEntity(
                        cutNumber = cutNumber.trim(),
                        modelName = resolvedModel.trim(),
                        fabricType = fabricType.trim(),
                        color = color.trim(),
                        season = "صيف 2026",
                        targetPieces = pieces,
                        notes = "تم إنشاؤها تلقائياً مع أول حركة وارد"
                    )
                )
            }

            _userMessage.value = "تمت إضافة وارد للقصة ($cutNumber) بعدد $pieces قطعة بنجاح"
            _currentTab.value = AppTab.TRANSACTIONS
        }
    }

    /**
     * Records an Outgoing (صرف) movement.
     * STRICT VALIDATION (Rule #12):
     * If requestedPieces > availableBalance, rejects the operation with error message!
     */
    fun addOutgoingTransaction(
        cutNumber: String,
        modelName: String,
        pieces: Int,
        rolls: Int,
        weightKg: Double,
        destinationWorkshop: String,
        stage: String,
        receiver: String,
        storekeeper: String,
        dateStr: String,
        docNumber: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (pieces <= 0) {
                _errorMessage.value = "يجب أن تكون كمية الصرف أكبر من صفر"
                return@launch
            }

            val available = repository.getCutAvailableBalance(cutNumber)
            if (pieces > available) {
                _errorMessage.value = "لا يمكن تنفيذ الصرف لأن الكمية المطلوبة ($pieces قطعة) أكبر من الرصيد المتاح ($available قطعة)."
                return@launch
            }

            val date = if (dateStr.isBlank()) {
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
            } else dateStr

            val resolvedModel = if (modelName.isBlank()) {
                repository.getCutByNumber(cutNumber)?.modelName ?: "بدون موديل"
            } else modelName

            val transaction = StockTransactionEntity(
                cutNumber = cutNumber.trim(),
                modelName = resolvedModel.trim(),
                transactionType = "OUT",
                category = "صرف تشغيل ($stage)",
                stage = stage.trim(),
                quantityPieces = pieces,
                quantityRolls = rolls,
                weightKg = weightKg,
                destination = destinationWorkshop.trim(),
                responsiblePerson = storekeeper.trim(),
                receiverName = receiver.trim(),
                documentNumber = docNumber.trim(),
                dateStr = date,
                notes = notes.trim()
            )
            repository.insertTransaction(transaction)

            val remainingAfter = available - pieces
            _userMessage.value = "تم صرف $pieces قطعة للقصة ($cutNumber). الرصيد المتبقي الآن: $remainingAfter قطعة"
            _currentTab.value = AppTab.TRANSACTIONS
        }
    }

    fun updateTransaction(transaction: StockTransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            _userMessage.value = "تم تحديث الحركة بنجاح"
        }
    }

    fun deleteTransaction(transaction: StockTransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _userMessage.value = "تم حذف حركة المخزن"
        }
    }

    // --- Audit / الجرد ---
    fun addAuditRecord(
        cutNumber: String,
        modelName: String,
        actualBalance: Int,
        notes: String,
        auditorName: String
    ) {
        viewModelScope.launch {
            val bookBalance = repository.getCutAvailableBalance(cutNumber)
            val diff = actualBalance - bookBalance
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

            val audit = AuditEntity(
                cutNumber = cutNumber.trim(),
                modelName = modelName.trim(),
                bookBalance = bookBalance,
                actualBalance = actualBalance,
                difference = diff,
                auditDate = date,
                auditorName = auditorName.trim(),
                notes = notes.trim(),
                isApproved = false
            )
            repository.insertAudit(audit)
            _userMessage.value = "تم تسجيل محضر جرد للقصة ($cutNumber) بفارق ($diff قطعة)"
        }
    }

    fun approveAuditRecord(audit: AuditEntity) {
        viewModelScope.launch {
            repository.updateAudit(audit.copy(isApproved = true))
            _userMessage.value = "تم اعتماد نتيجة الجرد للقصة (${audit.cutNumber})"
        }
    }

    fun deleteAuditRecord(audit: AuditEntity) {
        viewModelScope.launch {
            repository.deleteAudit(audit)
            _userMessage.value = "تم حذف سجل الجرد"
        }
    }

    // --- Excel Exports ---
    fun exportTransactionsExcel(context: Context) {
        viewModelScope.launch {
            val list = allTransactions.value
            if (list.isEmpty()) {
                _userMessage.value = "لا توجد حركات مخزن لتصديرها"
                return@launch
            }
            val csv = ExcelHelper.createTransactionsExcelSheet(list)
            val fileName = "شيت_حركات_المخزن_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(Date())}.csv"
            val uri = ExcelHelper.writeCsvToFile(context, fileName, csv)
            if (uri != null) {
                ExcelHelper.shareFile(context, uri, "شيت حركات المخزن Excel")
                _userMessage.value = "تم تجهيز ملف إكسل للمشاركة والفتح"
            } else {
                _errorMessage.value = "تعذر إنشاء ملف إكسل"
            }
        }
    }

    fun exportCutsExcel(context: Context) {
        viewModelScope.launch {
            val list = cutsBalances.value
            if (list.isEmpty()) {
                _userMessage.value = "لا توجد قصات لتصديرها"
                return@launch
            }
            val csv = ExcelHelper.createCutsBalanceExcelSheet(list)
            val fileName = "دليل_أرصدة_القصات_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(Date())}.csv"
            val uri = ExcelHelper.writeCsvToFile(context, fileName, csv)
            if (uri != null) {
                ExcelHelper.shareFile(context, uri, "دليل وأرصدة القصات Excel")
                _userMessage.value = "تم تجهيز ملف أرصدة القصات للمشاركة"
            } else {
                _errorMessage.value = "تعذر إنشاء ملف إكسل"
            }
        }
    }

    fun exportAuditExcel(context: Context) {
        viewModelScope.launch {
            val list = allAudits.value
            if (list.isEmpty()) {
                _userMessage.value = "لا توجد سجلات جرد لتصديرها"
                return@launch
            }
            val csv = ExcelHelper.createAuditExcelSheet(list)
            val fileName = "كشف_جرد_المخزن_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(Date())}.csv"
            val uri = ExcelHelper.writeCsvToFile(context, fileName, csv)
            if (uri != null) {
                ExcelHelper.shareFile(context, uri, "كشف جرد المخزن Excel")
                _userMessage.value = "تم تجهيز كشف الجرد Excel"
            } else {
                _errorMessage.value = "تعذر إنشاء ملف إكسل"
            }
        }
    }

    // --- Excel Import with Preview & Validation ---
    fun loadCsvForPreview(context: Context, uri: Uri) {
        viewModelScope.launch {
            val rows = ExcelHelper.parseAndValidateCsv(context, uri)
            if (rows.isEmpty()) {
                _errorMessage.value = "الملف المختار فارغ أو لا يحتوي على صفوف بيانات صالحة"
            } else {
                _importPreviewRows.value = rows
            }
        }
    }

    fun cancelImport() {
        _importPreviewRows.value = null
    }

    fun confirmImport() {
        viewModelScope.launch {
            val preview = _importPreviewRows.value ?: return@launch
            val validRows = preview.filter { it.isValid }
            if (validRows.isEmpty()) {
                _errorMessage.value = "لا توجد صفوف صالحة للاستيراد"
                return@launch
            }

            val transactionsToInsert = validRows.map { r ->
                StockTransactionEntity(
                    cutNumber = r.cutNumber,
                    modelName = r.modelName,
                    transactionType = r.type,
                    category = if (r.type == "IN") "استلام وارد مستورد" else "صرف مستورد",
                    stage = r.stage,
                    quantityPieces = r.pieces,
                    quantityRolls = r.rolls,
                    weightKg = r.weight,
                    destination = r.destination,
                    responsiblePerson = r.responsible,
                    documentNumber = r.docNumber,
                    dateStr = r.dateStr,
                    notes = r.notes
                )
            }
            repository.insertTransactions(transactionsToInsert)
            _userMessage.value = "تم استيراد ${validRows.size} حركة مخزن بنجاح إلى شيت إكسل"
            _importPreviewRows.value = null
        }
    }
}

class InventoryViewModelFactory(
    private val repository: InventoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
