package com.kevinwei.vote.activities.ballot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kevinwei.vote.databinding.FragmentBallotBinding
import com.kevinwei.vote.databinding.FragmentElectionBinding

class BallotFragment : Fragment() {
//    private lateinit var binding: FragmentBallotBinding
    private var _binding: FragmentBallotBinding? = null
    private val binding = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBallotBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}