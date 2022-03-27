package com.example.boardgamefinder.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.boardgamefinder.databinding.FragmentProfileBinding
import com.example.boardgamefinder.presentation.viewModels.ProfileViewModel

/**
 * Fragment for profile tab
 */
internal class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ToDo remove example
        /*binding.getBreedsButton.setOnClickListener {
            profileViewModel.getBreeds()
        }

        profileViewModel.breedsCount.observe(viewLifecycleOwner) {
            binding.countBreedsTextView.text = it.toString()
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}