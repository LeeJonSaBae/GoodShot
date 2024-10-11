package com.ijonsabae.presentation.replay

import android.app.AlertDialog
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemReplayBinding
import com.ijonsabae.presentation.shot.SwingLocalDataProcessor
import com.ijonsabae.presentation.util.formatDateFromLongKorea

private const val TAG = "ReplayAdapter_싸피"

class ReplayAdapter(private val context: Context) :
    PagingDataAdapter<SwingFeedback, ReplayAdapter.ReplayViewHolder>(
        Comparator
    ) {

    companion object Comparator : DiffUtil.ItemCallback<SwingFeedback>() {
        override fun areItemsTheSame(
            oldItem: SwingFeedback,
            newItem: SwingFeedback
        ): Boolean {
            return oldItem.swingCode == newItem.swingCode
        }

        override fun areContentsTheSame(
            oldItem: SwingFeedback,
            newItem: SwingFeedback
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var lastPosition = -1
    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: SwingFeedback)
        fun onLikeClick(item: SwingFeedback)
        fun onItemDelete(item: SwingFeedback)
        fun onTitleChange(item: SwingFeedback, title: String)
        fun changeClampStatus(item: SwingFeedback, clampStatus: Boolean)
        fun onTouchListener(position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    inner class ReplayViewHolder(private val binding: ItemReplayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val clamp = binding.root.width.toFloat() / 10 * 3
        fun bindInfo(position: Int) {
            val replayItem = getItem(position)
            replayItem?.let {
                Glide.with(binding.root)
                    .load(SwingLocalDataProcessor.getSwingThumbnailFile(context, replayItem.swingCode, replayItem.userID))
                    .into(binding.ivThumbnail)
                binding.tvTitle.text = replayItem.title
                binding.tvDate.text = formatDateFromLongKorea(SwingLocalDataProcessor.convertSwingcodeToTimestamp(replayItem.swingCode))
                binding.tvTag1.text = "점수 ${replayItem.score}점"
                binding.tvTag2.text = "템포 ${replayItem.tempo}"
                binding.root.setOnClickListener { itemClickListener.onItemClick(replayItem) }

                binding.ivLike.apply {
                    if (replayItem.likeStatus) {
                        binding.ivLike.setImageResource(R.drawable.ic_like2)
                    } else {
                        binding.ivLike.setImageResource(R.drawable.ic_unlike2)
                    }
                    setOnClickListener {
                        if (!replayItem.likeStatus) {
                            binding.ivLike.setImageResource(R.drawable.ic_like2)
                        } else {
                            binding.ivLike.setImageResource(R.drawable.ic_unlike2)
                        }
                        itemClickListener.onLikeClick(replayItem)
                    }
                }
                binding.ivEditTitle.setOnClickListener {
                    showEditCustomDialog(replayItem)
                }
                if (it.isClamped) {
                    binding.cvReplayItem.translationX =
                        binding.root.width * -1f / 10 * 3
                }
                else {
                    binding.cvReplayItem.translationX = 0f
                }

                binding.btnDelete.setOnClickListener {
                    if (getClamped())
                        showDeleteCustomDialog(replayItem)
                }
            }


        }

        fun setClamped(isClamped: Boolean) {
            val item = getItem(this.bindingAdapterPosition)
            item?.let {
                itemClickListener.changeClampStatus(item, isClamped)
            }
        }

        fun getClamped(): Boolean {
            if(this.bindingAdapterPosition != NO_POSITION){
                return getItem(this.bindingAdapterPosition)!!.isClamped
            }
            return false
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

    private fun showDeleteCustomDialog(replayItem: SwingFeedback) {
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
                itemClickListener.onItemDelete(replayItem)
                Toast.makeText(context, "삭제 완료!", Toast.LENGTH_SHORT).show()
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
        setDialogSize(dialogBuilder, 0.9)
    }

    private fun showEditCustomDialog(swingFeedback: SwingFeedback) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_edit_replay_title, null)
        val dialogBuilder = AlertDialog.Builder(context, R.style.RoundedDialog)
            .setView(dialogView)
            .create()

        val btnClose = dialogView.findViewById<ImageView>(R.id.btn_close)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)
        val etNewTitle = dialogView.findViewById<EditText>(R.id.et_new_title)

        btnClose.setOnClickListener { dialogBuilder.dismiss() }
        btnYes.setOnClickListener { // 수정
            if (etNewTitle.text.isNotBlank()) {
                itemClickListener.onTitleChange(swingFeedback,etNewTitle.text.toString())
                Toast.makeText(context, "수정 완료!", Toast.LENGTH_SHORT).show()
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
}