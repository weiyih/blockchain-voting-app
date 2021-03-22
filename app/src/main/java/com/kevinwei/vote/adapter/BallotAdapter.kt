package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.R
import com.kevinwei.vote.model.BallotJson
import com.kevinwei.vote.model.BallotVote
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.model.Election
import com.squareup.moshi.ToJson

class BallotAdapter: RecyclerView.Adapter<BallotAdapter.ViewHolder>() {
    // TODO ("District and election data")
//    var election = BallotJson()
//        set(value) {
//            field = value
//    }

    var data = listOf<Candidate>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        private val candidateName: TextView = itemView.findViewById(R.id.candidate_name)
        private val candidateDescription: TextView = itemView.findViewById(R.id.candidate_description)

        fun bind(item: Candidate) {
            candidateName.text = item.candidateName.toString()
            candidateDescription.text = item.candidateDescription.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_ballot, parent, false)
        return ViewHolder(view)
    }

    // TODO ("Might have to pass in ballot data with the candidate")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val candidate = data[position]
        viewHolder.bind(candidate)
    }

    override fun getItemCount(): Int {
        return data.size
    }

//    @ToJson fun toJson(): String {
//        val json = BallotVote("election_id")
//        return json
//    }
}