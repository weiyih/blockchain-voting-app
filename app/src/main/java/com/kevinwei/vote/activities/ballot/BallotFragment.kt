package com.kevinwei.vote.activities.ballot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.kevinwei.vote.R
import com.kevinwei.vote.adapter.BallotAdapter
import com.kevinwei.vote.adapter.BallotVoteListener
import com.kevinwei.vote.adapter.ElectionVoteListener
import com.kevinwei.vote.databinding.FragmentBallotBinding
import com.kevinwei.vote.model.Candidate
import com.kevinwei.vote.security.BiometricPromptUtils

class BallotFragment : Fragment() {
    private var _binding: FragmentBallotBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val ballotViewModel by viewModels<BallotViewModel>()

    val args: BallotFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ballot, container, false)
        _binding = FragmentBallotBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBallotList()
        setupSubmitBallot()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupBallotList() {
        val election = args.election
        binding.electionTitle.text = election?.electionName

        val ballotAdapter = BallotAdapter(
            ballotViewModel,
            BallotVoteListener { candidate ->
//            ballotViewModel.onCandidateClick(candidate)
                Toast.makeText(requireContext(), "${candidate.candidateName} ${candidate.selected.toString()}", Toast.LENGTH_SHORT).show()
            })

        // Retrieve and load the ballot data
        ballotViewModel.getBallot(election!!.electionId);
        ballotViewModel.candidateData.observe(viewLifecycleOwner, Observer { candidateList ->
//            Toast.makeText(requireContext(), "List Updated", Toast.LENGTH_SHORT).show()
            ballotAdapter.submitList(candidateList)
        })

        // Disable or enable the submission button
        ballotViewModel.selectedCandidate.observe(viewLifecycleOwner, Observer { selected ->
            when (selected) {
                null -> binding.submitVote.isEnabled = false
                else -> {
                    binding.submitVote.isClickable = true
                    binding.submitVote.isEnabled = true
                }
            }
        })
        binding.candidateList.adapter = ballotAdapter
    }

    private fun setupSubmitBallot() {
//        TODO("Observe if candidate has been selected")
//        ballotViewModel.selected

        binding.submitVote.setOnClickListener { _ ->
            displayBiometricPrompt()
            BiometricPromptUtils.voteBiometricPrompt(this.activity as AppCompatActivity)
        }

    }

    private fun displayBiometricPrompt() {
        Toast.makeText(requireContext(), "BIO PROMPT", Toast.LENGTH_SHORT).show()
    }
}