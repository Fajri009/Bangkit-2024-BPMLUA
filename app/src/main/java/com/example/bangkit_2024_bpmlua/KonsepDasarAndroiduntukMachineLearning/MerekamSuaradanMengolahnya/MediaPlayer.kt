package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MerekamSuaradanMengolahnya

import android.media.AudioAttributes
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bangkit_2024_bpmlua.R
import java.io.IOException

class MediaPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        val mediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

//        mediaPlayer?.setAudioAttributes(attribute)

//        val audioFile = applicationContext.resources.openRawResource(R.raw.guitar_background)
        try {
            // setDataSource untuk memasukkan detail informasi dari berkas audio yang akan diputar
//            mMediaPlayer?.setDataSource(audioFile.fileDescriptor, audioFile.startOffset, audioFile.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }

//        mediaPlayer.start()
    }
}