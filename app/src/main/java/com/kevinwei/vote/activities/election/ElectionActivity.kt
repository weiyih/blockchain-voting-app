package com.kevinwei.vote.activities.election

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.databinding.ActivityElectionBinding
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.network.ElectionsApiService

class ElectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityElectionBinding

    private val electionViewModel by viewModels<ElectionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView
        val electionAdapter = ElectionAdapter()
        binding.electionList.adapter = electionAdapter

        val electionListObserver = Observer<List<Election>> {
            electionAdapter.data = it
        }
        electionViewModel.electionData.observe(this, electionListObserver)


    }

//    private fun setupElectionCards() {
//        try{
//            electionViewModel.getElections()
//        } catch {
//
//        }
//    }
}