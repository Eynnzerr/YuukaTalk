package com.eynnzerr.yuukatalk.data.database

import com.eynnzerr.yuukatalk.data.database.source.CharacterDao
import com.eynnzerr.yuukatalk.data.database.source.TalkProjectDao
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.TalkProject
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val talkProjectDao: TalkProjectDao,
    private val characterDao: CharacterDao,
) {
    fun fetchAllProjects() = talkProjectDao.fetchAllProjects()

    fun fetchProjectById(id: Int) = talkProjectDao.fetchProjectById(id)

    fun addProject(project: TalkProject) = talkProjectDao.addProject(project)

    fun updateProject(project: TalkProject) = talkProjectDao.updateProject(project)

    fun removeProject(project: TalkProject) = talkProjectDao.removeProject(project)

    fun removeAllProject() = talkProjectDao.removeAllProjects()

    fun fetchAllCharacters() = characterDao.fetchCallCharacters()

    fun importCharacters(vararg characters: Character) = characterDao.importCharacters(*characters)
}