package com.kevinwei.vote.activities.election

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kevinwei.vote.R
import com.kevinwei.vote.activities.ballot.BallotFragmentDirections
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.adapter.ElectionVoteListener
import com.kevinwei.vote.databinding.FragmentElectionBinding
import com.kevinwei.vote.model.Election
import com.kevinwei.vote.network.ApiResult
import com.kevinwei.vote.network.FailedResult
import com.kevinwei.vote.network.SuccessResult

class ElectionFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private val electionViewModel by viewModels<ElectionViewModel>()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var navController: NavController

    // SharedPreference setting for biometric authentication
    private var biometricEnabled: Boolean = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        biometricEnabled = sharedPrefs.getBoolean(getString(R.string.pref_biometric), false)

        navController = findNavController()

        checkAuthenticationState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentElectionBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /*
    * observerAuthenticationState determines if user has successfully authenticated (ie. logged in)
    * if the user has not logged in navigate to the loginFragment
    */
    // TODO - Look into centralizing authentication state in MainActivity instead of ElectionFragment
    private fun checkAuthenticationState() {
        // NOTE: Observer is inefficient here as authentication state does not change at the moment
//        loginViewModel.authState.observe(viewLifecycleOwner, Observer { authState ->
//            when (authState) {
//                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
//                    checkBiometricEnabled()
//                }
//                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
//                    navController.navigate(R.id.loginFragment)
//                }
//            }
//        })
        val authState = loginViewModel.authState.value
        if (authState == LoginViewModel.AuthenticationState.UNAUTHENTICATED) {
            navController.navigate(R.id.loginFragment)
        }  else {
            checkBiometricEnabled()
            // setupElectionList() and setupBallotNavigation() called in checkBiometricEnabled
            loadElection()
        }

    }


    /*
    * checkBiometricEnabled determines if user has enabled biometrics
    * true -> proceed to load election data from API
    */
    private fun checkBiometricEnabled() {
        when (biometricEnabled) {
            true -> {
                setupElectionList()
                setupBallotNavigation()
            }
            false -> {
                navController.navigate(R.id.settingsFragment)
            }
        }
    }

    private fun loadElection() {
        if (biometricEnabled) electionViewModel.getElections()
    }

    /*
    * setupElectionList links the viewModel to the recyclerView(Adapter)
    */
    private fun setupElectionList() {
        val electionAdapter = ElectionAdapter(ElectionVoteListener { election ->
            electionViewModel.onElectionClicked(election)
        })
        binding.electionList.adapter = electionAdapter

        // Observe the LiveData from viewModel to refresh the recyclerView data
        electionViewModel.electionData.observe(viewLifecycleOwner, Observer { it ->
            electionAdapter.submitList(it)
        })

        // Observe the API results from retrieving election data
        electionViewModel.apiResult.observe(
            viewLifecycleOwner,
            Observer { apiResult: ApiResult ->
                val result = apiResult ?: return@Observer
                when (result) {
                    is SuccessResult -> {
                        // TODO - Disable progress bar
                    }
                    is FailedResult -> {
                        result.unauthenticated?.let {

                            // Display Alert Dialog when server responds with a 401
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
    * setupBallotNavigation uses navArgs to pass the election object from the adapter to the ballotFragment
    */
    private fun setupBallotNavigation() {
        electionViewModel.navigateToBallot.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                this.findNavController().navigate(
                    ElectionFragmentDirections.actionElectionFragmentToBallotFragment(election)
                )
                // Reset state to make sure we only navigate once, even if the device has a configuration change.
                electionViewModel.onBallotFragmentNavigated()
            }
        })
    }
}