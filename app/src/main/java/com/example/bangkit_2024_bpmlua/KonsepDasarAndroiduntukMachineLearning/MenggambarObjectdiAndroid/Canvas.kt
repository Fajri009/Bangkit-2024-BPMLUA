package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggambarObjectdiAndroid

import android.graphics.*
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.bangkit_2024_bpmlua.R
import com.example.bangkit_2024_bpmlua.databinding.ActivityCanvasBinding

class Canvas : AppCompatActivity() {
    private lateinit var binding: ActivityCanvasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanvasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        binding.myImageView.setImageBitmap(bitmap)

        val canvas = Canvas(bitmap)
        // Mengatur Background Canvas
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.blue_500, null))

        // Memberikan Warna Object 2D dalam Canvas dengan Paint
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.pink_500, null)

        // Menggambar Persegi
        paint.color = ResourcesCompat.getColor(resources, R.color.pink_200, null)
        // Cara Pertama
//        canvas.drawRect(
//            (bitmap.width / 2 - 100).toFloat(),
//            (bitmap.height / 2 - 100).toFloat(),
//            (bitmap.width / 2 + 100).toFloat(),
//            (bitmap.height / 2 + 100).toFloat(),
//            paint
//        )
        // Cara Kedua
//        val rect = Rect()
//        rect.set(bitmap.width / 2 - 100, bitmap.height /2 - 100, bitmap.width /2 + 100, bitmap.height /2 + 100)
//        canvas.drawRect(rect, paint)

        // Menyimpan Pengaturan Canvas
        canvas.save() // menyimpan pengaturan sebelumnya.

        // Memotong dan Menggabungkan Object Canvas (Clipping)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(bitmap.width/2 - 100F, bitmap.height/2 - 100F, bitmap.width/2 + 100F, bitmap.height/2 + 100F, Region.Op.DIFFERENCE)
            // Region.Op.DIFFERENCE yang berfungsi untuk memotong bagian dalam object lain
        } else {
            // Memotong bagian tengah lingkaran
            // clipOutRect memerlukan API 26 atau Android O ke atas
            canvas.clipOutRect(bitmap.width/2 - 100, bitmap.height/2 - 100, bitmap.width/2 + 100, bitmap.height/2 + 100)
        }

        // Menggambar Lingkaran
        canvas.drawCircle((bitmap.width / 2).toFloat(),(bitmap.height / 2).toFloat(), 200f, paint)

        canvas.restore() // mengembalikan pengaturan yang telah tersimpan.

        // Menulis Teks dalam Canvas
        val paintText = Paint(Paint.FAKE_BOLD_TEXT_FLAG) // untuk membuat style bold pada teks
        paintText.textSize = 20F
        paintText.color = ResourcesCompat.getColor(resources, R.color.white, null)

        val text = "Selamat Datang"
        val bounds = Rect()
        paintText.getTextBounds(text, 0, text.length, bounds)

        // Agar teks bisa di tengah layar
        val x: Int = bitmap.width / 2 - bounds.centerX()
        val y: Int = bitmap.height / 2 - bounds.centerY()

        canvas.drawText(text, x.toFloat(), y.toFloat(), paintText)
    }
}