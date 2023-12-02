package com.eynnzerr.yuukatalk.data.module

import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
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
    fun provideTalkProjectDao(): TalkProjectDao = YuukaTalkDatabase.getInstance(YuukaTalkApplication.context).getDao()
}