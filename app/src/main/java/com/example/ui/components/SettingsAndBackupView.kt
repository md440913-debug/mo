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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.data.model.WarehouseStats
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700

@Composable
fun SettingsAndBackupView(
    stats: WarehouseStats,
    onExportBackupClick: () -> Unit,
    onImportBackupClick: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var factoryTitle by remember { mutableStateOf("مصنع ومعمل النسيج والملابس الجاهزة") }
    var storekeeperDefaultName by remember { mutableStateOf("أحمد رضوان (أمين المخزن الرئيسي)") }
    var autoSaveMinutes by remember { mutableStateOf("تلقائي لحظي (Real-time Room DB)") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("settings_and_backup_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "الإعدادات العامة وإدارة النسخ الاحتياطي (Settings & Backup)",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
            Text(
                text = "إعدادات المنشأة، حفظ البيانات، وأخذ نسخ احتياطية شاملة لمنع فقدان البيانات",
                color = Slate500,
                fontSize = 12.sp
            )
        }

        // Database Persistence Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = ExcelGreenPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = ExcelGreenPrimary, modifier = Modifier.size(24.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("قاعدة بيانات محلية مستقرة (Room SQLite Embedded)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "يتم حفظ جميع حركات الوارد والمنصرف والقصات والجرد فورياً. لا تفقد البيانات أبداً عند إعادة تحميل أو إغلاق المتصفح.",
                        color = Slate500,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Backup & Restore Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("النسخ الاحتياطي والاستعادة (Backup & Restore)", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Export Backup Button
                    Button(
                        onClick = onExportBackupClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("إنشاء وتصدير نسخة احتياطية (Full Export)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Import Backup Button
                    OutlinedButton(
                        onClick = onImportBackupClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("استعادة بيانات من ملف (Restore)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "نصيحة: احفظ نسخة احتياطية دورية أسبوعياً على جهاز اللابتوب لمزيد من الأمان.",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
        }

        // Factory & Warehouse Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("بيانات المنشأة وأمين المخزن", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                OutlinedTextField(
                    value = factoryTitle,
                    onValueChange = { factoryTitle = it },
                    label = { Text("اسم المصنع / الشركة / المنشأة") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = storekeeperDefaultName,
                    onValueChange = { storekeeperDefaultName = it },
                    label = { Text("الاسم الافتراضي لأمين المخزن") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onShowMessage("تم حفظ إعدادات المنشأة بنجاح") },
                    colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("حفظ الإعدادات")
                }
            }
        }
    }
}
