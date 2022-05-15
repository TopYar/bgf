package com.example.boardgamefinder.presentation.views.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.boardgamefinder.R
import com.example.boardgamefinder.databinding.FragmentEventDetailsBinding
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.presentation.viewModels.EventDetailsViewModel
import com.example.boardgamefinder.presentation.views.activities.MainActivity
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat


class EventDetailsFragment(val eventId: Int) : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!
    private val eventDetailsViewModel: EventDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        eventDetailsViewModel.getEvent(eventId)

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val timeFormatter = SimpleDateFormat("HH:mm")

        eventDetailsViewModel.event.observe(viewLifecycleOwner){event ->
            binding.title.text = event.title
            binding.location.text = event.location

            binding.date.text = dateFormatter.format(event.eventDate.time)
            binding.time.text = timeFormatter.format(event.eventDate.time)
            binding.description.text = event.description

            // set image
            Glide.with(this)
                .load(event.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.color.dark_gray)
                .transform(CenterCrop(), RoundedCorners(50))
                .into(binding.imageView)

            if(!event.isCreator) {
                binding.joinButton.visibility = View.VISIBLE
                binding.joinButton.isEnabled = true

                // set subscription button state
                setJoinButton(event.subscriptionStatus)

                binding.joinButton.setOnClickListener {
                    if(event.subscriptionStatus == Event.SubscriptionStatus.NOT_SUBMITTED)
                        eventDetailsViewModel.joinEvent(event.id)
                    else
                        eventDetailsViewModel.leaveEvent(event.id)
                }

                eventDetailsViewModel.subscriptionStatus.observe(viewLifecycleOwner){
                    event.subscriptionStatus = it
                    setJoinButton(it)
                }
            }

            // set description scrollable
            binding.description.movementMethod = ScrollingMovementMethod()

            event.tags?.let { tags ->
                val uniqueTags = tags.distinctBy { it.title }
                // setting tags
                for (i in uniqueTags) {
                    val chip = Chip(context)
                    chip.text = i.title
                    binding.tags.addView(chip)
                }
            }

            binding.membersButton.setOnClickListener {
                (activity as MainActivity).replaceFragment(EventMembersFragment(event))
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setJoinButton(status: Event.SubscriptionStatus){
        if(status == Event.SubscriptionStatus.NOT_SUBMITTED){
            //ToDo to resources
            binding.joinButton.text = "JOIN"

            //ToDo binding.joinButton.style(R.style.Widget_BoardGameFinder_Button_SmallOutlinedButtonOnPrimary)
            //Paris.style(binding.joinButton).apply(R.style.Widget_BoardGameFinder_Button_SmallOutlinedButtonOnPrimary);
        }else{
            binding.joinButton.text = "LEAVE"
        }
    }
}