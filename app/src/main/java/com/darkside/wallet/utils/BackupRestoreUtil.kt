package com.darkside.wallet.utils

import android.content.Context
import android.net.Uri
import com.darkside.wallet.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Phase 10: Full App Backup & Restore Engine
 * 
 * Backs up:
 * 1. Room Database (wallet_database)
 * 2. SharedPreferences (wallet_prefs)
 * 3. Profile Photos (filesDir/profile_photo_*)
 */
object BackupRestoreUtil {
    
    private const val DB_NAME = "wallet_database"
    private const val PREFS_NAME = "wallet_prefs"

    suspend fun createFullBackup(context: Context, destinationUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                    ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                        
                        // 1. Backup Database
                        val dbFile = context.getDatabasePath(DB_NAME)
                        if (dbFile.exists()) {
                            // Room usually has -shm and -wal files too
                            backupFile(dbFile, "database/$DB_NAME", zipOut)
                            backupFile(File(dbFile.path + "-shm"), "database/$DB_NAME-shm", zipOut)
                            backupFile(File(dbFile.path + "-wal"), "database/$DB_NAME-wal", zipOut)
                        }

                        // 2. Backup Preferences
                        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        val allEntries = prefs.all
                        val prefsJson = JSONObject(allEntries)
                        
                        zipOut.putNextEntry(ZipEntry("preferences/settings.json"))
                        zipOut.write(prefsJson.toString().toByteArray())
                        zipOut.closeEntry()

                        // 3. Backup Photos
                        val filesDir = context.filesDir
                        filesDir.listFiles()?.forEach { file ->
                            if (file.name.startsWith("profile_photo_")) {
                                backupFile(file, "photos/${file.name}", zipOut)
                            }
                        }
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun backupFile(file: File, zipPath: String, zipOut: ZipOutputStream) {
        if (!file.exists()) return
        zipOut.putNextEntry(ZipEntry(zipPath))
        file.inputStream().use { it.copyTo(zipOut) }
        zipOut.closeEntry()
    }

    suspend fun restoreFullBackup(context: Context, sourceUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Close DB connection
                AppDatabase.getDatabase(context).close()

                context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                        var entry = zipIn.nextEntry
                        while (entry != null) {
                            val path = entry.name
                            when {
                                path.startsWith("database/") -> {
                                    val dbFile = File(context.dataDir, "databases/${p.basename(path)}")
                                    dbFile.parentFile?.mkdirs()
                                    restoreFile(zipIn, dbFile)
                                }
                                path == "preferences/settings.json" -> {
                                    val jsonStr = zipIn.bufferedReader().readText()
                                    restorePreferences(context, jsonStr)
                                }
                                path.startsWith("photos/") -> {
                                    val photoFile = File(context.filesDir, p.basename(path))
                                    restoreFile(zipIn, photoFile)
                                }
                            }
                            zipIn.closeEntry()
                            entry = zipIn.nextEntry
                        }
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun restoreFile(zipIn: ZipInputStream, targetFile: File) {
        targetFile.outputStream().use { zipIn.copyTo(it) }
    }

    private fun restorePreferences(context: Context, jsonStr: String) {
        val json = JSONObject(jsonStr)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.clear()
        
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.get(key)
            when (value) {
                is String -> prefs.putString(key, value)
                is Boolean -> prefs.putBoolean(key, value)
                is Int -> prefs.putInt(key, value)
                is Long -> prefs.putLong(key, value)
                is Double -> prefs.putFloat(key, value.toFloat())
            }
        }
        prefs.apply()
    }

    // Helper to get basename
    private object p {
        fun basename(path: String): String = path.substringAfterLast('/')
    }
}
