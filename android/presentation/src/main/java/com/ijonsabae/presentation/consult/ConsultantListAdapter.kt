package com.ijonsabae.presentation.consult

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.domain.model.Consultant
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemConsultantListBinding

private const val TAG = "ConsultantListAdapter_싸피"
class ConsultantListAdapter :
    ListAdapter<Consultant, ConsultantListAdapter.ConsultantListViewHolder>(
        Comparator
    ) {

    companion object Comparator : DiffUtil.ItemCallback<Consultant>() {
        override fun areItemsTheSame(
            oldItem: Consultant,
            newItem: Consultant
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: Consultant,
            newItem: Consultant
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var lastPosition = -1
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: Consultant)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    inner class ConsultantListViewHolder(private val binding: ItemConsultantListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(position: Int) {
            val consultant = getItem(position)

            Glide.with(binding.root)
                .load(consultant.profileImage).error(R.drawable.swing_example)
                .into(binding.ivThumbnail)
            Log.d(TAG, "bindInfo: ${consultant.profileImage}")
            binding.tvName.text = consultant.name
            binding.tvCareerTitle.text = "경력 ${consultant.career}년"
            binding.tvCourse.text = "${consultant.course}"

            binding.root.setOnClickListener { itemClickListener.onItemClick(consultant) }
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