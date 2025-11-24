package com.example.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebase.databinding.FragmentAddBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var databaseReference: DatabaseReference

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

        databaseReference = FirebaseDatabase.getInstance().getReference("expenses")

        binding.saveExpense.setOnClickListener {

            val amount = binding.amount.text.toString()
            val date = binding.date.text.toString()

            if (amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expenseId = databaseReference.push().key!!

            val expense = Expense(
                amount = amount,
                date = date
            )

            databaseReference.child(expenseId).setValue(expense)
                .addOnSuccessListener {
                    binding.amount.text.clear()
                    binding.date.text.clear()

                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
