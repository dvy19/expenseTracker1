package com.example.firebase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.FragmentHomeBinding
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var expenseList: ArrayList<Expense>
    private lateinit var adapter: ExpenseAdapter
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

        expenseList = ArrayList()
        adapter = ExpenseAdapter(expenseList)
        binding.recyclerView.adapter = adapter

        // Firebase Reference
        databaseRef = FirebaseDatabase.getInstance().getReference("expenses")

        loadExpenses()   // fetch data
    }

    private fun loadExpenses() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()   // Avoid duplicates
                var total = 0.0  // Changed from 'val' to 'var'

                val budget=500;





                for (data in snapshot.children) {
                    val expense = data.getValue(Expense::class.java)
                    if (expense != null) {
                        expenseList.add(expense)
                        // Safe conversion to Double
                        total += expense.amount?.toDoubleOrNull() ?: 0.0
                    }
                }

                val percent=(total.toFloat()/ budget.toFloat())*100f

                binding.expenseProgress.progress=percent.toInt()

                adapter.notifyDataSetChanged()
                binding.totalExpense.text = "${total}"

                if (expenseList.isEmpty()) {
                    binding.totalExpense.text = "₹ -"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}