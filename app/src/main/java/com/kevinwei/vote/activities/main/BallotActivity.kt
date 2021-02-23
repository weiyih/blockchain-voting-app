package com.kevinwei.vote.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kevinwei.vote.databinding.ActivityBallotBinding

class BallotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBallotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBallotBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}