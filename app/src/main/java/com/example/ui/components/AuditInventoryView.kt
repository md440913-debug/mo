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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.AuditEntity
import com.example.data.model.CutBalance
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutColor

@Composable
fun AuditInventoryView(
    cutBalances: List<CutBalance>,
    audits: List<AuditEntity>,
    onAddAuditRecord: (cutNumber: String, modelName: String, actualBalance: Int, notes: String, auditorName: String) -> Unit,
    onApproveAudit: (AuditEntity) -> Unit,
    onDeleteAudit: (AuditEntity) -> Unit,
    onExportAuditExcel: () -> Unit
) {
    var showNewAuditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("audit_inventory_view")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "جرد ومطابقة أرصدة المخزن (Stock Audit)",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "مقارنة الرصيد الدفتري بالرصيد الفعلي واحتساب الفروقات واعتماد الجرد",
                    color = Slate500,
                    fontSize = 12.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onExportAuditExcel,
                    colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("تصدير جرد Excel", fontSize = 12.sp)
                }

                Button(
                    onClick = { showNewAuditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_new_audit")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ تسجيل محضر جرد قصة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Audits Table
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("رقم القصة والموديل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2f))
                    Text("الرصيد الدفتري", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                    Text("الرصيد الفعلي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                    Text("الفرق (الفعلي - الدفتري)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("تاريخ الجرد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                    Text("الحالة والاعتماد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.8f))
                }

                Spacer(Modifier.height(8.dp))

                if (audits.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لم يتم تسجيل أي محاضر جرد حتى الآن. اضغط على (+ تسجيل محضر جرد قصة)", color = Slate500)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(audits, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(item.cutNumber, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Text(item.modelName, color = Slate500, fontSize = 11.sp)
                                }

                                Text("${item.bookBalance} ق", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1.2f))
                                Text("${item.actualBalance} ق", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1.2f))

                                val diffColor = when {
                                    item.difference == 0 -> Color(0xFF16A34A)
                                    item.difference > 0 -> Color(0xFF2563EB)
                                    else -> Color.Red
                                }
                                Text(
                                    text = if (item.difference > 0) "+${item.difference} (زيادة)" else if (item.difference < 0) "${item.difference} (عجز)" else "0 (مطابق)",
                                    fontWeight = FontWeight.Bold,
                                    color = diffColor,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1.5f)
                                )

                                Text(item.auditDate, fontSize = 11.sp, color = Slate700, modifier = Modifier.weight(1.2f))

                                Row(
                                    modifier = Modifier.weight(1.8f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (item.isApproved) {
                                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                            Text("معتمد ✓", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    } else {
                                        Button(
                                            onClick = { onApproveAudit(item) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("اعتماد", fontSize = 10.sp)
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteAudit(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showNewAuditDialog) {
        NewAuditDialog(
            cutBalances = cutBalances,
            onDismiss = { showNewAuditDialog = false },
            onConfirm = { cutNum, model, actual, notes, auditor ->
                onAddAuditRecord(cutNum, model, actual, notes, auditor)
                showNewAuditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewAuditDialog(
    cutBalances: List<CutBalance>,
    onDismiss: () -> Unit,
    onConfirm: (cutNumber: String, modelName: String, actualBalance: Int, notes: String, auditorName: String) -> Unit
) {
    var selectedCut by remember { mutableStateOf(cutBalances.firstOrNull()) }
    var actualCountStr by remember { mutableStateOf("") }
    var auditorName by remember { mutableStateOf("لجنة الجرد والمطابقة") }
    var notes by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val bookBalance = selectedCut?.remainingPieces ?: 0
    val actualCount = actualCountStr.toIntOrNull() ?: 0
    val diff = actualCount - bookBalance

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل نتيجة جرد فعلي لقصة", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedCut != null) "${selectedCut!!.cut.cutNumber} - ${selectedCut!!.cut.modelName}" else "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("اختر القصة المراد جردها") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        cutBalances.forEach { cb ->
                            DropdownMenuItem(
                                text = { Text("${cb.cut.cutNumber} - ${cb.cut.modelName} (دفتري: ${cb.remainingPieces})") },
                                onClick = {
                                    selectedCut = cb
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("الرصيد الدفتري المسجل بالحسابات: $bookBalance قطعة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (actualCountStr.isNotBlank()) {
                            Text(
                                text = "الفرق المحسوب (الفعلي - الدفتري): $diff قطعة (${if (diff == 0) "مطابق" else if (diff > 0) "زيادة" else "عجز"})",
                                color = if (diff == 0) Color(0xFF16A34A) else if (diff > 0) Color(0xFF2563EB) else Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = actualCountStr,
                    onValueChange = { actualCountStr = it },
                    label = { Text("الرصيد الفعلي بعد العد اليدوي *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_actual_count")
                )

                OutlinedTextField(
                    value = auditorName,
                    onValueChange = { auditorName = it },
                    label = { Text("القائم بالجرد / اسم أمين المخزن") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الجرد وأسباب الفروقات إن وجدت") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCut != null && actualCountStr.isNotBlank()) {
                        onConfirm(
                            selectedCut!!.cut.cutNumber,
                            selectedCut!!.cut.modelName,
                            actualCount,
                            notes,
                            auditorName
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary)
            ) {
                Text("حفظ محضر الجرد")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
