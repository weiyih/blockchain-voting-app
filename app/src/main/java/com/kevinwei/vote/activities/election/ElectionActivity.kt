package com.kevinwei.vote.activities.election

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.databinding.ActivityElectionBinding

class ElectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityElectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityElectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instantiate and bind adapter to data
        val electionAdapter = ElectionAdapter()
        binding.electionList.adapter = electionAdapter
    }
}