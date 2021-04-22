package com.kevinwei.vote.activities.ballot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.kevinwei.vote.R
import com.kevinwei.vote.databinding.FragmentBallotBinding
import com.kevinwei.vote.databinding.FragmentReceiptBinding
import java.text.SimpleDateFormat
import java.util.*

class ReceiptFragment : Fragment() {
    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    val args: ReceiptFragmentArgs by navArgs()

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

        setupReceiptText()
        setupReturnButton()
    }

    private fun setupReceiptText() {

        val election = args.election
        val electionName = election!!.electionName;
        val districtName = args.districtName;
        val timestamp = args.ballotTimestamp

        var dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
        var timeString = dateFormat.format(timestamp * 1000)

        binding.election.text = "Election: ${electionName}"
        binding.district.text = "District: ${districtName}"
        binding.timestamp.text = "Timestamp: ${timeString}"
    }

    private fun setupReturnButton() {
        binding.btnReturn.setOnClickListener {
            navController.navigate(R.id.electionFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}