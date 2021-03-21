package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.R
import com.kevinwei.vote.model.Election

class ElectionAdapter : RecyclerView.Adapter<ElectionAdapter.ViewHolder>() {
    // TODO("change later-data changes redraws the whole list")
    var data = listOf<Election>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // Access to card elements
    // TODO Convert to use data binding
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO - convert to cardView?
        private val electionTitle: TextView = itemView.findViewById(R.id.election_title)
        private val electionDescription: TextView = itemView.findViewById(R.id.election_description)

        // Binding UI to election data
        fun bind(item: Election) {
            electionTitle.text = item.electionName.toString()
            electionDescription.text = item.electionDescription.toString()
        }
    }

    // Creates and inflates view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_election, parent, false)
        return ViewHolder(view)
    }

    // Binds ViewHolder with data
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val election = data[position]
        viewHolder.bind(election)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}