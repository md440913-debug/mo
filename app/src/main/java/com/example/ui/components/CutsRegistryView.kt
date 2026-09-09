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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.entity.CutEntity
import com.example.data.model.CutBalance
import com.example.ui.theme.ExcelGreenDark
import com.example.ui.theme.ExcelGreenPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.StockInColor
import com.example.ui.theme.StockOutColor

@Composable
fun CutsRegistryView(
    cutBalances: List<CutBalance>,
    onAddCutClick: () -> Unit,
    onEditCutClick: (CutEntity) -> Unit,
    onDeleteCutClick: (CutEntity) -> Unit
) {
    var filterOnlyWithStock by remember { mutableStateOf(false) }

    val displayedList = if (filterOnlyWithStock) {
        cutBalances.filter { it.remainingPieces > 0 }
    } else {
        cutBalances
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("cuts_registry_view")
    ) {
        // Header with Add Cut button and filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onAddCutClick,
                colors = ButtonDefaults.buttonColors(containerColor = ExcelGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_add_cut")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("+ إضافة قصة جديدة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            FilterChip(
                selected = filterOnlyWithStock,
                onClick = { filterOnlyWithStock = !filterOnlyWithStock },
                label = { Text("متوفر بالمخزن فقط") }
            )
        }

        Spacer(Modifier.height(12.dp))

        if (displayedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد قصات مسجلة.",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedList, key = { it.cut.id }) { item ->
                    CutCard(
                        cutBalance = item,
                        onEdit = { onEditCutClick(item.cut) },
                        onDelete = { onDeleteCutClick(item.cut) }
                    )
                }
            }
        }
    }
}

@Composable
fun CutCard(
    cutBalance: CutBalance,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cut = cutBalance.cut

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cut_card_${cut.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Cut Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = ExcelGreenPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = cut.cutNumber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = ExcelGreenDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = cut.modelName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Fabric, Color, Season tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (cut.fabricType.isNotBlank()) {
                    Text(
                        text = "الخامة: ${cut.fabricType}",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                }
                if (cut.color.isNotBlank()) {
                    Text(
                        text = "• اللون: ${cut.color}",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                }
                if (cut.season.isNotBlank()) {
                    Text(
                        text = "• ${cut.season}",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Balance Summary Metrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("إجمالي الوارد", fontSize = 11.sp, color = Slate500)
                    Text(
                        "${cutBalance.totalInPieces} ق",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StockInColor
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("إجمالي المنصرف", fontSize = 11.sp, color = Slate500)
                    Text(
                        "${cutBalance.totalOutPieces} ق",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StockOutColor
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الرصيد المتبقي", fontSize = 11.sp, color = Slate500)
                    Text(
                        "${cutBalance.remainingPieces} ق",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = if (cutBalance.remainingPieces > 0) ExcelGreenPrimary else if (cutBalance.remainingPieces == 0) Color.Gray else StockOutColor
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("أثواب بالمخزن", fontSize = 11.sp, color = Slate500)
                    Text(
                        "${cutBalance.remainingRolls} ثوب",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Progress Bar (Percentage Dispatched)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "نسبة الصرف للتشغيل: ${cutBalance.completionPercentage}%",
                    fontSize = 11.sp,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = cutBalance.statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (cutBalance.remainingPieces > 0) ExcelGreenPrimary else Color.Gray
                )
            }

            Spacer(Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { cutBalance.completionPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = ExcelGreenPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (cut.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "ملاحظات: ${cut.notes}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
