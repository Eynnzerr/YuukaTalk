package com.eynnzerr.yuukatalk.data.module

import com.eynnzerr.yuukatalk.data.remote.ReleaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideAppNetworkDataSource(): ReleaseDataSource = ReleaseDataSource.instance
}