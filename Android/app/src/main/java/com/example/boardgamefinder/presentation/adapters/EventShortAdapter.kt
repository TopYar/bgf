package com.example.boardgamefinder.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.boardgamefinder.R
import com.example.boardgamefinder.domain.models.Event
import java.text.SimpleDateFormat

internal class EventShortAdapter(private val items: List<Event>, private val openEvent: (Event) -> Unit) : RecyclerView.Adapter<EventShortAdapter.EventViewHolder>(){
    private var context: Context? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.event_short_raw_object
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
            holder.place.text = location
        }

        // set event image
        context?.let{
            Glide.with(it)
                .load(items[position].imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.color.dark_gray)
                .transform(CenterCrop(), RoundedCorners(15))
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
        val place: TextView = itemView.findViewById(R.id.place)

        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val eventImage: ImageView = itemView.findViewById(R.id.event_image)

        val eventCard: CardView = itemView.findViewById(R.id.event_card)
    }
}