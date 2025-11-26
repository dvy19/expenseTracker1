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

    private lateinit var profileImage: ImageView
    private lateinit var selectUserImage: Button

    private var selectImageUri:Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Save data under this user’s UID
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // 🔹 Date Picker
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
                city = city
            )

            // Save data under user UID
            databaseReference.child(uid).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()

                    // Go to main screen
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
