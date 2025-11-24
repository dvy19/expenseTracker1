package com.example.firebase

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.firebase.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.add.setOnClickListener{
            goToFragment(AddFragment())
        }

        binding.home.setOnClickListener{
            goToFragment(HomeFragment())
        }

        binding.profile.setOnClickListener{
            goToFragment(ProfileFragment())
        }



    }

    private fun goToFragment(fragment:Fragment){

        fragmentManager=supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container,fragment).commit()


    }
}