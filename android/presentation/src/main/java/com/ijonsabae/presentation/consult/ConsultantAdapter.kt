package com.ijonsabae.presentation.consult

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
import com.ijonsabae.presentation.databinding.ItemConsultantBinding

class ConsultantAdapter() :
    ListAdapter<Consultant, ConsultantAdapter.ReplayViewHolder>(
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

    inner class ReplayViewHolder(private val binding: ItemConsultantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var check = false

        fun bindInfo(position: Int) {
            val consultant = getItem(position)

            if (consultant.profileImage == null) {
                // 없을 경우 기본 이미지. 글라이드 : 링크이미지 받아올 때
                Glide.with(binding.root)
                    .load("https://images-ext-1.discordapp.net/external/9pyEBG4x_J2aG-j5BeoaA8edEpEpfQEOEO9SdmT9hIg/https/k.kakaocdn.net/dn/cwObI9/btsGqPcg5ic/UHYbwvy2M2154EdZSpK8B1/img_110x110.jpg%2C?format=webp")
                    .into(binding.ivThumbnail)
            } else {
                Glide.with(binding.root)
                    .load(consultant.profileImage)
                    .into(binding.ivThumbnail)
            }

            binding.tvName.text = consultant.name
            binding.tvCareerTitle.text = "경력 ${consultant.career}년"
            binding.tvCourse.text = "${consultant.course} 과정"

            binding.root.setOnClickListener { itemClickListener.onItemClick(consultant) }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReplayViewHolder {
        return ReplayViewHolder(
            ItemConsultantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReplayViewHolder, position: Int) {
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