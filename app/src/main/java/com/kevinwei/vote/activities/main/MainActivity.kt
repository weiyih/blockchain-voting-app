package com.kevinwei.vote.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kevinwei.vote.R
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.databinding.ActivityLoginBinding
import com.kevinwei.vote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instantiate and bind adapter to data
        val electionAdapter = ElectionAdapter()
        binding.electionList.adapter = electionAdapter
    }




}