package com.mrdarksidetm.wallet.utils

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Phase 42: Local Crash Log Export (Zero-Network Crashlytics)
 *
 * CRITICAL: Zero-Network & Storage Limits
 * Since we operate offline-first, we intercept the JVM UncaughtExceptionHandler.
 * When a fatal crash occurs, the stack trace is written locally to the app's cache directory.
 * To prevent storage bloat on 4GB constraint devices, the file is strictly capped at 2MB.
 * If it exceeds this size, it resets. The user can export this via the Settings UI.
 */
class LocalCrashReporter(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val crashFile = File(context.cacheDir, "wallet_crash_logs.txt")
    private val MAX_FILE_SIZE = 2 * 1024 * 1024 // 2MB

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            if (crashFile.exists() && crashFile.length() > MAX_FILE_SIZE) {
                crashFile.delete() // Rolling log protection
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val writer = PrintWriter(FileWriter(crashFile, true))
            writer.println("--- Crash at $timestamp ---")
            e.printStackTrace(writer)
            writer.println("---------------------------")
            writer.close()
        } catch (ex: Exception) {
            // Failsafe, do nothing if we can't write the log
        } finally {
            defaultHandler?.uncaughtException(t, e)
        }
    }
}
