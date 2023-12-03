package com.eynnzerr.yuukatalk.data.database

import com.eynnzerr.yuukatalk.data.database.source.TalkProjectDao
import com.eynnzerr.yuukatalk.data.model.TalkProject
import javax.inject.Inject

class AppRepository @Inject constructor(
    // private val remoteDataSource: RemoteDataSource,
    private val localDataSource: TalkProjectDao
) {
    fun fetchAllProjects() = localDataSource.fetchAllProjects()

    fun fetchProjectById(id: Int) = localDataSource.fetchProjectById(id)

    fun addProject(project: TalkProject) = localDataSource.addProject(project)

    fun updateProject(project: TalkProject) = localDataSource.updateProject(project)

    fun removeProject(project: TalkProject) = localDataSource.removeProject(project)

    fun removeAllProject() = localDataSource.removeAllProjects()
}