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
import org.w3c.dom.Text

class ElectionAdapter : RecyclerView.Adapter<ElectionAdapter.ViewHolder>() {
    //class ElectionAdapter(private val dataSet: Array<String>) : RecyclerView.Adapter<ElectionAdapter.ViewHolder>() {
    private lateinit var binding: CardElectionBinding
    var data = listOf<Election>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TODO - convert to cardView?
        val cardView: CardView

        init {
            cardView = view.findViewById(R.id.card_election)
        }
    }

    // Creates and inflates view
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

//        binding = CardElectionBinding.inflate().inflate(layoutInflater)
//        view = CardElectionBinding.inflate(layoutInflater, viewGroup, false)

        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_election, viewGroup, false)
        return ViewHolder(view)
    }

    // Binds ViewHolder with data
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = data[position]

        binding.electionTitle.text = item.title.toString()
        binding.electionDescription.text = item.description.toString()

//        binding.voteButton
    }

    override fun getItemCount(): Int {
        return data.size
    }
}