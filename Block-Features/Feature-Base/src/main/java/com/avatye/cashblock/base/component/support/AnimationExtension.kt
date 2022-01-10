package com.avatye.cashblock.base.component.support

import android.view.View
import android.view.animation.*
import androidx.core.view.isVisible

// region { fade in/out }
fun View.animateFadeIn(duration: Long = 300, callback: AnimationEventCallback? = null) {
    isVisible = true
    val alphaAnim = AlphaAnimation(0f, 1f).apply {
        this.duration = duration
        this.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
                callback?.onAnimationRepeat(p0)
            }

            override fun onAnimationEnd(p0: Animation?) {
                callback?.onAnimationEnd(p0)
            }

            override fun onAnimationStart(p0: Animation?) {
                callback?.onAnimationStart(p0)
            }
        })
    }
    startAnimation(alphaAnim)
}


fun View.animateFadeOut(duration: Long = 300, callback: AnimationEventCallback? = null) {
    isVisible = true
    val alphaAnim = AlphaAnimation(1f, 0f).apply {
        this.duration = duration
        this.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                callback?.onAnimationRepeat(animation)
            }

            override fun onAnimationEnd(animation: Animation?) {
                callback?.onAnimationEnd(animation)
            }

            override fun onAnimationStart(animation: Animation?) {
                callback?.onAnimationStart(animation)
            }
        })
    }
    startAnimation(alphaAnim)
}
// endregion


// region { scale }
fun View.animateScaleIn(
    fromX: Float = 0.0F,
    toX: Float = 1.0F,
    fromY: Float = 0.0F,
    toY: Float = 1.0F,
    fromAlpha: Float = 0.0F,
    toAlpha: Float = 1.0F,
    interpolator: Interpolator = LinearInterpolator(),
    duration: Long = 300L,
    callback: () -> Unit = {}
) {
    val animationSet = AnimationSet(true).apply {
        this.fillAfter = true
        this.interpolator = interpolator
        this.setAnimationListener(object : AnimationEventCallback() {
            override fun onAnimationEnd(animation: Animation?) {
                callback()
            }
        })
    }
    val scaleAnimation: Animation = ScaleAnimation(
        fromX, toX, fromY, toY, ScaleAnimation.RELATIVE_TO_SELF, 0.5F, ScaleAnimation.RELATIVE_TO_SELF, 0.5F
    ).apply {
        this.duration = duration
    }
    val alphaAnimation: AlphaAnimation = AlphaAnimation(fromAlpha, toAlpha).apply {
        this.duration = duration
    }
    animationSet.addAnimation(scaleAnimation)
    animationSet.addAnimation(alphaAnimation)
    this.visibility = View.VISIBLE
    this.startAnimation(animationSet)
}
// endregion