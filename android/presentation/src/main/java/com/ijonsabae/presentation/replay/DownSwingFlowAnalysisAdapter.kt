package com.ijonsabae.presentation.replay

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemSwingFlowAnalysisBinding
import com.ijonsabae.presentation.model.SwingFeedbackCommentParcelable

class DownSwingFlowAnalysisAdapter() :
    ListAdapter<SwingFeedbackCommentParcelable, DownSwingFlowAnalysisAdapter.SwingFlowAnalysisViewHolder>(
        Comparator
    ) {
    companion object Comparator : DiffUtil.ItemCallback<SwingFeedbackCommentParcelable>() {
        override fun areItemsTheSame(
            oldItem: SwingFeedbackCommentParcelable,
            newItem: SwingFeedbackCommentParcelable
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: SwingFeedbackCommentParcelable,
            newItem: SwingFeedbackCommentParcelable
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class SwingFlowAnalysisViewHolder(private val binding: ItemSwingFlowAnalysisBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)

            Glide.with(binding.root)
                .load(
                    if (item.commentType == 1) ContextCompat.getDrawable(binding.root.context, R.drawable.ic_swing_report_checked)
                    else ContextCompat.getDrawable(binding.root.context, R.drawable.ic_swing_report_unchecked)
                )
                .into(binding.ivSwingFlowAnalysis)
            binding.tvSwingFlowAnalysisDescription.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwingFlowAnalysisViewHolder {
        val binding =
            ItemSwingFlowAnalysisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SwingFlowAnalysisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SwingFlowAnalysisViewHolder, position: Int) {
        holder.bind(position)
    }

}