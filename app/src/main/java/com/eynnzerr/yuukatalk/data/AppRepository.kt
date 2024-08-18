package com.eynnzerr.yuukatalk.data

import com.eynnzerr.yuukatalk.data.database.source.CharacterDao
import com.eynnzerr.yuukatalk.data.database.source.TalkFolderDao
import com.eynnzerr.yuukatalk.data.database.source.TalkProjectDao
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.TalkFolder
import com.eynnzerr.yuukatalk.data.model.TalkProject
import com.eynnzerr.yuukatalk.data.remote.ReleaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val talkProjectDao: TalkProjectDao,
    private val characterDao: CharacterDao,
    private val folderDao: TalkFolderDao,
    private val releaseDataSource: ReleaseDataSource,
) {
    fun fetchAllProjects() = talkProjectDao.fetchAllProjects()

    fun fetchAllFreeProjects() = talkProjectDao.fetchAllFreeProjects()

    fun fetchProjectById(id: Int) = talkProjectDao.fetchProjectById(id)

    fun addProject(project: TalkProject) = talkProjectDao.addProject(project)

    fun updateProject(project: TalkProject) = talkProjectDao.updateProject(project)

    fun removeProject(project: TalkProject) = talkProjectDao.removeProject(project)

    fun removeAllProject() = talkProjectDao.removeAllProjects()

    fun fetchAllCharacters() = characterDao.fetchCallCharacters()

    fun fetchCharactersByKeyword(keyword: String) = characterDao.fetchCharactersByKeyword(keyword)

    fun importCharacters(vararg characters: Character) = characterDao.importCharacters(*characters)

    fun removeAllCharacters() = characterDao.removeAllCharacters()

    fun removeCharacter(character: Character) = characterDao.removeCharacter(character)

    fun fetchAllFoldersWithProjects() = folderDao.fetchAllFoldersWithProjects()

    fun fetchAllFolders() = folderDao.fetchAllFolders()

    fun addFolder(folder: TalkFolder) = folderDao.addFolder(folder)

    fun updateFolder(folder: TalkFolder) = folderDao.updateFolder(folder)

    fun deleteFolder(folder: TalkFolder) = folderDao.removeFolder(folder)

    suspend fun getLatestRelease() = releaseDataSource.getLatestRelease()
}