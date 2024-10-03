package com.ijonsabae.presentation.replay

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemSwingFlowAnalysisBinding

private const val TAG = "굿샷_SwingFlowAnalysisAdapter"

class BackSwingFlowAnalysisAdapter(private val context: Context) :
    ListAdapter<SwingFlowAnalysisDTO, BackSwingFlowAnalysisAdapter.SwingFlowAnalysisViewHolder>(
        Comparator
    ) {
    companion object Comparator : DiffUtil.ItemCallback<SwingFlowAnalysisDTO>() {
        override fun areItemsTheSame(
            oldItem: SwingFlowAnalysisDTO,
            newItem: SwingFlowAnalysisDTO
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: SwingFlowAnalysisDTO,
            newItem: SwingFlowAnalysisDTO
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
                    if (item.isSuccess) context.getDrawable(R.drawable.ic_swing_report_checked)
                    else context.getDrawable(R.drawable.ic_swing_report_unchecked)
                )
                .into(binding.ivSwingFlowAnalysis)
            binding.tvSwingFlowAnalysisDescription.text = item.description
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

    fun updateData(newItems: List<SwingFlowAnalysisDTO>) {
        submitList(newItems)
        Log.d(TAG, "updateData: notifyDataSetChanged!")
    }
}