package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggunakanKameradanGaleri

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class IntentCamera: AppCompatActivity() {
    private lateinit var photoUri: Uri

    // Menggunakan Intent Camera dengan StartActivityForResult
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // ...
        }
    }

    // Menggunakan Intent Camera dengan TakePicture
    private val launcherTakePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        // Menggunakan Intent Camera dengan StartActivityForResult
        launcherIntentCamera.launch(intent)

        // Menggunakan Intent Camera dengan TakePicture
        launcherTakePicture.launch(photoUri)

        // Mendapatkan URI dari Intent Camera
        // Menggunakan MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "my_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val photoUri: Uri? = contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        // Menggunakan FileProvider
//        val file: File = .. // buat File pada lokasi yang diinginkan
//        val photoUri2: Uri = FileProvider.getUriForFile(this, "come.example.provider", file)
    }
}