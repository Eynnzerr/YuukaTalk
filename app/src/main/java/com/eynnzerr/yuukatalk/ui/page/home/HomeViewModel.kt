package com.eynnzerr.yuukatalk.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.database.AppRepository
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mmkv: MMKV,
    private val repository: AppRepository,
): ViewModel() {
    fun importCharactersFromFile() {
        val assetManager = YuukaTalkApplication.context.assets
        viewModelScope.launch(Dispatchers.IO) {
            val json = assetManager.open("characters.json").bufferedReader().readText()
            val characters = Json.decodeFromString<Array<Character>>(json)
            repository.importCharacters(*characters)
        }
    }

    fun encodeHideGuidance() = mmkv.encode(PreferenceKeys.SHOW_GUIDANCE, false)

    fun isShowGuidance() = mmkv.decodeBool(PreferenceKeys.SHOW_GUIDANCE, true)
}