/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ijonsabae.presentation.shot.flex

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.ijonsabae.presentation.shot.flex.FoldableUtils.moveToRightOf
import com.ijonsabae.presentation.shot.flex.FoldableUtils.moveToTopOf
import com.ijonsabae.presentation.shot.flex.FoldableUtils.restore
import javax.inject.Inject

private const val TAG = "FoldingStateActor 싸피"
class FoldingStateActor @Inject constructor(private val windowInfoTracker: WindowInfoTracker) {

    private var activeWindowLayoutInfo: WindowLayoutInfo? = null
    suspend fun checkFoldingState(
        activity: AppCompatActivity,
        cameraViewfinder: View,
        alertIcon: ImageView,
        alertText: TextView,
        cameraMenuLayout: ConstraintLayout,
        alertConstraintLayout: ConstraintLayout,
        rootConstraintLayout: ConstraintLayout
    ) {
        windowInfoTracker.windowLayoutInfo(activity)
            .collect { newLayoutInfo ->
                activeWindowLayoutInfo = newLayoutInfo
                updateLayoutByFoldingState(cameraViewfinder, alertIcon, alertText, cameraMenuLayout, alertConstraintLayout, rootConstraintLayout)
            }
    }

    private fun updateLayoutByFoldingState(cameraViewfinder: View, alertIcon: ImageView, alertText: TextView, cameraMenuLayout: ConstraintLayout, alertConstraintLayout: ConstraintLayout, rootConstraintLayout: ConstraintLayout) {
        val foldingFeature = activeWindowLayoutInfo?.displayFeatures
            ?.firstOrNull { it is FoldingFeature } as FoldingFeature?
            ?: return

        val foldPosition = FoldableUtils.getFeaturePositionInViewRect(
            foldingFeature,
            cameraViewfinder.parent as View
        ) ?: return

        if (foldingFeature.state == FoldingFeature.State.HALF_OPENED) {
            when (foldingFeature.orientation) {
                FoldingFeature.Orientation.VERTICAL -> {
                    /** Device is half open and kept vertical, so it is in book mode **/
                    cameraViewfinder.moveToRightOf(foldPosition)
                }
                FoldingFeature.Orientation.HORIZONTAL -> {
                    /** Device is half open and kept horizontal, so it is in tabletop mode **/
                    Log.d(TAG, "updateLayoutByFoldingState: 폴드")
                    cameraViewfinder.moveToTopOf(foldPosition, alertIcon, alertText, cameraMenuLayout, alertConstraintLayout, rootConstraintLayout)
                }
            }
        } else {
            Log.d(TAG, "updateLayoutByFoldingState: restore")
                cameraViewfinder.restore(foldPosition, alertIcon, alertText, cameraMenuLayout, alertConstraintLayout, rootConstraintLayout)
        }
    }
}