package com.example.bangkit_2024_bpmlua.MLKit

import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.example.bangkit_2024_bpmlua.databinding.ActivityCameraBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class CameraMLKitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var barcodeScanner: BarcodeScanner
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var firstCall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this,
//                    cameraSelector,
//                    preview
//                )
//
//            } catch (exc: Exception) {
//                Toast.makeText(
//                    this@CameraActivity,
//                    "Gagal memunculkan kamera.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.e(TAG, "startCamera: ${exc.message}")
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }

    // penerapan barcode scanner secara real time
    private fun startCamera() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        // MLKitAnalyzer merupakan wrapper yang berguna untuk menghubungkan ML Kit dengan CameraController pada CameraX
        // MlKitAnalyzer untuk memudahkan kita membuat object ImageAnalysis.Analyzer yang dibutuhkan CameraX untuk menganalisis suatu gambar
        val analyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED, // sistem akan mengambil koordinat dari hasil pemindaian gambar dan mengonversinya ke koordinat PreviewView secara otomatis.
            ContextCompat.getMainExecutor(this) /// menampilkan hasil analisis di UI secara langsung.
        ) { result: MlKitAnalyzer.Result? ->
            showResult(result)
        }

        // LifecycleCameraController yang digunakan untuk menghubungkan MLKitAnalyzer yang dibuat sebelumnya dengan CameraX
        val cameraController = LifecycleCameraController(baseContext)
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            analyzer
        )
        // fungsi ini berguna supaya proses analisa tidak terjadi terus menerus ketika lifecycle dalam keadaan tidak aktif, seperti onStop maupun onDestroy
        cameraController.bindToLifecycle(this)
        binding.viewFinder.controller = cameraController
    }

    private fun showResult(result: MlKitAnalyzer.Result?) {
//        val barcodeResults = result?.getValue(barcodeScanner)
//        if ((barcodeResults != null) &&
//            (barcodeResults.size != 0) &&
//            (barcodeResults.first() != null)
//        ) {
//            val barcode = barcodeResults[0]
//            val alertDialog = AlertDialog.Builder(this)
//                .setMessage(barcode.rawValue)
//                .setCancelable(false)
//                .create()
//            alertDialog.show()
//        }

        // untuk menandai apakah sudah berhasil mendapatkan data pertama atau tidak
        if (firstCall) {
            val barcodeResults = result?.getValue(barcodeScanner)
            // untuk memastikan terdapat datanya dan tidak kosong
            if ((barcodeResults != null) &&
                (barcodeResults.size != 0) &&
                (barcodeResults.first() != null)
            ) {
                firstCall = false
                val barcode = barcodeResults[0]

                val alertDialog = AlertDialog.Builder(this)
                alertDialog
                    .setMessage(barcode.rawValue)
                    .setPositiveButton("Buka") {_, _ ->
                        firstCall = true
                        when (barcode.valueType) {
                            Barcode.TYPE_URL -> {
                                val openBrowserIntent = Intent(Intent.ACTION_VIEW)
                                openBrowserIntent.data = Uri.parse(barcode.url?.url)
                                startActivity(openBrowserIntent)
                            }
                            else -> {
                                Toast.makeText(this, "Unsupported data type", Toast.LENGTH_SHORT)
                                    .show()
                                startCamera()
                            }
                        }
                    }
                    .setNegativeButton("Scan lagi") {_, _ ->
                        firstCall = true
                    }
                    .setCancelable(false)
                    .create()
                alertDialog.show()
            }
        }
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
        const val CAMERAX_RESULT = 200
    }
}