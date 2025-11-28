package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.UserBudgetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class UserBudget : AppCompatActivity() {

    private lateinit var binding: UserBudgetBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        binding.saveBudget.setOnClickListener {
            val budgetText = binding.userBudget.text.toString().trim()

            if (budgetText.isEmpty()) {
                Toast.makeText(applicationContext, "Please Enter a Budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newBudget = budgetText.toDoubleOrNull()
            if (newBudget == null) {
                Toast.makeText(applicationContext, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uid == null) {
                Toast.makeText(applicationContext, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate unique budget ID
            val budgetId = databaseReference.child(uid).child("budgets").push().key
                ?: UUID.randomUUID().toString()

            val budget = Budget(
                budgetId = budgetId,
                amount = newBudget,
                startDate = System.currentTimeMillis(),
                active = true
            )

            // Save to user-specific path "users/uid/budgets/budgetId"
            databaseReference.child(uid).child("budgets").child(budgetId).setValue(budget)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Budget Saved Successfully!", Toast.LENGTH_SHORT).show()
                    binding.userBudget.text.clear()


                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Error saving budget: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}