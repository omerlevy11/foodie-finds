package com.foodie_finds.ui.posts

import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foodie_finds.R
import com.foodie_finds.data.post.Post
import com.foodie_finds.data.post.PostModel
import com.foodie_finds.data.post.SerializableLatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso
import java.util.Locale
import java.util.UUID
import com.foodie_finds.utils.LocationApiManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class CreatePostFragment : Fragment() {
    private lateinit var view: View
    private lateinit var description: TextInputEditText
    private lateinit var locationName: TextView
    private lateinit var spinner: ProgressBar
    private lateinit var attachPictureButton: ImageButton
    private lateinit var submitButton: MaterialButton
    private var attachedPicture: Uri = Uri.EMPTY
    private var imageView: ImageView? = null
    private val auth = Firebase.auth
    private var hasImageChanged = false

    private val args: CreatePostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(
            R.layout.fragment_create_post, container, false
        )

        backFromCreatePost()
        setUserNameTextView()
        getDeviceLocation()
        initViews(view)
        handleSubmitButton()
        handleAttachProductPicture()

        lifecycleScope.launch {
            getLocationName("${args.post.position.latitude}", "${args.post.position.longitude}")
        }

        return view
    }

    private suspend fun getLocationName(lat : String, lng : String) {
        try {
            val locationNameResult = LocationApiManager().getLocationName(lat, lng)
            val locationString = locationNameResult.results[0].formatted_address
            locationName.text = locationString
        } catch (e: Exception) {
            locationName.text = "Missing Location"
        }
    }

    private fun getDeviceLocation() {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        val addresses =
            geocoder?.getFromLocation(args.post.position.latitude, args.post.position.longitude, 1)
        if (addresses?.size!! == 0) {
            findNavController().popBackStack()
        }
    }

    private fun backFromCreatePost() {
        view.findViewById<ImageView>(R.id.backFromCreatePostButton).setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUserNameTextView() {
        view.findViewById<TextView>(R.id.createPostTitleTextView).text =
            "@${auth.currentUser?.displayName}"
    }

    private fun initViews(view: View) {
        spinner = view.findViewById(R.id.create_post_progress)
        description = view.findViewById(R.id.post_description)
        locationName = view.findViewById(R.id.createPostLocationTextView)
        attachPictureButton = view.findViewById(R.id.upload_picture_button)
        imageView = view.findViewById(R.id.selected_image)
        submitButton = view.findViewById(R.id.post_submit)


        if (args.post.id.isNotEmpty()) {
            description.setText(args.post.description)
        } else {
            spinner.visibility = GONE
        }

        PostModel.instance.getPostImage(args.post.id) {
            attachedPicture = it
            Picasso.get().load(it).into(imageView)
            spinner.visibility = GONE
        }
    }

    private fun handleSubmitButton() {
        submitButton.setOnClickListener {
            createNewPost()
        }
    }

    private fun showDialogResponse(message: String) {
        val rootView: View = requireView()
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.black))
        val textView: TextView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(resources.getColor(R.color.white))
        snackBar.show()
    }

    private fun createNewPost() {
        val descriptionValue = description.text.toString()

        if (descriptionValue.isEmpty()) {
            showDialogResponse("Please enter a description")
            return
        }
        if (attachedPicture == Uri.EMPTY) {
            showDialogResponse("Please select a picture")
            return
        }

        val postId: String

        if (args.post.id.isNotEmpty()) {
            postId = args.post.id
        } else {
            postId = UUID.randomUUID().toString()
        }

        val newPost = auth.currentUser?.let {
            Post(
                postId,
                it.uid,
                "${locationName.text}\n${description.text.toString()}",
                SerializableLatLng(args.post.position.latitude, args.post.position.longitude),
            )
        }

        spinner.visibility = VISIBLE

        if (newPost != null) {
            if (args.post.id.isNotEmpty()) {
                PostModel.instance.updatePost(newPost) {
                    if (hasImageChanged) {
                        PostModel.instance.updatePostImage(newPost.id, attachedPicture) {
                            findNavController().popBackStack()
                        }
                    } else {
                        findNavController().popBackStack()
                    }
                }
            } else {
                PostModel.instance.addPost(newPost, attachedPicture) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private val pickImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            try {
                uri?.let {
                    attachedPicture = it
                    Picasso.get().load(it).into(imageView)
                    hasImageChanged = true
                }
            } catch (e: Exception) {
                Log.d("CreatePost", "${e.message}")
            }
        }

    private fun handleAttachProductPicture() {
        attachPictureButton.setOnClickListener {
            pickImageContract.launch("image/*")
        }
    }
}
