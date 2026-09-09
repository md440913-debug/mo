package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
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
import com.example.data.model.CutBalance
import com.example.data.model.WarehouseStats
import com.example.ui.DateRangeFilter
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInBg
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutBg
import com.example.ui.theme.StockOutColor

enum class ReportType(val title: String, val description: String) {
    GENERAL_MOVEMENTS("1. تقرير حركة المخزن العامة", "كشف تفصيلي بجميع عمليات الوارد والمنصرف بالتواريخ والكميات"),
    CUTS_BALANCES("2. تقرير أرصدة وجرد القصات", "أرصدة كل قصة ونسب الصرف للتشغيل وحالة توفر المخزون"),
    INCOMING_ONLY("3. تقرير الوارد بالتفصيل", "حركات استلام القصات ومصادر التوريد والأثواب والأوزان"),
    OUTGOING_ONLY("4. تقرير المنصرف بالتفصيل", "أوامر الصرف لخطوط الإنتاج والورش وجهات الصرف والمستلمين"),
    BY_MODEL("5. تقرير حسب الموديل", "تجميع كميات الوارد والمنصرف والمتبقي لكل موديل"),
    BY_FABRIC("6. تقرير حسب القماش والخامة", "استهلاك وأرصدة الأقمشة (قطن، جبردين، ميلتون، كتان)"),
    BY_WORKSHOP("7. تقرير حسب الورشة والجهة", "متابعة الكميات المسلّمة لكل ورشة خارجية أو خط إنتاج"),
    BY_STAGE("8. تقرير حسب المرحلة", "توزيع المنصرف على مراحل (قص، خياطة، طباعة، تطريز، تشطيب)"),
    AUDIT_DIFFS("9. تقرير جرد المخزن والفروقات", "نتائج المطابقة بين الرصيد الدفتري والفعلي والعجز والزيادة"),
    FULLY_DISPATCHED("10. تقرير القصات المنصرفة بالكامل", "أرشيف القصات التي تم استكمال صرفها بنسبة 100%"),
    IN_STOCK_ONLY("11. تقرير القصات التي لها رصيد متاح", "القصات الجاهزة حالياً بالمخزن لصرف مراحل جديدة")
}

