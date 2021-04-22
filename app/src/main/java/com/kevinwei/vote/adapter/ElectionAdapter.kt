package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.R
import com.kevinwei.vote.databinding.CardElectionBinding
import com.kevinwei.vote.model.Election
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ElectionAdapter(private val clickListener: ElectionVoteListener) :
    ListAdapter<Election, ElectionAdapter.ViewHolder>(ElectionDiffCallback()) {

    // Bind UI
    class ViewHolder private constructor(val binding: CardElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {


        // TODO - Handle display logic and function of advanced polling
        fun bind(item: Election, clickListener: ElectionVoteListener) {
            binding.election = item

            // Populate the election dates
            var electionStart: String = ""
            var electionEnd: String = ""
            when (item.advancedPolling) {
                true -> {
                    electionStart = convertTimestamp(item.advancedStartDate.toString())
                    electionEnd = convertTimestamp(item.advancedEndDate.toString())
                }
                false -> {
                    electionStart = convertTimestamp(item.electionStartDate)
                    electionEnd = convertTimestamp(item.electionEndDate)
                }
            }

            val displayDateString = "Starts: $electionStart  Ends: $electionEnd"
            binding.electionDates.text = displayDateString

            binding.clickListener = clickListener

            when (item.voteStatus) {
                in 0L..1L -> {
                    binding.voteButton.setText(R.string.vote_now)

                    binding.voteButton.isEnabled = checkValidTime(item)
                }
                else -> {
                    binding.voteButton.setText(R.string.vote_okay)
                    binding.voteButton.isEnabled = false
                }
            }


            binding.executePendingBindings()
        }

        private fun checkValidTime(election: Election): Boolean {
            val current = Instant.now()
            var instantStart: Instant
            if (election.advancedPolling) {
                instantStart = Instant.parse(election.advancedStartDate)
            } else {
                instantStart = Instant.parse(election.electionStartDate)
            }

            var instantEnd: Instant
            if (election.advancedPolling) {
                instantEnd = Instant.parse(election.advancedEndDate)
            } else {
                instantEnd = Instant.parse(election.electionEndDate)
            }

            if (current.isBefore(instantStart)) return false
            if (current.isAfter(instantEnd)) return false
            return true
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CardElectionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        private fun convertTimestamp(timestamp: String): String {

            // Note: Timezone may cause issues here on display
            val instant: Instant = Instant.parse(timestamp)
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .withLocale(Locale.CANADA)
                .withZone(ZoneId.systemDefault())

            val timeString: String = formatter.format(instant)
            return timeString
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

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
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