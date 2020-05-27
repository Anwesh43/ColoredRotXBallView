package com.anwesh.uiprojects.coloredrotballxview

/**
 * Created by anweshmishra on 28/05/20.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

val colors : Array<String> = arrayOf("#4CAF50", "#673AB7", "#009688", "#2196F3", "#F44336")
val lines : Int = 2
val rot : Float = 45f
val hFactor : Float = 2.2f
val rFactor : Float = 8f
val backColor : Int = Color.parseColor("#BDBDBD")
val parts : Int = 4
val scGap : Float = 0.02f / parts
val delay : Long = 20
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawColoredRotXBall(i : Int, sf : Float, w : Float, h : Float, paint : Paint) {
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val hGap : Float = Math.min(w, h) / hFactor
    val y : Float = hGap * sf2
    val r : Float = Math.min(w, h) / rFactor
    save()
    scale(1f, 1f - 2 * i)
    drawLine(0f, 0f, 0f, y, paint)
    drawCircle(0f, y, r * sf1, paint)
    restore()
}

fun Canvas.drawColoredRotXBalls(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    for (i in 0..(lines - 1)) {
        save()
        rotate(rot * sf * (1f - 2 * i))
        for (j in 0..(lines - 1)) {
            drawColoredRotXBall(i, sf, w, h, paint)
        }
        restore()
    }
}

fun Canvas.drawCRXBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, h / 2)
    drawColoredRotXBalls(scale, w, h, paint)
    restore()
}

class ColoredRotXBallView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}