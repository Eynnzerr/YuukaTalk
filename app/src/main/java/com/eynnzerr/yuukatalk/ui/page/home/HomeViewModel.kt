package com.eynnzerr.yuukatalk.ui.page.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.AppRepository
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.utils.VersionUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class HomeUiState(
    val showUpdate: Boolean,
    val updateContent: String,
    val newVersion: String,
    val newVersionUrl: String,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mmkv: MMKV,
    private val repository: AppRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            showUpdate = false,
            updateContent = "",
            newVersion = "",
            newVersionUrl = ""
        )
    )
    val uiState = _uiState.asStateFlow()

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

    fun checkVersion() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                Log.d(TAG, "checkVersion: pulling latest release.")
                repository.getLatestRelease()
            }.onSuccess { response ->
                Log.d(TAG, "checkVersion: $response")
                if (response.isSuccessful) {
                    response.body()?.let { latestRelease ->
                        val localVersion = VersionUtils.convertTagToVersion(VersionUtils.getLocalVersion())
                        val versionsIgnore = mmkv.decodeStringSet(PreferenceKeys.VERSIONS_IGNORE) ?: mutableSetOf()
                        if (localVersion < VersionUtils.convertTagToVersion(latestRelease.tag_name) && latestRelease.tag_name !in versionsIgnore) {
                            // 提示更新
                            _uiState.update {
                                it.copy(
                                    showUpdate = true,
                                    updateContent = latestRelease.body,
                                    newVersion = latestRelease.tag_name,
                                    newVersionUrl = latestRelease.html_url
                                )
                            }
                        }
                    }
                }
            }.onFailure { exception ->
                Log.d(TAG, "checkVersion: $exception")
                // TODO 弹窗表示检查版本失败
            }
        }
    }

    fun disableShowingUpdate() {
        _uiState.update { it.copy(showUpdate = false) }
    }

    fun addLatestVersionToIgnore() {
        val versionsIgnore = mmkv.decodeStringSet(PreferenceKeys.VERSIONS_IGNORE) ?: mutableSetOf()
        versionsIgnore.add(_uiState.value.newVersion)
        mmkv.encode(PreferenceKeys.VERSIONS_IGNORE, versionsIgnore)
    }
}

private const val TAG = "HomeViewModel"