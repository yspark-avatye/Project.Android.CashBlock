/*
 * Copyright (C) 2016 Robinhood Markets, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avatye.cashblock.base.component.widget.ticker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.os.Build
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.content.res.ResourcesCompat
import com.avatye.cashblock.R
import com.avatye.cashblock.base.component.widget.ticker.TickerUtils.provideAlphabeticalList
import com.avatye.cashblock.base.component.widget.ticker.TickerUtils.provideNumberList

/**
 * The primary view for showing a ticker text view that handles smoothly scrolling from the
 * current text to a given text. The scrolling behavior is defined by
 * [.setCharacterLists] which dictates what characters come in between the starting
 * and ending characters.
 *
 *
 * This class primarily handles the drawing customization of the ticker view, for example
 * setting animation duration, interpolator, colors, etc. It ensures that the canvas is properly
 * positioned, and then it delegates the drawing of each column of text to
 * [TickerColumnManager].
 *
 *
 * This class's API should behave similarly to that of a [android.widget.TextView].
 * However, I chose to extend from [View] instead of [android.widget.TextView]
 * because it allows me full flexibility in customizing the drawing and also support different
 * customization attributes as they are implemented.
 *
 * @author Jin Cao, Robinhood
 */
internal class TickerView : View {
    enum class ScrollingDirection { ANY, UP, DOWN }

    protected val textPaint: Paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val metrics = TickerDrawMetrics(textPaint)
    private val columnManager = TickerColumnManager(metrics)
    private val animator = ValueAnimator.ofFloat(1f)

    // Minor optimizations for re-positioning the canvas for the composer.
    private val viewBounds = Rect()
    private var text: String? = null
    private var lastMeasuredDesiredWidth = 0
    private var lastMeasuredDesiredHeight = 0

    // View attributes, defaults are set in init().
    private var gravity = 0
    /**
     * @return the current text color that's being used to draw the text.
     */
    /**
     * Sets the text color used by this view. The default text color is defined by
     * [.DEFAULT_TEXT_COLOR].
     *
     * @param color the color to set the text to.
     */
    var textColor = 0
        set(color) {
            if (textColor != color) {
                field = color
                textPaint.color = textColor
                invalidate()
            }
        }
    /**
     * @return the current text size that's being used to draw the text.
     */
    /**
     * Sets the text size used by this view. The default text size is defined by
     * [.DEFAULT_TEXT_SIZE].
     *
     * @param textSize the text size in pixel units.
     */
    var textSize = 0f
        set(textSize) {
            if (this.textSize != textSize) {
                field = textSize
                textPaint.textSize = textSize
                onTextPaintMeasurementChanged()
            }
        }
    private var textStyle = 0
    /**
     * @return the delay in milliseconds before the transition animations runs
     */
    /**
     * Sets the delay in milliseconds before this TickerView runs its transition animations. The
     * default animation delay is 0.
     *
     * @param animationDelayInMillis the delay in milliseconds.
     */
    var animationDelay: Long = 0
    /**
     * @return the duration in milliseconds that the transition animations run for.
     */
    /**
     * Sets the duration in milliseconds that this TickerView runs its transition animations. The
     * default animation duration is defined by [.DEFAULT_ANIMATION_DURATION].
     *
     * @param animationDurationInMillis the duration in milliseconds.
     */
    var animationDuration: Long = 0
    /**
     * @return the interpolator used to interpolate the animated values.
     */
    /**
     * Sets the interpolator for the transition animation. The default interpolator is defined by
     * [.DEFAULT_ANIMATION_INTERPOLATOR].
     *
     * @param animationInterpolator the interpolator for the animation.
     */
    var animationInterpolator: Interpolator? = null
    /**
     * @return whether or not we are currently animating measurement changes.
     */
    /**
     * Enables/disables the flag to animate measurement changes. If this flag is enabled, any
     * animation that changes the content's text width (e.g. 9999 to 10000) will have the view's
     * measured width animated along with the text width. However, a side effect of this is that
     * the entering/exiting character might get truncated by the view's view bounds as the width
     * shrinks or expands.
     *
     *
     * Warning: using this feature may degrade performance as it will force a re-measure and
     * re-layout during each animation frame.
     *
     *
     * This flag is disabled by default.
     *
     * @param animateMeasurementChange whether or not to animate measurement changes.
     */
    var animateMeasurementChange = false

