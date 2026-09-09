package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.CutEntity
import com.example.data.model.CutBalance
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val STAGES_LIST = listOf(
    "قص",
    "خياطة",
    "طباعة",
    "تطريز",
    "تجهيز",
    "تشطيب",
    "أخرى"
)

/**
 * Screen / Dialog for Adding Stock In (إضافة وارد)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomingScreen(
    availableCuts: List<CutEntity>,
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) }

    var cutNumber by remember { mutableStateOf("") }
    var modelName by remember { mutableStateOf("") }
    var fabricType by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var quantityPieces by remember { mutableStateOf("") }
    var quantityRolls by remember { mutableStateOf("1") }
    var weightKg by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("صالة القص الرئيسية") }
    var responsible by remember { mutableStateOf("أمين المخزن") }
    var dateStr by remember { mutableStateOf(today) }
    var docNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf<String?>(null) }
    var cutDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .testTag("add_incoming_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = StockInColor)
                        Text(
                            text = "إدخال حركة وارد جديد إلى المخزن",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = StockInColor
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Cut Number with Dropdown
                ExposedDropdownMenuBox(
                    expanded = cutDropdownExpanded,
                    onExpandedChange = { cutDropdownExpanded = !cutDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = cutNumber,
                        onValueChange = { cutNumber = it },
                        label = { Text("رقم القصة *") },
                        placeholder = { Text("مثال: 1001") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cutDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("input_in_cut_number")
                    )

                    if (availableCuts.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = cutDropdownExpanded,
                            onDismissRequest = { cutDropdownExpanded = false }
                        ) {
                            availableCuts.forEach { cut ->
                                DropdownMenuItem(
                                    text = { Text("${cut.cutNumber} - ${cut.modelName}") },
                                    onClick = {
                                        cutNumber = cut.cutNumber
                                        modelName = cut.modelName
                                        fabricType = cut.fabricType
                                        color = cut.color
                                        cutDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Model Name
                OutlinedTextField(
                    value = modelName,
                    onValueChange = { modelName = it },
                    label = { Text("اسم الموديل *") },
                    placeholder = { Text("مثال: تيشيرت بولو صيفي") },
                    modifier = Modifier.fillMaxWidth().testTag("input_in_model_name")
                )

                Spacer(Modifier.height(10.dp))

                // Fabric Type & Color
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = fabricType,
                        onValueChange = { fabricType = it },
                        label = { Text("نوع القماش / الخامة") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("اللون") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Quantities: Pieces, Rolls, Weight
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityPieces,
                        onValueChange = { quantityPieces = it },
                        label = { Text("عدد القطع *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_in_pieces")
                    )
                    OutlinedTextField(
                        value = quantityRolls,
                        onValueChange = { quantityRolls = it },
                        label = { Text("عدد الأثواب / الرولات") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        label = { Text("الوزن (كجم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Source & Responsible
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = source,
                        onValueChange = { source = it },
                        label = { Text("مصدر الوارد (المقصدار / المورد)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = responsible,
                        onValueChange = { responsible = it },
                        label = { Text("اسم المسؤول / المستلم") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Document Number & Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        label = { Text("رقم المستند / إذن الاستلام") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("التاريخ (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorText!!, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("إلغاء")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val pieces = quantityPieces.toIntOrNull() ?: 0
                            if (cutNumber.isBlank() || modelName.isBlank() || pieces <= 0) {
                                errorText = "يرجى ملء الحقول المطلوبة وعدد قطع أكبر من صفر"
                                return@Button
                            }
                            onSave(
                                cutNumber,
                                modelName,
                                fabricType,
                                color,
                                pieces,
                                quantityRolls.toIntOrNull() ?: 0,
                                weightKg.toDoubleOrNull() ?: 0.0,
                                source,
                                responsible,
                                dateStr,
                                docNumber,
                                notes
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StockInColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_save_incoming")
                    ) {
                        Text("حفظ حركة الوارد", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Screen / Dialog for Adding Stock Out (إضافة صرف)
 * WITH STRICT VALIDATION: blocks if requested > available balance!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutgoingScreen(
    availableCuts: List<CutBalance>,
    onDismiss: () -> Unit,
    onSave: (
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
    ) -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) }

    var selectedCutBalance by remember { mutableStateOf<CutBalance?>(availableCuts.firstOrNull()) }
    var cutNumber by remember { mutableStateOf(selectedCutBalance?.cut?.cutNumber ?: "") }
    var modelName by remember { mutableStateOf(selectedCutBalance?.cut?.modelName ?: "") }

    var quantityPieces by remember { mutableStateOf("") }
    var quantityRolls by remember { mutableStateOf("1") }
    var weightKg by remember { mutableStateOf("") }
    var destinationWorkshop by remember { mutableStateOf("") }
    var selectedStage by remember { mutableStateOf(STAGES_LIST.first()) }
    var receiver by remember { mutableStateOf("") }
    var storekeeper by remember { mutableStateOf("أمين المخزن") }
    var dateStr by remember { mutableStateOf(today) }
    var docNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf<String?>(null) }
    var cutDropdownExpanded by remember { mutableStateOf(false) }
    var stageDropdownExpanded by remember { mutableStateOf(false) }

    val availablePieces = selectedCutBalance?.remainingPieces ?: 0
    val requestedPiecesInt = quantityPieces.toIntOrNull() ?: 0
    val isOverLimit = requestedPiecesInt > availablePieces && requestedPiecesInt > 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .testTag("add_outgoing_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = StockOutColor)
                        Text(
                            text = "إخراج / صرف من المخزن للتشغيل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = StockOutColor
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Cut Selector & Balance Banner
                ExposedDropdownMenuBox(
                    expanded = cutDropdownExpanded,
                    onExpandedChange = { cutDropdownExpanded = !cutDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedCutBalance != null) "${selectedCutBalance!!.cut.cutNumber} - ${selectedCutBalance!!.cut.modelName}" else cutNumber,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("اختر القصة المراد الصرف منها *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cutDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("select_out_cut")
                    )

                    ExposedDropdownMenu(
                        expanded = cutDropdownExpanded,
                        onDismissRequest = { cutDropdownExpanded = false }
                    ) {
                        availableCuts.forEach { cb ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${cb.cut.cutNumber} - ${cb.cut.modelName}")
                                        Text(
                                            "الرصيد: ${cb.remainingPieces} ق",
                                            color = if (cb.remainingPieces > 0) ExcelGreenPrimary else Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCutBalance = cb
                                    cutNumber = cb.cut.cutNumber
                                    modelName = cb.cut.modelName
                                    cutDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Available Balance Indicator Banner
                Surface(
                    color = if (availablePieces > 0) ExcelGreenPrimary.copy(alpha = 0.12f) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "الرصيد الدفتري المتاح بالمخزن حالياً:",
                                fontSize = 12.sp,
                                color = Slate700
                            )
                            Text(
                                text = "$availablePieces قطعة (${selectedCutBalance?.remainingRolls ?: 0} ثوب)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = if (availablePieces > 0) ExcelGreenPrimary else Color.Red
                            )
                        }

                        if (isOverLimit) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                Text("تجاوز الرصيد!", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Quantities
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityPieces,
                        onValueChange = {
                            quantityPieces = it
                            errorText = null
                        },
                        label = { Text("عدد القطع المنصرفة *") },
                        isError = isOverLimit,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_out_pieces")
                    )
                    OutlinedTextField(
                        value = quantityRolls,
                        onValueChange = { quantityRolls = it },
                        label = { Text("عدد الأثواب") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        label = { Text("الوزن (كجم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isOverLimit) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "لا يمكن تنفيذ الصرف لأن الكمية المطلوبة ($requestedPiecesInt) أكبر من الرصيد المتاح ($availablePieces قطعة).",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Stage Dropdown & Destination Workshop
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = stageDropdownExpanded,
                        onExpandedChange = { stageDropdownExpanded = !stageDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedStage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المرحلة *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = stageDropdownExpanded,
                            onDismissRequest = { stageDropdownExpanded = false }
                        ) {
                            STAGES_LIST.forEach { stage ->
                                DropdownMenuItem(
                                    text = { Text(stage) },
                                    onClick = {
                                        selectedStage = stage
                                        stageDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = destinationWorkshop,
                        onValueChange = { destinationWorkshop = it },
                        label = { Text("جهة الصرف / اسم الورشة *") },
                        placeholder = { Text("مثال: ورشة الأمل للخياطة") },
                        modifier = Modifier.weight(1f).testTag("input_out_destination")
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Receiver & Storekeeper
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = receiver,
                        onValueChange = { receiver = it },
                        label = { Text("اسم المستلم (المشرف / السائق)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = storekeeper,
                        onValueChange = { storekeeper = it },
                        label = { Text("اسم أمين المخزن") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Document Number & Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        label = { Text("رقم إذن الصرف / المستند") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("التاريخ (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الصرف") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorText!!, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(18.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("إلغاء")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val pieces = quantityPieces.toIntOrNull() ?: 0
                            if (pieces <= 0) {
                                errorText = "يجب تحديد عدد قطع صالح أكبر من صفر"
                                return@Button
                            }
                            if (pieces > availablePieces) {
                                errorText = "لا يمكن تنفيذ الصرف لأن الكمية المطلوبة ($pieces) أكبر من الرصيد المتاح ($availablePieces قطعة)."
                                return@Button
                            }
                            if (destinationWorkshop.isBlank()) {
                                errorText = "يرجى تحديد جهة الصرف أو الورشة"
                                return@Button
                            }

                            onSave(
                                cutNumber,
                                modelName,
                                pieces,
                                quantityRolls.toIntOrNull() ?: 0,
                                weightKg.toDoubleOrNull() ?: 0.0,
                                destinationWorkshop,
                                selectedStage,
                                receiver,
                                storekeeper,
                                dateStr,
                                docNumber,
                                notes
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StockOutColor),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isOverLimit && requestedPiecesInt > 0,
                        modifier = Modifier.testTag("btn_save_outgoing")
                    ) {
                        Text("تأكيد وخصم الصرف", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog for Adding or Editing a Cut Entity
 */
