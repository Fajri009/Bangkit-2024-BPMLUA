package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MerekamSuaradanMengolahnya

import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bangkit_2024_bpmlua.R
import java.io.File
import java.io.FileOutputStream

// Merekam suara ke dalam aplikasi
class MediaRecorder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_recorder)

        val mediaRecorder = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.media.MediaRecorder(this)
        } else MediaRecorder()

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//            setOutputFile(FileOutputStream(audioFile).fd)
            prepare()
        }

        // Salah satu yang bisa Anda lakukan adalah membangun berkas dengan pengaturan cachedir
        // Artinya, berkas akan disimpan sementara dalam direktori cache
        val audioFile = File(cacheDir, "audio.mp3")

        // Cukup memanggil function start untuk memulai merekam dan function stop untuk menghentikan proses merekam.
        fun startRecord() {
            mediaRecorder?.start()
        }

        fun stopRecord() {
            mediaRecorder?.stop()
        }

        // Setelah proses merekam audio berhasil disimpan, Anda bisa menjalankan berkas tersebut menggunakan MediaPlayer atau Media3 ExoPlayer.
    }
}