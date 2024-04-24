package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MerekamSuaradanMengolahnya

import android.media.browse.MediaBrowser.MediaItem
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bangkit_2024_bpmlua.R


// Sama seperti MediaPlayer tetapi fiturnya lebih banyak
class ExoPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)

//        val mediaItem = MediaItem.fromUri(Uri.parse("file:///sdcard/audio.mp3"))
//        val mediaSource = ProgressiveMediaSource.Factory.createMediaSource(mediaItem)

        // Menginisialisasi ExoPlauer
//        val player = ExoPlayer.Builder(this).build()
        // Menghubungkan MediaItem dengan ExoPlayer
//        player.setMediaSource(mediaSource)

//        player.play()
    }
}