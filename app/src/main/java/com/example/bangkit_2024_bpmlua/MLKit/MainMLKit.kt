package com.example.bangkit_2024_bpmlua.MLKit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.LOICDCBML.CameraActivity.Companion.CAMERAX_RESULT
import com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.LOICDCBML.getImageUri
import com.example.bangkit_2024_bpmlua.R
import com.example.bangkit_2024_bpmlua.databinding.ActivityMainMlKitBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainMLKit : AppCompatActivity() {
    private lateinit var binding: ActivityMainMlKitBinding

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMlKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraMLKitActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraMLKitActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    // mengirimkan data gambar dari MainTextRecognition ke ResulActivity
    private fun analyzeImage(uri: Uri) {
        binding.progressIndicator.visibility = View.VISIBLE

//        // Untuk aksara Cina
//        val textRecognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
//
//        // Untuk aksara Devanagari (India)
//        val textRecognizer = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
//
//        // Untuk aksara Jepang
//        val textRecognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
//
//        // Untuk aksara Korea
//        val textRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        // Untuk aksara latin (alfabet), yang digunakan di sebagian besar bahasa, seperti bahasa Indonesia, bahasa Inggris, dan bahasa Spanyol.
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        // InputImage adalah sebuah kelas dalam ML Kit yang digunakan untuk mewakili gambar yang akan diproses oleh model machine learning
        val inputImage = InputImage.fromFilePath(this, uri)

        /*
            fromFilePath: membuat InputImage dari file gambar yang diidentifikasi dengan path file (Uri).
            fromBitmap: membuat InputImage dari Bitmap.
            fromByteArray: membuat InputImage dari ByteArray, biasanya dari callback Camera.
            fromByteBuffer: membuat InputImage dari ByteBuffer. Biasanya digunakan untuk deteksi secara real-time (kontinu).
            fromMediaImage: membuat InputImage dari object media.image yang didapat dari CameraX.
         */

        // untuk mulai memproses gambar
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText: Text ->
                val detectedText: String = visionText.text

                if (detectedText.isNotBlank()) {
                    binding.progressIndicator.visibility = View.GONE

                    val intent = Intent(this, ResultMLKitActivity::class.java)
                    intent.putExtra(ResultMLKitActivity.EXTRA_IMAGE_URI, uri.toString())
                    intent.putExtra(ResultMLKitActivity.EXTRA_RESULT, detectedText)
                    startActivity(intent)
                } else {
                    binding.progressIndicator.visibility = View.GONE
                    showToast(getString(R.string.no_text_recognized))
                }
            }
            .addOnFailureListener {
                binding.progressIndicator.visibility = View.GONE
                showToast(it.message.toString())
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}