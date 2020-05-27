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
val parts : Int = 3
val scGap : Float = 0.02f / parts
val delay : Long = 20
