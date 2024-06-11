package com.example.foodie_finds.activities.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie_finds.R
import com.example.foodie_finds.data.post.Post
import com.example.foodie_finds.data.post.PostModel
import com.example.foodie_finds.data.user.User
import com.example.foodie_finds.ui.home.HomeFragmentDirections

class PostsFragment : Fragment(), PostCardsAdapter.OnPostItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noPostText: TextView
    private val viewModel by activityViewModels<PostViewModel>()
    private var onPostItemClickListener: OnPostItemClickListener? = null

    interface OnPostItemClickListener {
        fun onPostItemClicked(postId: String)
    }

    private fun observePostViewModel(
        recyclerView: RecyclerView,
        posts: LiveData<MutableList<Post>>?,
        users: LiveData<MutableList<User>>?
    ) {
        posts?.observe(viewLifecycleOwner) { currPosts: List<Post> ->
            if (currPosts.isNotEmpty()) {
                noPostText.isVisible = false
                recyclerView.isVisible = true
                users?.observe(viewLifecycleOwner) { currUsers: List<User> ->
                    val postCardsAdapter = PostCardsAdapter(currPosts, currUsers)
                    postCardsAdapter.setOnPostItemClickListener(this)
                    recyclerView.adapter = postCardsAdapter
                }
            } else {
                noPostText.isVisible = true
                recyclerView.isVisible = false
            }
        }
    }

    private fun getLayoutResourceId(): Int {
        return R.layout.posts_list_fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View? = inflater.inflate(getLayoutResourceId(), container, false)
        if (view != null) {
            initViews(view)
        }
        viewModel.assignPosts(PostModel.instance.getPosts())
        noPostText = view?.findViewById<TextView>(R.id.no_posts_text_view)!!
        setupRecyclerView()
        observePostViewModel()
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.posts_recycler_view)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observePostViewModel() {
        observePostViewModel(recyclerView, viewModel.posts, viewModel.users)
    }

    fun setOnPostItemClickListener(listener: PostsMapFragment) {
        onPostItemClickListener = listener
    }

    // Call the listener when an item is clicked
    override fun onPostItemClicked(postId: String) {
        onPostItemClickListener?.onPostItemClicked(postId)
    }

    override fun onPostDeleteClicked(postId: String) {
        viewModel.posts.value?.let { posts ->
            val post = posts.firstOrNull { it.id == postId }
            if (post != null) {
                PostModel.instance.deletePost(post) {}
            }
        }
    }

    override fun onPostEditClicked(post: Post) {
              try {
        val action = HomeFragmentDirections.actionHomeToCreatePost(post)
        findNavController().navigate(action)
        } catch (e: Exception) {
            e.message?.let { Log.d("PostCreate", it) }
        }

    }

    fun onMarkerClicked(postId: String) {
        val index = viewModel.posts.value?.indexOfFirst { post -> post.id == postId }
        if (index != null) {
            recyclerView.scrollToPosition(index)
        }
    }
}