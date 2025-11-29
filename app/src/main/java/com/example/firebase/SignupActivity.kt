package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivitySignupBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        binding.toLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }
        binding.signUp.setOnClickListener{
            var mail=binding.eMail.text.toString().trim()
            var password=binding.password.text.toString()


            if (password.length < 6) {
                binding.password.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }
            if(mail.isEmpty() && password.isEmpty()){
                Toast.makeText(applicationContext,"Please Enter details",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener{
                    task->
                    if(task.isSuccessful){

                        val user=auth.currentUser

                        user?.getIdToken(true)
                            ?.addOnSuccessListener { result ->
                                val idToken = result.token
                                Toast.makeText(this, "$idToken", Toast.LENGTH_SHORT).show()
                                // redirect to home/dashboard screen
                            }

                        val intent=Intent(this, UserDetailsActivity::class.java)
                        startActivity(intent)
                        finish()


                    }

                    else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                    }
                }


        }


    }
