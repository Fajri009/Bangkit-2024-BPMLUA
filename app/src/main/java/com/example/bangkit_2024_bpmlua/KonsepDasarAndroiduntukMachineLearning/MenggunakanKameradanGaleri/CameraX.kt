package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggunakanKameradanGaleri

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bangkit_2024_bpmlua.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraX : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)

        val previewView: PreviewView = findViewById(R.id.previewView)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Konfigurasi Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Pilih kamera belakang sebagai default
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // Konfigureasi ImageCapture
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            // Jika ingin menambahkan fitur untuk menangkap gambar dari CameraX, Anda dapat memanfaatkan ImageCapture
            try {
                // Bind use cases ke kamera yang dipilih dan menambahkan imageCapture
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                // Handle kesalahan saat bind use cases ke kamera
            }
        }, ContextCompat.getMainExecutor(this))

        // Untuk menangkap gambar di kamera
//        btnCapture.setOnClickListener {
//            // Membuat file untuk menyimpan gambar
//            val photoFile = File(outputDirectory, SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg")
//
//            // Pengaturan konfigurasi ImageCapture untuk menyimpan gambar
//            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//            // Ambil gambar
//            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    // Tindakan setelah gambar berhasil disimpan
//                }
//                override fun onError(exception: ImageCaptureException) {
//                    // Penanganan kesalahan saat mengambil gambar
//                }
//            })
//        }
    }
}