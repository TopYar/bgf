package com.example.boardgamefinder.presentation.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boardgamefinder.R
import com.example.boardgamefinder.databinding.FragmentHomeBinding
import com.example.boardgamefinder.databinding.FragmentMyEventsBinding
import com.example.boardgamefinder.presentation.adapters.EventAdapter
import com.example.boardgamefinder.presentation.adapters.EventShortAdapter
import com.example.boardgamefinder.presentation.viewModels.HomeViewModel
import com.example.boardgamefinder.presentation.viewModels.MyEventsViewModel
import com.example.boardgamefinder.presentation.views.activities.MainActivity

class MyEventsFragment : Fragment() {
    private var _binding: FragmentMyEventsBinding? = null
    private val binding get() = _binding!!
    private val myEventsViewModel: MyEventsViewModel by viewModels()

    private var adapter: EventShortAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.recycler.layoutManager = LinearLayoutManager(context)

        myEventsViewModel.events.observe(viewLifecycleOwner) {
            adapter = EventShortAdapter(myEventsViewModel.events.value ?: listOf()){
                (activity as MainActivity).replaceFragment(EventDetailsFragment(it.id))
            }
            binding.recycler.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}