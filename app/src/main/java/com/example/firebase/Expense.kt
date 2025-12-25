package com.example.firebase

data class Expense(
    val amount: String? = "",
    val note:String?="",
    val date: String? = "",
    val category: String? = "",
    val budgetId: String? = ""  // Add this field to link to budget
)