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
import com.kevinwei.vote.R
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.adapter.ElectionVoteListener
import com.kevinwei.vote.databinding.FragmentElectionBinding
import com.kevinwei.vote.model.Election

class ElectionFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val electionViewModel by viewModels<ElectionViewModel>()
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        observeAuthenticationState()
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

    // TODO
    override fun onResume() {
        super.onResume()
//        refreshData() // Checks if user has voted
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    /*
    Determines if user has authenticated/logged in
    */
    private fun observeAuthenticationState() {
        loginViewModel.authState.observe(viewLifecycleOwner, Observer { authState ->
            when (authState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    checkBiometricEnabled()
                }
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    navController.navigate(R.id.loginFragment)
                }
            }
        })
    }

    /*
    Determines if user has enabled biometrics
    */
    private fun checkBiometricEnabled() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)

        val biometricKey = getString(R.string.pref_biometric)
        val biometricEnabled = sharedPreferences.getBoolean(biometricKey, false)

        when (biometricEnabled) {
            true -> {
                setupElectionList()
                setupBallotNavigation()
            }
            false -> {
                // TODO - Display addition fragment that navigates to settings fragment
                navController.navigate(R.id.settingsFragment)
            }
        }



    }

    private fun setupElectionList() {
        val electionAdapter = ElectionAdapter(ElectionVoteListener { election ->
            electionViewModel.onElectionClicked(election)
        })
        binding.electionList.adapter = electionAdapter

        //        electionAdapter.submitList(testList)
        electionViewModel.electionData.observe(viewLifecycleOwner, Observer { it ->
            electionAdapter.submitList(it)
        })
    }

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