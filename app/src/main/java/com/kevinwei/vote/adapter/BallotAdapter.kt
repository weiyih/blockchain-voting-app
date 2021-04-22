package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.activities.ballot.BallotViewModel
import com.kevinwei.vote.databinding.CardBallotBinding
import com.kevinwei.vote.model.Candidate

class BallotAdapter(
    private val viewModel: BallotViewModel
) :
    ListAdapter<Candidate, BallotAdapter.ViewHolder>(BallotDiffCallback()) {
    inner class ViewHolder(private val binding: CardBallotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Enable only 1 selection
            binding.checkedCandidate.setOnClickListener{
                val candidate = binding.candidate!!
                viewModel.onCandidateSelected(absoluteAdapterPosition, candidate)
                notifyDataSetChanged()
            }
        }


        fun bind(candidate: Candidate) {
            binding.viewModel = viewModel
            binding.candidate = candidate
            binding.executePendingBindings()
        }
    }


    // Creates and inflates view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BallotAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardBallotBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    // Binds ViewHolder with data
    override fun onBindViewHolder(viewHolder: BallotAdapter.ViewHolder, position: Int) {
        val candidate = getItem(position)
        viewHolder.bind(candidate)
    }
}

class BallotDiffCallback : DiffUtil.ItemCallback<Candidate>() {
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.selected == newItem.selected
    }

    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem == newItem
    }
}




