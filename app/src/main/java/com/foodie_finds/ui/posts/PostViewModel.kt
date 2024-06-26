package com.foodie_finds.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodie_finds.data.post.Post
import com.foodie_finds.data.user.User
import com.foodie_finds.data.user.UserModel

class PostViewModel : ViewModel() {
    var posts: LiveData<MutableList<Post>> = MutableLiveData();
    var users: LiveData<MutableList<User>> = MutableLiveData();

    fun assignPosts(postsList: LiveData<MutableList<Post>>) {
        posts = postsList;
        users = UserModel.instance.getAllUsers();
    }
}