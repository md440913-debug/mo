package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.entity.AuditEntity
import com.example.data.entity.CutEntity
import com.example.data.entity.StockTransactionEntity
import com.example.data.model.CutBalance
import com.example.data.model.ImportPreviewRow
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelHelper {

    // UTF-8 Byte Order Mark (BOM) ensures Microsoft Excel on Windows & Mac displays Arabic text properly
    private const val UTF8_BOM = "\uFEFF"

    private fun escapeCsv(value: Any?): String {
        if (value == null) return ""
        val str = value.toString().replace("\"", "\"\"")
        return if (str.contains(",") || str.contains("\n") || str.contains("\r") || str.contains("\"") || str.contains(";")) {
            "\"$str\""
        } else {
            str
        }
    }

    /**
     * Generates Excel-compatible CSV for all warehouse movements with specific requested columns A to L
     * A: رقم القصة, B: الموديل, C: نوع الحركة, D: المرحلة, E: عدد القطع, F: عدد الأثواب, G: الوزن, H: الجهة, I: المسؤول, J: التاريخ, K: رقم المستند, L: الملاحظات
     */
    fun createTransactionsExcelSheet(transactions: List<StockTransactionEntity>): String {
        val sb = StringBuilder()
        sb.append(UTF8_BOM)

        // Column headers matching user specification
        val headers = listOf(
            "رقم القصة",
            "الموديل",
            "نوع الحركة",
            "المرحلة",
            "عدد القطع",
            "عدد الأثواب",
            "الوزن",
            "الجهة",
            "المسؤول",
            "التاريخ",
            "رقم المستند",
            "الملاحظات"
        )
        sb.append(headers.joinToString(",") { escapeCsv(it) }).append("\n")

        var totalIn = 0
        var totalOut = 0
        var totalRolls = 0
        var totalWeight = 0.0

        transactions.forEach { item ->
            val typeStr = if (item.isIncoming) "وارد" else "صرف"
            if (item.isIncoming) totalIn += item.quantityPieces else totalOut += item.quantityPieces
            totalRolls += item.quantityRolls
            totalWeight += item.weightKg

            val row = listOf(
                item.cutNumber,
                item.modelName,
                typeStr,
                item.stage.ifBlank { item.category },
                item.quantityPieces,
                item.quantityRolls,
                if (item.weightKg > 0) String.format(Locale.ENGLISH, "%.2f", item.weightKg) else "0",
                item.destination,
                item.responsiblePerson,
                item.dateStr,
                item.documentNumber,
                item.notes
            )
            sb.append(row.joinToString(",") { escapeCsv(it) }).append("\n")
        }

        // Bottom Totals Summary
        sb.append("\n")
        sb.append(listOf("إجمالي الوارد", "", "", "", totalIn, "", "", "", "", "", "", "").joinToString(",") { escapeCsv(it) }).append("\n")
        sb.append(listOf("إجمالي المنصرف", "", "", "", totalOut, "", "", "", "", "", "", "").joinToString(",") { escapeCsv(it) }).append("\n")
        sb.append(listOf("صافي الرصيد", "", "", "", (totalIn - totalOut), "", "", "", "", "", "", "").joinToString(",") { escapeCsv(it) }).append("\n")

        return sb.toString()
    }

    /**
     * Generates Excel-compatible CSV for Cut Balances Registry
     */
    fun createCutsBalanceExcelSheet(cutBalances: List<CutBalance>): String {
        val sb = StringBuilder()
        sb.append(UTF8_BOM)

        val headers = listOf(
            "رقم القصة",
            "الموديل",
            "نوع القماش",
            "اللون",
            "الموسم",
            "إجمالي الوارد",
            "إجمالي المنصرف",
            "الرصيد المتاح",
            "الأثواب المتبقية",
            "نسبة الصرف %",
            "الحالة",
            "ملاحظات"
        )
        sb.append(headers.joinToString(",") { escapeCsv(it) }).append("\n")

        var totalIn = 0
        var totalOut = 0
        var totalRemaining = 0
        var totalRolls = 0

        cutBalances.forEach { cb ->
            totalIn += cb.totalInPieces
            totalOut += cb.totalOutPieces
            totalRemaining += cb.remainingPieces
            totalRolls += cb.remainingRolls

            val row = listOf(
                cb.cut.cutNumber,
                cb.cut.modelName,
                cb.cut.fabricType,
                cb.cut.color,
                cb.cut.season,
                cb.totalInPieces,
                cb.totalOutPieces,
                cb.remainingPieces,
                cb.remainingRolls,
                "${cb.completionPercentage}%",
                cb.statusText,
                cb.cut.notes
            )
            sb.append(row.joinToString(",") { escapeCsv(it) }).append("\n")
        }

        // Summary row
        sb.append("\n")
        sb.append(listOf("الإجمالي العام", "${cutBalances.size} قصة", "", "", "", totalIn, totalOut, totalRemaining, totalRolls, "", "صافي المخزن", "").joinToString(",") { escapeCsv(it) }).append("\n")

        return sb.toString()
    }

    /**
     * Generates Excel-compatible CSV for Stock Reconciliation / Audits (الجرد)
     */
    fun createAuditExcelSheet(audits: List<AuditEntity>): String {
        val sb = StringBuilder()
        sb.append(UTF8_BOM)

        val headers = listOf(
            "رقم القصة",
            "الموديل",
            "الرصيد الدفتري",
            "الرصيد الفعلي",
            "الفرق (الفعلي - الدفتري)",
            "تاريخ الجرد",
            "القائم بالجرد",
            "الحالة",
            "ملاحظات"
        )
        sb.append(headers.joinToString(",") { escapeCsv(it) }).append("\n")

        var sumBook = 0
        var sumActual = 0
        var sumDiff = 0

        audits.forEach { a ->
            sumBook += a.bookBalance
            sumActual += a.actualBalance
            sumDiff += a.difference

            val row = listOf(
                a.cutNumber,
                a.modelName,
                a.bookBalance,
                a.actualBalance,
                a.difference,
                a.auditDate,
                a.auditorName,
                if (a.isApproved) "معتمد" else "قيد المراجعة",
                a.notes
            )
            sb.append(row.joinToString(",") { escapeCsv(it) }).append("\n")
        }

        sb.append("\n")
        sb.append(listOf("الإجمالي", "", sumBook, sumActual, sumDiff, "", "", "", "").joinToString(",") { escapeCsv(it) }).append("\n")

        return sb.toString()
    }

    /**
     * Saves CSV file to application cache and returns shareable URI
     */
    fun writeCsvToFile(context: Context, fileName: String, csvContent: String): Uri? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }
            val file = File(exportDir, fileName)
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    writer.write(csvContent)
                    writer.flush()
                }
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareFile(context: Context, uri: Uri, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/comma-separated-values"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "تم تصدير ملف إكسل من نظام مخازن القصات: $title")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "مشاركة أو فتح شيت إكسل عبر:")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Parses uploaded CSV or TSV file and validates rows for import preview
     */
    fun parseAndValidateCsv(context: Context, uri: Uri): List<ImportPreviewRow> {
        val previewRows = mutableListOf<ImportPreviewRow>()
        val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    var line: String?
                    var lineNum = 0
                    var isFirstLine = true

                    while (reader.readLine().also { line = it } != null) {
                        var l = line ?: ""
                        if (isFirstLine) {
                            if (l.startsWith(UTF8_BOM)) {
                                l = l.substring(1)
                            }
                            isFirstLine = false
                        }
                        lineNum++
                        if (l.isBlank()) continue

                        val tokens = parseCsvLine(l)
                        // Ignore header rows or summary rows
                        val firstToken = tokens.firstOrNull() ?: ""
                        if (firstToken.contains("رقم القصة") || firstToken.contains("إجمالي") || firstToken.contains("الإجمالي") || firstToken.contains("صافي")) {
                            continue
                        }

                        if (tokens.isNotEmpty()) {
                            val cutNum = tokens.getOrNull(0)?.trim() ?: ""
                            val model = tokens.getOrNull(1)?.trim() ?: ""
                            val typeRaw = tokens.getOrNull(2)?.trim() ?: "وارد"
                            val type = if (typeRaw.contains("صرف") || typeRaw.contains("منصرف") || typeRaw.equals("OUT", ignoreCase = true)) "OUT" else "IN"
                            val stage = tokens.getOrNull(3)?.trim() ?: "خياطة"
                            val piecesStr = tokens.getOrNull(4)?.trim() ?: "0"
                            val rollsStr = tokens.getOrNull(5)?.trim() ?: "0"
                            val weightStr = tokens.getOrNull(6)?.trim() ?: "0.0"
                            val dest = tokens.getOrNull(7)?.trim() ?: ""
                            val resp = tokens.getOrNull(8)?.trim() ?: "أمين المخزن"
                            val date = tokens.getOrNull(9)?.trim()?.takeIf { it.isNotBlank() } ?: defaultDate
                            val doc = tokens.getOrNull(10)?.trim() ?: ""
                            val notes = tokens.getOrNull(11)?.trim() ?: "مستورد من Excel"

                            val pieces = piecesStr.toIntOrNull() ?: 0
                            val rolls = rollsStr.toIntOrNull() ?: 0
                            val weight = weightStr.toDoubleOrNull() ?: 0.0

                            // Validation rules
                            val errors = mutableListOf<String>()
                            if (cutNum.isBlank()) errors.add("رقم القصة مطلوب")
                            if (model.isBlank()) errors.add("اسم الموديل مطلوب")
                            if (pieces <= 0) errors.add("عدد القطع يجب أن يكون أكبر من 0")

                            val isValid = errors.isEmpty()

                            previewRows.add(
                                ImportPreviewRow(
                                    rowIndex = lineNum,
                                    cutNumber = cutNum,
                                    modelName = model,
                                    type = type,
                                    stage = stage,
                                    pieces = pieces,
                                    rolls = rolls,
                                    weight = weight,
                                    destination = dest,
                                    responsible = resp,
                                    dateStr = date,
                                    docNumber = doc,
                                    notes = notes,
                                    isValid = isValid,
                                    errorMessage = errors.joinToString("، ")
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return previewRows
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var insideQuotes = false

        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '\"' -> {
                    if (insideQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                        sb.append('\"')
                        i++
                    } else {
                        insideQuotes = !insideQuotes
                    }
                }
                (c == ',' || c == ';' || c == '\t') && !insideQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.setLength(0)
                }
                else -> {
                    sb.append(c)
                }
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }
}
