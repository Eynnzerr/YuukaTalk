package com.eynnzerr.yuukatalk.data.database.source

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.TalkProject

@Database(
    version = 1,
    entities = [TalkProject::class, Character::class],
)
abstract class YuukaTalkDatabase: RoomDatabase() {
    abstract fun getTalkProjectDao(): TalkProjectDao

    abstract fun getCharacterDao(): CharacterDao

    companion object {

        private const val databaseName = "eynnzerr-yuuka-talk"

        @Volatile
        private var INSTANCE: YuukaTalkDatabase? = null

        @Synchronized
        fun getInstance(context: Context): YuukaTalkDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                YuukaTalkDatabase::class.java,
                databaseName
            ).fallbackToDestructiveMigration().build()
        }
    }
}