package com.ijonsabae.presentation.shot.flex

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.ijonsabae.presentation.shot.CameraState
import com.ijonsabae.presentation.shot.flex.FoldableSwingExampleUtils.moveToTopOf
import com.ijonsabae.presentation.shot.flex.FoldableSwingExampleUtils.restore
import com.ijonsabae.presentation.shot.flex.FoldableUtils.moveToRightOf
import com.ijonsabae.presentation.shot.flex.FoldableUtils.moveToTopOf
import com.ijonsabae.presentation.shot.flex.FoldableUtils.restore
import javax.inject.Inject

private const val TAG = "FoldingStateActor 싸피"

class FoldingStateActor @Inject constructor(private val windowInfoTracker: WindowInfoTracker) {

    private var activeWindowLayoutInfo: WindowLayoutInfo? = null
    suspend fun checkFoldingStateForSwingExample(
        activity: AppCompatActivity,
        exampleLayout: ConstraintLayout,
        swingExample: ImageView,
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
        windowInfoTracker.windowLayoutInfo(activity)
            .collect { newLayoutInfo ->
                activeWindowLayoutInfo = newLayoutInfo
                updateLayoutByFoldingStateForSwingExample(
                    swingExample,
                    exampleLayout,
                    menuLayout,
                    replayCardView,
                    replayLayout,
                    swingCardView,
                    swingLayout,
                    replayTitle,
                    swingTitle,
                    replayDescription,
                    swingDescription,
                    replayLogo,
                    swingLogo
                )
            }
    }

    suspend fun checkFoldingStateForCamera(
        activity: AppCompatActivity,
        cameraState: CameraState?,
        progressTitle: TextView,
        resultHeader: TextView,
        resultSubHeader: TextView,
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
                updateLayoutByFoldingStateForCamera(
                    cameraState,
                    progressTitle,
                    resultHeader,
                    resultSubHeader,
                    cameraViewfinder,
                    alertIcon,
                    alertText,
                    cameraMenuLayout,
                    alertConstraintLayout,
                    rootConstraintLayout
                )
            }
    }

    private fun updateLayoutByFoldingStateForSwingExample(
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
        val foldingFeature = activeWindowLayoutInfo?.displayFeatures
            ?.firstOrNull { it is FoldingFeature } as FoldingFeature?
            ?: return

        val foldPosition = FoldableSwingExampleUtils.getFeaturePositionInViewRect(
            foldingFeature,
            exampleLayout as View
        ) ?: return

        if (foldingFeature.state == FoldingFeature.State.HALF_OPENED) {
            when (foldingFeature.orientation) {
                FoldingFeature.Orientation.VERTICAL -> {
                    exampleLayout.moveToRightOf(foldPosition)
                }

                FoldingFeature.Orientation.HORIZONTAL -> {
                    swingExample.moveToTopOf(
                        foldPosition,
                        swingExample,
                        exampleLayout,
                        menuLayout,
                        replayCardView,
                        replayLayout,
                        swingCardView,
                        swingLayout,
                        replayTitle,
                        swingTitle,
                        replayDescription,
                        swingDescription,
                        replayLogo,
                        swingLogo
                    )
                }
            }
        } else {
            swingExample.restore(
                foldPosition,
                swingExample,
                exampleLayout,
                menuLayout,
                replayCardView,
                replayLayout,
                swingCardView,
                swingLayout,
                replayTitle,
                swingTitle,
                replayDescription,
                swingDescription,
                replayLogo,
                swingLogo
            )
        }
    }


    private fun updateLayoutByFoldingStateForCamera(
        cameraState: CameraState?,
        progressTitle: TextView,
        resultHeader: TextView,
        resultSubHeader: TextView,
        cameraViewfinder: View,
        alertIcon: ImageView,
        alertText: TextView,
        cameraMenuLayout: ConstraintLayout,
        alertConstraintLayout: ConstraintLayout,
        rootConstraintLayout: ConstraintLayout
    ) {
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
                    cameraViewfinder.moveToTopOf(
                        foldPosition,
                        cameraState,
                        progressTitle,
                        resultHeader,
                        resultSubHeader,
                        alertIcon,
                        alertText,
                        cameraMenuLayout,
                        alertConstraintLayout,
                        rootConstraintLayout
                    )
                }
            }
        } else {
            Log.d(TAG, "updateLayoutByFoldingState: restore")
            cameraViewfinder.restore(
                foldPosition,
                cameraState,
                progressTitle,
                resultHeader,
                resultSubHeader,
                alertIcon,
                alertText,
                cameraMenuLayout,
                alertConstraintLayout,
                rootConstraintLayout
            )
        }
    }
}