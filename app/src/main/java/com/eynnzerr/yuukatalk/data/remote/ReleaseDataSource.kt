package com.eynnzerr.yuukatalk.data.remote

import com.eynnzerr.yuukatalk.data.model.LatestRelease
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ReleaseDataSource {

    @GET(ReleaseApi.LATEST)
    suspend fun getLatestRelease(): Response<LatestRelease>

    companion object {
        val instance: ReleaseDataSource by lazy {
            Retrofit.Builder()
                .baseUrl(ReleaseApi.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ReleaseDataSource::class.java)
        }
    }
}