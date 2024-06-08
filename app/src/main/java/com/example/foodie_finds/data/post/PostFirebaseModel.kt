package com.example.foodie_finds.data.post

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class PostFirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }


    fun getPosts(since: Long, callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val student = Post.fromJSON(json.data)
                            posts.add(student)
                        }
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getImage(imageId: String, callback: (Uri) -> Unit) {
        storage.reference.child("images/$POSTS_COLLECTION_PATH/$imageId")
            .downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri)
            }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).document(post.id).set(post.json)
            .addOnSuccessListener {
                callback()
            }
    }

    fun addPostImage(postId: String, selectedImageUri: Uri, callback: () -> Unit) {
        val imageRef = storage.reference.child("images/$POSTS_COLLECTION_PATH/${postId}")
        imageRef.putFile(selectedImageUri).addOnSuccessListener {
            callback()
        }
    }

    fun deletePost(post: Post?, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(post!!.id).update(post.deleteJson).addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't delete this post document: " + it.message)
            }
    }

    fun updatePost(post: Post?, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(post!!.id).update(post.updateJson)
            .addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't update this post document: " + it.message)
            }
    }
}