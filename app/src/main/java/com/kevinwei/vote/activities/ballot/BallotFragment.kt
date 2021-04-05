package com.kevinwei.vote.activities.ballot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.kevinwei.vote.adapter.BallotAdapter
import com.kevinwei.vote.databinding.FragmentBallotBinding

class BallotFragment : Fragment() {
    private var _binding: FragmentBallotBinding? = null
    private val binding get() = _binding!!

    val args: BallotFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBallotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBallotList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupBallotList() {
        val election = args.election
        binding.electionTitle.text = election?.electionName

//        binding.electionDistrict.text =



        val ballotAdapter = BallotAdapter()
        binding.candidateList.adapter = ballotAdapter
    }
}