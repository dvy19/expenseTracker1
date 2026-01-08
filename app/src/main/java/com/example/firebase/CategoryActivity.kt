package com.example.firebase

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.CategoryBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding:CategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= CategoryBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val barChart = binding.barChart

        // Step 1: Create bar entries
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 5000f)) // Home
        entries.add(BarEntry(1f, 2000f)) // Travel
        entries.add(BarEntry(2f, 3000f)) // Education
        entries.add(BarEntry(3f, 1500f)) // Food

        // Step 2: Create dataset
        val barDataSet = BarDataSet(entries, "Expenses")
        barDataSet.valueTextSize = 14f

        // Step 3: Set data
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Step 4: Category labels
        val labels = listOf("Home", "Travel", "Education", "Food")

        barChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        barChart.xAxis.granularity = 1f
        barChart.description.text = "Monthly Expenses"
        barChart.animateY(1000)
    }
}