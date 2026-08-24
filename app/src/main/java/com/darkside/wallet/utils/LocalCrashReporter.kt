package com.darkside.wallet.utils

import android.content.Context
import android.content.Intent
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

            // Launch CrashActivity to show the error UI
            val intent = Intent(context, com.darkside.wallet.CrashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("EXTRA_ERROR_TRACE", e.stackTraceToString())
            }
            context.startActivity(intent)

        } catch (ex: Exception) {
            // Failsafe, do nothing if we can't write the log
        } finally {
            // Kill the process to ensure a clean state
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}
