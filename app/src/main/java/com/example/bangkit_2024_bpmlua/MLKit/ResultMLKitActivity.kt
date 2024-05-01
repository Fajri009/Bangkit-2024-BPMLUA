package com.example.bangkit_2024_bpmlua.MLKit

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bangkit_2024_bpmlua.R
import com.example.bangkit_2024_bpmlua.databinding.ActivityResultMlKitBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class ResultMLKitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultMlKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultMlKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menerima data gambar dari MainTextRecognition
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        val detectedText = intent.getStringExtra(EXTRA_RESULT)
        binding.resultText.text = detectedText

        binding.translateButton.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            translateText(detectedText)
        }
    }

    private fun translateText(detectedText: String?) {
        // translate dari bahasa inggris ke bahasa indonesia
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val indonesianEnglishTranslator = Translation.getClient(options)

        // mengunduh model yang dibutuhkan
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        indonesianEnglishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // memulai proses penerjemahan
                indonesianEnglishTranslator.translate(detectedText.toString())
                    .addOnSuccessListener { translatedText ->
                        binding.translatedText.text = translatedText
                        indonesianEnglishTranslator.close()
                        binding.progressIndicator.visibility = View.GONE
                    }
                    .addOnFailureListener { exception ->
                        showToast(exception.message.toString())
                        print(exception.stackTrace)
                        indonesianEnglishTranslator.close()
                        binding.progressIndicator.visibility = View.GONE
                    }
            }
            .addOnFailureListener { exception ->
                showToast(getString(R.string.downloading_model_fail))
                binding.progressIndicator.visibility = View.GONE
            }
        // Hal ini berguna supaya object Translator bisa dihapus ketika memasuki state onPause(), onStop(), dan onDestroy().
        lifecycle.addObserver(indonesianEnglishTranslator)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}