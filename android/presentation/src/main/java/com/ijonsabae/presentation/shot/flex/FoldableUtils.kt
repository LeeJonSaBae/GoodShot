package com.ijonsabae.presentation.shot.flex

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Constraints
import androidx.core.animation.doOnEnd
import androidx.window.layout.DisplayFeature
import com.ijonsabae.presentation.config.Const
import com.ijonsabae.presentation.shot.CameraState
import com.ijonsabae.presentation.shot.flex.FoldableUtils.moveToTopOf

object FoldableUtils {
    private const val TAG = "FoldableUtils 싸피"
    lateinit var animator1: ValueAnimator
    lateinit var animator2: ValueAnimator
    lateinit var animator: ValueAnimator
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

    fun View.moveToRightOf(foldingFeatureRect: Rect) {
        x = foldingFeatureRect.left.toFloat()
        layoutParams = layoutParams.apply {
            width = (parent as View).width - foldingFeatureRect.left
        }
    }

    fun View.moveToTopOf(foldingFeatureRect: Rect, cameraState: CameraState?, progressTitle: TextView, resultHeader:TextView, resultSubHeader: TextView, alertIcon: ImageView, alertText: TextView, cameraMenuLayout: ConstraintLayout, alertConstraintLayout: ConstraintLayout, rootConstraintLayout: ConstraintLayout) {
        if(::animator.isInitialized){ animator.cancel() }
        val cameraHeight = this.height
        val alertHeight = alertConstraintLayout.height
        if(cameraState == CameraState.RESULT){
            resultHeader.visibility = View.VISIBLE
            resultSubHeader.visibility = View.VISIBLE
        }else{
            resultHeader.visibility = View.GONE
            resultSubHeader.visibility = View.GONE
        }
        if(cameraState == CameraState.ANALYZING){
            progressTitle.visibility = View.VISIBLE
        }else{
            progressTitle.visibility = View.GONE
        }

        ConstraintSet().apply {
            clone(alertConstraintLayout)
            connect(alertIcon.id, ConstraintSet.BOTTOM, alertText.id, ConstraintSet.TOP)
            connect(alertText.id, ConstraintSet.TOP, alertIcon.id, ConstraintSet.BOTTOM)
            connect(alertText.id, ConstraintSet.BOTTOM, alertConstraintLayout.id, ConstraintSet.BOTTOM)

            connect(alertIcon.id, ConstraintSet.START, alertConstraintLayout.id, ConstraintSet.START)
            connect(alertIcon.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)

            connect(alertText.id, ConstraintSet.START, alertConstraintLayout.id, ConstraintSet.START)
            connect(alertText.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)
            applyTo(alertConstraintLayout)
        }

        ConstraintSet().apply {
            clone(rootConstraintLayout)
            connect(this@moveToTopOf.id, ConstraintSet.TOP, rootConstraintLayout.id, ConstraintSet.TOP)
            connect(cameraMenuLayout.id, ConstraintSet.TOP, this@moveToTopOf.id, ConstraintSet.BOTTOM)
            clear(cameraMenuLayout.id, ConstraintSet.BOTTOM)
            connect(this@moveToTopOf.id, ConstraintSet.BOTTOM, cameraMenuLayout.id, ConstraintSet.TOP)
            connect(alertConstraintLayout.id, ConstraintSet.TOP, cameraMenuLayout.id, ConstraintSet.BOTTOM)
            applyTo(rootConstraintLayout)
        }




// 목표 위치를 정의합니다.
        animator1 = ValueAnimator.ofFloat(0F, foldingFeatureRect.bottom.toFloat() - this@moveToTopOf.bottom + cameraMenuLayout.height).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            val top = this@moveToTopOf.top
            val bottom = this@moveToTopOf.bottom
            val alerttop = alertConstraintLayout.top
            val alertbottom = alertConstraintLayout.bottom
            val alertIcontop = alertIcon.top
            val alertIconbottom = alertIcon.bottom
            val alertTextTop = alertText.top
            val alertTextbottom = alertText.bottom
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@moveToTopOf.elevation = 10F
                this@moveToTopOf.translationY = animatedValue

                cameraMenuLayout.translationY = animatedValue
                alertConstraintLayout.top = alerttop + animatedValue.toInt()

                alertIcon.translationY = -animatedValue/2
                alertText.translationY = -animatedValue/2

            }
        }
