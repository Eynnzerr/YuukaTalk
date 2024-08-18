package com.eynnzerr.yuukatalk.data.database.source

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.TalkFolder
import com.eynnzerr.yuukatalk.data.model.TalkProject

@Database(
    version = 3,
    entities = [TalkProject::class, Character::class, TalkFolder::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (
            from = 2,
            to = 3
        ),
    ]
)

abstract class YuukaTalkDatabase: RoomDatabase() {
    abstract fun getTalkProjectDao(): TalkProjectDao

    abstract fun getCharacterDao(): CharacterDao

    abstract fun getTalkFolderDao(): TalkFolderDao

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
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}