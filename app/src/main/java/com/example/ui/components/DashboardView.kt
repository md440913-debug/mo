package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun DashboardView(
    stats: WarehouseStats,
    recentTransactions: List<StockTransactionEntity>,
    cutBalances: List<CutBalance>,
    dateRangeFilter: DateRangeFilter,
    onDateRangeChange: (DateRangeFilter) -> Unit,
    onAddIncomingClick: () -> Unit,
    onAddOutgoingClick: () -> Unit,
    onViewAllTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("dashboard_view"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Bar: Welcome & Date Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "لوحة التحكم وإحصائيات المخزن",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "متابعة لحظية لحركات إدخال وإخراج القصات والأرصدة الدفترية",
                    color = Slate500,
                    fontSize = 13.sp
                )
            }

            // Quick Actions Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAddIncomingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = StockInColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("dashboard_btn_add_incoming")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+ إضافة وارد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onAddOutgoingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = StockOutColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("dashboard_btn_add_outgoing")
                ) {
                    Icon(Icons.Default.RemoveCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("- إضافة صرف", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Filter Bar (Period Selection)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("الفترة الزمنية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate700)
            DateRangeFilter.values().forEach { filter ->
                FilterChip(
                    selected = dateRangeFilter == filter,
                    onClick = { onDateRangeChange(filter) },
                    label = { Text(filter.label, fontSize = 12.sp) }
                )
            }
        }

        // Primary KPI Cards Grid (4 Top Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Net Balance
            DashboardKpiCard(
                title = "صافي الرصيد الحالي",
                value = "${stats.currentStockBalance} قطعة",
                subtext = "${stats.currentRollsBalance} ثوب / رول بالمخزن",
                icon = Icons.Default.Inventory,
                color = ExcelGreenPrimary,
                modifier = Modifier.weight(1f)
            )

            // Total In
            DashboardKpiCard(
                title = "إجمالي الوارد",
                value = "${stats.totalInPieces} قطعة",
                subtext = "${stats.totalInRolls} ثوب وارد",
                icon = Icons.Default.ArrowDownward,
                color = StockInColor,
                modifier = Modifier.weight(1f)
            )

            // Total Out
            DashboardKpiCard(
                title = "إجمالي المنصرف",
                value = "${stats.totalOutPieces} قطعة",
                subtext = "${stats.totalOutRolls} ثوب منصرف",
                icon = Icons.Default.ArrowUpward,
                color = StockOutColor,
                modifier = Modifier.weight(1f)
            )

            // Total Cuts
            DashboardKpiCard(
                title = "إجمالي عدد القصات",
                value = "${stats.totalCutsCount} قصة",
                subtext = "${stats.totalTransactionsCount} حركة مسجلة",
                icon = Icons.Default.ContentCut,
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f)
            )
        }

        // Secondary Cuts Breakdown Metrics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Available Cuts
            MiniStatusCard(
                title = "القصات المتاحة بالمخزن",
                count = stats.availableCutsCount,
                badgeColor = Color(0xFF16A34A),
                description = "قصات بها رصيد متاح للتشغيل",
                modifier = Modifier.weight(1f)
            )

            // Partially Dispatched
            MiniStatusCard(
                title = "صرف جزئي للتشغيل",
                count = stats.partiallyDispatchedCutsCount,
                badgeColor = Color(0xFFD97706),
                description = "تم صرف مراحل ولم تكتمل",
                modifier = Modifier.weight(1f)
            )

            // Fully Dispatched
            MiniStatusCard(
                title = "منصرفة بالكامل",
                count = stats.fullyDispatchedCutsCount,
                badgeColor = Color(0xFF64748B),
                description = "تم صرف كامل كمية الوارد",
                modifier = Modifier.weight(1f)
            )
        }

        // Daily & Monthly Summary Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ملخص حركة التشغيل (اليومي والشهري)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("وارد اليوم", fontSize = 12.sp, color = Slate500)
                        Text("${stats.todayInPieces} قطعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StockInColor)
                    }
                    Column {
                        Text("منصرف اليوم", fontSize = 12.sp, color = Slate500)
                        Text("${stats.todayOutPieces} قطعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StockOutColor)
                    }
                    Column {
                        Text("وارد الشهر الحالي", fontSize = 12.sp, color = Slate500)
                        Text("${stats.thisMonthInPieces} قطعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StockInColor)
                    }
                    Column {
                        Text("منصرف الشهر الحالي", fontSize = 12.sp, color = Slate500)
                        Text("${stats.thisMonthOutPieces} قطعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StockOutColor)
                    }
                }
            }
        }

        // Recent Transactions Table Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "آخر حركات المخزن المسجلة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Button(
                        onClick = onViewAllTransactions,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("عرض كل الحركات", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (recentTransactions.isEmpty()) {
                    Text("لا توجد حركات مسجلة حالياً.", color = Slate500, fontSize = 13.sp)
                } else {
                    recentTransactions.take(5).forEach { trx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = if (trx.isIncoming) StockInBg else StockOutBg,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (trx.isIncoming) "وارد" else "صرف",
                                        color = if (trx.isIncoming) StockInColor else StockOutColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "${trx.cutNumber} - ${trx.modelName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${trx.dateStr} • ${trx.destination} • إذن: ${trx.documentNumber.ifBlank { "بدون" }}",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            Text(
                                text = "${if (trx.isIncoming) "+" else "-"}${trx.quantityPieces} ق",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (trx.isIncoming) StockInColor else StockOutColor
                            )
                        }
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardKpiCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Surface(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = color
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = subtext,
                color = Slate500,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun MiniStatusCard(
    title: String,
    count: Int,
    badgeColor: Color,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(description, fontSize = 11.sp, color = Slate500)
            }

            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$count",
                    color = badgeColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
