package com.darkside.wallet.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.darkside.wallet.MainActivity

/**
 * Phase 15: Home Screen Shortcuts & Deep Linking
 * 
 * Creates a dynamic home screen shortcut that lets users instantly jump
 * into the Add Transaction screen without navigating the Dashboard first.
 */
object ShortcutsUtil {
    fun setupShortcuts(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("route", "/add-transaction")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val shortcut = ShortcutInfoCompat.Builder(context, "add_expense")
            .setShortLabel("Add Expense")
            .setLongLabel("Log a new expense")
            // .setIcon(IconCompat.createWithResource(context, R.drawable.ic_add_expense))
            .setIntent(intent)
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }
}
