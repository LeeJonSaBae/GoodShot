package com.ijonsabae.presentation.replay

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.getDefaultUIUtil
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.RecyclerView
import com.ijonsabae.presentation.R
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class SwipeDeleteHelper @Inject constructor() : ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val view = getView(viewHolder)
        clamp = view.width.toFloat() / 10 * 3
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        getDefaultUIUtil().`clearView`(getView(viewHolder))
        previousPosition = viewHolder.bindingAdapterPosition
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.bindingAdapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val view = getView(viewHolder)
        val isClamped = getClamped(viewHolder as ReplayAdapter.ReplayViewHolder)
        val x = clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)
        currentDx = x
        getDefaultUIUtil().onDraw(
            c,
            recyclerView,
            view,
            x,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        val maxSwipe: Float =
            -view.width.toFloat() / 10 * 3
        val right = 0f
        val x = if (isClamped) {
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }
        return min(
            max(maxSwipe, x),
            right
        )
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        setClamped(viewHolder as ReplayAdapter.ReplayViewHolder, currentDx <= -clamp)
        return 2f
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as ReplayAdapter.ReplayViewHolder).itemView.findViewById(R.id.cv_replay_item)
    }

    private fun setClamped(viewHolder: ReplayAdapter.ReplayViewHolder, isClamped: Boolean) {
        viewHolder.setClamped(isClamped)
    }

    private fun getClamped(viewHolder: ReplayAdapter.ReplayViewHolder): Boolean {
        return viewHolder.getClamped()
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f

            setClamped(viewHolder as ReplayAdapter.ReplayViewHolder, false)
            previousPosition = null
        }
    }


}
