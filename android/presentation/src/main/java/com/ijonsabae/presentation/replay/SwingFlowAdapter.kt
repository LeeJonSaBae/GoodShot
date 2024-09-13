package com.ijonsabae.presentation.replay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.databinding.ItemSwingFlowBinding

class SwingFlowAdapter :
    ListAdapter<SwingFlowDTO, SwingFlowAdapter.SwingFlowViewHolder>(Comparator) {
    companion object Comparator : DiffUtil.ItemCallback<SwingFlowDTO>() {
        override fun areItemsTheSame(oldItem: SwingFlowDTO, newItem: SwingFlowDTO): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(oldItem: SwingFlowDTO, newItem: SwingFlowDTO): Boolean {
            return oldItem == newItem
        }
    }

    inner class SwingFlowViewHolder(private val binding: ItemSwingFlowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)

            binding.tvSwingFlowItem.text = item.title
            Glide.with(binding.root).load(item.swingImg).into(binding.ivSwingFlowItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwingFlowViewHolder {
        val binding =
            ItemSwingFlowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SwingFlowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SwingFlowViewHolder, position: Int) {
        holder.bind(position)
    }
}