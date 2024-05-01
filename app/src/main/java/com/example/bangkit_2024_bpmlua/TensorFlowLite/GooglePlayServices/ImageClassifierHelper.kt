package com.example.bangkit_2024_bpmlua.TensorFlowLite.GooglePlayServices

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.camera.core.ImageProxy
import com.example.bangkit_2024_bpmlua.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.*
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.classifier.*

class ImageClassifierHelper(
    var threshold: Float = 0.1f,
    var maxResults: Int = 3,
    val modelName: String = "mobilenet_v1.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        // untuk memeriksa ketersediaan GPU Delegate dan menginisialisasi TFLiteVision
        // kita menggunakan fungsi isGpuDelegateAvailable dari TfLiteGpu untuk memeriksa apakah GPU delegate tersedia
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            // untuk mengaktifkan GPU Delegate
            // GPU Delegate adalah driver yang memungkinkan TensorFlow Lite untuk mengakses hardware GPU
            setupImageClassifier()
        }.addOnFailureListener {
            classifierListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
        }
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            // menentukan batas minimal keakuratan dari hasil yang ditampilkan. 0.1 artinya 10%
            .setScoreThreshold(threshold)
            // menentukan batas maksimal jumlah hasil yang ditampilkan.
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            // menentukan jumlah thread yang digunakan untuk melakukan inferensi.
//            .setNumThreads(4)

        // untuk menggunakan GPU pada ImageClassifier yang kita buat sebelumnya
        if (CompatibilityList().isDelegateSupportedOnThisDevice){
            baseOptionsBuilder.useGpu()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            baseOptionsBuilder.useNnapi()
        } else {
            // Menggunakan CPU
            baseOptionsBuilder.setNumThreads(4)
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        // untuk mengembalikan nilai apabila proses inisialisasi gagal
        try {
            // createFromFileAndOptions untuk membuat ImageClassifier berdasarkan nama assetfilemodel dan option yang didefinisikan sebelumnya.
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    // untuk melakukan pemrosesan klasifikasi
    fun classifyImage(image: ImageProxy) {
        // memeriksa apakah TfLiteVision sudah diinisialisasi atau belum sebelum menggunakan TensorFlow Lite API dengan menggunakan fungsi isInitialized
        if (!TfLiteVision.isInitialized()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            Log.e(TAG, errorMessage)
            classifierListener?.onError(errorMessage)
            return
        }

        if (imageClassifier == null) {
            setupImageClassifier()
        }

        // untuk melakukan preprocessing pada gambar sesuai dengan metadata pada model, yakni menggunakan ukuran 224x224 dan tipe data UINT8
        val imageProcessor = ImageProcessor.Builder()
            // ResizeOp: digunakan untuk mengubah ukuran tinggi dan lebar gambar supaya sesuai dengan ukuran di dalam model
            // NEAREST_NEIGHBOR: mengambil piksel terdekat dari gambar asli untuk mengisi piksel baru di gambar yang diubah ukurannya
            // BILINEAR: menggunakan interpolasi linier untuk memperkirakan nilai piksel baru di gambar yang diubah ukurannya
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            // CastOp: digunakan untuk mengubah tipe data tensor
            .add(CastOp(DataType.UINT8))
            .build()
        // TensorImage merupakan wrapper class yang digunakan untuk mewakili gambar sebelum diproses oleh TensorFlow Lite
        // Fungsi TensorImage.fromBitmap() digunakan untuk mengonversi Bitmap menjadi TensorImage
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

        // ImageProcessingOptions untuk mengatur orientasi gambar supaya sesuai dengan gambar pada model
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(image.imageInfo.rotationDegrees))
            .build()

        // untuk mengetahui selisih waktu yang dibutuhkan untuk inferensi
        var inferenceTime = SystemClock.uptimeMillis()
        // memanggil fungsi classify untuk memulai proses pengklasifikasian
        val results = imageClassifier?.classify(tensorImage, imageProcessingOptions)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        classifierListener?.onResults(
            results,
            inferenceTime
        )
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation? {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    // untuk mengonversi ImageProxy menjadi Bitmap
    private fun toBitmap(image: ImageProxy): Bitmap {
        // btmapBuffer adalah cuplikan gambar yang didapat dari video yang diambil secara real-time
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        // untuk menyalin piksel dari ImageProxy ke Bitmap secara bertahap
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    // Listener di sini berfungsi untuk memberi tahu class utama ketika proses yang dilakukan berhasil atau gagal
    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}