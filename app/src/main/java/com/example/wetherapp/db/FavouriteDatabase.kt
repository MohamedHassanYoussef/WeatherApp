package com.example.wetherapp.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Dao
@Database(entities = arrayOf(PlaceFavPojo::class), version = 1)
abstract class FavouriteDatabase: RoomDatabase() {
    abstract fun getDao(): FavouriteDao
    companion object {
        private var instance: FavouriteDatabase? = null
        fun getInstance(context: Context): FavouriteDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavouriteDatabase::class.java,
                    "table"
                ).build()
            }
        }
    }
}