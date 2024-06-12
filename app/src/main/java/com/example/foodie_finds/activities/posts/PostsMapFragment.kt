package com.example.foodie_finds.activities.posts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.foodie_finds.R
import com.example.foodie_finds.data.post.Post
import com.example.foodie_finds.data.post.SerializableLatLng
import com.example.foodie_finds.databinding.FragmentHomeBinding
import com.example.foodie_finds.ui.home.HomeFragmentDirections
import com.example.foodie_finds.viewModels.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

abstract class PostsMapFragment : Fragment(), OnMapReadyCallback,
    PostsFragment.OnPostItemClickListener, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val viewModel by activityViewModels<PostViewModel>()
    private val locationViewModel by activityViewModels<LocationViewModel>()
    private var currLocationMarker: Marker? = null
    private var myLocationIcon: BitmapDescriptor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        myLocationIcon = BitmapDescriptorFactory.fromBitmap(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.my_location
                ), 200, 200, false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.customPostsFragment.getFragment<PostsFragment>().setOnPostItemClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.setOnMapLongClickListener {
            val tempPost = Post("", "", "", SerializableLatLng.fromGoogleLatLng(it))
            val action = HomeFragmentDirections.actionHomeToCreatePost(tempPost)
            findNavController().navigate(action)
        }
        locationViewModel.location.observe(viewLifecycleOwner, Observer {
            currLocationMarker?.remove()
            currLocationMarker = map.addMarker(
                MarkerOptions().position(LatLng(it.latitude, it.longitude)).icon(myLocationIcon)
            )!!
        })
        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            map.clear()

            posts.forEach { post ->
                val bd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                val marker =
                    map.addMarker(MarkerOptions().icon(bd).position(post.position.toGoogleLatLng()))
                if (marker != null) {
                    marker.tag = post.id
                }
            }
        })
    }

    override fun onPostItemClicked(postId: String) {
        val post = viewModel.posts.value?.find { curr -> curr.id === postId }
        if (post != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(post.position.toGoogleLatLng(), 9f))
        }
    }

    override fun onMarkerClick(clickedMarker: Marker): Boolean {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(clickedMarker.position, 9f))
        binding.customPostsFragment.getFragment<PostsFragment>()
            .onMarkerClicked(clickedMarker.tag.toString())
        return true
    }
}