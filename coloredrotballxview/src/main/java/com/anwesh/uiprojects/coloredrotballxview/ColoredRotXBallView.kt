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
        rotate(rot * sf.divideScale(2, 3) * (1f - 2 * i))
        for (j in 0..(lines - 1)) {
            drawColoredRotXBall(j, sf, w, h, paint)
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas: Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale: Float = 0f, var dir: Float = 0f, var prevScale: Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CRXBNode(var i : Int, val state : State = State()) {

        private var next : CRXBNode? = null
        private var prev : CRXBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CRXBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCRXBNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CRXBNode {
            var curr : CRXBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class ColoredRotXBall(var i : Int) {

        private var curr: CRXBNode = CRXBNode(0)
        private var dir: Int = 1

        fun draw(canvas: Canvas, paint: Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : ColoredRotXBallView) {

        private val animator : Animator = Animator(view)
        private val crxb : ColoredRotXBall = ColoredRotXBall(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            crxb.draw(canvas, paint)
            animator.animate {
                crxb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            crxb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : ColoredRotXBallView {
            val view : ColoredRotXBallView = ColoredRotXBallView(activity)
            activity.setContentView(view)
            return view
        }
    }
}