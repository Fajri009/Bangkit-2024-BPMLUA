package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MerekamSuaradanMengolahnya

import android.media.AudioRecord
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bangkit_2024_bpmlua.R
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

// Untuk merekam audio dari media perekam dalam setiap platform
class AudioRecorder : AppCompatActivity() {
    private lateinit var audioRecorder: AudioRecord
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recorder)

//        val audioRecorder = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
//            var RECORDER_SAMPLERATE : kotlin . Any ? = null,
//            val RECORDER_CHANNELS: Any? = null,
//            val RECORDER_AUDIO_ENCODING: Any? = null,
//            val bufferSize: Any? = null
//        )

        /*
            *  MediaRecorder.AudioSource.MIC: Parameter pertama, AudioSource, menentukan sumber audio yang akan direkam. Dalam contoh ini, MIC adalah sumber audio dari mikrofon perangkat yang akan digunakan untuk merekam suara.
            * RECORDER_SAMPLERATE: Parameter kedua adalah sampleRateInHz. Ini adalah frekuensi sampel dalam Hertz (Hz) yang menentukan seberapa sering sampel audio direkam per detik. Nilai yang umum digunakan adalah 44100 Hz untuk bisa dijalankan di semua perangkat.
            * RECORDER_CHANNELS: Parameter ketiga, channelConfig, menentukan konfigurasi saluran audio. AudioFormat.CHANNEL_IN_MONO digunakan untuk merekam audio dalam mode mono (saluran tunggal), sedangkan AudioFormat.CHANNEL_IN_STEREO akan merekam dalam mode stereo (dua saluran).
            * RECORDER_AUDIO_ENCODING: Parameter keempat adalah audioFormat. Ini menentukan format data audio yang akan direkam.
            * bufferSize: Parameter terakhir, bufferSizeInBytes, adalah ukuran buffer dalam byte yang akan digunakan untuk menampung data audio yang direkam. Besar buffer harus diperoleh melalui pemanggilan AudioRecord.getMinBufferSize() dengan parameter sesuai keinginan.
        */

        audioRecorder.startRecording()

        // Memanggil metode stop() pada objek AudioRecord akan menghentikan proses perekaman audio yang sedang berjalan
        audioRecorder.stop()

        // Metode release() yang digunakan untuk melepaskan semua sumber daya yang digunakan oleh AudioRecord
        audioRecorder.release()
    }

    // Untuk membaca nilai audio yang terekam dalam AudioRecord ke berkas outputStream
    private fun recordAudio() {
//        val data = ByteArray(bufferSize)
        val filePath = externalCacheDir?.absolutePath + "/recorded_audio.pcm"
        val outputStream = DataOutputStream(FileOutputStream(File(filePath)))

//        while (isRecording) {
//            val read = audioRecorder?.read(data, 0, bufferSize) ?: 0
//            if (read != AudioRecord.ERROR_INVALID_OPERATION) {
//                outputStream.write(data)
//            }
//        }

        outputStream.close()
    }
}