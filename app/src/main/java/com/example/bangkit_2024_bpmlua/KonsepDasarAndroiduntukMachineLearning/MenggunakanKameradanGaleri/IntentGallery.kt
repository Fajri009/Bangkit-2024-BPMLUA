package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggunakanKameradanGaleri

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class IntentGallery: AppCompatActivity() {
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
        }
    }

    // Photo Picker
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        // ...
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")

        launcherIntentGallery.launch(chooser)

        // Photo Picker merupakan solusi terbaru dari Android untuk mendapatkan media tanpa perlu membutuhkan permission apa pun atau bisa dikatakan permissionless
        // Hanya menampilkan gambar yang dipilih oleh pengguna
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}