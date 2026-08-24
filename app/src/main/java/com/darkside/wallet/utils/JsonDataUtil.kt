package com.darkside.wallet.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.darkside.wallet.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Utility for Full JSON Data Portability
 * Exports/Imports all app entities to/from a single JSON file.
 * Matches current Room schema exactly.
 */
object JsonDataUtil {

    private const val EXPORT_VERSION = 1

    suspend fun exportDataToJson(
        context: Context,
        accounts: List<AccountEntity>,
        categories: List<CategoryEntity>,
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        goals: List<GoalEntity>,
        people: List<PersonEntity>,
        loans: List<LoanEntity>,
        recurring: List<RecurringEntity>
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val root = JSONObject()
                root.put("version", EXPORT_VERSION)
                root.put("exportedAt", System.currentTimeMillis())

                root.put("accounts", JSONArray().apply {
                    accounts.forEach { acc ->
                        put(JSONObject().apply {
                            put("uuid", acc.uuid)
                            put("name", acc.name)
                            put("bankName", acc.bankName)
                            put("number", acc.number)
                            put("validThru", acc.validThru)
                            put("icon", acc.icon)
                            put("color", acc.color)
                            put("isPredefined", acc.isPredefined)
                            put("balance", acc.balance)
                            put("isArchived", acc.isArchived)
                            put("isDeleted", acc.isDeleted)
                            put("isDefault", acc.isDefault)
                            put("createdAt", acc.createdAt)
                            put("updatedAt", acc.updatedAt)
                            put("order", acc.order)
                            put("type", acc.type.name)
                        })
                    }
                })

                root.put("categories", JSONArray().apply {
                    categories.forEach { cat ->
                        put(JSONObject().apply {
                            put("uuid", cat.uuid)
                            put("name", cat.name)
                            put("description", cat.description)
                            put("icon", cat.icon)
                            put("color", cat.color)
                            put("budgetLimit", cat.budgetLimit ?: JSONObject.NULL)
                            put("isBudget", cat.isBudget)
                            put("isPredefined", cat.isPredefined)
                            put("isDeleted", cat.isDeleted)
                            put("createdAt", cat.createdAt)
                            put("updatedAt", cat.updatedAt)
                            put("type", cat.type.name)
                        })
                    }
                })

                root.put("transactions", JSONArray().apply {
                    transactions.forEach { tx ->
                        put(JSONObject().apply {
                            put("uuid", tx.uuid)
                            put("amount", tx.amount)
                            put("note", tx.note ?: JSONObject.NULL)
                            put("date", tx.date)
                            put("type", tx.type.name)
                            put("categoryId", tx.categoryId)
                            put("accountId", tx.accountId)
                            put("personId", tx.personId ?: JSONObject.NULL)
                            put("transferAccountId", tx.transferAccountId ?: JSONObject.NULL)
                            put("tags", tx.tags ?: JSONObject.NULL)
                            put("icon", tx.icon ?: JSONObject.NULL)
                            put("color", tx.color ?: JSONObject.NULL)
                            put("isTemplate", tx.isTemplate)
                            put("createdAt", tx.createdAt)
                            put("updatedAt", tx.updatedAt)
                        })
                    }
                })

                root.put("budgets", JSONArray().apply {
                    budgets.forEach { b ->
                        put(JSONObject().apply {
                            put("uuid", b.uuid)
                            put("amount", b.amount)
                            put("categoryId", b.categoryId)
                            put("period", b.period.name)
                            put("startDate", b.startDate)
                            put("endDate", b.endDate)
                            put("isActive", b.isActive)
                            put("isDeleted", b.isDeleted)
                            put("createdAt", b.createdAt)
                            put("updatedAt", b.updatedAt)
                        })
                    }
                })

                root.put("goals", JSONArray().apply {
                    goals.forEach { g ->
                        put(JSONObject().apply {
                            put("uuid", g.uuid)
                            put("name", g.name)
                            put("targetAmount", g.targetAmount)
                            put("currentAmount", g.currentAmount)
                            put("deadline", g.deadline)
                            put("color", g.color)
                            put("icon", g.icon ?: JSONObject.NULL)
                            put("accountId", g.accountId ?: JSONObject.NULL)
                            put("isCompleted", g.isCompleted)
                            put("isDeleted", g.isDeleted)
                            put("createdAt", g.createdAt)
                            put("updatedAt", g.updatedAt)
                        })
                    }
                })

                root.put("people", JSONArray().apply {
                    people.forEach { p ->
                        put(JSONObject().apply {
                            put("uuid", p.uuid)
                            put("name", p.name)
                            put("contact", p.contact ?: JSONObject.NULL)
                            put("avatar", p.avatar ?: JSONObject.NULL)
                            put("color", p.color)
                            put("isDeleted", p.isDeleted)
                            put("createdAt", p.createdAt)
                            put("updatedAt", p.updatedAt)
                        })
                    }
                })

                root.put("loans", JSONArray().apply {
                    loans.forEach { l ->
                        put(JSONObject().apply {
                            put("uuid", l.uuid)
                            put("personId", l.personId)
                            put("amount", l.amount)
                            put("type", l.type.name)
                            put("dueDate", l.dueDate ?: JSONObject.NULL)
                            put("isPaid", l.isPaid)
                            put("isActive", l.isActive)
                            put("note", l.note ?: JSONObject.NULL)
                            put("isDeleted", l.isDeleted)
                            put("createdAt", l.createdAt)
                            put("updatedAt", l.updatedAt)
                        })
                    }
                })

                root.put("recurring", JSONArray().apply {
                    recurring.forEach { r ->
                        put(JSONObject().apply {
                            put("uuid", r.uuid)
                            put("name", r.name)
                            put("amount", r.amount)
                            put("type", r.type.name)
                            put("accountId", r.accountId)
                            put("categoryId", r.categoryId)
                            put("transferAccountId", r.transferAccountId ?: JSONObject.NULL)
                            put("frequency", r.frequency.name)
                            put("nextDate", r.nextDate)
                            put("endDate", r.endDate ?: JSONObject.NULL)
                            put("isActive", r.isActive)
                            put("isDeleted", r.isDeleted)
                            put("createdAt", r.createdAt)
                            put("updatedAt", r.updatedAt)
                        })
                    }
                })

                val jsonContent = root.toString(2)

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Wallet_Data_Portability_${System.currentTimeMillis()}.json")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/WalletApp")
                }

                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(jsonContent.toByteArray())
                    }
                    true
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun importDataFromJson(
        context: Context,
        uri: Uri,
        onImport: suspend (JSONObject) -> Boolean
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonStr = reader.readText()
                    val root = JSONObject(jsonStr)
                    onImport(root)
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
