package com.github.sundeepk.compactcalendarview

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarAnimationListener
import kotlin.math.sqrt

internal class AnimationHandler(private val compactCalendarController: CompactCalendarController, private val compactCalendarView: CompactCalendarView) {
    var isAnimating = false
        private set
    private var compactCalendarAnimationListener: CompactCalendarAnimationListener? = null
    fun setCompactCalendarAnimationListener(compactCalendarAnimationListener: CompactCalendarAnimationListener?) {
        this.compactCalendarAnimationListener = compactCalendarAnimationListener
    }

    fun openCalendar() {
        if (isAnimating) {
            return
        }
        isAnimating = true
        val heightAnim = getCollapsingAnimation(true)
        heightAnim.duration = HEIGHT_ANIM_DURATION_MILLIS.toLong()
        heightAnim.interpolator = AccelerateDecelerateInterpolator()
        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR)
        setUpAnimationLisForOpen(heightAnim)
        compactCalendarView.layoutParams.height = 0
        compactCalendarView.requestLayout()
        compactCalendarView.startAnimation(heightAnim)
    }

    fun closeCalendar() {
        if (isAnimating) {
            return
        }
        isAnimating = true
        val heightAnim = getCollapsingAnimation(false)
        heightAnim.duration = HEIGHT_ANIM_DURATION_MILLIS.toLong()
        heightAnim.interpolator = AccelerateDecelerateInterpolator()
        setUpAnimationLisForClose(heightAnim)
        compactCalendarController.setAnimationStatus(CompactCalendarController.EXPAND_COLLAPSE_CALENDAR)
        compactCalendarView.layoutParams.height = compactCalendarView.height
        compactCalendarView.requestLayout()
        compactCalendarView.startAnimation(heightAnim)
    }

    fun openCalendarWithAnimation() {
        if (isAnimating) {
            return
        }
        isAnimating = true
        val indicatorAnim = getIndicatorAnimator(1f, compactCalendarController.dayIndicatorRadius)
        val heightAnim = getExposeCollapsingAnimation(true)
        compactCalendarView.layoutParams.height = 0
        compactCalendarView.requestLayout()
        setUpAnimationLisForExposeOpen(indicatorAnim, heightAnim)
        compactCalendarView.startAnimation(heightAnim)
    }

    fun closeCalendarWithAnimation() {
        if (isAnimating) {
            return
        }
        isAnimating = true
        val indicatorAnim = getIndicatorAnimator(compactCalendarController.dayIndicatorRadius, 1f)
        val heightAnim = getExposeCollapsingAnimation(false)
        compactCalendarView.layoutParams.height = compactCalendarView.height
        compactCalendarView.requestLayout()
        setUpAnimationLisForExposeClose(indicatorAnim, heightAnim)
        compactCalendarView.startAnimation(heightAnim)
    }

    private fun setUpAnimationLisForExposeOpen(indicatorAnim: Animator, heightAnim: Animation) {
        heightAnim.setAnimationListener(object : AnimationListener() {
            override fun onAnimationStart(animation: Animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.EXPOSE_CALENDAR_ANIMATION)
            }

            override fun onAnimationEnd(animation: Animation) {
                indicatorAnim.start()
            }
        })
        indicatorAnim.addListener(object : AnimatorListener() {
            override fun onAnimationStart(animation: Animator) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.ANIMATE_INDICATORS)
            }

            override fun onAnimationEnd(animation: Animator) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.IDLE)
                onOpen()
                isAnimating = false
            }
        })
    }

    private fun setUpAnimationLisForExposeClose(indicatorAnim: Animator, heightAnim: Animation) {
        heightAnim.setAnimationListener(object : AnimationListener() {
            override fun onAnimationStart(animation: Animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.EXPOSE_CALENDAR_ANIMATION)
                indicatorAnim.start()
            }

            override fun onAnimationEnd(animation: Animation) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.IDLE)
                onClose()
                isAnimating = false
            }
        })
        indicatorAnim.addListener(object : AnimatorListener() {
            override fun onAnimationStart(animation: Animator) {
                compactCalendarController.setAnimationStatus(CompactCalendarController.ANIMATE_INDICATORS)
            }

            override fun onAnimationEnd(animation: Animator) {}
        })
    }

    private fun getExposeCollapsingAnimation(isCollapsing: Boolean): Animation {
        val heightAnim = getCollapsingAnimation(isCollapsing)
        heightAnim.duration = HEIGHT_ANIM_DURATION_MILLIS.toLong()
        heightAnim.interpolator = AccelerateDecelerateInterpolator()
        return heightAnim
    }

    private fun getCollapsingAnimation(isCollapsing: Boolean): Animation {
        return CollapsingAnimation(compactCalendarView, compactCalendarController, compactCalendarController.targetHeight, targetGrowRadius, isCollapsing)
    }

    private fun getIndicatorAnimator(from: Float, to: Float): Animator {
        val animIndicator = ValueAnimator.ofFloat(from, to)
        animIndicator.duration = INDICATOR_ANIM_DURATION_MILLIS.toLong()
        animIndicator.interpolator = OvershootInterpolator()
        animIndicator.addUpdateListener { animation ->
            compactCalendarController.growFactorIndicator = (animation.animatedValue as Float)
            compactCalendarView.invalidate()
        }
        return animIndicator
    }

    private val targetGrowRadius: Int
        get() {
            val heightSq = compactCalendarController.targetHeight * compactCalendarController.targetHeight
            val widthSq = compactCalendarController.width * compactCalendarController.width
            return (0.5 * sqrt(heightSq + widthSq.toDouble())).toInt()
        }

    private fun onOpen() {
        if (compactCalendarAnimationListener != null) {
            compactCalendarAnimationListener!!.onOpened()
        }
    }

    private fun onClose() {
        if (compactCalendarAnimationListener != null) {
            compactCalendarAnimationListener!!.onClosed()
        }
    }

    private fun setUpAnimationLisForOpen(openAnimation: Animation) {
        openAnimation.setAnimationListener(object : AnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                super.onAnimationEnd(animation)
                onOpen()
                isAnimating = false
            }
        })
    }

    private fun setUpAnimationLisForClose(openAnimation: Animation) {
        openAnimation.setAnimationListener(object : AnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                super.onAnimationEnd(animation)
                onClose()
                isAnimating = false
            }
        })
    }

    companion object {
        private const val HEIGHT_ANIM_DURATION_MILLIS = 650
        private const val INDICATOR_ANIM_DURATION_MILLIS = 600
    }
}
