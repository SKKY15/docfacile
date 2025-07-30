package com.example.startup_app.animations

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ProgressBarDotted@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle)  {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
    }

    private var rotationAngle = 0f
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = RectF(
            20f,
            20f,
            width - 20f,
            height - 20f
        )

        canvas.save()
        canvas.rotate(rotationAngle, width / 2f, height / 2f)
        canvas.drawArc(rect, -90f, 360f, false, paint)
        canvas.restore()
    }
}