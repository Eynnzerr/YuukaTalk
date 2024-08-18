package com.eynnzerr.yuukatalk.data.module

import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.database.source.CharacterDao
import com.eynnzerr.yuukatalk.data.database.source.TalkFolderDao
import com.eynnzerr.yuukatalk.data.database.source.TalkProjectDao
import com.eynnzerr.yuukatalk.data.database.source.YuukaTalkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDaoModule {
    @Provides
    @Singleton
    fun provideTalkProjectDao(): TalkProjectDao = YuukaTalkDatabase.getInstance(YuukaTalkApplication.context).getTalkProjectDao()

    @Provides
    @Singleton
    fun provideCharacterDao(): CharacterDao = YuukaTalkDatabase.getInstance(YuukaTalkApplication.context).getCharacterDao()

    @Provides
    @Singleton
    fun provideFolderDao(): TalkFolderDao = YuukaTalkDatabase.getInstance(YuukaTalkApplication.context).getTalkFolderDao()
}