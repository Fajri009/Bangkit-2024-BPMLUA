package com.example.bangkit_2024_bpmlua.TensorFlowLite.Interpreter

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.example.bangkit_2024_bpmlua.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.*
import java.nio.*
import java.nio.channels.FileChannel

class PredictionHelper(
    private val modelName: String = "rice_stock.tflite",
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
) {
    private var isGPUSupported: Boolean = false
    private var interpreter: InterpreterApi? = null

    init {
        // inisialisasi TFLite dari Google Play Service
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
                isGPUSupported = true
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            loadLocalModel()
        }.addOnFailureListener {
            onError(context.getString(R.string.tflite_is_not_initialized_yet))
        }
    }

    private fun loadLocalModel() {
        try {
            val buffer: ByteBuffer = loadModelFile(context.assets, modelName)
            initializeInterpreter(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // untuk mengakses model yang berada di folder asset
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        // openFd dari AssetManager untuk membuka FileDescriptor guna mengetahui deskripsi dari model tersebut
        // fungsi use digunakan untuk memastikan setiap proses tersebut akan ditutup ketika sudah selesai sehingga tidak terjadi memory leak
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                // startOffset digunakan untuk menunjukkan mulai dari mana berkas yang akan dibaca
                val startOffset = fileDescriptor.startOffset
                // declaredLength digunakan untuk mengetahui panjang berkas yang akan dibaca
                val declaredLength = fileDescriptor.declaredLength
                // mode READ_ONLY karena tidak perlu mengubah berkas
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    // menginisialisasi InterpreterAPI
    private fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            if (isGPUSupported){
                options.addDelegateFactory(GpuDelegateFactory())
            }
            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            }
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e(TAG, e.message.toString())
        }
    }

    fun close() {
        interpreter?.close()
    }

    // untuk melakukan inferensi
    fun predict(inputString: String) {
        if (interpreter == null) {
            return
        }

        // kita menggunakan FloatArray karena data yang dianalisis pada model berupa angka desimal
        val inputArray = FloatArray(1)
        inputArray[0] = inputString.toFloat()
        val outputArray = Array(1) { FloatArray(1) }

        try {
            interpreter?.run(inputArray, outputArray)
            onResult(outputArray[0][0].toString())
        } catch (e: Exception) {
            onError(context.getString(R.string.no_tflite_interpreter_loaded))
            Log.e(TAG, e.message.toString())
        }
    }

    companion object {
        private const val TAG = "PredictionHelper"
    }
}