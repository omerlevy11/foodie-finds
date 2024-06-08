package com.example.foodie_finds.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodie_finds.FoodieFindsApp
import com.example.foodie_finds.data.post.PostDAO
import com.example.foodie_finds.data.user.User
import com.example.foodie_finds.data.user.UserDAO
import com.example.foodie_finds.data.post.LatLngConverter
import com.example.foodie_finds.data.post.Post

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
            context,
            AppLocalDbRepository::class.java,
            "my-trip"
        ).fallbackToDestructiveMigration()
            .build()
    }
}