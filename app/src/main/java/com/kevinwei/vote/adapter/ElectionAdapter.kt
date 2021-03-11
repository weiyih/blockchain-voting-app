package com.kevinwei.vote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kevinwei.vote.R
import com.kevinwei.vote.databinding.ActivityLoginBinding
import com.kevinwei.vote.databinding.CardElectionBinding
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
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TODO - convert to cardView?
        val electionTitle: TextView = view.findViewById(R.id.election_title)
        val electionDescription: TextView = view.findViewById(R.id.election_description)

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
