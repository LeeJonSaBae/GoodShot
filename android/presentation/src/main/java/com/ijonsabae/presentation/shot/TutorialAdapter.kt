package com.ijonsabae.presentation.shot

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.databinding.ItemTutorialBinding

class TutorialAdapter(
    private val items: List<Int>
) : RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {

    inner class TutorialViewHolder(
        val binding: ItemTutorialBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = items[position]
            val resourceName = "tutorial_${item}"
            val resourceId = binding.root.context.resources.getIdentifier(
                resourceName,
                "drawable",
                binding.root.context.packageName
            )

            if (resourceId != 0) {
                Glide.with(binding.root)
                    .asGif()
                    .load(resourceId)
                    .into(object : CustomTarget<GifDrawable>() {
                        override fun onResourceReady(
                            resource: GifDrawable,
                            transition: Transition<in GifDrawable>?
                        ) {
                            resource.setLoopCount(1) // 1회 재생 설정
                            binding.ivTutorial.setImageDrawable(resource)
                            resource.start()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            binding.ivTutorial.setImageDrawable(placeholder)
                        }
                    })
            } else {
                binding.ivTutorial.setImageResource(R.drawable.btn_option_swing_pose_default)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val binding =
            ItemTutorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TutorialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}