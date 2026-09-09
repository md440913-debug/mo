package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ImportPreviewRow
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutColor

@Composable
fun ExcelImportPreviewDialog(
    previewRows: List<ImportPreviewRow>,
    onDismiss: () -> Unit,
    onConfirmImport: () -> Unit
) {
    val validCount = previewRows.count { it.isValid }
    val invalidCount = previewRows.count { !it.isValid }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(600.dp)
                .padding(16.dp)
                .testTag("excel_import_preview_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = ExcelGreenPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = ExcelGreenPrimary)
                            }
                        }
                        Column {
                            Text(
                                text = "معاينة والتحقق من بيانات ملف Excel قبل الاستيراد",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "تم فحص الأعمدة والكميات وتدقيق السجلات",
                                color = Slate500,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Stats Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("إجمالي الصفوف المفحوصة: ${previewRows.size}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("صفوف سليمة: $validCount", color = StockInColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (invalidCount > 0) {
                        Text("صفوف غير مقبولة: $invalidCount", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Spreadsheet Table Preview (Scrollable)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    LazyColumn {
                        item {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ExcelGreenDark, RoundedCornerShape(4.dp))
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("الحالة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(60.dp))
                                Text("رقم القصة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                                Text("الموديل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(120.dp))
                                Text("نوع الحركة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(70.dp))
                                Text("القطع", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(60.dp))
                                Text("الجهة / الورشة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(100.dp))
                                Text("الملاحظات / الخطأ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(140.dp))
                            }
                        }

                        items(previewRows) { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 6.dp)
                                    .background(
                                        if (row.isValid) Color.Transparent else Color(0xFFFFEBEE),
                                        RoundedCornerShape(4.dp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(modifier = Modifier.width(60.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (row.isValid) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "صالح", tint = StockInColor, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("مقبول", color = StockInColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Error, contentDescription = "خطأ", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("مرفوض", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(row.cutNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                                Text(row.modelName, fontSize = 12.sp, modifier = Modifier.width(120.dp))
                                Text(if (row.type == "IN") "وارد" else "صرف", fontSize = 12.sp, color = if (row.type == "IN") StockInColor else StockOutColor, modifier = Modifier.width(70.dp))
                                Text("${row.pieces}", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                                Text(row.destination.ifBlank { "-" }, fontSize = 11.sp, modifier = Modifier.width(100.dp))
                                Text(
                                    if (row.isValid) row.notes else row.errorMessage,
                                    color = if (row.isValid) Slate500 else Color.Red,
                                    fontSize = 11.sp,
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                            Divider(color = Color(0xFFE2E8F0))
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "لن يتم إدراج أي سجل إلا بعد الضغط على تأكيد الاستيراد.",
                        fontSize = 11.sp,
                        color = Slate500
                    )

                    Row {
                        OutlinedButton(onClick = onDismiss) {
                            Text("إلغاء الأمر")
                        }
                        Spacer(Modifier.width(10.dp))
                        Button(
                            onClick = onConfirmImport,
                            enabled = validCount > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_confirm_import_excel")
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("تأكيد استيراد ($validCount) حركة", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
