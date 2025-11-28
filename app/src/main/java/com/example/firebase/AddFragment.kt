package com.example.firebase

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebase.databinding.FragmentAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentActiveBudgetId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Get the current active budget when fragment starts
        getCurrentActiveBudget()

        binding.date.setOnClickListener {
            showDatePicker()
        }

        binding.saveExpense.setOnClickListener {
            saveExpenseToUserDatabase()
        }
    }

    private fun getCurrentActiveBudget() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        databaseReference.child(userId).child("budgets")
            .orderByChild("active").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get the first active budget
                        for (budgetSnapshot in snapshot.children) {
                            currentActiveBudgetId = budgetSnapshot.key
                            val budget = budgetSnapshot.getValue(Budget::class.java)
                            Toast.makeText(
                                requireContext(),
                                "Adding to budget: ${budget?.amount}",
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No active budget found. Please set a budget first.",
                            Toast.LENGTH_LONG
                        ).show()
                        currentActiveBudgetId = null
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error checking budget", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveExpenseToUserDatabase() {
        val amount = binding.amount.text.toString()
        val date = binding.date.text.toString()
        val category = binding.category.text.toString()

        if (amount.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if there's an active budget
        if (currentActiveBudgetId == null) {
            Toast.makeText(requireContext(), "No active budget found. Please set a budget first.", Toast.LENGTH_LONG).show()
            return
        }

        val userId = currentUser.uid
        val expenseId = databaseReference.child(userId).child("expenses").push().key!!

        // Create expense with budget reference
        val expense = Expense(
            amount = amount,
            date = date,
            category = category,
            budgetId = currentActiveBudgetId // Link to current active budget
        )

        // Save expense under user's expenses
        databaseReference.child(userId).child("expenses").child(expenseId).setValue(expense)
            .addOnSuccessListener {
                // Update the budget's total spent
                updateBudgetSpent(amount.toDoubleOrNull() ?: 0.0)

                // Clear fields after successful save
                binding.amount.text.clear()
                binding.date.text.clear()
                binding.category.text.clear()
                Toast.makeText(requireContext(), "Expense saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save expense: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateBudgetSpent(expenseAmount: Double) {
        val userId = auth.currentUser?.uid ?: return
        val budgetId = currentActiveBudgetId ?: return

        // Get current budget and update total spent
        databaseReference.child(userId).child("budgets").child(budgetId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val budget = snapshot.getValue(Budget::class.java)
                    budget?.let {
                        val newTotalSpent = (it.totalSpent ?: 0.0) + expenseAmount

                        // Update budget with new total spent
                        databaseReference.child(userId).child("budgets").child(budgetId)
                            .child("totalSpent").setValue(newTotalSpent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error updating budget", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                binding.date.setText(formattedDate)
            }, year, month, day
        )
        datePicker.show()
    }
}