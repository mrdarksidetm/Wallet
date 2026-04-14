package com.darkside.wallet.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.darkside.wallet.data.domain.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CsvExportUtil {
    /**
     * CRITICAL: Offline-First CSV Export
     * This utility formats transactions into a strictly formatted CSV string and uses the 
     * Android MediaStore API (Storage Access Framework) to save the file securely to the 
     * 'Downloads' folder. 
     * 
     * Why MediaStore? 
     * Android 14 (API 34) aggressively restricts legacy storage permissions. MediaStore 
     * allows us to write to public directories like Downloads without requesting invasive 
     * MANAGE_EXTERNAL_STORAGE permissions. The entire process happens locally; absolutely 
     * zero network calls are made, adhering strictly to the SPEC.md mandates.
     */
    suspend fun exportTransactionsToCsv(context: Context, transactions: List<Transaction>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val csvHeader = "ID,Amount,Note,Date,Type,CategoryID,AccountID\n"
                val csvData = transactions.joinToString(separator = "\n") {
                    "${it.id},${it.amount},${it.note?.replace(",", " ") ?: ""},${it.date},${it.type.name},${it.categoryId},${it.accountId}"
                }
                
                val fileContent = csvHeader + csvData

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Wallet_Export_${System.currentTimeMillis()}.csv")
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/WalletApp")
                }

                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(fileContent.toByteArray())
                    }
                    true
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
