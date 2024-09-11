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
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowInsets
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowInsetsCompat
import androidx.window.layout.DisplayFeature
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.Const
import com.ijonsabae.presentation.main.MainActivity
import com.ijonsabae.presentation.shot.flex.FoldableUtils.getStatusBarHeight
import com.ijonsabae.presentation.shot.flex.FoldableUtils.moveToTopOf
import com.ijonsabae.presentation.shot.flex.FoldableUtils.restore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking

object FoldableUtils {
    private const val TAG = "FoldableUtils 싸피"
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

    fun View.moveToTopOf(foldingFeatureRect: Rect, constraintLayout: ConstraintLayout) {
        val height = this.height
        val translationY = this.translationY
// 목표 위치를 정의합니다.
        val animator1 = ValueAnimator.ofFloat(0F, foldingFeatureRect.bottom.toFloat() - height).apply {
            duration = 100 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@moveToTopOf.layoutParams.height  = (height + animatedValue).toInt()

                this@moveToTopOf.requestLayout()
                this@moveToTopOf.invalidate()
                // View의 위치를 설정합니다.
                Log.d(TAG, "animation1 : y ${y}")
                doOnEnd {
                    val animator = ValueAnimator.ofFloat(0F,y).apply {
                        duration = 300 // 애니메이션 지속 시간 (밀리초)
                        interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
                        addUpdateListener { valueAnimator ->
                            val animatedValue = valueAnimator.animatedValue as Float
                            Log.d(TAG, "animation: $animatedValue")
                            this@moveToTopOf.translationY  = translationY - animatedValue
                            // View의 위치를 설정합니다.
                            Log.d(TAG, "animation : y ${y}")
//                this@restore.translationY = animatedValue
//                this@restore.layoutParams.height = height - animatedValue.toInt()
//                this@restore.requestLayout()
                        }
                    }
                    animator.start()
                }
            }
        }

        animator1.start()
    }

    fun px2dp(px: Int, context: Context) = px / ((context.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)

    fun View.getStatusBarHeight(): Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = WindowInsetsCompat.toWindowInsetsCompat(this.rootWindowInsets)
            insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        } else {
            // Legacy method for older versions
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }
    }

    fun View.restore(foldingFeatureRect: Rect, constraintLayout: ConstraintLayout) {


        val translationY = this@restore.translationY

        // 위치 이동
        val animator = ValueAnimator.ofFloat(0F, translationY).apply {
            duration = 300 // 애니메이션 지속 시간 (밀리초)
            interpolator = AccelerateDecelerateInterpolator() // 애니메이션의 속도 조절
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                this@restore.translationY  = translationY - animatedValue
            }
        }
        // 애니메이션 시작
        animator.start()
        this.layoutParams.height = MATCH_CONSTRAINT
//        this.layoutParams.height = MATCH_PARENT
        requestLayout()
    }
}