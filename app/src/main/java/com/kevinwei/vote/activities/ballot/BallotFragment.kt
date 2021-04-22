package com.kevinwei.vote.activities.ballot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kevinwei.vote.adapter.BallotAdapter
import com.kevinwei.vote.databinding.FragmentBallotBinding
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.network.ApiResult
import com.kevinwei.vote.network.FailedResult
import com.kevinwei.vote.network.SuccessResult
import com.kevinwei.vote.security.BiometricPromptUtils

class BallotFragment : Fragment() {
    private var _binding: FragmentBallotBinding? = null
    private val binding get() = _binding!!
    private val ballotViewModel by viewModels<BallotViewModel>()
    private lateinit var navController: NavController
    private val args: BallotFragmentArgs by navArgs()
    private lateinit var election: Election


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentBallotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        election = args.election

        setupBallotList()
        setupSubmitBallot()
        observeBallotResponse()
        observeSubmitResponse()
        loadBallot()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Return back to the election page onResume
    override fun onResume() {
        super.onResume()
        navController.popBackStack();
    }

    private fun loadBallot() {
        ballotViewModel.getBallot(election.electionId);
    }


    private fun setupBallotList() {
        binding.electionTitle.text = election.electionName

        val ballotAdapter = BallotAdapter(ballotViewModel)
        binding.candidateList.adapter = ballotAdapter

        // Observer to update the recycler view on data load
        // NOTE: Observer is uncessary as data should not change. Refactor into load ballot
        ballotViewModel.candidateData.observe(viewLifecycleOwner, Observer { candidateList ->
            ballotAdapter.submitList(candidateList)
        })

        // Observer on selected canidate to disable or enable the submission button
        ballotViewModel.selectedCandidate.observe(viewLifecycleOwner, Observer { selected ->
            when (selected) {
                null -> binding.submitVote.isEnabled = false
                else -> {
                    binding.submitVote.isClickable = true
                    binding.submitVote.isEnabled = true
                }
            }
        })
    }

    /*
    * observeBallotResponse  observe retrieving the ballot information result from the API
    */
    private fun observeBallotResponse() {
        ballotViewModel.apiLoadResult.observe(
            viewLifecycleOwner,
            Observer { apiResult: ApiResult ->
                val result = apiResult ?: return@Observer
                when (result) {
                    is SuccessResult -> {
                        // Do Nothing. Data is handled by the observer on the LiveData
                    }
                    is FailedResult -> {
                        result.unauthenticated?.let {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Session Timed Out")
                                .setMessage("Your voting session has timed out. Application will now close. You can login again to vote")
                                .setPositiveButton("EXIT") { dialog, which ->
                                    requireActivity().finishAffinity()
                                }
                                .show()
                        }
                        // TODO - Disable progress bar
                        result.error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }


    /*
    * setupSubmitBallot registers a clickListner on the submit vote button
     */
    private fun setupSubmitBallot() {
        binding.submitVote.setOnClickListener { _ ->
            showConfirmBiometricPrompt()
            BiometricPromptUtils.voteBiometricPrompt(this.activity as AppCompatActivity)
        }
    }

    /*
    * Displays a biometricPrompt for confirming ballot submission
    */
    private fun showConfirmBiometricPrompt() {
        // BiometricPrompt callback
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                submitBallot(result)
                // TODO - Enable loading screen
            }
        }

        val biometricPrompt =
            BiometricPromptUtils.createBiometricPrompt(requireActivity(), callback)

        val promptInfo = BiometricPromptUtils.voteBiometricPrompt(requireActivity())
        biometricPrompt.authenticate(promptInfo)
    }

    private fun submitBallot(authResult: BiometricPrompt.AuthenticationResult) {
        ballotViewModel.submitBallot()
    }

    /*
    * observeSubmitResponse registers observer on the API responses to submitting a ballot
    */
    private fun observeSubmitResponse() {
        // Observe the ballot submit result from the API
        ballotViewModel.apiSubmitResult.observe(
            viewLifecycleOwner,
            Observer { apiResult: ApiResult ->
                val result = apiResult ?: return@Observer
                when (result) {
                    is SuccessResult -> {
                        // TODO - Disable progress bar
                        val ballotTimestamp = ballotViewModel.timestamp;
                        val districtName = ballotViewModel.districtName;

                        this.navController.navigate(
                            BallotFragmentDirections.actionBallotFragmentToReceiptFragment(
                                election,
                                districtName,
                                ballotTimestamp)
                        )
                    }
                    is FailedResult -> {
                        result.unauthenticated?.let {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Session Timed Out")
                                .setMessage("Your voting session has timed out. Application will now close. You can login again to vote")
                                .setPositiveButton("EXIT") { dialog, which ->
                                    requireActivity().finishAffinity()
                                }
                                .show()
                        }
                        // TODO - Disable progress bar
                        result.error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }
}