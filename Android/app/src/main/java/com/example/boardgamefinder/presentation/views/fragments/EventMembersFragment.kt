package com.example.boardgamefinder.presentation.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boardgamefinder.R
import com.example.boardgamefinder.databinding.FragmentEventDetailsBinding
import com.example.boardgamefinder.databinding.FragmentEventMemebersBinding
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.presentation.adapters.EventAdapter
import com.example.boardgamefinder.presentation.adapters.MembersAdapter
import com.example.boardgamefinder.presentation.viewModels.EventMembersViewModel
import com.example.boardgamefinder.presentation.viewModels.HomeViewModel
import com.example.boardgamefinder.presentation.views.activities.MainActivity

class EventMembersFragment (val event: Event) : Fragment() {

    private var _binding: FragmentEventMemebersBinding? = null
    private val binding get() = _binding!!
    private val eventMembersViewModel: EventMembersViewModel by viewModels()

    private var adapter: MembersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEventMemebersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.title.text = event.title
        val peopleLimit = event.visitorsCount.toString() + "/" + event.visitorsLimit
        binding.peopleLimit.text = peopleLimit

        binding.recycler.layoutManager = LinearLayoutManager(context)

        eventMembersViewModel.getMembers(event.id)

        eventMembersViewModel.members.observe(viewLifecycleOwner){
            val items = eventMembersViewModel.members.value ?: listOf()
            adapter = MembersAdapter(items){
                (activity as MainActivity).replaceFragment(ProfileFragment(it))
            }
            binding.recycler.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}