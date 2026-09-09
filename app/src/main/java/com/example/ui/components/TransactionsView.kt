package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.StockTransactionEntity
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

enum class TransactionFilterType {
    ALL, INCOMING_ONLY, OUTGOING_ONLY
}

@Composable
fun TransactionsView(
    transactions: List<StockTransactionEntity>,
    onAddIncomingClick: () -> Unit,
    onAddOutgoingClick: () -> Unit,
    onDeleteTransaction: (StockTransactionEntity) -> Unit
) {
    var activeFilter by remember { mutableStateOf(TransactionFilterType.ALL) }
    var selectedStageFilter by remember { mutableStateOf("الكل") }
    var sortDescending by remember { mutableStateOf(true) }

    val filteredList = transactions.filter { item ->
        val matchesType = when (activeFilter) {
            TransactionFilterType.ALL -> true
            TransactionFilterType.INCOMING_ONLY -> item.isIncoming
            TransactionFilterType.OUTGOING_ONLY -> !item.isIncoming
        }
        val matchesStage = if (selectedStageFilter == "الكل") true else {
            item.stage.equals(selectedStageFilter, ignoreCase = true) || item.category.contains(selectedStageFilter)
        }
        matchesType && matchesStage
    }.let { list ->
        if (sortDescending) list.sortedByDescending { it.id } else list.sortedBy { it.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("transactions_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Quick Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سجل حركات المخزن (الوارد والمنصرف)",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "إجمالي الحركات المعروضة: ${filteredList.size} حركة",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddIncomingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = StockInColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_add_incoming_tx")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ إضافة وارد", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onAddOutgoingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = StockOutColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_add_outgoing_tx")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("- إضافة صرف", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Filters Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = activeFilter == TransactionFilterType.ALL,
                    onClick = { activeFilter = TransactionFilterType.ALL },
                    label = { Text("الكل") }
                )
                FilterChip(
                    selected = activeFilter == TransactionFilterType.INCOMING_ONLY,
                    onClick = { activeFilter = TransactionFilterType.INCOMING_ONLY },
                    label = { Text("وارد فقط") }
                )
                FilterChip(
                    selected = activeFilter == TransactionFilterType.OUTGOING_ONLY,
                    onClick = { activeFilter = TransactionFilterType.OUTGOING_ONLY },
                    label = { Text("منصرف فقط") }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { sortDescending = !sortDescending },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (sortDescending) "الأحدث أولاً" else "الأقدم أولاً", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Transactions Table
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ExcelHeaderBg)
                        .border(0.5.dp, ExcelBorder)
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("التاريخ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(1.2f))
                    Text("الحركة", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(0.9f))
                    Text("رقم القصة والموديل", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(2.5f))
                    Text("المرحلة والجهة", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(2f))
                    Text("القطع والأثواب", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(1.5f))
                    Text("المسؤول والمستند", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(1.5f))
                    Text("إجراء", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate700, modifier = Modifier.weight(0.6f))
                }

                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لا توجد حركات مخزن مطابقة للفلتر المحدد.", color = Slate500)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filteredList, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, ExcelBorder)
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.dateStr, fontSize = 11.sp, color = Slate700, modifier = Modifier.weight(1.2f))

                                Surface(
                                    color = if (item.isIncoming) StockInBg else StockOutBg,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Text(
                                        text = if (item.isIncoming) "وارد" else "صرف",
                                        color = if (item.isIncoming) StockInColor else StockOutColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(2.5f)) {
                                    Text(item.cutNumber, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = ExcelGreenDark)
                                    Text(item.modelName, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                }

                                Column(modifier = Modifier.weight(2f)) {
                                    Text(item.destination.ifBlank { "المخزن" }, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text(item.stage.ifBlank { item.category }, color = Slate500, fontSize = 11.sp)
                                }

                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        text = "${if (item.isIncoming) "+" else "-"}${item.quantityPieces} قطعة",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (item.isIncoming) StockInColor else StockOutColor
                                    )
                                    Text(
                                        text = "${item.quantityRolls} ثوب • ${if (item.weightKg > 0) "${item.weightKg} كجم" else "-"}",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }

                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(item.responsiblePerson.ifBlank { "أمين المخزن" }, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text("مستند: ${item.documentNumber.ifBlank { "-" }}", fontSize = 10.sp, color = Slate500)
                                }

                                IconButton(
                                    onClick = { onDeleteTransaction(item) },
                                    modifier = Modifier.weight(0.6f).size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
