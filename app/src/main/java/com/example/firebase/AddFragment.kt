package com.example.firebase


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebase.databinding.FragmentAddBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

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



        binding.date.setOnClickListener {
            showDatePicker()
        }

        binding.saveExpense.setOnClickListener {

            val amount = binding.amount.text.toString()
            val date = binding.date.text.toString()
            val category=binding.category.text.toString()

            if (amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            val expenseId = databaseReference.push().key!!

            val expense = Expense(
                amount = amount,
                date = date,
                category=category

            )

            databaseReference.child(expenseId).setValue(expense)
                .addOnSuccessListener {
                    binding.amount.text.clear()
                    binding.date.text.clear()
                    binding.category.text.clear()
                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
        }


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
