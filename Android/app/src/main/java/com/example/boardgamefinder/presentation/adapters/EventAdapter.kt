package com.example.boardgamefinder.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.boardgamefinder.R
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.presentation.viewModels.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

// ToDo change openEvent delegate to (Int)->Unit
internal class EventAdapter(private val items: List<Event>, private val viewModel: HomeViewModel, private val openEvent: (Event) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(){
    private var context: Context? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.event_raw_object
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(viewType, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val timeFormatter = SimpleDateFormat("HH:mm")

        with(items[position]){
            holder.username.text = creator.username
            holder.date.text = dateFormatter.format(eventDate.time)
            holder.time.text = timeFormatter.format(eventDate.time)
            holder.eventName.text = title
            holder.likes.text = likes.toString()
            holder.place.text = location
            val members = "$visitorsCount/$visitorsLimit"
            holder.members.text = members
        }

        // setting like button into correct state
        holder.likeButton.apply {
            if (items[position].liked) {
                setImageResource(R.drawable.ic_like)
                setColorFilter(ContextCompat.getColor(context!!, R.color.heart))
            } else {
                setImageResource(R.drawable.ic_like_outlined)
                setColorFilter(ContextCompat.getColor(context!!, R.color.dark_gray))
            }
        }

        holder.likeButton.setOnClickListener {
            if(!items[position].liked)
                viewModel.setLike(position)
            else
                viewModel.removeLike(position)
        }

        if(!items[position].isCreator) {
            // setting join button into correct state
            holder.joinButton.apply {
                visibility = View.VISIBLE

                when (items[position].subscriptionStatus) {
                    Event.SubscriptionStatus.NOT_SUBMITTED -> {
                        setText(R.string.join)
                        setTextColor(MySettings.getSecondaryColor(context))
                    }
                    Event.SubscriptionStatus.REQUESTED -> {
                        setText(R.string.requested)
                        setTextColor(ContextCompat.getColor(context!!, R.color.dark_gray))
                    }
                    else -> {
                        setText(R.string.leave)
                        setTextColor(ContextCompat.getColor(context!!, R.color.dark_gray))
                    }
                }
            }
        }else
            holder.joinButton.visibility = View.INVISIBLE

        holder.joinButton.setOnClickListener {
            if(items[position].subscriptionStatus == Event.SubscriptionStatus.NOT_SUBMITTED)
                viewModel.joinEvent(position)
            else
                viewModel.leaveEvent(position)
        }

        items[position].tags?.let { tags ->
            val uniqueTags = tags.distinctBy { it.title }
            // setting tags
            for (i in uniqueTags) {
                val chip = Chip(context)
                chip.text = i.title
                holder.tags.addView(chip)
            }
        }

        // set event image
        context?.let{
            Glide.with(it)
                .load(items[position].imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.color.dark_gray)
                .transform(CenterCrop(), RoundedCorners(50))
                .into(holder.eventImage)
        }

        // set avatar image
        context?.let{
            Glide.with(it)
                .load(items[position].creator.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.color.dark_gray)
                .into(holder.avatar)
        }

        // open event page
        holder.eventCard.setOnClickListener {
            openEvent(items[position])
        }
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val likes: TextView = itemView.findViewById(R.id.likes)
        val place: TextView = itemView.findViewById(R.id.place)
        val members: TextView = itemView.findViewById(R.id.members)

        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val eventImage: ImageView = itemView.findViewById(R.id.event_image)

        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        val joinButton: TextView = itemView.findViewById(R.id.join_button)

        val tags: ChipGroup = itemView.findViewById(R.id.tags)

        val eventCard: CardView = itemView.findViewById(R.id.event_card)
    }
}