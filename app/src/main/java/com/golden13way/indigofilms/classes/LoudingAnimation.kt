package com.golden13way.indigofilms.classes

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.golden13way.indigofilms.R
import kotlin.math.cos
import kotlin.math.sin

class DoubleOrbitView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val ballRadius = 35f
    private val orbitRadius = 80f
    private val orbitCenter = PointF()
    private val ball1Position = PointF()
    private val ball2Position = PointF()
    private val ballPaint1 = Paint().apply {
        shader = RadialGradient(
            ballRadius / 2f, ballRadius / 2f, ballRadius,
            intArrayOf(ContextCompat.getColor(context, R.color.blueIF), ContextCompat.getColor(context, R.color.blueIF)),
            floatArrayOf(0.2f, 1f),
            Shader.TileMode.CLAMP
        )
        style = Paint.Style.FILL
    }
    private val ballPaint2 = Paint().apply {
        shader = RadialGradient(
            ballRadius / 2f, ballRadius / 2f, ballRadius,
            intArrayOf(ContextCompat.getColor(context, R.color.purpleIF), ContextCompat.getColor(context, R.color.purpleIF)),
            floatArrayOf(0.2f, 1f),
            Shader.TileMode.CLAMP
        )
        style = Paint.Style.FILL
    }

    private var ball1Animator: ValueAnimator? = null
    private var ball2Animator: ValueAnimator? = null

    init {
        holder.addCallback(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        orbitCenter.set(w / 2f, h / 2f)
        ball1Position.set(orbitCenter.x - orbitRadius, orbitCenter.y)
        ball2Position.set(orbitCenter.x + orbitRadius, orbitCenter.y)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startAnimation()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopAnimation()
    }

    private fun drawBalls(canvas: Canvas) {
        // Очистить холст
        canvas.drawColor(ContextCompat.getColor(context, R.color.mainbg2), PorterDuff.Mode.CLEAR)

        // Нарисовать фон
        canvas.drawColor(ContextCompat.getColor(context, R.color.mainbg2))

        // Нарисовать шарики
        canvas.drawCircle(ball1Position.x, ball1Position.y, ballRadius, ballPaint1)
        canvas.drawCircle(ball2Position.x, ball2Position.y, ballRadius, ballPaint2)
    }

    private fun update() {
        ball1Position.x = orbitCenter.x + orbitRadius * sin(angle)
        ball1Position.y = orbitCenter.y + orbitRadius * cos(angle) / 2

        ball2Position.x = orbitCenter.x + orbitRadius * sin(angle + Math.PI.toFloat())
        ball2Position.y = orbitCenter.y + orbitRadius * cos(angle + Math.PI.toFloat()) / 2

        angle += 0.05f
    }

    private var angle = 0f

    public fun startAnimation() {
        stopAnimation()
        ball1Animator = ValueAnimator.ofFloat(0f, 2 * Math.PI.toFloat()).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { valueAnimator ->
                update()
                holder.lockCanvas()?.let { canvas ->
                    drawBalls(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
            }
            start()
        }

        ball2Animator = ValueAnimator.ofFloat(0f, 2 * Math.PI.toFloat()).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { valueAnimator ->
                update()
                holder.lockCanvas()?.let { canvas ->
                    drawBalls(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
            }
            start()
        }
    }

    public fun stopAnimation() {
        ball1Animator?.cancel()
        ball2Animator?.cancel()
        ball1Animator = null
        ball2Animator = null
    }
}