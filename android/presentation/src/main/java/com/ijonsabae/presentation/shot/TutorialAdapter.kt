package com.ijonsabae.presentation.shot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
                binding.ivTutorial.setImageResource(resourceId)
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