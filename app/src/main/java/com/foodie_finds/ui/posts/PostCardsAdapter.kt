package com.foodie_finds.ui.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodie_finds.R
import com.foodie_finds.data.post.Post
import com.foodie_finds.data.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class PostCardsAdapter(private val posts: List<Post>, private val users: List<User>) :
    RecyclerView.Adapter<PostCardsAdapter.PostViewHolder>() {
    private var onPostItemClickListener: OnPostItemClickListener? = null
    private val userId = Firebase.auth.uid

    interface OnPostItemClickListener {
        fun onPostItemClicked(
            postId: String
        )

        fun onPostDeleteClicked(
            postId: String
        )

        fun onPostEditClicked(
            post: Post
        )
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.card_image)
        val user: TextView = itemView.findViewById(R.id.card_user)
        val description: TextView = itemView.findViewById(R.id.card_description)
        val card: CardView = itemView.findViewById(R.id.card)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_button)
        val editBtn: ImageButton = itemView.findViewById(R.id.edit_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_card, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val user = users.find { it.id == post.userId }
        Glide.with(holder.itemView)
            .load(post.photo)
            .into(holder.image)
        holder.image.contentDescription = post.description
        holder.user.text = "${user?.userName}"
        holder.description.text = post.description

        holder.deleteBtn.isVisible = post.userId == userId
        holder.editBtn.isVisible = post.userId == userId

        handleClicksCard(holder, position)
    }

    fun setOnPostItemClickListener(listener: PostsFragment) {
        onPostItemClickListener = listener
    }

    private fun handleClicksCard(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.card.setOnClickListener {
            onPostItemClickListener?.onPostItemClicked(post.id)
        }
        holder.deleteBtn.setOnClickListener {
            onPostItemClickListener?.onPostDeleteClicked(post.id)
        }
        holder.editBtn.setOnClickListener {
            onPostItemClickListener?.onPostEditClicked(post)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}