package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggambarObjectdiAndroid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bangkit_2024_bpmlua.R

class MainCanvasView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CanvasView(this))
    }
}