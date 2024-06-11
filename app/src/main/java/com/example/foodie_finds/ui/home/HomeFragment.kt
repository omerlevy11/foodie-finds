package com.example.foodie_finds.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.foodie_finds.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.foodie_finds.data.post.PostModel
import com.example.foodie_finds.activities.posts.PostViewModel
import com.example.foodie_finds.activities.posts.PostsMapFragment


class HomeFragment : PostsMapFragment() {

    private val viewModel by activityViewModels<PostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        viewModel.assignPosts(PostModel.instance.getMyPosts())
        return view
    }
}