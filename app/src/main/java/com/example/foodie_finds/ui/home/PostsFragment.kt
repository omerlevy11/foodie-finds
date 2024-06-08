package com.example.foodie_finds.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie_finds.R

class PostsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noPostText : TextView

    fun getLayoutResourceId(): Int {
        return R.layout.posts_list_fragment
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = inflater.inflate(getLayoutResourceId(), container, false)
        if (view != null) {
            initViews(view)
        }
        noPostText = view?.findViewById<TextView>(R.id.no_posts_text_view)!!;
        setupRecyclerView()
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.posts_recycler_view)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}