package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MenggambarObjectdiAndroid

import android.content.Context
import android.graphics.*
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class CustomCircleView(context: Context, attrs: AttributeSet): View(context, attrs) {
    val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    val paint = Paint()

    init {
        paint.setColor(Color.GREEN)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(bitmap.width, bitmap.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((bitmap.width/2).toFloat(), (bitmap.height/2).toFloat(), 200f, paint)
    }
}