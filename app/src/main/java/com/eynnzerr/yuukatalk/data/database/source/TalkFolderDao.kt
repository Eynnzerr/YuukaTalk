package com.eynnzerr.yuukatalk.data.database.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eynnzerr.yuukatalk.data.model.FolderWithProjects
import com.eynnzerr.yuukatalk.data.model.TalkFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface TalkFolderDao {
    @Transaction
    @Query("SELECT * FROM momotalk_folder")
    fun fetchAllFoldersWithProjects(): Flow<List<FolderWithProjects>>

    @Query("SELECT * FROM momotalk_folder")
    fun fetchAllFolders(): Flow<List<TalkFolder>>

    @Query("SELECT * FROM momotalk_folder WHERE id = :id")
    fun fetchFolderById(id: Int): TalkFolder?

    @Query("SELECT * FROM momotalk_folder WHERE name = :name LIMIT 1")
    fun fetchFolderByName(name: String): TalkFolder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFolder(folder: TalkFolder): Long

    @Update
    fun updateFolder(folder: TalkFolder)

    @Delete
    fun removeFolder(folder: TalkFolder)
}