package com.ijonsabae.presentation.consult

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ijonsabae.presentation.databinding.ItemConsultantDialogBinding

class ConsultantCertificationAdapter :
    ListAdapter<String, ConsultantCertificationAdapter.ConsultantCertificationViewHolder>(
        Comparator
    ) {

    companion object Comparator : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ConsultantCertificationViewHolder(private val binding: ItemConsultantDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(position: Int) {
            val data = getItem(position)
            binding.data.text = "${data}"
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConsultantCertificationViewHolder {
        return ConsultantCertificationViewHolder(
            ItemConsultantDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConsultantCertificationViewHolder, position: Int) {
        holder.bindInfo(position)
    }
}