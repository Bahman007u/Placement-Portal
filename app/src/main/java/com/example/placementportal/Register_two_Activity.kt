package com.example.placementportal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.placementportal.databinding.ActivityRegisterTwoBinding
import com.example.placementportal.model.Student

class Register_two_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterTwoBinding

    // Dropdown Data
    private val wholeCgpaList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private val decimalCgpaList = arrayOf(".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9")
    private val backlogList = (0..15).map { it.toString() }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val student = intent.getParcelableExtra<Student>("STUDENT_DATA")

        setupDropdowns()

        binding.etStartYear.setOnClickListener {
            showYearPicker { year -> binding.etStartYear.setText(year) }
        }

        binding.etEndYear.setOnClickListener {
            showYearPicker { year -> binding.etEndYear.setText(year) }
        }

        binding.btnAcademicNext.setOnClickListener {
            val whole = binding.autoCgpaWhole.text.toString()
            val decimal = binding.autoCgpaDecimal.text.toString()
            val backlogsText = binding.autoBacklogs.text.toString()
            val startYearText = binding.etStartYear.text.toString()
            val endYearText = binding.etEndYear.text.toString()

            if (whole.isEmpty() || decimal.isEmpty() || backlogsText.isEmpty() || startYearText.isEmpty() || endYearText.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Logic: Combine Whole + Decimal (e.g. "7" + ".5" = "7.5")
            // Edge case: If whole is 10, decimal should ideally stay .0
            val combinedCgpa = if (whole == "10") "10.0" else "$whole$decimal"

            if (startYearText.toInt() > endYearText.toInt()) {
                binding.etStartYear.error = "Invalid start year"
                return@setOnClickListener
            }

            student?.apply {
                this.cgpa = combinedCgpa
                this.backlogs = backlogsText
                this.startYear = startYearText
                this.endYear = endYearText
            }

            val intent = Intent(this, Register_three_Activity::class.java)
            intent.putExtra("STUDENT_DATA", student)
            startActivity(intent)
        }
    }

    private fun setupDropdowns() {
        val wholeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wholeCgpaList)
        binding.autoCgpaWhole.setAdapter(wholeAdapter)

        val decimalAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, decimalCgpaList)
        binding.autoCgpaDecimal.setAdapter(decimalAdapter)

        val backlogAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, backlogList)
        binding.autoBacklogs.setAdapter(backlogAdapter)
    }

    private fun showYearPicker(onYearSelected: (String) -> Unit) {
        val numberPicker = NumberPicker(this).apply {
            minValue = 1940
            maxValue = 2030
            value = 2024
            wrapSelectorWheel = false
        }

        AlertDialog.Builder(this)
            .setTitle("Select Year")
            .setView(numberPicker)
            .setPositiveButton("OK") { _, _ -> onYearSelected(numberPicker.value.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}