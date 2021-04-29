package com.kevinwei.vote.activities.ballot

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.kevinwei.vote.MainActivity
import com.kevinwei.vote.R
import com.kevinwei.vote.databinding.FragmentReceiptBinding
import java.text.SimpleDateFormat
import java.util.*

class ReceiptFragment : Fragment() {
    private val TAG = "ReceiptFragment"
    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val args: ReceiptFragmentArgs by navArgs()

    /*
    * Override the device back button to return to the election main
    */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            navigateToElectionFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setupReceiptText()
        setupReturnButton()
    }

    /*
    * Remove the ballot from the navigation stack
    */
    private fun navigateToElectionFragment() {
        navController.popBackStack(R.id.electionFragment, true)
    }

    private fun setupReceiptText() {

        val election = args.election
        val electionName = election!!.electionName;
        val districtName = args.districtName;
        val timestamp = args.ballotTimestamp

        var dateFormat = SimpleDateFormat("EEE dd MMMM yyyy, hh:mm a z", Locale.CANADA)
        var timeString = dateFormat.format(timestamp)

        binding.election.text = "Election: ${electionName}"
        binding.district.text = "${districtName}"
        binding.timestamp.text = "Submitted: ${timeString}"
    }

    private fun setupReturnButton() {
        binding.btnReturn.setOnClickListener {
            navigateToElectionFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}