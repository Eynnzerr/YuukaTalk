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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProject(project: TalkProject)

    @Update
    fun updateProject(project: TalkProject)

    @Delete
    fun removeProject(project: TalkProject)
}