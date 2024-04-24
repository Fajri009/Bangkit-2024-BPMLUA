package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MerekamSuaradanMengolahnya

import android.media.SoundPool
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bangkit_2024_bpmlua.R

// Komponen ini lebih cocok digunakan untuk memainkan audio yang berdurasi pendek
class SoundPool : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_pool)

        var soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .build()

        var spLoaded = false
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                spLoaded = true
            } else {
                Toast.makeText(this@SoundPool, "Gagal load", Toast.LENGTH_SHORT).show()
            }
        }
//
//    btnPlay.setOnClickListener {
//        if (spLoaded) {
//            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
//        }
//    }
    }
}