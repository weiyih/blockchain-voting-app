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
    private val viewModel: BallotViewModel,
    private val clickListener: BallotVoteListener
) :
    ListAdapter<Candidate, BallotAdapter.ViewHolder>(BallotDiffCallback()) {

    // TODO ("District and election data")
    inner class ViewHolder(private val binding: CardBallotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var candidate: Candidate? = null

        init {
            // Enable only 1 selection
            binding.checkedCandidate.setOnClickListener{
                val candidate = binding.candidate!!
                viewModel.onCandidateSelected(adapterPosition, candidate)
                notifyDataSetChanged()
            }
        }


        fun bind(candidate: Candidate, clickListener: BallotVoteListener) {
//            this.candidate = candidate // ? Not sure if i can just use databinding here
            binding.viewModel = viewModel
            binding.candidate = candidate
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }


    // Creates and inflates view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BallotAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding = DataBindingUtil.inflate(layoutInflater, R.layout.card_ballot ,parent, false )
        val binding = CardBallotBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
//        return ViewHolder.from(parent)
    }

    // Binds ViewHolder with data
    override fun onBindViewHolder(viewHolder: BallotAdapter.ViewHolder, position: Int) {
        val candidate = getItem(position)
        viewHolder.bind(candidate, clickListener)
    }
}

class BallotVoteListener(val clickListener: (candidate: Candidate) -> Unit) {
    fun onClick(candidate: Candidate) = clickListener(candidate)
}


class BallotDiffCallback : DiffUtil.ItemCallback<Candidate>() {
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.selected == newItem.selected
    }

    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem == newItem
    }
}




