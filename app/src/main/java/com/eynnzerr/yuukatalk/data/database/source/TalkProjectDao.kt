package com.eynnzerr.yuukatalk.data.database.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eynnzerr.yuukatalk.data.model.TalkProject
import kotlinx.coroutines.flow.Flow

@Dao
interface TalkProjectDao {
    @Query("SELECT * FROM momotalk_project")
    fun fetchAllProjects(): Flow<List<TalkProject>>

    @Query("SELECT * FROM momotalk_project WHERE id = :id")
    fun fetchProjectById(id: Int): TalkProject

    @Query("SELECT * FROM momotalk_project WHERE folderId IS NULL OR folderId NOT IN (SELECT id FROM momotalk_folder)")
    fun fetchAllFreeProjects(): Flow<List<TalkProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProject(project: TalkProject): Long

    @Update
    fun updateProject(project: TalkProject)

    @Delete
    fun removeProject(project: TalkProject)

    @Query("DELETE FROM MOMOTALK_PROJECT")
    fun removeAllProjects()
}