//
//
//        animator2 = ValueAnimator.ofFloat(0F, (this@moveToTopOf.parent as View).bottom - foldingFeatureRect.bottom.toFloat() - alertHeight).apply {
//            duration = 300 // 애니메이션 지속 시간 (밀리초)
//            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
//
//            addUpdateListener { valueAnimator ->
//                val animatedValue = valueAnimator.animatedValue as Float
//                Log.d(TAG, "moveToTopOf animate: $animatedValue")
//                alertConstraintLayout.layoutParams.height = (alertHeight + animatedValue).toInt()
////                alertConstraintLayout.layoutParams.height = alertHeight - alertHeight/2
////                alertConstraintLayout.layoutParams.height = alertHeight - animatedValue.toInt()
////                alertConstraintLayout.top = top - animatedValue.toInt()
//                Log.d(TAG, "moveToTopOf: ${alertConstraintLayout.layoutParams.height}")
//            }
//            doOnEnd {
//                Log.d(TAG, "moveToTopOfEnd: ${alertConstraintLayout.layoutParams.height}")
//            }
//        }

        animator1.start()
    }

    fun View.restore(foldingFeatureRect: Rect, cameraState: CameraState?, progressTitle: TextView, resultHeader:TextView, resultSubHeader: TextView, alertIcon: ImageView, alertText: TextView, cameraMenuLayout: ConstraintLayout, alertConstraintLayout: ConstraintLayout, rootConstraintLayout: ConstraintLayout) {
        if(::animator1.isInitialized){ animator1.cancel() }
        if(::animator2.isInitialized){ animator2.cancel() }
        resultHeader.visibility = View.GONE
        resultSubHeader.visibility = View.GONE
        progressTitle.visibility = View.GONE
        val height = this.height
        animator = ValueAnimator.ofFloat(this.translationY, 0F).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@restore.translationY = animatedValue
                cameraMenuLayout.translationY = animatedValue
                alertIcon.translationY = animatedValue
                alertText.translationY = animatedValue
            }
        }

        animator.start()

        ConstraintSet().apply {
            clone(rootConstraintLayout)
            connect(alertConstraintLayout.id, ConstraintSet.TOP, cameraMenuLayout.id, ConstraintSet.BOTTOM)
            connect(cameraMenuLayout.id, ConstraintSet.TOP, rootConstraintLayout.id, ConstraintSet.TOP)
            clear(cameraMenuLayout.id, ConstraintSet.BOTTOM)
            connect(this@restore.id, ConstraintSet.TOP, cameraMenuLayout.id, ConstraintSet.BOTTOM)
            connect(this@restore.id, ConstraintSet.BOTTOM, alertConstraintLayout.id, ConstraintSet.TOP)
            connect(alertConstraintLayout.id, ConstraintSet.TOP, this@restore.id, ConstraintSet.BOTTOM)
            connect(alertConstraintLayout.id, ConstraintSet.BOTTOM, rootConstraintLayout.id, ConstraintSet.BOTTOM)
            applyTo(rootConstraintLayout)
        }


        ConstraintSet().apply {
            clone(alertConstraintLayout)
            connect(alertIcon.id, ConstraintSet.TOP, alertConstraintLayout.id, ConstraintSet.TOP)
            connect(
                alertIcon.id,
                ConstraintSet.BOTTOM,
                alertConstraintLayout.id,
                ConstraintSet.BOTTOM
            )
            connect(
                alertIcon.id,
                ConstraintSet.START,
                alertConstraintLayout.id,
                ConstraintSet.START
            )
            connect(alertIcon.id, ConstraintSet.END, alertText.id, ConstraintSet.START)

            connect(alertText.id, ConstraintSet.TOP, alertConstraintLayout.id, ConstraintSet.TOP)
            connect(
                alertText.id,
                ConstraintSet.BOTTOM,
                alertConstraintLayout.id,
                ConstraintSet.BOTTOM
            )
            connect(alertText.id, ConstraintSet.START, alertIcon.id, ConstraintSet.END)
            connect(alertText.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)
            applyTo(alertConstraintLayout)
        }
//        this.layoutParams.height = MATCH_CONSTRAINT
//        this.layoutParams.height = MATCH_PARENT
        alertConstraintLayout.layoutParams.height = MATCH_CONSTRAINT
    }
}