package com.darkside.wallet.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel {
    INFO,
    PERFORMANCE,
    WARNING,
    ERROR
}

data class LogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val message: String,
    val stackTrace: String? = null
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(timestamp))
}

/**
 * Diagnostic log collector service for local, zero-network runtime debugging and monitoring.
 */
object LogService {
    private const val MAX_LOGS = 500
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    fun log(level: LogLevel, message: String, stackTrace: String? = null) {
        val entry = LogEntry(level = level, message = message, stackTrace = stackTrace)
        val currentList = _logs.value.toMutableList()
        if (currentList.size >= MAX_LOGS) {
            currentList.removeAt(0)
        }
        currentList.add(entry)
        _logs.value = currentList
    }

    fun info(message: String) = log(LogLevel.INFO, message)
    fun performance(message: String) = log(LogLevel.PERFORMANCE, message)
    fun warning(message: String) = log(LogLevel.WARNING, message)
    fun error(message: String, stackTrace: String? = null) = log(LogLevel.ERROR, message, stackTrace)

    fun clear() {
        _logs.value = emptyList()
    }
}
