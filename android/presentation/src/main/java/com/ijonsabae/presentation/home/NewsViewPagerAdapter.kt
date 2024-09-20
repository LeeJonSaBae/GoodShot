package com.ijonsabae.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemNewsBinding

class NewsViewPagerAdapter(val context: Context) :
    ListAdapter<News, NewsViewPagerAdapter.NewsViewHolder>(Comparator) {

    companion object Comparator : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val actualPosition = position % currentList.size // 무한스크롤
            val item = getItem(actualPosition)
            binding.tvNewsTitle.text = item.title
            binding.tvNewsDescription.text = item.description
            Glide.with(binding.root).load(context.resources.getDrawable(R.drawable.golf_dummy_img))
                .into(binding.ivNewsImg)

            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return return if (currentList.isEmpty()) 0 else Int.MAX_VALUE
    }
}