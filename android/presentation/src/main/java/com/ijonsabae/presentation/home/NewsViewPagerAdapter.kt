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
import com.ijonsabae.presentation.databinding.ItemHomeNewsBinding

class NewsViewPagerAdapter(val context: Context) :
    ListAdapter<NewsDTO, NewsViewPagerAdapter.NewsViewHolder>(Comparator) {

    companion object Comparator : DiffUtil.ItemCallback<NewsDTO>() {
        override fun areItemsTheSame(oldItem: NewsDTO, newItem: NewsDTO): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(oldItem: NewsDTO, newItem: NewsDTO): Boolean {
            return oldItem == newItem
        }
    }

    inner class NewsViewHolder(private val binding: ItemHomeNewsBinding) :
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
        val binding =
            ItemHomeNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}