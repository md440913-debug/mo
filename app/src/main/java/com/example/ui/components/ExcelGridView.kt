package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.StockTransactionEntity
import com.example.data.model.CutBalance
import com.example.ui.theme.ExcelBorder
import com.example.ui.theme.ExcelCellAlt
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.ExcelHeaderBg
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInBg
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutBg
import com.example.ui.theme.StockOutColor

enum class SortColumn {
    CUT_NUMBER, PIECES, DATE
}

@Composable
fun ExcelGridView(
    transactions: List<StockTransactionEntity>,
    cutBalances: List<CutBalance>,
    onExportTransactions: () -> Unit,
    onExportBalances: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onDeleteTransaction: (StockTransactionEntity) -> Unit,
    onEditTransaction: (StockTransactionEntity) -> Unit = {}
) {
    var selectedSheetTab by remember { mutableStateOf(0) }
    var filterType by remember { mutableStateOf("ALL") }
    var sortColumn by remember { mutableStateOf(SortColumn.DATE) }
    var sortAscending by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val horizontalScrollState = rememberScrollState()

    // Filtered & Sorted Transactions
    val displayedTransactions = remember(transactions, filterType, sortColumn, sortAscending, searchQuery) {
        transactions.filter { item ->
            val matchesType = when (filterType) {
                "IN" -> item.isIncoming
                "OUT" -> !item.isIncoming
                else -> true
            }
            val matchesQuery = if (searchQuery.isBlank()) true else {
                item.cutNumber.contains(searchQuery, ignoreCase = true) ||
                        item.modelName.contains(searchQuery, ignoreCase = true) ||
                        item.destination.contains(searchQuery, ignoreCase = true) ||
                        item.stage.contains(searchQuery, ignoreCase = true) ||
                        item.documentNumber.contains(searchQuery, ignoreCase = true)
            }
            matchesType && matchesQuery
        }.let { list ->
            when (sortColumn) {
                SortColumn.CUT_NUMBER -> if (sortAscending) list.sortedBy { it.cutNumber } else list.sortedByDescending { it.cutNumber }
                SortColumn.PIECES -> if (sortAscending) list.sortedBy { it.quantityPieces } else list.sortedByDescending { it.quantityPieces }
                SortColumn.DATE -> if (sortAscending) list.sortedBy { it.dateStr } else list.sortedByDescending { it.dateStr }
            }
        }
    }

    // Totals
    val totalIn = displayedTransactions.filter { it.isIncoming }.sumOf { it.quantityPieces }
    val totalOut = displayedTransactions.filter { !it.isIncoming }.sumOf { it.quantityPieces }
    val netBalance = totalIn - totalOut

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("excel_grid_view")
    ) {
        // Excel Top Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExcelGreenDark, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFF107C41),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "XLSX",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "شيت المخزن التفاعلي (Live Excel Grid)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddTransactionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("excel_toolbar_add_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ إضافة صف حركة", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (selectedSheetTab == 0) onExportTransactions() else onExportBalances()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("excel_toolbar_export_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("تصدير Excel", fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tabs: Transactions Sheet vs Cuts Balance Sheet
        TabRow(
            selectedTabIndex = selectedSheetTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ExcelGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSheetTab]),
                    color = ExcelGreenPrimary
                )
            }
        ) {
            Tab(
                selected = selectedSheetTab == 0,
                onClick = { selectedSheetTab = 0 },
                text = { Text("ورقة 1: سجل حركات المخزن التفصيلي (A - L)", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSheetTab == 1,
                onClick = { selectedSheetTab = 1 },
                text = { Text("ورقة 2: كشف أرصدة وجرد القصات", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Search & Filter Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث في خلايا الشيت...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f).height(46.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = filterType == "ALL",
                    onClick = { filterType = "ALL" },
                    label = { Text("الكل (${transactions.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterType == "IN",
                    onClick = { filterType = "IN" },
                    label = { Text("الوارد فقط", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = filterType == "OUT",
                    onClick = { filterType = "OUT" },
                    label = { Text("المنصرف فقط", fontSize = 11.sp) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Excel Formula Bar (fx)
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().border(0.5.dp, ExcelBorder, RoundedCornerShape(4.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("fx", fontWeight = FontWeight.ExtraBold, color = ExcelGreenPrimary, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Divider(modifier = Modifier.height(16.dp).width(1.dp), color = Color.LightGray)
                Text(
                    text = "=SUM(الوارد: $totalIn ق) - SUM(المنصرف: $totalOut ق) = صافي رصيد المخزن ($netBalance ق)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Slate700
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Full Interactive Spreadsheet Table (Columns A to L) with Horizontal Scroll & Freeze Header
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Freeze Header (Columns A to L)
                Row(
                    modifier = Modifier
                        .background(ExcelHeaderBg)
                        .border(1.dp, ExcelBorder)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExcelHeaderCell("#", 45.dp)
                    ExcelHeaderCell("A رقم القصة", 100.dp, onClick = {
                        sortColumn = SortColumn.CUT_NUMBER
                        sortAscending = !sortAscending
                    })
                    ExcelHeaderCell("B الموديل", 150.dp)
                    ExcelHeaderCell("C نوع الحركة", 90.dp)
                    ExcelHeaderCell("D المرحلة", 100.dp)
                    ExcelHeaderCell("E عدد القطع", 90.dp, onClick = {
                        sortColumn = SortColumn.PIECES
                        sortAscending = !sortAscending
                    })
                    ExcelHeaderCell("F عدد الأثواب", 85.dp)
                    ExcelHeaderCell("G الوزن (كجم)", 90.dp)
                    ExcelHeaderCell("H الجهة / الورشة", 140.dp)
                    ExcelHeaderCell("I المسؤول", 120.dp)
                    ExcelHeaderCell("J التاريخ", 105.dp, onClick = {
                        sortColumn = SortColumn.DATE
                        sortAscending = !sortAscending
                    })
                    ExcelHeaderCell("K رقم المستند", 100.dp)
                    ExcelHeaderCell("L الملاحظات", 150.dp)
                    ExcelHeaderCell("إجراءات", 70.dp)
                }

                // Table Rows
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(displayedTransactions, key = { _, item -> item.id }) { index, item ->
                        val rowBg = if (index % 2 == 0) MaterialTheme.colorScheme.surface else ExcelCellAlt

                        Row(
                            modifier = Modifier
                                .background(rowBg)
                                .border(0.5.dp, ExcelBorder)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Row Number (1, 2, 3...)
                            ExcelDataCell("${index + 1}", 45.dp, color = Slate500, align = TextAlign.Center)
                            // A: Cut Number
                            ExcelDataCell(item.cutNumber, 100.dp, fontWeight = FontWeight.Bold, color = ExcelGreenDark)
                            // B: Model Name
                            ExcelDataCell(item.modelName, 150.dp)
                            // C: Movement Type
                            Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                                Surface(
                                    color = if (item.isIncoming) StockInBg else StockOutBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (item.isIncoming) "وارد" else "صرف",
                                        color = if (item.isIncoming) StockInColor else StockOutColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            // D: Stage
                            ExcelDataCell(item.stage.ifBlank { item.category }, 100.dp)
                            // E: Pieces
                            ExcelDataCell(
                                "${item.quantityPieces}",
                                90.dp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isIncoming) StockInColor else StockOutColor,
                                align = TextAlign.Center
                            )
                            // F: Rolls
                            ExcelDataCell("${item.quantityRolls}", 85.dp, align = TextAlign.Center)
                            // G: Weight
                            ExcelDataCell(if (item.weightKg > 0) "${item.weightKg}" else "-", 90.dp, align = TextAlign.Center)
                            // H: Destination
                            ExcelDataCell(item.destination.ifBlank { "المخزن" }, 140.dp)
                            // I: Responsible
                            ExcelDataCell(item.responsiblePerson.ifBlank { "أمين المخزن" }, 120.dp)
                            // J: Date
                            ExcelDataCell(item.dateStr, 105.dp, align = TextAlign.Center)
                            // K: Document Number
                            ExcelDataCell(item.documentNumber.ifBlank { "-" }, 100.dp, align = TextAlign.Center)
                            // L: Notes
                            ExcelDataCell(item.notes.ifBlank { "-" }, 150.dp)

                            // Actions (Delete)
                            Box(modifier = Modifier.width(70.dp), contentAlignment = Alignment.Center) {
                                IconButton(
                                    onClick = { onDeleteTransaction(item) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف صف", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Sticky Bottom Totals Bar (Columns A to L)
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE2E8F0))
                        .border(1.dp, Color.Gray)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExcelDataCell("الإجماليات", 145.dp, fontWeight = FontWeight.ExtraBold, color = ExcelGreenDark)
                    ExcelDataCell("", 150.dp)
                    ExcelDataCell("ملخص", 90.dp, fontWeight = FontWeight.Bold, align = TextAlign.Center)
                    ExcelDataCell("", 100.dp)
                    ExcelDataCell(
                        "وارد: $totalIn\nصرف: $totalOut",
                        90.dp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ExcelGreenPrimary,
                        align = TextAlign.Center
                    )
                    ExcelDataCell("", 85.dp)
                    ExcelDataCell("", 90.dp)
                    ExcelDataCell("الصافي بالمخزن: $netBalance قطعة", 260.dp, fontWeight = FontWeight.ExtraBold, color = ExcelGreenDark)
                    ExcelDataCell("", 105.dp)
                    ExcelDataCell("", 100.dp)
                    ExcelDataCell("", 150.dp)
                    ExcelDataCell("", 70.dp)
                }
            }
        }
    }
}

@Composable
private fun ExcelHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Slate700,
                textAlign = TextAlign.Center
            )
            if (onClick != null) {
                Spacer(Modifier.width(2.dp))
                Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(14.dp), tint = Slate500)
            }
        }
    }
}

@Composable
private fun ExcelDataCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified,
    align: TextAlign = TextAlign.Start
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp),
        contentAlignment = when (align) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = color,
            textAlign = align,
            maxLines = 2
        )
    }
}
