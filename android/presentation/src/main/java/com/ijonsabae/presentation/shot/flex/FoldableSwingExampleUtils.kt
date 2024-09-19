package com.ijonsabae.presentation.shot.flex

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.window.layout.DisplayFeature
import com.ijonsabae.presentation.R

object FoldableSwingExampleUtils {
    private const val TAG = "FoldableUtils 싸피"

    lateinit var setExampleImageHalfAnimation: ValueAnimator
    lateinit var setBottomLayoutHalfAnimation: ValueAnimator
    lateinit var restoreAnimation: ValueAnimator
    fun getFeaturePositionInViewRect(
        displayFeature: DisplayFeature,
        view: View,
        includePadding: Boolean = true
    ): Rect? {
        val viewLocationInWindow = IntArray(2)
        view.getLocationInWindow(viewLocationInWindow)

        val viewRect = Rect(
            viewLocationInWindow[0], viewLocationInWindow[1],
            viewLocationInWindow[0] + view.width, viewLocationInWindow[1] + view.height
        )

        if (includePadding) {
            viewRect.left += view.paddingLeft
            viewRect.top += view.paddingTop
            viewRect.right -= view.paddingRight
            viewRect.bottom -= view.paddingBottom
        }

        val featureRectInView = Rect(displayFeature.bounds)
        val intersects = featureRectInView.intersect(viewRect)
        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
            !intersects
        ) {
            return null
        }
        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

