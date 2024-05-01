package com.example.bangkit_2024_bpmlua.TensorFlowLite.ObjectDetection

import android.content.Context
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.bangkit_2024_bpmlua.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.*
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.*

class ObjectDetectorHelper(
    // menambah jumlah hasil output menjadi lima dengan batas minimal threshold 50%
    var threshold: Float = 0.5f,
    var maxResults: Int = 5,
    val modelName: String = "efficientdet_lite0_v1.tflite",
    val context: Context,
    val detectorListener: DetectorListener?
) {
    private var objectDetector: ObjectDetector? = null

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
            setupObjectDetector()
        }.addOnFailureListener {
            detectorListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
        }
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            // menentukan batas minimal keakuratan dari hasil yang ditampilkan. 0.1 artinya 10%
            .setScoreThreshold(threshold)
            // menentukan batas maksimal jumlah hasil yang ditampilkan.
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .useGpu()
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        // untuk mengembalikan nilai apabila proses inisialisasi gagal
        try {
            // createFromFileAndOptions untuk membuat ObjectDetector berdasarkan nama assetfilemodel dan option yang didefinisikan sebelumnya.
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            detectorListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    // untuk melakukan pemrosesan klasifikasi
    fun detectObject(image: ImageProxy) {
        // memeriksa apakah TfLiteVision sudah diinisialisasi atau belum sebelum menggunakan TensorFlow Lite API dengan menggunakan fungsi isInitialized
        if (!TfLiteVision.isInitialized()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            Log.e(TAG, errorMessage)
            detectorListener?.onError(errorMessage)
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        // untuk melakukan preprocessing pada gambar sesuai dengan metadata pada model, yakni menggunakan ukuran 224x224 dan tipe data UINT8
        val imageProcessor = ImageProcessor.Builder()
            // Rot90Op digunakan untuk merotasi gambar yang masuk sebesar 90 berlawanan arah jarum jam
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90))
            .build()
        // TensorImage merupakan wrapper class yang digunakan untuk mewakili gambar sebelum diproses oleh TensorFlow Lite
        // Fungsi TensorImage.fromBitmap() digunakan untuk mengonversi Bitmap menjadi TensorImage
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

        // untuk mengetahui selisih waktu yang dibutuhkan untuk inferensi
        var inferenceTime = SystemClock.uptimeMillis()
        // memanggil fungsi classify untuk memulai proses pengklasifikasian
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        detectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
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
    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        private const val TAG = "ObjectDetectorHelper"
    }
}