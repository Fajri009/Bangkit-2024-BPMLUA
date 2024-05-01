package com.example.bangkit_2024_bpmlua.TensorFlowLite.Interpreter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bangkit_2024_bpmlua.R
import com.example.bangkit_2024_bpmlua.databinding.ActivityMainInterpreterBinding

class MainInterpreter : AppCompatActivity() {
    private lateinit var binding: ActivityMainInterpreterBinding
    private lateinit var predictionHelper: PredictionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainInterpreterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        predictionHelper = PredictionHelper(
            context = this,
            onResult = { result ->
                binding.tvResult.text = result
            },
            onError = {errorMessage ->
                Toast.makeText(this@MainInterpreter, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        binding.btnPredict.setOnClickListener {
            val input = binding.edSales.text.toString()
            predictionHelper.predict(input)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        predictionHelper.close()
    }
}