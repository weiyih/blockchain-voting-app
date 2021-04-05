package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.databinding.CardBallotBinding
import com.kevinwei.vote.model.Vote
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.model.Election

class BallotAdapter(val clickListener: BallotVoteListener) :
    ListAdapter<Candidate, BallotAdapter.ViewHolder>(BallotDiffCallback()) {

    // TODO ("District and election data")
    class ViewHolder private constructor(val binding: CardBallotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Candidate, clickListener: BallotVoteListener) {
            binding.candidate = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): BallotAdapter.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CardBallotBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    // Creates and inflates view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BallotAdapter.ViewHolder {
        return BallotAdapter.ViewHolder.from(parent)
    }

    // Binds ViewHolder with data
    override fun onBindViewHolder(viewHolder: BallotAdapter.ViewHolder, position: Int) {
        val candidate = getItem(position)
        viewHolder.bind(candidate, clickListener)
    }
}

// Note: This is useless as the list should not change
class BallotDiffCallback : DiffUtil.ItemCallback<Candidate>() {
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.candidateId == newItem.candidateId
    }

    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem == newItem
    }
}

class BallotVoteListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}
