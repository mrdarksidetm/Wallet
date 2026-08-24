package com.darkside.wallet.utils

import android.content.Context
import android.net.Uri
import com.darkside.wallet.data.domain.TransactionService
import com.darkside.wallet.data.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvImportUtil {
    suspend fun importTransactionsFromCsv(
        context: Context,
        uri: Uri,
        transactionService: TransactionService
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val header = reader.readLine() // Skip header
                    
                    var line: String? = reader.readLine()
                    while (line != null) {
                        val parts = line.split(",")
                        if (parts.size >= 8) {
                            // ID,UUID,Amount,Note,Date,Type,CategoryID,AccountID,PersonID,TransferAccountID,Tags
                            val amount = parts[2].toDoubleOrNull() ?: 0.0
                            val note = parts[3].ifEmpty { null }
                            val date = parts[4].toLongOrNull() ?: System.currentTimeMillis()
                            val type = try { TransactionType.valueOf(parts[5]) } catch (e: Exception) { TransactionType.EXPENSE }
                            val categoryId = parts[6].toLongOrNull() ?: 0L
                            val accountId = parts[7].toLongOrNull() ?: 0L
                            val personId = if (parts.size > 8) parts[8].toLongOrNull() ?: 0L else 0L
                            val transferAccountId = if (parts.size > 9) parts[9].toLongOrNull() else null
                            val tags = if (parts.size > 10) parts[10].ifEmpty { null } else null

                            if (amount > 0 && accountId > 0) {
                                transactionService.addTransaction(
                                    amount = amount,
                                    date = date,
                                    type = type,
                                    accountId = accountId,
                                    categoryId = categoryId,
                                    personId = personId,
                                    note = note,
                                    transferAccountId = transferAccountId,
                                    tags = tags
                                )
                            }
                        }
                        line = reader.readLine()
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
