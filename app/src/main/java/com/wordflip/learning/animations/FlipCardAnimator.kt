package com.wordflip.learning.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Reusable flip animations for card-like UIs.
 */
object FlipCardAnimator {

    private const val ANIMATION_DURATION: Long = 600
    private const val FLIP_DISTANCE: Float = 8000f

    fun createFlipAnimation(
        frontView: View,
        backView: View,
        isShowingFront: Boolean,
        onAnimationMiddle: (() -> Unit)? = null,
        onAnimationEnd: (() -> Unit)? = null
    ): AnimatorSet {
        val animatorSet = AnimatorSet()
        if (isShowingFront) {
            animatorSet.playSequentially(
                createFlipOutAnimation(frontView) {
                    frontView.visibility = View.GONE
                    backView.visibility = View.VISIBLE
                    onAnimationMiddle?.invoke()
                },
                createFlipInAnimation(backView) {
                    onAnimationEnd?.invoke()
                }
            )
        } else {
            animatorSet.playSequentially(
                createFlipOutAnimation(backView) {
                    backView.visibility = View.GONE
                    frontView.visibility = View.VISIBLE
                    onAnimationMiddle?.invoke()
                },
                createFlipInAnimation(frontView) {
                    onAnimationEnd?.invoke()
                }
            )
        }
        return animatorSet
    }

    private fun createFlipOutAnimation(view: View, onComplete: () -> Unit): AnimatorSet {
        val rotationY = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.5f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationY, scaleX, scaleY, alpha)
        animatorSet.duration = ANIMATION_DURATION / 2
        animatorSet.interpolator = FastOutSlowInInterpolator()
        view.cameraDistance = FLIP_DISTANCE

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete.invoke()
            }
        })
        return animatorSet
    }

    private fun createFlipInAnimation(view: View, onComplete: () -> Unit): AnimatorSet {
        view.rotationY = -90f
        view.scaleX = 0.9f
        view.scaleY = 0.9f
        view.alpha = 0.5f

        val rotationY = ObjectAnimator.ofFloat(view, "rotationY", -90f, 0f)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationY, scaleX, scaleY, alpha)
        animatorSet.duration = ANIMATION_DURATION / 2
        animatorSet.interpolator = FastOutSlowInInterpolator()
        view.cameraDistance = FLIP_DISTANCE

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete.invoke()
            }
        })
        return animatorSet
    }

    fun createQuickFlip(view: View, onComplete: () -> Unit) {
        val rotationY = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f)
        rotationY.duration = 400L
        rotationY.interpolator = FastOutSlowInInterpolator()
        view.cameraDistance = FLIP_DISTANCE
        rotationY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete.invoke()
            }
        })
        rotationY.start()
    }
}


