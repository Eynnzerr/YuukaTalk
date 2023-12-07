package com.eynnzerr.yuukatalk.data.database.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eynnzerr.yuukatalk.data.model.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM momotalk_character")
    fun fetchCallCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM momotalk_character WHERE " +
            "name LIKE '%' || :keyword || '%' OR " +
            "nameRoma LIKE '%' || :keyword || '%' OR " +
            "school LIKE '%' || :keyword || '%'"
    )
    fun fetchCharactersByKeyword(keyword: String): Flow<List<Character>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun importCharacters(vararg characters: Character)

    @Query("DELETE FROM momotalk_character")
    fun removeAllCharacters()
}