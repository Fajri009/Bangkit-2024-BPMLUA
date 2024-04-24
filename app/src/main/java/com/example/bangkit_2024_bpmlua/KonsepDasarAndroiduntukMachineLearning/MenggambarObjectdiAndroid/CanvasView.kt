package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggambarObjectdiAndroid

import android.content.Context
import android.util.AttributeSet
import android.view.View

// Operator ini (@JvmOverloads) berfungsi untuk memberikan parameter default pada constructor secara otomatis tanpa perlu mengulang-ulang menulis kode
class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs) {
//    // Constructor pertama dipakai jika CustomView dibuat secara programmatic (kode)
//    constructor(
//        context: Context
//    ): super(context){ }
//
//    // Constructor kedua dipakai jika CustomView dibuat di XML.
//    constructor(
//        context: Context, attrs: AttributeSet
//    ): super(context, attrs) { }
//
//    //Constructor ketiga dipakai jika CustomView dibuat di XML dengan style default tertentu.
//    constructor(
//        context: Context, attrs: AttributeSet, defStyleAttr: Int
//    ) : super(context, attrs, defStyleAttr) { }

//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        // Di sini Anda bisa langsung menggunakan canvas dari kelas View.
//    }
}