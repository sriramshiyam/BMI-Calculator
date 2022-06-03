package org.code.bmicalculator

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var kgInput : EditText
    private lateinit var cmInput : EditText
    private lateinit var calculateButton : Button
    private lateinit var numberView : TextView
    private lateinit var descriptionView : TextView
    private lateinit var sf : SharedPreferences
    private lateinit var edit : SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        kgInput = findViewById(R.id.etWeight)
        cmInput = findViewById(R.id.etHeight)
        calculateButton = findViewById(R.id.calculate)
        numberView = findViewById(R.id.number)
        descriptionView = findViewById(R.id.description)

        sf = getSharedPreferences("b_m_i", MODE_PRIVATE)
        edit = sf.edit()

        calculateButton.setOnClickListener {
            val kg = kgInput.text.toString()
            val cm = cmInput.text.toString()
            if (validateInput(kg, cm)){
                cmInput.clearFocus()
                kgInput.clearFocus()
                val bmi = "%.1f".format(kg.toInt() /
                        ((cm.toInt().toDouble() / 100) *
                            (cm.toInt().toDouble() / 100))
                ).toDouble()
                numberView.text = bmi.toString()
                descriptionView.text = calculate(bmi)
            }
        }
    }

    private fun validateInput(weight:String?, height:String?): Boolean {
        return when {
            weight.isNullOrEmpty() && height.isNullOrEmpty() -> {
                Toast.makeText(this, "Weight and Height are empty", Toast.LENGTH_SHORT).show()
                false
            }
            weight.isNullOrEmpty() -> {
                Toast.makeText(this, "Weight is empty", Toast.LENGTH_SHORT).show()
                false
            }
            height.isNullOrEmpty() -> {
                Toast.makeText(this, "Height is empty", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun calculate(bmi: Double?): String {
        var result = ""
        if (bmi != null) {
            when {
                bmi < 18.5 -> result = getString(R.string.underweight)
                bmi in 18.5..24.9 -> result = getString(R.string.healthyweight)
                bmi in 25.0..29.9 -> result = getString(R.string.overweight)
                bmi > 29.0 -> result = getString(R.string.obesity)
            }
        }
        return result
    }

    override fun onPause() {
        super.onPause()
        val kg = kgInput.text.toString()
        val cm = cmInput.text.toString()
        val number = numberView.text.toString()
        val description = descriptionView.text.toString()
        edit.apply {
            putInt("bmi_kg", if (kg.isEmpty()) "0".toInt() else kg.toInt())
            putInt("bmi_cm", if (cm.isEmpty()) "0".toInt() else cm.toInt())
            putString("bmi_num", number)
            putString("bmi_desc", description)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        val kg = sf.getInt("bmi_kg", 0)
        val cm = sf.getInt("bmi_cm", 0)
        val number = sf.getString("bmi_num", null)
        val description = sf.getString("bmi_desc", null)
        if (number != null){
            kgInput.setText(kg.toString())
            cmInput.setText(cm.toString())
            numberView.text = number
            descriptionView.text = description
        }
    }
}
