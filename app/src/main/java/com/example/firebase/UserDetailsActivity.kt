package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.UserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: UserInfoBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //gets the user id of the current login user,
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Setup Gender Spinner
        setupGenderSpinner()

        // Setup Age Group Spinner
        setupAgeGroupSpinner()

        // 🔹 Handle Save Button
        binding.userSignup.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            val fullName = binding.userFullName.text.toString().trim()
            val selectedGender = binding.genderSpinner.selectedItem.toString()
            val selectedAgeGroup = binding.ageGroupSpinner.selectedItem.toString()


            // Validate fields
            if (fullName.isEmpty() ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate gender selection (first item is "Select Gender")
            if (selectedGender == "Select Gender") {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate age group selection (first item is "Select Age Group")
            if (selectedAgeGroup == "Select Age Group") {
                Toast.makeText(this, "Please select your age group", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uid == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                name = fullName,
                gender = selectedGender,
                ageGroup = selectedAgeGroup, // Using ageGroup instead of dob
                profileImageUrl = "" // Default empty, you can update this later
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

    private fun setupGenderSpinner() {
        val genderOptions = arrayOf("Select Gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = adapter
    }

    private fun setupAgeGroupSpinner() {
        val ageGroupOptions = arrayOf("Select Age Group", "13-18", "18-24", "25+")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageGroupOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageGroupSpinner.adapter = adapter
    }
}