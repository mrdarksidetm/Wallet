# Phase 14: Release Preparation & Memory Optimization

# CRITICAL: 4GB RAM Optimization Rules
# 1. Shrink resources to remove unused Material 3 vectors.
# 2. Obfuscate code to reduce DEX size.
# 3. Preserve Room database classes from obfuscation so SQL queries don't break.

-keep class com.mrdarksidetm.wallet.data.domain.** { *; }
-keep class com.mrdarksidetm.wallet.data.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Preserve Vico charts and native canvas elements
-keep class com.patrykandpatrick.vico.** { *; }

# General optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
