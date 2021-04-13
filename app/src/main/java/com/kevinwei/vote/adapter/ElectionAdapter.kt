package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.databinding.CardElectionBinding
import com.kevinwei.vote.model.Election

class ElectionAdapter(private val clickListener: ElectionVoteListener) :
    ListAdapter<Election, ElectionAdapter.ViewHolder>(ElectionDiffCallback()) {

    // Bind UI

    class ViewHolder private constructor(val binding: CardElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // TODO - Handle display logic and function of advanced polling
        fun bind(item: Election, clickListener: ElectionVoteListener) {
            binding.election = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CardElectionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    // Creates and inflates view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // Binds ViewHolder with data
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val election = getItem(position)
        viewHolder.bind(election, clickListener)
    }
}

class ElectionDiffCallback: DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.electionId == newItem.electionId
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

class ElectionVoteListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}