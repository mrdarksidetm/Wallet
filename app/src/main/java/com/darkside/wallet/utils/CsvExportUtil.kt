package com.darkside.wallet.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.darkside.wallet.data.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CsvExportUtil {
    /**
     * CRITICAL: Offline-First CSV Export
     * This utility formats transactions into a strictly formatted CSV string and uses the 
     * Android MediaStore API (Storage Access Framework) to save the file securely to the 
     * 'Downloads' folder. 
     */
    suspend fun exportTransactionsToCsv(context: Context, transactions: List<TransactionEntity>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val csvHeader = "ID,UUID,Amount,Note,Date,Type,CategoryID,AccountID,PersonID,TransferAccountID,Tags\n"
                val csvData = transactions.joinToString(separator = "\n") {
                    "${it.id},${it.uuid},${it.amount},${it.note?.replace(",", " ") ?: ""},${it.date},${it.type.name},${it.categoryId},${it.accountId},${it.personId},${it.transferAccountId ?: ""},${it.tags ?: ""}"
                }
                
                val fileContent = csvHeader + csvData

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Wallet_Transactions_Export_${System.currentTimeMillis()}.csv")
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