    // pending text set from XML because we didn't have a character list initially
    private var pendingTextToSet: String? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    /**
     * We currently only support the following set of XML attributes:
     *
     *  * app:textColor
     *  * app:textSize
     *
     *
     * @param context context from constructor
     * @param attrs attrs from constructor
     * @param defStyleAttr defStyleAttr from constructor
     * @param defStyleRes defStyleRes from constructor
     */
    protected fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val res = context.resources
        val styledAttributes = StyledAttributes(res)
        // Set the view attributes from XML or from default values defined in this class
        val arr = context.obtainStyledAttributes(attrs, R.styleable.TickerView, defStyleAttr, defStyleRes)
        val textAppearanceResId = arr.getResourceId(R.styleable.TickerView_android_textAppearance, -1)
        // Check textAppearance first
        if (textAppearanceResId != -1) {
            val textAppearanceArr = context.obtainStyledAttributes(
                textAppearanceResId, R.styleable.TickerView
            )
            styledAttributes.applyTypedArray(textAppearanceArr)
            textAppearanceArr.recycle()
        }
        // Custom set attributes on the view should override textAppearance if applicable.
        styledAttributes.applyTypedArray(arr)
        // Apply Custom Font
        val customTypeface = ResourcesCompat.getFont(context, R.font.cashblock_font_family)
        textPaint.typeface = customTypeface
        // After we've fetched the correct values for the attributes, set them on the view
        animationInterpolator = DEFAULT_ANIMATION_INTERPOLATOR
        animationDuration = arr.getInt(R.styleable.TickerView_acb_widget_animation_duration, DEFAULT_ANIMATION_DURATION).toLong()
        animateMeasurementChange = arr.getBoolean(R.styleable.TickerView_acb_widget_animate_measurement_change, false)
        gravity = styledAttributes.gravity
        if (styledAttributes.shadowColor != 0) {
            textPaint.setShadowLayer(
                styledAttributes.shadowRadius,
                styledAttributes.shadowDx,
                styledAttributes.shadowDy,
                styledAttributes.shadowColor
            )
        }
        if (styledAttributes.textStyle != 0) {
            textStyle = styledAttributes.textStyle
            typeface = textPaint.typeface
        }
        textColor = styledAttributes.textColor
        textSize = styledAttributes.textSize
        val defaultCharList = arr.getInt(R.styleable.TickerView_acb_widget_character_list, 0)
        when (defaultCharList) {
            1 -> setCharacterLists(provideNumberList())
            2 -> setCharacterLists(provideAlphabeticalList())
            else -> if (isInEditMode) {
                setCharacterLists(provideNumberList())
            }
        }
        val defaultPreferredScrollingDirection = arr.getInt(R.styleable.TickerView_acb_widget_preferred_scrolling_direction, 0)
        when (defaultPreferredScrollingDirection) {
            0 -> metrics.preferredScrollingDirection = ScrollingDirection.ANY
            1 -> metrics.preferredScrollingDirection = ScrollingDirection.UP
            2 -> metrics.preferredScrollingDirection = ScrollingDirection.DOWN
            else -> throw IllegalArgumentException("Unsupported ticker_defaultPreferredScrollingDirection: $defaultPreferredScrollingDirection")
        }
        if (isCharacterListsSet) {
            setText(styledAttributes.text, false)
        } else {
            pendingTextToSet = styledAttributes.text
        }
        arr.recycle()
        animator.addUpdateListener { animation ->
            columnManager.setAnimationProgress(animation.animatedFraction)
            checkForRelayout()
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                columnManager.onAnimationEnd()
                checkForRelayout()
                invalidate()
            }
        })
    }

    /**
     * Only attributes that can be applied from `android:textAppearance` should be added here.
     */
    private inner class StyledAttributes internal constructor(res: Resources) {
        var gravity: Int
        var shadowColor = 0
        var shadowDx = 0f
        var shadowDy = 0f
        var shadowRadius = 0f
        var text: String? = null
        var textColor: Int
        var textSize: Float
        var textStyle = 0
        fun applyTypedArray(arr: TypedArray) {
            gravity = arr.getInt(R.styleable.TickerView_android_gravity, gravity)
            shadowColor = arr.getColor(R.styleable.TickerView_android_shadowColor, shadowColor)
            shadowDx = arr.getFloat(R.styleable.TickerView_android_shadowDx, shadowDx)
            shadowDy = arr.getFloat(R.styleable.TickerView_android_shadowDy, shadowDy)
            shadowRadius = arr.getFloat(R.styleable.TickerView_android_shadowRadius, shadowRadius)
            text = arr.getString(R.styleable.TickerView_android_text)
            textColor = arr.getColor(R.styleable.TickerView_android_textColor, textColor)
            textSize = arr.getDimension(R.styleable.TickerView_android_textSize, textSize)
            textStyle = arr.getInt(R.styleable.TickerView_android_textStyle, textStyle)
        }

        init {
            textColor = DEFAULT_TEXT_COLOR
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                DEFAULT_TEXT_SIZE.toFloat(), res.displayMetrics
            )
            gravity = DEFAULT_GRAVITY
        }
    }
    /********** BEGIN PUBLIC API  */
    /**
     * This is the primary class that Ticker uses to determine how to animate from one character
     * to another. The provided strings dictates what characters will appear between
     * the start and end characters.
     *
     *
     * For example, given the string "abcde", if the view wants to animate from 'd' to 'b',
     * it will know that it has to go from 'd' to 'c' to 'b', and these are the characters
     * that avatye_animator_cashfloter_show up during the animation scroll.
     *
     *
     * We allow for multiple character lists, and the character lists will be prioritized with
     * latter lists given a higher priority than the previous lists. e.g. given "123" and "13",
     * an animation from 1 to 3 will use the sequence [1,3] rather than [1,2,3].
     *
     *
     * You can find some helpful character list in [TickerUtils].
     *
     * @param characterLists the list of character lists that dictates animation.
     */
    fun setCharacterLists(vararg characterLists: String?) {
        columnManager.setCharacterLists(*characterLists)
        if (pendingTextToSet != null) {
            setText(pendingTextToSet, false)
            pendingTextToSet = null
        }
    }

    /**
     * @return whether or not the character lists (via [.setCharacterLists]) have been set.
     * Can use this value to determine if you need to call [.setCharacterLists]
     * before calling [.setText].
     */
    val isCharacterListsSet: Boolean
        get() = columnManager.characterLists != null

    /**
     * Sets the string value to display. If the TickerView is currently empty, then this method
     * will immediately display the provided text. Otherwise, it will run the default animation
     * to reach the provided text.
     *
     * @param text the text to display.
     */
    fun setText(text: String?) {
        setText(text, !TextUtils.isEmpty(this.text))
    }

    /**
     * Similar to [.setText] but provides the optional argument of whether to
     * animate to the provided text or not.
     *
     * @param text the text to display.
     * @param animate whether to animate to text.
     */
    fun setText(text: String?, animate: Boolean) {
        if (TextUtils.equals(text, this.text)) {
            return
        }
        this.text = text
        val targetText = text?.toCharArray() ?: CharArray(0)
        columnManager.setText(targetText)
        contentDescription = text
        if (animate) { // Kick off the animator that draws the transition
            if (animator.isRunning) {
                animator.cancel()
            }
            animator.startDelay = animationDelay
            animator.duration = animationDuration
            animator.interpolator = animationInterpolator
            animator.start()
        } else {
            columnManager.setAnimationProgress(1f)
            columnManager.onAnimationEnd()
            checkForRelayout()
            invalidate()
        }
    }

    /**
     * Get the last set text on the view. This does not equate to the current shown text on the
     * UI because the animation might not have started or finished yet.
     *
     * @return last set text on this view.
     */
    fun getText(): String? {
        return text
    }

    /**
     * @return the current text typeface.
     */
    /**
     * Sets the typeface size used by this view.
     *
     * @param typeface the typeface to use on the text.
     */
    var typeface: Typeface?
        get() = textPaint.typeface
        set(value) {
            var tf = value
            if (textStyle == Typeface.BOLD_ITALIC) {
                tf = Typeface.create(tf, Typeface.BOLD_ITALIC)
            } else if (textStyle == Typeface.BOLD) {
                tf = Typeface.create(tf, Typeface.BOLD)
            } else if (textStyle == Typeface.ITALIC) {
                tf = Typeface.create(tf, Typeface.ITALIC)
            }
            textPaint.typeface = tf
            onTextPaintMeasurementChanged()
        }

    /**
     * Sets the preferred scrolling direction for ticker animations.
     * Eligible params include [ScrollingDirection.ANY], [ScrollingDirection.UP]
     * and [ScrollingDirection.DOWN].
     *
     * The default value is [ScrollingDirection.ANY].
     *
     * @param direction the preferred [ScrollingDirection]
     */
    fun setPreferredScrollingDirection(direction: ScrollingDirection?) {
        metrics.preferredScrollingDirection = direction!!
    }

    /**
     * @return the current text gravity used to align the text. Should be one of the values defined
     * in [android.view.Gravity].
     */
    fun getGravity(): Int {
        return gravity
    }

    /**
     * Sets the gravity used to align the text.
     *
     * @param gravity the new gravity, should be one of the values defined in
     * [android.view.Gravity].
     */
    fun setGravity(gravity: Int) {
        if (this.gravity != gravity) {
            this.gravity = gravity
            invalidate()
        }
    }

    /**
     * Adds a custom [android.animation.Animator.AnimatorListener] to listen to animator
     * update events used by this view.
     *
     * @param animatorListener the custom animator listener.
     */
    fun addAnimatorListener(animatorListener: Animator.AnimatorListener?) {
        animator.addListener(animatorListener)
    }

    /**
     * Removes the specified custom [android.animation.Animator.AnimatorListener] from
     * this view.
     *
     * @param animatorListener the custom animator listener.
     */
    fun removeAnimatorListener(animatorListener: Animator.AnimatorListener?) {
        animator.removeListener(animatorListener)
    }

    /**
     * Configures the textpaint used for drawing individual ticker characters.
     * See [Paint.setFlags] for more information.
     *
     * @param flags the new flag bits for the paint
     */
    fun setPaintFlags(flags: Int) {
        textPaint.flags = flags
        onTextPaintMeasurementChanged()
    }

    /**
     * Exposing method to add or remove blur mask filter to ticker text.
     *
     * @param style Blur mask filter type
     * @param radius Density of filter
     */
    fun setBlurMaskFilter(style: Blur?, radius: Float) {
        if (style != null && radius > 0f) {
            val filter = BlurMaskFilter(radius, style)
            textPaint.maskFilter = filter
        } else {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
            textPaint.maskFilter = null
        }
    }
    /********** END PUBLIC API  */
    /**
     * Force the view to call [.requestLayout] if the new text doesn't match the old bounds
     * we set for the previous view state.
     */
    private fun checkForRelayout() {
        val widthChanged = lastMeasuredDesiredWidth != computeDesiredWidth()
        val heightChanged = lastMeasuredDesiredHeight != computeDesiredHeight()
        if (widthChanged || heightChanged) {
            requestLayout()
        }
    }

    private fun computeDesiredWidth(): Int {
        val contentWidth =
            (if (animateMeasurementChange) columnManager.currentWidth else columnManager.minimumRequiredWidth).toInt()
        return contentWidth + paddingLeft + paddingRight
    }

    private fun computeDesiredHeight(): Int {
        return metrics.charHeight.toInt() + paddingTop + paddingBottom
    }

    /**
     * Re-initialize all of our variables that are dependent on the TextPaint measurements.
     */
    private fun onTextPaintMeasurementChanged() {
        metrics.invalidate()
        checkForRelayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        lastMeasuredDesiredWidth = computeDesiredWidth()
        lastMeasuredDesiredHeight = computeDesiredHeight()
        val desiredWidth = resolveSize(lastMeasuredDesiredWidth, widthMeasureSpec)
        val desiredHeight = resolveSize(lastMeasuredDesiredHeight, heightMeasureSpec)
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        viewBounds[paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        realignAndClipCanvasForGravity(canvas)
        // canvas.drawText writes the text on the baseline so we need to translate beforehand.
        canvas.translate(0f, metrics.charBaseline)
        columnManager.draw(canvas, textPaint)
        canvas.restore()
    }

    private fun realignAndClipCanvasForGravity(canvas: Canvas) {
        val currentWidth = columnManager.currentWidth
        val currentHeight = metrics.charHeight
        realignAndClipCanvasForGravity(canvas, gravity, viewBounds, currentWidth, currentHeight)
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE = 12
        private const val DEFAULT_TEXT_COLOR = Color.BLACK
        private const val DEFAULT_ANIMATION_DURATION = 350
        private val DEFAULT_ANIMATION_INTERPOLATOR: Interpolator =
            AccelerateDecelerateInterpolator()
        private const val DEFAULT_GRAVITY = Gravity.START

        // VisibleForTesting
        fun realignAndClipCanvasForGravity(
            canvas: Canvas, gravity: Int, viewBounds: Rect,
            currentWidth: Float, currentHeight: Float
        ) {
            val availableWidth = viewBounds.width()
            val availableHeight = viewBounds.height()
            var translationX = 0f
            var translationY = 0f
            if (gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL) {
                translationY = viewBounds.top + (availableHeight - currentHeight) / 2f
            }
            if (gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL) {
                translationX = viewBounds.left + (availableWidth - currentWidth) / 2f
            }
            if (gravity and Gravity.TOP == Gravity.TOP) {
                translationY = 0f
            }
            if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
                translationY = viewBounds.top + (availableHeight - currentHeight)
            }
            if (gravity and Gravity.START == Gravity.START) {
                translationX = 0f
            }
            if (gravity and Gravity.END == Gravity.END) {
                translationX = viewBounds.left + (availableWidth - currentWidth)
            }
            canvas.translate(translationX, translationY)
            canvas.clipRect(0f, 0f, currentWidth, currentHeight)
        }
    }
}