@Composable
fun AddCutDialog(
    initialCut: CutEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        cutNumber: String,
        modelName: String,
        fabricType: String,
        color: String,
        season: String,
        targetPieces: Int,
        notes: String
    ) -> Unit
) {
    var cutNumber by remember { mutableStateOf(initialCut?.cutNumber ?: "") }
    var modelName by remember { mutableStateOf(initialCut?.modelName ?: "") }
    var fabricType by remember { mutableStateOf(initialCut?.fabricType ?: "") }
    var color by remember { mutableStateOf(initialCut?.color ?: "") }
    var season by remember { mutableStateOf(initialCut?.season ?: "صيف 2025") }
    var targetPieces by remember { mutableStateOf(initialCut?.targetPieces?.toString() ?: "0") }
    var notes by remember { mutableStateOf(initialCut?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialCut == null) "تسجيل كود قصة جديدة" else "تعديل بيانات القصة",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = cutNumber,
                    onValueChange = { cutNumber = it },
                    label = { Text("رقم القصة *") },
                    enabled = initialCut == null,
                    modifier = Modifier.fillMaxWidth().testTag("input_cut_number")
                )

                OutlinedTextField(
                    value = modelName,
                    onValueChange = { modelName = it },
                    label = { Text("اسم الموديل *") },
                    modifier = Modifier.fillMaxWidth().testTag("input_cut_model")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fabricType,
                        onValueChange = { fabricType = it },
                        label = { Text("نوع القماش") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("اللون") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = season,
                        onValueChange = { season = it },
                        label = { Text("الموسم") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetPieces,
                        onValueChange = { targetPieces = it },
                        label = { Text("الكمية المستهدفة") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Text(errorText!!, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (cutNumber.isBlank() || modelName.isBlank()) {
                        errorText = "يرجى إدخال رقم القصة واسم الموديل"
                        return@Button
                    }
                    onConfirm(
                        cutNumber,
                        modelName,
                        fabricType,
                        color,
                        season,
                        targetPieces.toIntOrNull() ?: 0,
                        notes
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary)
            ) {
                Text(if (initialCut == null) "إضافة القصة" else "حفظ التعديلات")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

