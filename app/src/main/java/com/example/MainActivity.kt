package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.entity.CutEntity
import com.example.data.entity.StockTransactionEntity
import com.example.data.repository.InventoryRepository
import com.example.ui.AppTab
import com.example.ui.InventoryViewModel
import com.example.ui.InventoryViewModelFactory
import com.example.ui.components.AddCutDialog
import com.example.ui.components.AddIncomingScreen
import com.example.ui.components.AddOutgoingScreen
import com.example.ui.components.AuditInventoryView
import com.example.ui.components.CutsRegistryView
import com.example.ui.components.DashboardView
import com.example.ui.components.DesktopSidebar
import com.example.ui.components.ExcelGridView
import com.example.ui.components.ExcelImportPreviewDialog
import com.example.ui.components.ReportsAndExportView
import com.example.ui.components.SettingsAndBackupView
import com.example.ui.components.TransactionsView
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = InventoryRepository(database.cutDao(), database.stockTransactionDao(), database.auditDao())

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: InventoryViewModel = viewModel(
                        factory = InventoryViewModelFactory(repository)
                    )
                    WarehouseAppScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseAppScreen(viewModel: InventoryViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val filteredCutsBalances by viewModel.filteredCutsBalances.collectAsStateWithLifecycle()
    val cutsBalances by viewModel.cutsBalances.collectAsStateWithLifecycle()
    val allCuts by viewModel.cuts.collectAsStateWithLifecycle()
    val allAudits by viewModel.allAudits.collectAsStateWithLifecycle()
    val dateRangeFilter by viewModel.dateRangeFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val importPreviewRows by viewModel.importPreviewRows.collectAsStateWithLifecycle()

    var isSearchActive by remember { mutableStateOf(false) }
    var showAddIncomingDialog by remember { mutableStateOf(false) }
    var showAddOutgoingDialog by remember { mutableStateOf(false) }
    var showAddCutDialog by remember { mutableStateOf(false) }
    var editingCut by remember { mutableStateOf<CutEntity?>(null) }
    var cutToDelete by remember { mutableStateOf<CutEntity?>(null) }
    var transactionToDelete by remember { mutableStateOf<StockTransactionEntity?>(null) }

    // File picker launcher for CSV / Excel
    val importCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.loadCsvForPreview(context, uri)
        }
    }

    // React to user and error messages via SnackBar
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarHostState.showSnackbar("⚠️ $msg")
            viewModel.clearErrorMessage()
        }
    }

    // Responsive Desktop / Laptop vs Mobile check
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktopLayout = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Surface(
                    color = ExcelGreenDark,
                    tonalElevation = 4.dp
                ) {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = Color(0xFF1B5E20),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.TableChart,
                                            contentDescription = "شعار إكسل",
                                            tint = Color(0xFF81C784),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "مخازن القصات • Excel ERP",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        text = "إدارة الوارد والمنصرف والأرصدة الدفترية ومطابقة الجرد",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Top Action Icons
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showAddIncomingDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = StockInColor),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("+ وارد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showAddOutgoingDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = StockOutColor),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("- صرف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = {
                                        isSearchActive = !isSearchActive
                                        if (!isSearchActive) viewModel.setSearchQuery("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                        contentDescription = "بحث",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.exportTransactionsExcel(context) }
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "مشاركة شيت إكسل",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Search input banner
                        AnimatedVisibility(visible = isSearchActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.setSearchQuery(it) },
                                    placeholder = { Text("بحث برقم القصة، الموديل، الخامة، أو الورشة...", fontSize = 13.sp) },
                                    singleLine = true,
                                    trailingIcon = {
                                        if (searchQuery.isNotBlank()) {
                                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Color.White)
                                            }
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                        cursorColor = Color.White,
                                        focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("search_input_field"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Only show bottom navigation on mobile/small screens
                if (!isDesktopLayout) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.navigationBarsPadding(),
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == AppTab.DASHBOARD,
                            onClick = { viewModel.setTab(AppTab.DASHBOARD) },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text("الرئيسية", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentTab == AppTab.CUTS,
                            onClick = { viewModel.setTab(AppTab.CUTS) },
                            icon = { Icon(Icons.Default.ContentCut, contentDescription = null) },
                            label = { Text("القصات", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentTab == AppTab.TRANSACTIONS,
                            onClick = { viewModel.setTab(AppTab.TRANSACTIONS) },
                            icon = { Icon(Icons.Default.SwapVert, contentDescription = null) },
                            label = { Text("الحركات", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentTab == AppTab.EXCEL_SHEET,
                            onClick = { viewModel.setTab(AppTab.EXCEL_SHEET) },
                            icon = { Icon(Icons.Default.TableChart, contentDescription = null) },
                            label = { Text("إكسل", fontSize = 10.sp) }
                        )
                        NavigationBarItem(
                            selected = currentTab == AppTab.REPORTS,
                            onClick = { viewModel.setTab(AppTab.REPORTS) },
                            icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                            label = { Text("التقارير", fontSize = 10.sp) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Desktop Right Sidebar (in RTL, first child in Row is on the RIGHT!)
                if (isDesktopLayout) {
                    DesktopSidebar(
                        currentTab = currentTab,
                        onTabSelect = { viewModel.setTab(it) },
                        onAddIncomingClick = { showAddIncomingDialog = true },
                        onAddOutgoingClick = { showAddOutgoingDialog = true },
                        onExportExcelClick = { viewModel.exportTransactionsExcel(context) },
                        onImportExcelClick = { importCsvLauncher.launch("text/*") },
                        stats = stats
                    )
                }

                // Main Central Workspace
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentTab) {
                        AppTab.DASHBOARD -> {
                            DashboardView(
                                stats = stats,
                                recentTransactions = allTransactions,
                                cutBalances = cutsBalances,
                                dateRangeFilter = dateRangeFilter,
                                onDateRangeChange = { viewModel.setDateRangeFilter(it) },
                                onAddIncomingClick = { showAddIncomingDialog = true },
                                onAddOutgoingClick = { showAddOutgoingDialog = true },
                                onViewAllTransactions = { viewModel.setTab(AppTab.TRANSACTIONS) }
                            )
                        }

                        AppTab.CUTS -> {
                            CutsRegistryView(
                                cutBalances = filteredCutsBalances,
                                onAddCutClick = { showAddCutDialog = true },
                                onEditCutClick = { editingCut = it },
                                onDeleteCutClick = { cutToDelete = it }
                            )
                        }

                        AppTab.TRANSACTIONS -> {
                            TransactionsView(
                                transactions = filteredTransactions,
                                onAddIncomingClick = { showAddIncomingDialog = true },
                                onAddOutgoingClick = { showAddOutgoingDialog = true },
                                onDeleteTransaction = { transactionToDelete = it }
                            )
                        }

                        AppTab.EXCEL_SHEET -> {
                            ExcelGridView(
                                transactions = filteredTransactions,
                                cutBalances = filteredCutsBalances,
                                onExportTransactions = { viewModel.exportTransactionsExcel(context) },
                                onExportBalances = { viewModel.exportCutsExcel(context) },
                                onAddTransactionClick = { showAddIncomingDialog = true },
                                onDeleteTransaction = { transactionToDelete = it }
                            )
                        }

                        AppTab.REPORTS -> {
                            ReportsAndExportView(
                                stats = stats,
                                transactions = allTransactions,
                                cutBalances = cutsBalances,
                                onExportTransactionsExcel = { viewModel.exportTransactionsExcel(context) },
                                onExportCutsExcel = { viewModel.exportCutsExcel(context) },
                                onExportAuditExcel = { viewModel.exportAuditExcel(context) },
                                onImportCsvClick = { importCsvLauncher.launch("text/*") }
                            )
                        }

                        AppTab.AUDIT -> {
                            AuditInventoryView(
                                cutBalances = cutsBalances,
                                audits = allAudits,
                                onAddAuditRecord = { cutNum, model, actual, notes, auditor ->
                                    viewModel.addAuditRecord(cutNum, model, actual, notes, auditor)
                                },
                                onApproveAudit = { viewModel.approveAuditRecord(it) },
                                onDeleteAudit = { viewModel.deleteAuditRecord(it) },
                                onExportAuditExcel = { viewModel.exportAuditExcel(context) }
                            )
                        }

                        AppTab.SETTINGS -> {
                            SettingsAndBackupView(
                                stats = stats,
                                onExportBackupClick = { viewModel.exportTransactionsExcel(context) },
                                onImportBackupClick = { importCsvLauncher.launch("text/*") },
                                onShowMessage = { viewModel.showMessage(it) }
                            )
                        }

                        AppTab.ADD_INCOMING -> {
                            // Direct switch to dashboard with dialog open
                            LaunchedEffect(Unit) {
                                showAddIncomingDialog = true
                                viewModel.setTab(AppTab.DASHBOARD)
                            }
                        }

                        AppTab.ADD_OUTGOING -> {
                            // Direct switch to dashboard with dialog open
                            LaunchedEffect(Unit) {
                                showAddOutgoingDialog = true
                                viewModel.setTab(AppTab.DASHBOARD)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Incoming
    if (showAddIncomingDialog) {
        AddIncomingScreen(
            availableCuts = allCuts,
            onDismiss = { showAddIncomingDialog = false },
            onSave = { cutNum, model, fabric, color, pieces, rolls, weight, source, resp, date, doc, notes ->
                viewModel.addIncomingTransaction(
                    cutNumber = cutNum,
                    modelName = model,
                    fabricType = fabric,
                    color = color,
                    pieces = pieces,
                    rolls = rolls,
                    weightKg = weight,
                    source = source,
                    responsible = resp,
                    dateStr = date,
                    docNumber = doc,
                    notes = notes
                )
            }
        )
    }

    // Modal: Add Outgoing (Strict Balance Checking)
    if (showAddOutgoingDialog) {
        AddOutgoingScreen(
            availableCuts = cutsBalances,
            onDismiss = { showAddOutgoingDialog = false },
            onSave = { cutNum, model, pieces, rolls, weight, dest, stage, receiver, keeper, date, doc, notes ->
                viewModel.addOutgoingTransaction(
                    cutNumber = cutNum,
                    modelName = model,
                    pieces = pieces,
                    rolls = rolls,
                    weightKg = weight,
                    destinationWorkshop = dest,
                    stage = stage,
                    receiver = receiver,
                    storekeeper = keeper,
                    dateStr = date,
                    docNumber = doc,
                    notes = notes
                )
            }
        )
    }

    // Modal: Add Cut
    if (showAddCutDialog) {
        AddCutDialog(
            initialCut = null,
            onDismiss = { showAddCutDialog = false },
            onConfirm = { cutNum, model, fabric, color, season, target, notes ->
                viewModel.addCut(
                    cutNumber = cutNum,
                    modelName = model,
                    fabricType = fabric,
                    color = color,
                    season = season,
                    targetPieces = target,
                    notes = notes
                )
            }
        )
    }

    // Modal: Edit Cut
    editingCut?.let { cut ->
        AddCutDialog(
            initialCut = cut,
            onDismiss = { editingCut = null },
            onConfirm = { cutNum, model, fabric, color, season, target, notes ->
                viewModel.updateCut(
                    cut.copy(
                        cutNumber = cutNum,
                        modelName = model,
                        fabricType = fabric,
                        color = color,
                        season = season,
                        targetPieces = target,
                        notes = notes
                    )
                )
                editingCut = null
            }
        )
    }

    // Modal: Confirm Delete Cut
    cutToDelete?.let { cut ->
        AlertDialog(
            onDismissRequest = { cutToDelete = null },
            title = { Text("تأكيد حذف القصة") },
            text = { Text("هل أنت متأكد من حذف القصة (${cut.cutNumber} - ${cut.modelName}) من سجل المخزن؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCut(cut)
                        cutToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockOutColor)
                ) {
                    Text("نعم، احذف")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { cutToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Modal: Confirm Delete Transaction
    transactionToDelete?.let { trx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("تأكيد حذف حركة المخزن") },
            text = {
                Text("هل أنت متأكد من حذف حركة (${if (trx.isIncoming) "الوارد" else "المنصرف"}) للقصة (${trx.cutNumber}) بعدد ${trx.quantityPieces} قطعة؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(trx)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StockOutColor)
                ) {
                    Text("نعم، احذف")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { transactionToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Modal: Excel Import Preview & Validation
    importPreviewRows?.let { rows ->
        ExcelImportPreviewDialog(
            previewRows = rows,
            onDismiss = { viewModel.cancelImport() },
            onConfirmImport = { viewModel.confirmImport() }
        )
    }
}
