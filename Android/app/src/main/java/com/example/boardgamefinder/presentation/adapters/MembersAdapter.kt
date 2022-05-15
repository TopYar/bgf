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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.boardgamefinder.R
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.domain.models.User

internal class MembersAdapter(private val items: List<User>, private val openUser: (Int) -> Unit) : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>(){
    private var context: Context? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.member_raw_object
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(viewType, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MemberViewHolder,
        position: Int
    ) {
        if(position != 0)
            holder.status.visibility = View.GONE

        holder.username.text = items[position].username

        holder.card.setOnClickListener {
            openUser(items[position].id)
        }

        context?.let{
            Glide.with(it)
                .load(items[position].imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.color.dark_gray)
                .into(holder.avatar)
        }
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val status: TextView = itemView.findViewById(R.id.status)
        val avatar: ImageView = itemView.findViewById(R.id.avatar)

        val card: CardView = itemView.findViewById(R.id.user_card)
    }
}