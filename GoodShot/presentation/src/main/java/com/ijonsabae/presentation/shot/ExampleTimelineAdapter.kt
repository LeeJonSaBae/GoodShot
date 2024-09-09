package com.ijonsabae.presentation.shot


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.ijonsabae.presentation.databinding.ItemTimelineBinding

class TimeLineAdapter(private val mFeedList: List<String>) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if(!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return TimeLineViewHolder(ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        val timeLineModel = mFeedList[position]
        holder.set(timeLineModel)
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(private var binding: ItemTimelineBinding, viewType: Int) : RecyclerView.ViewHolder(binding.root) {
        val timeline = binding.timeline
        fun set(string: String){
            binding.textTimelineTitle.text = string
        }
        init {
            timeline.initLine(viewType)
        }
    }

}