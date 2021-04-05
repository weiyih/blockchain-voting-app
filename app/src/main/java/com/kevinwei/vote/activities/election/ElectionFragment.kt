package com.kevinwei.vote.activities.election

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.kevinwei.vote.R
import com.kevinwei.vote.activities.login.LoginViewModel
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.adapter.ElectionVoteListener
import com.kevinwei.vote.databinding.FragmentElectionBinding
import com.kevinwei.vote.model.Election

class ElectionFragment : Fragment() {
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
        savedInstanceState: Bundle?
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

            Toast.makeText(this.requireContext(), authState.toString(), Toast.LENGTH_SHORT).show()

            when (authState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    setupElectionList()
                    setupBallotNavigation()
                }
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    navController.navigate(R.id.loginFragment)
                }
            }
        })
    }

    private fun setupElectionList() {
        val electionAdapter = ElectionAdapter(ElectionVoteListener { election ->
            electionViewModel.onElectionClicked(election)
        })
        binding.electionList.adapter = electionAdapter

        electionAdapter.submitList(testList)
        // TODO("Setup observer on retrieving list of elections
//        val electionListObserver = Observer<List<Election>> {
//            electionAdapter.data = it
//        }
//        electionViewModel.electionData.observe(this, electionListObserver)
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

    val testList = listOf(
        Election(
            "1111",
            "Election A",
            "Description of Election A",
            "startDate",
            "endDate",
            "advStartDate",
            "advEndDate",
            true,
            "created",
            "updated"
        ),
        Election(
            "2222",
            "Election B",
            "Description of Election B",
            "startDate",
            "endDate",
            "advStartDate",
            "advEndDate",
            false,
            "created",
            "updated"
        )
    )
}