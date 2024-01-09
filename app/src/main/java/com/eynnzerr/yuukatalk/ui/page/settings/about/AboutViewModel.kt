package com.eynnzerr.yuukatalk.ui.page.settings.about

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.AppRepository
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.utils.VersionUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.eynnzerr.yuukatalk.R
import kotlinx.coroutines.withContext

data class AboutUiState(
    val showUpdate: Boolean,
    val updateContent: String,
    val newVersion: String,
    val newVersionUrl: String,
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val repository: AppRepository,
    private val mmkv: MMKV
): ViewModel() {

    private val _uiState = MutableStateFlow(
        AboutUiState(
            showUpdate = false,
            updateContent = "",
            newVersion = "",
            newVersionUrl = ""
        )
    )
    val uiState = _uiState.asStateFlow()

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
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    YuukaTalkApplication.context,
                                    YuukaTalkApplication.context.getText(R.string.toast_already_newest),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }.onFailure { exception ->
                Log.d(TAG, "checkVersion: $exception")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        YuukaTalkApplication.context,
                        YuukaTalkApplication.context.getText(R.string.toast_check_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

private const val TAG = "AboutViewModel"