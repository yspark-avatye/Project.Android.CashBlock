package com.avatye.cashblock.feature.roulette.component.widget.miscellaneous

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.avatye.cashblock.base.component.support.AnimatorEventCallback
import com.avatye.cashblock.base.component.support.toPX
import com.avatye.cashblock.feature.roulette.R

internal class CircleProgressView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    internal interface IProgressCallback {
        fun onCompleted(progressValue: Float)
    }

    private var angleFrom = -90f
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var progressDuration = 250L

    private var progressValue = 0F
        private set(value) {
            field = kotlin.math.min(value, progressMax)
            if (progressMax <= field) {
                this.post {
                    progressCallback?.onCompleted(field)
                }
            }
            invalidate()
        }

    var progressMin = 0F
        set(value) {
            field = value
            invalidate()
        }

    var progressMax = 100F
        set(value) {
            field = value
            invalidate()
        }

    val progressCurrent: Float
        get() {
            return progressValue
        }

    val progressCompleted: Boolean
        get() {
            return (progressValue >= progressMax)
        }

    private var backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var backgroundPaintColor: Int = Color.BLACK

    private var progressBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressBackgroundPaintColor: Int = Color.WHITE

    private var arcRectangle = RectF()
    private var progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressPaintColor: Int = Color.RED
    private var progressBorder = 5f
    private var progressThickness = 30f

    private var progressForegroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressForegroundColor: Int = Color.GRAY
    private var progressForegroundThickness = 0f

    var progressCallback: IProgressCallback? = null

    init {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView, 0, 0)
        initializeViews(typedArray)
    }

    private fun initializeViews(typedArray: TypedArray) {
        try {
            progressBorder = typedArray.getDimension(R.styleable.CircleProgressView_acbsr_widget_progress_border_width, progressBorder)
            progressThickness = typedArray.getDimension(R.styleable.CircleProgressView_acbsr_widget_progress_thickness, progressThickness)
            backgroundPaintColor = typedArray.getColor(R.styleable.CircleProgressView_acbsr_widget_progress_border_color, backgroundPaintColor)
            progressBackgroundPaintColor = typedArray.getColor(R.styleable.CircleProgressView_acbsr_widget_progress_background_color, progressPaintColor)
            progressPaintColor = typedArray.getColor(R.styleable.CircleProgressView_acbsr_widget_progress_foreground_color, progressPaintColor)
            progressForegroundColor = typedArray.getColor(R.styleable.CircleProgressView_acbsr_widget_progress_center_color, progressForegroundColor)
            progressValue = typedArray.getFloat(R.styleable.CircleProgressView_acbsr_widget_progress_value, progressValue)
            progressMin = typedArray.getFloat(R.styleable.CircleProgressView_acbsr_widget_progress_min, progressMin)
            progressMax = typedArray.getFloat(R.styleable.CircleProgressView_acbsr_widget_progress_max, progressMax)
        } finally {
            typedArray.recycle()
        }
        // background
        backgroundPaint.color = backgroundPaintColor
        backgroundPaint.style = Paint.Style.FILL
        // progress-background
        progressBackgroundPaint.color = progressBackgroundPaintColor
        progressBackgroundPaint.style = Paint.Style.FILL
        // progress
        progressPaint.color = progressPaintColor
        progressPaint.style = Paint.Style.FILL
        // progress-foreground
        progressForegroundPaint.color = progressForegroundColor
        progressForegroundPaint.style = Paint.Style.FILL
        progressForegroundThickness = progressBorder + progressThickness
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
        radius = kotlin.math.min(measuredWidth, measuredHeight) / 2.toFloat()
        centerX = (measuredWidth / 2).toFloat()
        centerY = (measuredHeight / 2).toFloat()
        arcRectangle[progressBorder, progressBorder, measuredWidth - progressBorder] = measuredHeight - progressBorder
    }

    override fun onDraw(canvas: Canvas) {
        val angleTo = 360 * progressValue / progressMax
        // background
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        // progress-background
        canvas.drawCircle(centerX, centerY, radius - progressBorder, progressBackgroundPaint)
        // progress
        canvas.drawArc(arcRectangle, angleFrom, angleTo, true, progressPaint)
        // progress-foreground
        canvas.drawCircle(centerX, centerY, (radius - progressForegroundThickness), backgroundPaint)
        // progress gap(padding)
        canvas.drawCircle(centerX, centerY, (radius - progressForegroundThickness) - 2F.toPX, progressForegroundPaint)
        super.onDraw(canvas)
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun updateSmoothProgressValue(progress: Float) {
        this.animation?.cancel()
        val objectAnimator = ObjectAnimator.ofFloat(this, "ProgressValue", progressValue, progress)
        objectAnimator.duration = progressDuration
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }


    @SuppressLint("ObjectAnimatorBinding")
    fun updateSmoothProgressValue(fromValue: Float, toValue: Float, duration: Long, callback: () -> Unit) {
        progressValue = fromValue
        this.animation?.cancel()
        val objectAnimator = ObjectAnimator.ofFloat(this, "ProgressValue", progressValue, toValue)
        objectAnimator.duration = duration
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.addListener(object : AnimatorEventCallback() {
            override fun onAnimationEnd(animation: Animator?) {
                callback()
            }
        })
        objectAnimator.start()
    }


    fun updateProgressBorderColor(color: Int) {
        backgroundPaintColor = color
        backgroundPaint.color = backgroundPaintColor
        invalidate()
    }

    fun updateProgressCenterColor(color: Int) {
        progressForegroundColor = color
        progressForegroundPaint.color = progressForegroundColor
        invalidate()
    }

    fun updateProgressBackgroundColor(color: Int) {
        progressBackgroundPaintColor = color
        progressBackgroundPaint.color = progressBackgroundPaintColor
        invalidate()
    }

    fun updateProgressColor(color: Int) {
        progressPaintColor = color
        progressPaint.color = progressPaintColor
        invalidate()
    }

}