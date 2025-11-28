package com.example.firebase

data class Budget(
    val budgetId: String = "",
    val amount: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val active: Boolean = true,
    val totalSpent: Double = 0.0
)