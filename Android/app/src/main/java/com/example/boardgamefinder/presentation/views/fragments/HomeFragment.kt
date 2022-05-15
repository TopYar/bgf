package com.example.boardgamefinder.presentation.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boardgamefinder.R
import com.example.boardgamefinder.databinding.FragmentHomeBinding
import com.example.boardgamefinder.databinding.FragmentProfileBinding
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.presentation.adapters.EventAdapter
import com.example.boardgamefinder.presentation.viewModels.HomeViewModel
import com.example.boardgamefinder.presentation.views.activities.MainActivity

/**
 * Fragment for event feed tab
 */
internal class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    private var adapter: EventAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(context)

        homeViewModel.events.observe(viewLifecycleOwner) {
            adapter = EventAdapter(it ?: listOf(), homeViewModel){event ->
                (activity as MainActivity).replaceFragment(EventDetailsFragment(event.id))
            }
            binding.recycler.adapter = adapter
        }

        homeViewModel.updateRecycler.observe(viewLifecycleOwner){
            binding.recycler.adapter?.notifyItemChanged(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}