package com.kevinwei.vote.activities.ballot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kevinwei.vote.adapter.BallotAdapter
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.databinding.FragmentBallotBinding
import com.kevinwei.vote.databinding.FragmentElectionBinding

class BallotFragment : Fragment() {
    private var _binding: FragmentBallotBinding? = null
    private val binding = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBallotBinding.inflate(inflater, container, false)

        setupBallotList()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupBallotList() {
        val ballotAdapter = BallotAdapter()
        binding.electionList.adapter = ballotAdapter

//        ballotAdapter.data = testList

    }
}