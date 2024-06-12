package com.foodie_finds.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foodie_finds.FoodieFindsApp
import com.foodie_finds.data.post.LatLngConverter
import com.foodie_finds.data.post.Post
import com.foodie_finds.data.post.PostDAO
import com.foodie_finds.data.user.User
import com.foodie_finds.data.user.UserDAO

@Database(entities = [User::class, Post::class], version = 7, exportSchema = true)
@TypeConverters(LatLngConverter::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun postDao(): PostDAO
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = FoodieFindsApp.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context, AppLocalDbRepository::class.java, "foodie-finds"
        ).fallbackToDestructiveMigration().build()
    }
}