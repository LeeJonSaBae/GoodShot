package com.ijonsabae.presentation.consult

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.domain.model.Expert
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemConsultantListBinding
class ConsultantListAdapter :
    PagingDataAdapter<Expert, ConsultantListAdapter.ConsultantListViewHolder>(
        Comparator
    ) {

    companion object Comparator : DiffUtil.ItemCallback<Expert>() {
        override fun areItemsTheSame(
            oldItem: Expert,
            newItem: Expert
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: Expert,
            newItem: Expert
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var lastPosition = -1
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: Expert)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    inner class ConsultantListViewHolder(private val binding: ItemConsultantListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(position: Int) {
            val expert = getItem(position)
            expert?.let {
                val data = it
                Glide.with(binding.root)
                    .load(data.imageUrl).error(R.drawable.swing_example)
                    .into(binding.ivThumbnail)
                binding.tvName.text = data.name
                binding.tvCareerTitle.text = "경력 ${data.expYears}년"
                binding.tvField.text = "${data.field}"
                binding.root.setOnClickListener { itemClickListener.onItemClick(data) }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConsultantListViewHolder {
        return ConsultantListViewHolder(
            ItemConsultantListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConsultantListViewHolder, position: Int) {
        holder.bindInfo(position)
        // 애니메이션 설정
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // 애니메이션 딜레이 설정
        if (position > lastPosition) {
            val animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_from_right)
            animation.startOffset = (position * 100).toLong()
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

}