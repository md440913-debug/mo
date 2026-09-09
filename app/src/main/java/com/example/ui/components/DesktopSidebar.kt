package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Divider
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
import com.example.data.model.WarehouseStats
import com.example.ui.AppTab
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutColor

@Composable
fun DesktopSidebar(
    currentTab: AppTab,
    onTabSelect: (AppTab) -> Unit,
    onAddIncomingClick: () -> Unit,
    onAddOutgoingClick: () -> Unit,
    onExportExcelClick: () -> Unit,
    onImportExcelClick: () -> Unit,
    stats: WarehouseStats,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight(),
        color = Color(0xFF1E293B), // Professional dark sidebar for accounting ERP
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header Branding
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = ExcelGreenPrimary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "مخازن القصات",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "نظام إكسل المكتبي المالي",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                // Navigation Items
                SidebarNavItem(
                    title = "🏠 الرئيسية (Dashboard)",
                    icon = Icons.Default.Home,
                    isSelected = currentTab == AppTab.DASHBOARD,
                    onClick = { onTabSelect(AppTab.DASHBOARD) },
                    testTag = "sidebar_nav_dashboard"
                )

                SidebarNavItem(
                    title = "📦 دليل القصات",
                    icon = Icons.Default.ContentCut,
                    isSelected = currentTab == AppTab.CUTS,
                    onClick = { onTabSelect(AppTab.CUTS) },
                    testTag = "sidebar_nav_cuts"
                )

                SidebarNavItem(
                    title = "↕️ حركات المخزن",
                    icon = Icons.Default.SwapVert,
                    isSelected = currentTab == AppTab.TRANSACTIONS,
                    onClick = { onTabSelect(AppTab.TRANSACTIONS) },
                    testTag = "sidebar_nav_transactions"
                )

                Spacer(Modifier.height(4.dp))

                // Action Buttons for Incoming / Outgoing
                SidebarActionItem(
                    title = "➕ إضافة وارد",
                    icon = Icons.Default.AddCircle,
                    tint = StockInColor,
                    onClick = onAddIncomingClick,
                    testTag = "sidebar_action_add_in"
                )

                SidebarActionItem(
                    title = "➖ إضافة صرف",
                    icon = Icons.Default.RemoveCircle,
                    tint = StockOutColor,
                    onClick = onAddOutgoingClick,
                    testTag = "sidebar_action_add_out"
                )

                Spacer(Modifier.height(4.dp))

                SidebarNavItem(
                    title = "📊 التقارير الشاملة",
                    icon = Icons.Default.Assessment,
                    isSelected = currentTab == AppTab.REPORTS,
                    onClick = { onTabSelect(AppTab.REPORTS) },
                    testTag = "sidebar_nav_reports"
                )

                SidebarNavItem(
                    title = "📋 الجرد والمطابقة",
                    icon = Icons.Default.FactCheck,
                    isSelected = currentTab == AppTab.AUDIT,
                    onClick = { onTabSelect(AppTab.AUDIT) },
                    testTag = "sidebar_nav_audit"
                )

                SidebarNavItem(
                    title = "📑 شيت المخزن (Excel)",
                    icon = Icons.Default.TableChart,
                    isSelected = currentTab == AppTab.EXCEL_SHEET,
                    onClick = { onTabSelect(AppTab.EXCEL_SHEET) },
                    testTag = "sidebar_nav_excel_sheet"
                )

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                SidebarActionItem(
                    title = "📤 تصدير Excel",
                    icon = Icons.Default.FileDownload,
                    tint = Color(0xFF60A5FA),
                    onClick = onExportExcelClick,
                    testTag = "sidebar_action_export_excel"
                )

                SidebarActionItem(
                    title = "📥 استيراد Excel",
                    icon = Icons.Default.FileUpload,
                    tint = Color(0xFFFBBF24),
                    onClick = onImportExcelClick,
                    testTag = "sidebar_action_import_excel"
                )

                SidebarNavItem(
                    title = "⚙️ الإعدادات والنسخ",
                    icon = Icons.Default.Settings,
                    isSelected = currentTab == AppTab.SETTINGS,
                    onClick = { onTabSelect(AppTab.SETTINGS) },
                    testTag = "sidebar_nav_settings"
                )
            }

            // Bottom Mini Card: Live Warehouse Health
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "الرصيد الفعلي الحالي:",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${stats.currentStockBalance} قطعة",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${stats.totalCutsCount} قصة • ${stats.totalTransactionsCount} حركة مسجلة",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = if (isSelected) ExcelGreenPrimary.copy(alpha = 0.25f) else Color.Transparent
    val contentColor = if (isSelected) Color(0xFF4ADE80) else Color(0xFFCBD5E1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .background(bgColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun SidebarActionItem(
    title: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
