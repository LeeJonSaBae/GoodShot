package com.ijonsabae.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemHomeYoutubeBinding

private const val TAG = "굿샷_YoutubeRecyclerViewAdapter"

class YoutubeRecyclerViewAdapter(
    val context: Context,
    private val listener: OnYoutubeItemClickListener
) :
    ListAdapter<YoutubeDTO, YoutubeRecyclerViewAdapter.YoutubeViewHolder>(Comparator) {

    companion object Comparator : DiffUtil.ItemCallback<YoutubeDTO>() {
        override fun areItemsTheSame(oldItem: YoutubeDTO, newItem: YoutubeDTO): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(oldItem: YoutubeDTO, newItem: YoutubeDTO): Boolean {
            return oldItem == newItem
        }
    }

    interface OnYoutubeItemClickListener {
        fun onYoutubeItemClick(position: Int)
    }

    inner class YoutubeViewHolder(private val binding: ItemHomeYoutubeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)
            binding.tvYoutubeTitle.text = item.title
            Glide.with(binding.root).load(context.resources.getDrawable(R.drawable.dummy_youtube_1))
                .into(binding.ivYoutubeThumbnail)

            // visibility
            binding.ivBgBlackWithOpacity.visibility =
                if (item.isVisible) View.VISIBLE else View.GONE
            binding.tvYoutubeTitle.visibility = if (item.isVisible) View.VISIBLE else View.GONE
            binding.btnYoutubeLink.visibility = if (item.isVisible) View.VISIBLE else View.GONE

            binding.btnYoutubeLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                binding.root.context.startActivity(intent)
                Log.d(TAG, "bind: 유튜브 링크 바로가기 클릭")
            }
            binding.root.setOnClickListener {
                listener.onYoutubeItemClick(position)
                Log.d(TAG, "bind: root view 클릭")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeViewHolder {
        val binding =
            ItemHomeYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YoutubeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YoutubeViewHolder, position: Int) {
        if (position == 0) holder.bind(position)
        else holder.bind(position)
    }

}