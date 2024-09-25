package com.ijonsabae.presentation.replay

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemReplayBinding

private const val TAG = "SearchResultOfMountainListAdapter_싸피"

class ReplayAdapter() :
    ListAdapter<ReplayDTO, ReplayAdapter.ReplayViewHolder>(
        Comparator
    ) {
    
    companion object Comparator : DiffUtil.ItemCallback<ReplayDTO>() {
        override fun areItemsTheSame(
            oldItem: ReplayDTO,
            newItem: ReplayDTO
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: ReplayDTO,
            newItem: ReplayDTO
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var lastPosition = -1
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: ReplayDTO)
        fun onLikeClick(item: ReplayDTO, check: Boolean)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    inner class ReplayViewHolder(private val binding: ItemReplayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var check = false

        fun bindInfo(position: Int) {
            val replayItem = getItem(position)

            if (replayItem.thumbnail == null) {
                // 없을 경우 기본 이미지. 글라이드 : 링크이미지 받아올 때
                Glide.with(binding.root)
                    .load("https://images-ext-1.discordapp.net/external/9pyEBG4x_J2aG-j5BeoaA8edEpEpfQEOEO9SdmT9hIg/https/k.kakaocdn.net/dn/cwObI9/btsGqPcg5ic/UHYbwvy2M2154EdZSpK8B1/img_110x110.jpg%2C?format=webp")
                    .into(binding.ivThumbnail)
            } else {
                Glide.with(binding.root)
                    .load(replayItem.thumbnail)
                    .into(binding.ivThumbnail)
            }

            binding.tvTitle.text = replayItem.title
            binding.tvDate.text = replayItem.date
            binding.tvTag1.text = replayItem.swingPose
            binding.tvTag2.text = replayItem.golfClub

            binding.root.setOnClickListener { itemClickListener.onItemClick(replayItem) }
            binding.ivLike.setOnClickListener {
                check = !check // Toggle the favorite state
                binding.ivLike.imageTintList =
                    if (check) ColorStateList.valueOf(
                        ContextCompat.getColor(binding.root.context, R.color.like_yellow)
                    )
                    else ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.gray))
                itemClickListener.onLikeClick(replayItem, replayItem.like)
            }

            if (replayItem.isClamped) binding.cvReplayItem.translationX =
                binding.root.width * -1f / 10 * 3
            else binding.cvReplayItem.translationX = 0f

            binding.btnDelete.setOnClickListener {
                if (getClamped())
                    showCustomDialog(binding.root.context, replayItem, adapterPosition)
            }
        }

        fun setClamped(isClamped: Boolean) {
            getItem(adapterPosition).isClamped = isClamped
        }

        fun getClamped(): Boolean {
            return getItem(adapterPosition).isClamped
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReplayViewHolder {
        return ReplayViewHolder(
            ItemReplayBinding.inflate(
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

    private fun showCustomDialog(context: Context, replayItem: ReplayDTO, adapterPosition: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null)
        val dialogBuilder = AlertDialog.Builder(context, R.style.RoundedDialog)
            .setView(dialogView)
            .create()

        val btnClose = dialogView.findViewById<ImageView>(R.id.btn_close)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)
        val btnNo = dialogView.findViewById<Button>(R.id.btn_no)

        btnClose.setOnClickListener { dialogBuilder.dismiss() }
        btnNo.setOnClickListener { dialogBuilder.dismiss() }
        btnYes.setOnClickListener { // 삭제
            if (replayItem.isClamped) {
                removeItem(adapterPosition)
                Toast.makeText(context, "삭제 완료!", Toast.LENGTH_SHORT).show()
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()

        setDialogSize(dialogBuilder, 0.9)

    }

    private fun setDialogSize(dialogBuilder: AlertDialog, widthRatio: Double) {
        val window = dialogBuilder.window
        if (window != null) {
            val displayMetrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            window.setLayout((width * widthRatio).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }

    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)

        // 여러 아이템이 한 번에 삭제 버튼이 보이는 경우 없도록
        newList.forEach { it.isClamped = false }
        submitList(newList.toList())
    }
}