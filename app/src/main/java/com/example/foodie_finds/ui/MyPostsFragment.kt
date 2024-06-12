package com.example.foodie_finds.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.foodie_finds.ui.posts.PostViewModel
import com.example.foodie_finds.ui.posts.PostsMapFragment
import com.example.foodie_finds.data.post.PostModel


class MyPostsFragment : PostsMapFragment() {

    private val viewModel by activityViewModels<PostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        viewModel.assignPosts(PostModel.instance.getMyPosts());
        return view;
    }
}