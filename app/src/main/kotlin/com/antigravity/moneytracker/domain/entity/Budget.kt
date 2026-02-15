package com.antigravity.moneytracker.domain.entity

import java.util.Date

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val startDate: Date,
    val endDate: Date,
    val spent: Double = 0.0
)
