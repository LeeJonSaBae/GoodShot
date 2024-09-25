package com.ijonsabae.presentation.shot


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.ijonsabae.presentation.databinding.ItemFeedBackCheckListBinding

class CheckListAdapter : ListAdapter<String, CheckListAdapter.CheckListViewHolder>(
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

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        if(!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return CheckListViewHolder(
            ItemFeedBackCheckListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), viewType
        )
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class CheckListViewHolder(
        private val binding: ItemFeedBackCheckListBinding,
        viewType: Int
    ) : RecyclerView.ViewHolder(binding.root) {
        val timeline = binding.timeline

        fun onBind(position: Int){
            val item = getItem(position)
            binding.tvFeedback.text = item
        }
    }

}