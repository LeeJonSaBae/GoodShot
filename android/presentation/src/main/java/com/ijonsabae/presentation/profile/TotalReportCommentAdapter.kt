package com.ijonsabae.presentation.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ijonsabae.presentation.databinding.ItemTotalReportCommentsBinding

private const val TAG = "굿샷_TotalReportAdapter"

class TotalReportCommentAdapter :
    ListAdapter<String, TotalReportCommentAdapter.TotalReportViewHolder>(
        Comparator
    ) {
    companion object Comparator : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class TotalReportViewHolder(private val binding: ItemTotalReportCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)
            Log.d(TAG, "bind: item = $item")
            binding.tvContent.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalReportViewHolder {
        val binding =
            ItemTotalReportCommentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TotalReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalReportViewHolder, position: Int) {
        holder.bind(position)
    }

}