        return featureRectInView
    }

    fun View.moveToTopOf(
        foldPosition: Rect,
        swingExample: ImageView,
        exampleLayout: ConstraintLayout,
        menuLayout: ConstraintLayout,
        replayCardView: CardView,
        replayLayout: ConstraintLayout,
        swingCardView: CardView,
        swingLayout: ConstraintLayout,
        replayTitle: TextView,
        swingTitle: TextView,
        replayDescription: TextView,
        swingDescription: TextView,
        replayLogo: ImageView,
        swingLogo: ImageView
    ) {
        if(::restoreAnimation.isInitialized){ restoreAnimation.cancel() }
        menuLayout.setBackgroundColor(Color.BLACK)

        val height = this.height
        setExampleImageHalfAnimation = ValueAnimator.ofFloat(foldPosition.bottom - height.toFloat()).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@moveToTopOf.layoutParams.height = height + animatedValue.toInt()
                this@moveToTopOf.requestLayout()
            }
            doOnEnd {
                val view = this@moveToTopOf as ImageView
                view.scaleType = ImageView.ScaleType.FIT_XY
                this@moveToTopOf.invalidate()
            }
        }

        setBottomLayoutHalfAnimation = ValueAnimator.ofFloat(0F, (this@moveToTopOf.parent as View).bottom - foldPosition.bottom.toFloat() - menuLayout.height).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            val menuLayoutHeight = menuLayout.height
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                menuLayout.layoutParams.height = (menuLayoutHeight + animatedValue).toInt()
            }
            doOnEnd {
                replayDescription.visibility = View.VISIBLE
                swingDescription.visibility = View.VISIBLE

                swingCardView.layoutParams.height = WRAP_CONTENT
                replayCardView.layoutParams.height = WRAP_CONTENT
                ConstraintSet().apply {
                    clone(menuLayout)
                    setDimensionRatio(swingCardView.id, null)
                    setDimensionRatio(replayCardView.id, null)

                    applyTo(menuLayout)
                }

                ConstraintSet().apply {
                    clone(replayLayout)
//                    clear(replayTitle.id)
//                    clear(replayLogo.id)
                    setMargin(replayTitle.id, ConstraintSet.START, 0)

                    connect(replayLogo.id, ConstraintSet.TOP, replayLayout.id, ConstraintSet.TOP)
                    connect(replayLogo.id, ConstraintSet.BOTTOM, replayTitle.id, ConstraintSet.TOP)
                    connect(replayLogo.id, ConstraintSet.START, replayLayout.id, ConstraintSet.START)
                    connect(replayLogo.id, ConstraintSet.END, replayLayout.id, ConstraintSet.END)

                    connect(replayTitle.id, ConstraintSet.TOP, replayLogo.id, ConstraintSet.BOTTOM)
                    connect(replayTitle.id, ConstraintSet.BOTTOM, replayDescription.id, ConstraintSet.TOP)
                    connect(replayTitle.id, ConstraintSet.START, replayLayout.id, ConstraintSet.START)
                    connect(replayTitle.id, ConstraintSet.END, replayLayout.id, ConstraintSet.END)

                    applyTo(replayLayout)
                }

                ConstraintSet().apply {
                    clone(swingLayout)
//                    clear(swingTitle.id)
//                    clear(swingLogo.id)

                    connect(swingLogo.id, ConstraintSet.TOP, swingLayout.id, ConstraintSet.TOP)
                    connect(swingLogo.id, ConstraintSet.BOTTOM, swingTitle.id, ConstraintSet.TOP)
                    connect(swingLogo.id, ConstraintSet.START, swingLayout.id, ConstraintSet.START)
                    connect(swingLogo.id, ConstraintSet.END, swingLayout.id, ConstraintSet.END)

                    connect(swingTitle.id, ConstraintSet.TOP, swingLogo.id, ConstraintSet.BOTTOM)
                    connect(swingTitle.id, ConstraintSet.BOTTOM, swingDescription.id, ConstraintSet.TOP)
                    connect(swingTitle.id, ConstraintSet.START, swingLayout.id, ConstraintSet.START)
                    connect(swingTitle.id, ConstraintSet.END, swingLayout.id, ConstraintSet.END)

                    applyTo(swingLayout)
                }
            }
        }


        setExampleImageHalfAnimation.start()
        setBottomLayoutHalfAnimation.start()
    }

    fun View.restore(
        foldPosition: Rect,
        swingExample: ImageView,
        exampleLayout: ConstraintLayout,
        menuLayout: ConstraintLayout,
        replayCardView: CardView,
        replayLayout: ConstraintLayout,
        swingCardView: CardView,
        swingLayout: ConstraintLayout,
        replayTitle: TextView,
        swingTitle: TextView,
        replayDescription: TextView,
        swingDescription: TextView,
        replayLogo: ImageView,
        swingLogo: ImageView
    ) {
        if(::setExampleImageHalfAnimation.isInitialized){ setExampleImageHalfAnimation.cancel() }
        if(::setBottomLayoutHalfAnimation.isInitialized){ setBottomLayoutHalfAnimation.cancel() }
        replayDescription.visibility = View.GONE
        swingDescription.visibility = View.GONE
        menuLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.alpha70_black))

        val height = this@restore.height
        menuLayout.layoutParams.height = WRAP_CONTENT
        restoreAnimation =
            ValueAnimator.ofInt(0, exampleLayout.height - swingExample.height).apply {
                duration = 300 // 애니메이션 지속 시간 (밀리초)
                interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
                addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    this@restore.layoutParams.height = height + animatedValue
                    this@restore.requestLayout()
                }
                doOnEnd {
                    replayLayout.layoutParams.height = WRAP_CONTENT
                    swingLayout.layoutParams.height = WRAP_CONTENT
                    ConstraintSet().apply {
                        clone(replayLayout)

                        connect(replayLogo.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                        connect(replayLogo.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                        connect(replayLogo.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                        connect(replayLogo.id, ConstraintSet.END, replayTitle.id, ConstraintSet.START)

                        setMargin(replayTitle.id, ConstraintSet.START, 10)
                        connect(replayTitle.id, ConstraintSet.TOP, replayLayout.id, ConstraintSet.TOP)
                        connect(replayTitle.id, ConstraintSet.BOTTOM, replayLayout.id, ConstraintSet.BOTTOM)
                        connect(replayTitle.id, ConstraintSet.START, replayLogo.id, ConstraintSet.END)
                        connect(replayTitle.id, ConstraintSet.END, replayLayout.id, ConstraintSet.END)

                        applyTo(replayLayout)
                        Log.d(TAG, "restore: replaylayout ${replayLayout.height}")
                    }


                    ConstraintSet().apply {
                        clone(swingLayout)

                        connect(swingLogo.id, ConstraintSet.TOP, swingLayout.id, ConstraintSet.TOP)
                        connect(swingLogo.id, ConstraintSet.BOTTOM, swingLayout.id, ConstraintSet.BOTTOM)
                        connect(swingLogo.id, ConstraintSet.START, swingLayout.id, ConstraintSet.START)
                        connect(swingLogo.id, ConstraintSet.END, swingTitle.id, ConstraintSet.START)

                        setMargin(swingTitle.id, ConstraintSet.START, 10)
                        connect(swingTitle.id, ConstraintSet.TOP, swingLayout.id, ConstraintSet.TOP)
                        connect(swingTitle.id, ConstraintSet.BOTTOM, swingLayout.id, ConstraintSet.BOTTOM)
                        connect(swingTitle.id, ConstraintSet.START, swingLogo.id, ConstraintSet.END)
                        connect(swingTitle.id, ConstraintSet.END, swingLayout.id, ConstraintSet.END)

                        applyTo(swingLayout)
                    }

                    swingCardView.layoutParams.height = MATCH_CONSTRAINT
                    replayCardView.layoutParams.height = MATCH_CONSTRAINT

                    ConstraintSet().apply {
                        clone(menuLayout)

                        setDimensionRatio(replayCardView.id, "2.28:1")
                        setDimensionRatio(swingCardView.id, "2.28:1")

                        applyTo(menuLayout)
                    }
                }
            }
        restoreAnimation.start()
    }
}