/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ijonsabae.presentation.shot.flex

import android.animation.ValueAnimator
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.window.layout.DisplayFeature
import com.ijonsabae.presentation.shot.CameraState

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

    fun View.moveToTopOf(foldingFeatureRect: Rect, cameraState: CameraState?, progressTitle: TextView, resultHeader:TextView, resultSubHeader: TextView, alertIcon: ImageView, alertText: TextView, cameraMenuLayout: ConstraintLayout, alertConstraintLayout: ConstraintLayout, constraintLayout: ConstraintLayout) {
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
// 목표 위치를 정의합니다.
        animator1 = ValueAnimator.ofFloat(0F, foldingFeatureRect.bottom.toFloat() - cameraHeight - cameraMenuLayout.height).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절

            ConstraintSet().apply {
                clone(constraintLayout)
                clear(this@moveToTopOf.id, ConstraintSet.BOTTOM)
                applyTo(constraintLayout)
            }

            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@moveToTopOf.layoutParams.height  = (cameraHeight + animatedValue).toInt()
                this@moveToTopOf.requestLayout()

                ConstraintSet().apply {
                    clone(alertConstraintLayout)
                    connect(this@moveToTopOf.id, ConstraintSet.TOP, cameraMenuLayout.id, ConstraintSet.BOTTOM)
                    connect(alertIcon.id, ConstraintSet.BOTTOM, alertText.id, ConstraintSet.TOP)
                    connect(alertText.id, ConstraintSet.TOP, alertIcon.id, ConstraintSet.BOTTOM)
                    connect(alertText.id, ConstraintSet.BOTTOM, alertConstraintLayout.id, ConstraintSet.BOTTOM)


                    connect(alertIcon.id, ConstraintSet.START, alertConstraintLayout.id, ConstraintSet.START)
                    connect(alertIcon.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)

                    connect(alertText.id, ConstraintSet.START, alertConstraintLayout.id, ConstraintSet.START)
                    connect(alertText.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)
                    applyTo(alertConstraintLayout)
                }

            }
        }

        animator2 = ValueAnimator.ofFloat(0F, (this@moveToTopOf.parent as View).bottom - foldingFeatureRect.bottom.toFloat() - alertHeight).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절

            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                alertConstraintLayout.layoutParams.height = (alertHeight + animatedValue).toInt()

                // View의 위치를 설정합니다.
                Log.d(TAG, "animation2 : y ${alertConstraintLayout.y}")
                doOnEnd {
//                    alertConstraintLayout.requestLayout()
                    ConstraintSet().apply {
                        clone(alertConstraintLayout)
                        connect(alertIcon.id, ConstraintSet.TOP, alertConstraintLayout.id, ConstraintSet.TOP)
                        connect(alertIcon.id, ConstraintSet.BOTTOM, alertText.id, ConstraintSet.TOP)
                        connect(alertText.id, ConstraintSet.TOP, alertIcon.id, ConstraintSet.BOTTOM)
                        connect(alertText.id, ConstraintSet.BOTTOM, alertConstraintLayout.id, ConstraintSet.BOTTOM)


                        connect(alertIcon.id, ConstraintSet.START, alertConstraintLayout.id, ConstraintSet.START)
                        connect(alertIcon.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)

                        connect(alertText.id, ConstraintSet.START, alertConstraintLayout.id, ConstraintSet.START)
                        connect(alertText.id, ConstraintSet.END, alertConstraintLayout.id, ConstraintSet.END)
                        applyTo(alertConstraintLayout)
                    }
                }
            }
        }

        animator1.start()
        animator2.start()
    }

    fun View.restore(foldingFeatureRect: Rect, cameraState: CameraState?, progressTitle: TextView, resultHeader:TextView, resultSubHeader: TextView, alertIcon: ImageView, alertText: TextView, cameraMenuLayout: ConstraintLayout, alertConstraintLayout: ConstraintLayout, rootConstraintLayout: ConstraintLayout) {
        if(::animator1.isInitialized){ animator1.cancel() }
        if(::animator2.isInitialized){ animator2.cancel() }
        resultHeader.visibility = View.GONE
        resultSubHeader.visibility = View.GONE
        progressTitle.visibility = View.GONE
        val height = this.height
        animator = ValueAnimator.ofFloat(0F, rootConstraintLayout.height - cameraMenuLayout.height - height.toFloat()).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@restore.layoutParams.height = height + animatedValue.toInt()
                this@restore.requestLayout()
            }
            doOnEnd {
                ConstraintSet().apply {
                    clone(rootConstraintLayout)
                    connect(this@restore.id, ConstraintSet.BOTTOM, rootConstraintLayout.id, ConstraintSet.BOTTOM)
                    applyTo(rootConstraintLayout)
                }
            }
        }

        animator.start()

        ConstraintSet().apply {
            clone(rootConstraintLayout)
            connect(cameraMenuLayout.id, ConstraintSet.TOP, rootConstraintLayout.id, ConstraintSet.TOP)
            connect(this@restore.id, ConstraintSet.TOP, cameraMenuLayout.id, ConstraintSet.BOTTOM)
            clear(this@restore.id, ConstraintSet.BOTTOM)
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
        alertConstraintLayout.layoutParams.height = WRAP_CONTENT
        alertConstraintLayout.requestLayout()
//        requestLayout()
    }
}