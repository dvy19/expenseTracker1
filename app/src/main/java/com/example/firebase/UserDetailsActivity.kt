package com.example.firebase

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.UserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: UserInfoBinding
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ UPDATED: Better database structure - Save under "users/uid/profile"
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // 🔹 Date Picker (Keep as is - it's perfect)
        binding.userDOB.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.userDOB.setText("$selectedDay-${selectedMonth + 1}-$selectedYear")
                }, year, month, day
            )
            datePicker.show()
        }

        // 🔹 Handle Save Button
        binding.userSignup.setOnClickListener {

            val full_name = binding.userFullName.text.toString().trim()
            val city = binding.userCity.text.toString().trim()
            val dob = binding.userDOB.text.toString().trim()
            val gender = binding.userGender.text.toString().trim()



            if (full_name.isEmpty() || city.isEmpty() || dob.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uid == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                name = full_name,
                gender = gender,
                dob = dob,
                city = city,

            )

            // ✅ UPDATED: Save to user-specific path "users/uid/profile"
            databaseReference.child(uid).child("profile").setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()

                    // Go to main screen
                    startActivity(Intent(this, UserBudget::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving profile: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}