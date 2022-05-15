package com.example.boardgamefinder.presentation.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.boardgamefinder.R
import com.example.boardgamefinder.databinding.FragmentProfileBinding
import com.example.boardgamefinder.presentation.viewModels.ProfileViewModel
import com.example.boardgamefinder.presentation.views.activities.LogInActivity
import com.example.boardgamefinder.presentation.views.activities.MainActivity


/**
 * Fragment for profile tab
 */
internal class ProfileFragment(private val userId: Int = -1) : Fragment() {
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

        profileViewModel.getProfile(userId)

        profileViewModel.user.observe(viewLifecycleOwner){
            binding.name.text = it.username
            binding.followersCount.text = (it.subscribersCount ?: 0).toString()
            binding.followingCount.text = (it.subscriptionsCount ?: 0).toString()
            Glide.with(requireContext())
                .load(it.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.color.dark_gray)
                .into(binding.profilePicture)

            if(it.isMe!!){
                binding.eventsButton.visibility = View.VISIBLE
                binding.eventsButton.isEnabled = true
                binding.eventsButton.setOnClickListener {
                    (activity as MainActivity).replaceFragment(MyEventsFragment())
                }

                binding.logoutButton.visibility = View.VISIBLE
                binding.logoutButton.isEnabled = true

                binding.logoutButton.setOnClickListener {
                    profileViewModel.logOut()
                }

                profileViewModel.logOutSuccess.observe(viewLifecycleOwner){success->
                    if(success)
                        (activity as MainActivity).openLogIntActivity()
                }
            }else{
                binding.logoutButton.isEnabled = false

                binding.followButton.visibility = View.VISIBLE
                binding.followButton.isEnabled = true

                setFollowButton(it.isSubscription == true)
            }
        }

        profileViewModel.subscription.observe(viewLifecycleOwner){
            binding.followersCount.text = profileViewModel.user.value?.subscribersCount.toString()
            setFollowButton(it)
        }
    }

    private fun setFollowButton(subscription: Boolean){
        if (subscription) {
            //ToDo move to resources
            binding.followButton.text = "UNFOLLOW"
            binding.followButton.setOnClickListener {_->
                profileViewModel.user.value?.id?.let { it1 -> profileViewModel.unsubscribe(it1) }
            }
        }else {
            //ToDo move to resources
            binding.followButton.text = "FOLLOW"
            binding.followButton.setOnClickListener { _ ->
                profileViewModel.user.value?.id?.let { it1 -> profileViewModel.subscribe(it1) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}