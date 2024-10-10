package com.example.wetherapp.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Dao

@Database(entities = [PlaceFavPojo::class, AlertPojo::class], version = 2)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDao(): LocalDataDao
    companion object {
        @Volatile
        private var instance: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "local_database"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}
