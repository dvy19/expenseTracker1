package com.example.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var expenseList: ArrayList<Expense>
    private lateinit var adapter: ExpenseAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var userBudget: Double = 0.0
    private var userName: String = ""
    private var totalSpent: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

        expenseList = ArrayList()
        adapter = ExpenseAdapter(expenseList)
        binding.recyclerView.adapter = adapter

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        // Load user profile and active budget
        loadUserProfileAndBudget()
    }

    private fun loadUserProfileAndBudget() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        val userId = currentUser.uid

        // First load user profile
        databaseRef.child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(profileSnapshot: DataSnapshot) {
                if (profileSnapshot.exists()) {
                    val user = profileSnapshot.getValue(User::class.java)
                    userName = user?.name ?: "User"
                    binding.usernameDisplay.text = "$userName!"
                } else {
                    binding.usernameDisplay.text = "Hello, User!"
                }

                // Then load active budget
                loadActiveBudget()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                binding.usernameDisplay.text = "Hello, User!"
                loadActiveBudget()
            }
        })
    }

    private fun loadActiveBudget() {
        databaseRef.child("budgets")
            .orderByChild("active").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get the first active budget
                        for (budgetSnapshot in snapshot.children) {
                            val budget = budgetSnapshot.getValue(Budget::class.java)
                            budget?.let {
                                userBudget = it.amount
                                totalSpent = it.totalSpent ?: 0.0

                                // Update UI with budget data

                                updateBudgetProgress()

                                // Now load expenses
                                loadExpenses()
                            }
                            return
                        }
                    } else {
                        // No active budget found

                        binding.totalExpense.text = "₹0"
                        binding.expenseProgress.progress = 0
                        Toast.makeText(requireContext(), "No active budget set", Toast.LENGTH_SHORT).show()
                        loadExpenses()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load budget", Toast.LENGTH_SHORT).show()

                    loadExpenses()
                }
            })
    }

    private fun loadExpenses() {
        val expensesRef = databaseRef.child("expenses")

        expensesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()

                // If we don't have budget data yet, calculate from expenses
                var calculatedTotalSpent = 0.0

                if (!snapshot.exists()) {
                    binding.totalExpense.text = "₹0"
                    updateBudgetProgress()
                    adapter.notifyDataSetChanged()
                    return
                }

                for (data in snapshot.children) {
                    val expense = data.getValue(Expense::class.java)
                    if (expense != null) {
                        expenseList.add(expense)
                        // Add to calculated total if we need it
                        calculatedTotalSpent += expense.amount?.toDoubleOrNull() ?: 0.0
                    }
                }

                // If we don't have totalSpent from budget, use calculated value
                if (totalSpent == 0.0) {
                    totalSpent = calculatedTotalSpent
                }

                binding.totalExpense.text = "₹${totalSpent.toInt()}"
                updateBudgetProgress()
                adapter.notifyDataSetChanged()

                // Show message if no expenses
                if (expenseList.isEmpty()) {
                    binding.totalExpense.text = "₹0"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load expenses: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBudgetProgress() {
        val progress = if (userBudget > 0) {
            (totalSpent / userBudget * 100).toInt()
        } else {
            0
        }

        val safeProgress = progress.coerceAtMost(100)
        binding.expenseProgress.progress = safeProgress

        // Optional: Show budget status message
        val remaining = userBudget - totalSpent

    }
}