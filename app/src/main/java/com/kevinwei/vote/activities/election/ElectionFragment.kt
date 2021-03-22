package com.kevinwei.vote.activities.election

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.kevinwei.vote.adapter.ElectionAdapter
import com.kevinwei.vote.adapter.ElectionVoteListener
import com.kevinwei.vote.databinding.FragmentElectionBinding
import com.kevinwei.vote.model.Election

class ElectionFragment : Fragment() {
    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val electionViewModel by viewModels<ElectionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentElectionBinding.inflate(inflater, container, false)
        setupElectionList()
        setupBallotNavigation()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupElectionList() {
        val electionAdapter = ElectionAdapter(ElectionVoteListener { electionId ->
            Toast.makeText(
                context,
                "${electionId}",
                Toast.LENGTH_LONG
            ).show()
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
//        electionViewModel.navigateToBallot.observe(viewLifecycleOwner, Observer { electionData ->
//            electionData?.let {
//                navController.navigate(
//                    ElectionFragmentDirections.actionElectionFragmentToBallotFragment(electionData)
//                )
//            }
//        })
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