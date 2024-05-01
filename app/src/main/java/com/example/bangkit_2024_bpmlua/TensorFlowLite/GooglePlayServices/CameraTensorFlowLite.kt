package com.example.bangkit_2024_bpmlua.TensorFlowLite.GooglePlayServices

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.bangkit_2024_bpmlua.databinding.ActivityCameraTensorFlowLiteBinding
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import java.text.NumberFormat
import java.util.concurrent.Executors

class CameraTensorFlowLite: AppCompatActivity() {
    private lateinit var binding: ActivityCameraTensorFlowLiteBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraTensorFlowLiteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {
        // mengintegrasikan ImageClassifier dengan CameraX
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@CameraTensorFlowLite, error, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    // menggunakan block runOnUiThread karena pada dasarnya kita tidak dapat meng-update UI dari background thread
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }
                                val displayResult =
                                    sortedCategories.joinToString("\n") {
                                        "${it.label} " + NumberFormat.getPercentInstance()
                                            .format(it.score).trim()
                                    }
                                binding.tvResult.text = displayResult
                                binding.tvInferenceTime.text = "$inferenceTime ms"
                            } else {
                                binding.tvResult.text = ""
                                binding.tvInferenceTime.text = ""
                            }
                        }
                    }
                }
            }
        )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // mengintegrasikan ImageClassifier dengan CameraX dengan memanfaatkan ImageAnalysis
        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()

            // ImageAnalysis adalah salah satu use case dalam CameraX yang memungkinkan Anda untuk menganalisis gambar yang diambil oleh kamera dan melakukan tindakan berdasarkan hasil analisis tersebut
            val imageAnalyzer = ImageAnalysis.Builder()
                // menentukan resolusi gambar yang akan dianalisis
                .setResolutionSelector(resolutionSelector)
                // mengatur rotasi target gambar berdasarkan rotasi tampilan yang terkait dengan viewFinder
                // Ini memastikan bahwa gambar yang dianalisis sesuai dengan orientasi layar
                .setTargetRotation(binding.viewFinder.display.rotation)
                // mengatur strategi untuk mengatasi masalah penundaan dalam pemrosesan gambar
                // STRATEGY_KEEP_ONLY_LATEST memungkinkan analisis tetap menggunakan gambar terbaru walaupun gambar-gambar sebelumnya belum selesai diproses
                // STRATEGY_BLOCK_PRODUCER jika Anda ingin setiap gambar diproses sampai selesai dahulu sebelum lanjut ke gambar berikutnya
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                // mengatur format gambar keluaran yang dihasilkan oleh ImageAnalysis menjadi RGBA_8888
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            // untuk menganalisis gambar dengan menjalankan sebuah executor yang akan menghasilkan ImageProxy
            // ImageProxy merupakan wrapper class yang digunakan untuk mewakili gambar dalam format raw yang diambil dari kamera
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                // untuk mengklasifikasikan gambar tersebut
                imageClassifierHelper.classifyImage(image)
            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                // fitur untuk menambahkan use case
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraTensorFlowLite,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 20
    }
}