@Composable
fun ReportsAndExportView(
    stats: WarehouseStats,
    transactions: List<StockTransactionEntity>,
    cutBalances: List<CutBalance>,
    onExportTransactionsExcel: () -> Unit,
    onExportCutsExcel: () -> Unit,
    onExportAuditExcel: () -> Unit,
    onImportCsvClick: () -> Unit
) {
    var selectedReport by remember { mutableStateOf(ReportType.GENERAL_MOVEMENTS) }
    var reportSearchQuery by remember { mutableStateOf("") }
    var reportDateRange by remember { mutableStateOf(DateRangeFilter.ALL) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reports_and_export_view"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column (Report Selector Menu)
        Card(
            modifier = Modifier
                .width(280.dp)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "قائمة التقارير المعتمدة",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "اختر التقرير المطلوب لعرضه وتصديره",
                    fontSize = 11.sp,
                    color = Slate500
                )

                Spacer(Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(8.dp))

                ReportType.values().forEach { r ->
                    val isSelected = selectedReport == r
                    Surface(
                        color = if (isSelected) ExcelGreenPrimary.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReport = r }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = null,
                                tint = if (isSelected) ExcelGreenPrimary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = r.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ExcelGreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(12.dp))

                // Excel Import / Export Quick Hub
                Text("مركز ملفات Excel:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onExportTransactionsExcel,
                    colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("تصدير حركات Excel", fontSize = 11.sp)
                }

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = onExportCutsExcel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("تصدير أرصدة القصات", fontSize = 11.sp)
                }

                Spacer(Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onImportCsvClick,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("استيراد شيت إكسل", fontSize = 11.sp)
                }
            }
        }

        // Right Column: Selected Report Live Viewer
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Report Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedReport.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = selectedReport.description,
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (selectedReport == ReportType.CUTS_BALANCES || selectedReport == ReportType.IN_STOCK_ONLY || selectedReport == ReportType.FULLY_DISPATCHED) {
                                    onExportCutsExcel()
                                } else if (selectedReport == ReportType.AUDIT_DIFFS) {
                                    onExportAuditExcel()
                                } else {
                                    onExportTransactionsExcel()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("تصدير التقرير Excel", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Search & Date Filter inside Report
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = reportSearchQuery,
                        onValueChange = { reportSearchQuery = it },
                        placeholder = { Text("بحث داخل التقرير...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(48.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )

                    DateRangeFilter.values().forEach { d ->
                        FilterChip(
                            selected = reportDateRange == d,
                            onClick = { reportDateRange = d },
                            label = { Text(d.label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(8.dp))

                // Report Content Table
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedReport) {
                        ReportType.CUTS_BALANCES, ReportType.IN_STOCK_ONLY, ReportType.FULLY_DISPATCHED, ReportType.BY_MODEL, ReportType.BY_FABRIC -> {
                            val itemsToShow = cutBalances.filter { cb ->
                                when (selectedReport) {
                                    ReportType.IN_STOCK_ONLY -> cb.remainingPieces > 0
                                    ReportType.FULLY_DISPATCHED -> cb.remainingPieces == 0 && cb.totalInPieces > 0
                                    else -> true
                                } && (reportSearchQuery.isBlank() || cb.cut.cutNumber.contains(reportSearchQuery) || cb.cut.modelName.contains(reportSearchQuery) || cb.cut.fabricType.contains(reportSearchQuery))
                            }

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(ExcelGreenDark, RoundedCornerShape(4.dp)).padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("رقم القصة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                                        Text("الموديل والخامة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2.5f))
                                        Text("الوارد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text("المنصرف", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text("الرصيد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                        Text("نسبة الصرف", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                        Text("الحالة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                    }
                                }
                                items(itemsToShow) { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(item.cut.cutNumber, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                                        Column(modifier = Modifier.weight(2.5f)) {
                                            Text(item.cut.modelName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("${item.cut.fabricType} • ${item.cut.color}", color = Slate500, fontSize = 10.sp)
                                        }
                                        Text("${item.totalInPieces} ق", color = StockInColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text("${item.totalOutPieces} ق", color = StockOutColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text(
                                            "${item.remainingPieces} ق",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = if (item.remainingPieces > 0) ExcelGreenPrimary else Color.Gray,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        Text("${item.completionPercentage}%", fontWeight = FontWeight.Medium, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                        Text(item.statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                                    }
                                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                                }
                            }
                        }

                        else -> {
                            val transactionsToShow = transactions.filter { t ->
                                val matchType = when (selectedReport) {
                                    ReportType.INCOMING_ONLY -> t.isIncoming
                                    ReportType.OUTGOING_ONLY -> !t.isIncoming
                                    else -> true
                                }
                                val matchQuery = if (reportSearchQuery.isBlank()) true else {
                                    t.cutNumber.contains(reportSearchQuery) || t.modelName.contains(reportSearchQuery) || t.destination.contains(reportSearchQuery) || t.stage.contains(reportSearchQuery)
                                }
                                matchType && matchQuery
                            }

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(ExcelGreenDark, RoundedCornerShape(4.dp)).padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("التاريخ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                        Text("القصة والموديل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2.5f))
                                        Text("الحركة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text("الكمية", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                        Text("المرحلة والورشة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2f))
                                        Text("المستند", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                                    }
                                }
                                items(transactionsToShow) { t ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(t.dateStr, fontSize = 11.sp, color = Slate700, modifier = Modifier.weight(1.2f))
                                        Column(modifier = Modifier.weight(2.5f)) {
                                            Text("${t.cutNumber} - ${t.modelName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Surface(
                                            color = if (t.isIncoming) StockInBg else StockOutBg,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = if (t.isIncoming) "وارد" else "صرف",
                                                color = if (t.isIncoming) StockInColor else StockOutColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            "${t.quantityPieces} ق (${t.quantityRolls} ث)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (t.isIncoming) StockInColor else StockOutColor,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        Column(modifier = Modifier.weight(2f)) {
                                            Text(t.destination.ifBlank { "المخزن" }, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                            Text(t.stage.ifBlank { t.category }, color = Slate500, fontSize = 10.sp)
                                        }
                                        Text(t.documentNumber.ifBlank { "-" }, fontSize = 11.sp, color = Slate500, modifier = Modifier.weight(1.2f))
                                    }
                                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
