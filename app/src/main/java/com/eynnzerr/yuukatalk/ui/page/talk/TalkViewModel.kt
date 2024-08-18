package com.eynnzerr.yuukatalk.ui.page.talk

import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.AppRepository
import com.eynnzerr.yuukatalk.data.model.Character
import com.eynnzerr.yuukatalk.data.model.Sensei
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.data.model.TalkFolder
import com.eynnzerr.yuukatalk.data.model.TalkProject
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.utils.ImageUtils
import com.eynnzerr.yuukatalk.utils.PathUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

data class TalkUiState(
    val chatName: String,
    val text: String,
    val talkList: List<Talk>,
    val studentList: List<Character>,
    val currentStudent: Character,
    val isFirstTalking: Boolean,
    val isMoreToolsOpen: Boolean,
    val searchText: String,
    val narrationText: String,
    val textBranches: List<String>,
    val talkListStateChange: List<TalkListState>, // operate with recyclerView change
    val filteredStudents: List<Character>,
    val isEdited: Boolean,
    val gestureEnabled: Boolean,
    val showBundled: Boolean,
    val showDIY: Boolean,
    val filterSchoolName: String,
    val inputFolderName: String,
    val allFolders: List<TalkFolder>,
)

sealed class TalkListState(val type: Int) {
    class Initialized: TalkListState(type = 0)
    class Refresh: TalkListState(type = 1)
    class Push: TalkListState(type = 2)
    class Pop: TalkListState(type = 3)
    class Modified(val index: Int): TalkListState(type = 4)
    class Removed(val index: Int): TalkListState(type = 5)
    class Inserted(val index: Int): TalkListState(type = 6)
}

@HiltViewModel
class TalkViewModel @Inject constructor(
    private val repository: AppRepository,
    private val mmkv: MMKV,
) : ViewModel() {

    private val talkList = mutableListOf<Talk>()
    private var studentList = mutableListOf<Character>(Sensei)
    private val branchArray
        get() = _uiState.value.textBranches.toTypedArray()
    private lateinit var allStudents: List<Character>

    private val _uiState = MutableStateFlow(
        TalkUiState(
            chatName = "MomoTalk",
            text = "",
            talkList = talkList,
            studentList = studentList,
            currentStudent = studentList[0],
            isFirstTalking = true,
            isMoreToolsOpen = false,
            searchText = "",
            narrationText = "",
            textBranches = listOf(""),
            talkListStateChange = listOf(TalkListState.Initialized()),
            filteredStudents = emptyList(),
            isEdited = false,
            gestureEnabled = mmkv.decodeBool(PreferenceKeys.USE_SWIPE_GESTURE, true),
            showBundled = true,
            showDIY = true,
            filterSchoolName = "All",
            allFolders = emptyList(),
            inputFolderName = "",
        )
    )
    private val stateValue
        get() = _uiState.value
    val uiState = _uiState.asStateFlow()

    private var projectId = -1
    private var folderId: Int? = null

    fun setProjectId(id: Int) {
        projectId = id
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAllCharacters().collect { characters ->
                allStudents = characters
                _uiState.update { it.copy(filteredStudents = characters) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAllFolders().collect { folders ->
                _uiState.update { it.copy(allFolders = folders) }
            }
        }
    }

    fun loadHistory() {
        if (isHistoryTalk() && talkList.isEmpty()) {
            // means we now enter into a history talk for the first time.
            Log.d(TAG, "loadHistory...")
            viewModelScope.launch {
                val historyState = withContext(Dispatchers.IO) {
                    repository.fetchProjectById(projectId)
                }
                talkList.addAll(historyState.talkHistory)
                studentList.apply {
                    clear()
                    addAll(historyState.studentList)
                }
                folderId = historyState.folderId
                _uiState.update {
                    it.copy(
                        chatName = historyState.name,
                        talkList = historyState.talkHistory,
                        currentStudent = historyState.currentStudent,
                        isFirstTalking = historyState.isFirstTalking,
                        talkListStateChange = listOf(TalkListState.Refresh()),
                        inputFolderName = _uiState.value.allFolders.firstOrNull { folder -> folder.id == historyState.folderId }?.name ?: ""
                    )
                }
            }
        }
    }

    fun updateText(newText: String) = _uiState.update { it.copy(text = newText) }

    fun updateNarrationText(newText: String) = _uiState.update { it.copy(narrationText = newText) }

    fun updateChatName(newText: String) = _uiState.update { it.copy(chatName = newText) }

    fun appendBranch() {
        val newList = listOf(*branchArray, "")
        _uiState.update {
            it.copy(textBranches = newList)
        }
    }

    fun removeBranch() {
        val newList = mutableListOf(*branchArray).apply { if(size > 1) removeLastOrNull() }
        _uiState.update { it.copy(textBranches = newList) }
    }

    fun editBranchAtIndex(newText: String, index: Int) {
        if (index > branchArray.lastIndex) return
        val newList = mutableListOf(*branchArray).apply { this[index] = newText }
        _uiState.update { it.copy(textBranches = newList) }
    }

    fun sendPureText() {
        val newPureText = Talk.PureText(
            talker = _uiState.value.currentStudent,
            text = _uiState.value.text,
            isFirst = _uiState.value.isFirstTalking
        )
        talkList.add(newPureText)

        _uiState.update {
            it.copy(
                text = "",
                isFirstTalking = false,
                talkListStateChange = listOf(TalkListState.Push()),
                isEdited = true
            )
        }
    }

    fun sendPureText(index: Int) {
        val newPureText = Talk.PureText(
            talker = _uiState.value.currentStudent,
            text = _uiState.value.text,
            isFirst = true
        )
        talkList.add(index, newPureText)

        val talkListChanges = mutableListOf<TalkListState>(TalkListState.Inserted(index))
        // 处理上下文。插入只影响本次和之后
        // 如果插入后，插入处有上文，且说话人和本次相同，则设isFirst=false
        if (index > 0) {
            talkList[index - 1].getTalker()?.let {
                newPureText.isFirst = newPureText.talker != it
            }
        }

        // 插入必有下文，下文和插入不是一个说话人，且下文isFirstTalking=false。则需要下文isFirstTalking变为true;
        // 下文和插入是一个说话人，且下文isFirstTalking=true。则下文isFirstTalking需要变为false。
        val nextTalk = talkList[index + 1]
        if (nextTalk is Talk.PureText) {
            if ((nextTalk.talker == newPureText.talker) == nextTalk.isFirst) {
                // 直接改原引用的isFirst也能重组，符合预期效果，但不符合重组原理（引用没变却触发了重组）
                val newNextTalk = Talk.PureText(
                    nextTalk.talker,
                    nextTalk.text,
                    !nextTalk.isFirst
                )
                talkList[index + 1] = newNextTalk
                talkListChanges.add(TalkListState.Modified(index + 1))
            }
        } else if (nextTalk is Talk.Photo) {
            if ((nextTalk.talker == newPureText.talker) == nextTalk.isFirst) {
                // 这里必须做一个新的Talk.Photo出来，否则notifyItemChanged不会触发子项ComposeView重组
                val newNextTalk = Talk.Photo(
                    nextTalk.talker,
                    nextTalk.uri,
                    !nextTalk.isFirst
                )
                talkList[index + 1] = newNextTalk
                talkListChanges.add(TalkListState.Modified(index + 1))
            }
        }

        _uiState.update {
            it.copy(
                text = "",
                talkListStateChange = talkListChanges,
                isEdited = true
            )
        }
    }

    fun sendPhoto(uri: String) {
        viewModelScope.launch {
            Log.d(TAG, "sendPhoto: current thread is ${Thread.currentThread().name}")
            val newPhoto = Talk.Photo(
                talker = _uiState.value.currentStudent,
                uri = if (mmkv.decodeBool(PreferenceKeys.USE_BASE64, false)) ImageUtils.convertImageToBase64(uri)!! else uri,
                isFirst = _uiState.value.isFirstTalking
            )
            talkList.add(newPhoto)

            _uiState.update {
                it.copy(
                    isFirstTalking = false,
                    talkListStateChange = listOf(TalkListState.Push()),
                    isEdited = true
                )
            }
        }
    }

    fun sendPhoto(uri: String, index: Int) {
        viewModelScope.launch {
            val newPhoto = Talk.Photo(
                talker = _uiState.value.currentStudent,
                uri = if (mmkv.decodeBool(PreferenceKeys.USE_BASE64, false)) ImageUtils.convertImageToBase64(uri)!! else uri,
                isFirst = true
            )
            talkList.add(index, newPhoto)

            val talkListChanges = mutableListOf<TalkListState>(TalkListState.Inserted(index))
            // 处理上下文。插入只影响本次和之后
            // 如果插入后，插入处有上文，且说话人和本次相同，则设isFirst=false
            if (index > 0) {
                val lastTalk = talkList[index - 1]
                lastTalk.getTalker()?.let {
                    newPhoto.isFirst = newPhoto.talker != it
                }
            }
            // 插入必有下文，如果下文说话人和本次相同，则设下文isFirst=false
            // TODO 这里理论上要和SendPureText作相同修改，但是实测没出现问题，不过还是改了
            val nextTalk = talkList[index + 1]
            if (nextTalk is Talk.PureText) {
                if ((nextTalk.talker == newPhoto.talker) == nextTalk.isFirst) {
                    // 直接改原引用的isFirst也能重组，符合预期效果，但不符合重组原理（引用没变却触发了重组）
                    val newNextTalk = Talk.PureText(
                        nextTalk.talker,
                        nextTalk.text,
                        !nextTalk.isFirst
                    )
                    talkList[index + 1] = newNextTalk
                    talkListChanges.add(TalkListState.Modified(index + 1))
                }
            } else if (nextTalk is Talk.Photo) {
                if ((nextTalk.talker == newPhoto.talker) == nextTalk.isFirst) {
                    // 这里必须做一个新的Talk.Photo出来，否则notifyItemChanged不会触发子项ComposeView重组
                    val newNextTalk = Talk.Photo(
                        nextTalk.talker,
                        nextTalk.uri,
                        !nextTalk.isFirst
                    )
                    talkList[index + 1] = newNextTalk
                    talkListChanges.add(TalkListState.Modified(index + 1))
                }
            }

            _uiState.update {
                it.copy(
                    talkListStateChange = talkListChanges,
                    isEdited = true
                )
            }
        }
    }

    fun sendLoveScene() {
        val newLoveScene = Talk.LoveScene(
            studentName = _uiState.value.currentStudent.name
        )
        talkList.add(newLoveScene)

        _uiState.update {
            it.copy(
                isFirstTalking = true,
                talkListStateChange = listOf(TalkListState.Push()),
                isEdited = true
            )
        }
    }

    fun sendLoveScene(index: Int) {
        val newLoveScene = Talk.LoveScene(
            studentName = _uiState.value.currentStudent.name
        )
        talkList.add(index, newLoveScene)

        // 特殊题材插入：对上文无影响，而一定使下文图文变为firstTalking
        copyAndSetFirst(index + 1, true)

        _uiState.update {
            it.copy(
                talkListStateChange = listOf(TalkListState.Inserted(index), TalkListState.Modified(index + 1)),
                isEdited = true
            )
        }
    }

    fun sendNarration() {
        val newNarration = Talk.Narration(
            text = _uiState.value.narrationText
        )
        talkList.add(newNarration)

        _uiState.update {
            it.copy(
                isFirstTalking = true,
                narrationText = "",
                talkListStateChange = listOf(TalkListState.Push()),
                isEdited = true
            )
        }
    }

    fun sendNarration(index: Int) {
        val newNarration = Talk.Narration(
            text = _uiState.value.narrationText
        )
        talkList.add(index, newNarration)

        // 特殊题材插入：对上文无影响，而一定使下文图文变为firstTalking
        copyAndSetFirst(index + 1, true)

        _uiState.update {
            it.copy(
                narrationText = "",
                talkListStateChange = listOf(TalkListState.Inserted(index), TalkListState.Modified(index + 1)),
                isEdited = true
            )
        }
    }

    fun sendBranches() {
        val newBranches = Talk.Branch(
            textOptions = _uiState.value.textBranches
        )
        talkList.add(newBranches)

        _uiState.update {
            it.copy(
                isFirstTalking = true,
                textBranches = listOf(""),
                talkListStateChange = listOf(TalkListState.Push()),
                isEdited = true
            )
        }
    }

    fun sendBranches(index: Int) {
        val newBranches = Talk.Branch(
            textOptions = _uiState.value.textBranches
        )
        talkList.add(index, newBranches)

        // 特殊题材插入：对上文无影响，而一定使下文图文变为firstTalking
        copyAndSetFirst(index + 1, true)

        _uiState.update {
            it.copy(
                textBranches = listOf(""),
                talkListStateChange = listOf(TalkListState.Inserted(index), TalkListState.Modified(index + 1)),
                isEdited = true
            )
        }
    }

    fun removeTalkPiece(index: Int) {
        talkList.removeAt(index)

        // 处理上下文: 删除只影响下文
        // 如果删除位置为首位，检查删除后首位说话人，设其isFirst=true
        // 如果删除位置为末尾，直接删除即可
        // 如果删除位置在中间，检查若删除后上下文说话人相同，设置下文说话人isFirst=false,否则设为true，更新下文
        // 如果删除的是唯一一条消息，设置isFirstTalking=true
        if (index == 0) {
            if (talkList.isEmpty()) {
                _uiState.update { it.copy(isFirstTalking = true) }
            } else {
                copyAndSetFirst(0, true)
            }
        } else if (index < talkList.size) {
            val nextTalk = talkList[index]
            if (nextTalk.hasTalker()) {
                val lastTalk = talkList[index - 1]
                copyAndSetFirst(index, lastTalk.getTalker() != nextTalk.getTalker())
            }
        }

        val talkListChanges = mutableListOf(
            TalkListState.Removed(index),
            TalkListState.Modified(index)
        )
        _uiState.update {
            it.copy(
                isEdited = true,
                talkListStateChange = talkListChanges
            )
        }
    }

    fun editTalkHistory(talk: Talk, index: Int) {
        viewModelScope.launch {
            talkList[index] = talk
            val talkListChanges = mutableListOf(TalkListState.Modified(index))
            // 考虑上下文
            if (talk is Talk.PureText) {
                // 如有上文，当前isFirstTalking为!= lastCharacter
                if (index > 0) {
                    val lastTalker = talkList[index - 1].getTalker()
                    lastTalker?.let {
                        talk.isFirst = talk.talker != it
                        talkListChanges.add(TalkListState.Modified(index - 1))
                    }
                }
                // 如有下文，顺便修改下文isFirstTalking为!= currentCharacter
                if (index < talkList.lastIndex) {
                    copyAndSetFirst(index + 1, talkList[index + 1].getTalker() != talk.talker)
                    talkListChanges.add(TalkListState.Modified(index + 1))
                }  else {
                    // 若为修改末尾消息，判断下一条消息是否为学生首次
                    _uiState.update {
                        it.copy(isFirstTalking = talk.talker != _uiState.value.currentStudent)
                    }
                }
            } else if (talk is Talk.Photo) {
                if (mmkv.decodeBool(PreferenceKeys.USE_BASE64)) talk.uri = ImageUtils.convertImageToBase64(talk.uri)!!

                // 如有上文，当前isFirstTalking为!= lastCharacter
                if (index > 0) {
                    val lastTalker = talkList[index - 1].getTalker()
                    lastTalker?.let {
                        talk.isFirst = talk.talker != it
                        talkListChanges.add(TalkListState.Modified(index - 1))
                    }
                }
                // 如有下文，顺便修改下文isFirstTalking为!= currentCharacter
                if (index < talkList.lastIndex) {
                    copyAndSetFirst(index + 1, talkList[index + 1].getTalker() != talk.talker)
                    talkListChanges.add(TalkListState.Modified(index + 1))
                }  else {
                    // 若为修改末尾消息，判断下一条消息是否为学生首次
                    _uiState.update {
                        it.copy(isFirstTalking = talk.talker != _uiState.value.currentStudent)
                    }
                }
            }

            _uiState.update {
                it.copy(
                    isEdited = true,
                    talkListStateChange = talkListChanges
                )
            }
        }
    }

    fun updateToolVision() {
        _uiState.update { it.copy(isMoreToolsOpen = !it.isMoreToolsOpen) }
    }

    fun selectStudent(student: Character) {
        val lastTalker = talkList.lastOrNull()?.getTalker()
        _uiState.update { it.copy(currentStudent = student, isFirstTalking = student != lastTalker) }
    }

    fun addStudent(student: Character) {
        viewModelScope.launch {
            if (mmkv.decodeBool(PreferenceKeys.USE_BASE64, false)) {
                student.currentAvatar = ImageUtils.convertImageToBase64(student.currentAvatar)!!
            }
            studentList.add(student)
        }
    }

    fun removeStudent(student: Character) {
        val index = studentList.indexOf(student)
        if (index != -1) {
            studentList.removeAt(index)
            if (_uiState.value.currentStudent == student) {
                _uiState.update { it.copy(currentStudent = studentList[index - 1]) }
            }
        }
    }

    fun updateStudent(student: Character) {
        viewModelScope.launch {
            // 由于Character的equals被重写为名字相同则视为相同，无法感知头像变化，需要手动刷新
            val index = studentList.indexOf(student)
            if (index != -1) {
                if (mmkv.decodeBool(PreferenceKeys.USE_BASE64, false)) {
                    student.currentAvatar = ImageUtils.convertImageToBase64(student.currentAvatar)!!
                }
                studentList[index] = student
            }
        }
    }

    fun clearTalk() {
        talkList.clear()
        _uiState.update { it.copy(
            isFirstTalking = true,
            isEdited = true,
            talkListStateChange = listOf(TalkListState.Refresh())
        ) }
    }

    fun resetListStateChange() {
        _uiState.update { it.copy(
            talkList = talkList,
            talkListStateChange = listOf(TalkListState.Initialized()))
        }
    }

    fun updateProjectTitle(title: String) {
        // 更新当前项目标题
        if (title != _uiState.value.chatName) {
            _uiState.update {
                it.copy(
                    chatName = title,
                    isEdited = true,
                )
            }
        }
    }

    fun saveProject() {
        // 如果当前项目已存在则更新，没有则新建
        viewModelScope.launch(Dispatchers.IO) {
            // 如果当前输入分组不存在，则新建该名称分组，否则将当前项目加入目的分组
            val targetFolderName =  _uiState.value.inputFolderName
            if (targetFolderName != "") {
                val targetFolder = _uiState.value.allFolders.firstOrNull { it.name == targetFolderName }
                if (targetFolder == null) {
                    val folder = TalkFolder(name = targetFolderName)
                    folderId = repository.addFolder(folder).toInt()
                } else {
                    folderId = targetFolder.id
                }
            }

            if (projectId == -1) {
                val currentProject = TalkProject(
                    name = _uiState.value.chatName,
                    talkHistory = _uiState.value.talkList,
                    studentList = _uiState.value.studentList,
                    currentStudent = _uiState.value.currentStudent,
                    isFirstTalking = _uiState.value.isFirstTalking,
                    folderId = folderId,
                )
                projectId = repository.addProject(currentProject).toInt()
            } else {
                val currentProject = TalkProject(
                    id = projectId,
                    name = _uiState.value.chatName,
                    talkHistory = talkList,
                    studentList = _uiState.value.studentList,
                    currentStudent = _uiState.value.currentStudent,
                    isFirstTalking = _uiState.value.isFirstTalking,
                    folderId = folderId,
                )
                repository.updateProject(currentProject)
            }
            _uiState.update { it.copy(isEdited = false)}
            if (mmkv.decodeBool(PreferenceKeys.USE_AUTO_SAVE, false)) {
                saveTalkAsJson()
            }
        }
    }

    fun isHistoryTalk() = projectId >= 0

    fun saveTalkAsJson() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentProject = TalkProject(
                name = _uiState.value.chatName,
                talkHistory = talkList,
                studentList = _uiState.value.studentList,
                currentStudent = _uiState.value.currentStudent,
                isFirstTalking = _uiState.value.isFirstTalking
            )
            val jsonString = Json.encodeToString(currentProject)
            val jsonFileRoot = File(
                mmkv.decodeString(PreferenceKeys.FILE_EXPORT_PATH) ?: PathUtils.getFileFallbackExportDir().absolutePath
            ).apply {
                if (!exists()) mkdirs()
            }
            val jsonFile = File(jsonFileRoot, "${_uiState.value.chatName}.json").apply {
                if (!exists()) createNewFile()
            }
            jsonFile.writeText(jsonString)
        }
    }

    fun shareTalkAsJson(): Uri {
        val currentProject = TalkProject(
            name = _uiState.value.chatName,
            talkHistory = talkList,
            studentList = _uiState.value.studentList,
            currentStudent = _uiState.value.currentStudent,
            isFirstTalking = _uiState.value.isFirstTalking
        )
        val jsonString = Json.encodeToString(currentProject)
        val jsonFileRoot = File(PathUtils.getFileFallbackExportDir().absolutePath).apply {
            if (!exists()) mkdirs()
        }
        val jsonFile = File(jsonFileRoot, "${_uiState.value.chatName}.json").apply {
            if (!exists()) createNewFile()
        }
        viewModelScope.launch(Dispatchers.IO) {
            jsonFile.writeText(jsonString)
        }
        return FileProvider.getUriForFile(
            YuukaTalkApplication.context,
            "com.eynnzerr.yuukatalk.file_provider",
            jsonFile
        )
    }

    private fun Talk.hasTalker() = this is Talk.PureText || this is Talk.Photo
    private fun Talk.getTalker() = if (this is Talk.PureText) this.talker else if (this is Talk.Photo) this.talker else null

    // 深拷贝置换talkList中某项并设置其isFirst属性（如果为text、photo）。
    private fun copyAndSetFirst(index: Int, isFirst: Boolean): Boolean {
        if (index !in talkList.indices) return false

        val talkPiece = talkList[index]
        if (talkPiece is Talk.PureText) {
            talkList[index] = Talk.PureText(
                talker = talkPiece.talker,
                text = talkPiece.text,
                isFirst = isFirst
            )
            return isFirst != talkPiece.isFirst
        } else if (talkPiece is Talk.Photo) {
            talkList[index] = Talk.Photo(
                talker = talkPiece.talker,
                uri = talkPiece.uri,
                isFirst = isFirst
            )
            return isFirst != talkPiece.isFirst
        }
        return false
    }

    // search bar related
    private fun filterCharacters() {
        var result = listOf(*allStudents.toTypedArray()) // deep copy
        if (stateValue.searchText != "") {
            result = result.filter { student ->
                student.name.contains(stateValue.searchText, ignoreCase = true) ||
                        student.nameRoma.contains(stateValue.searchText, ignoreCase = true) ||
                        student.school.contains(stateValue.searchText, ignoreCase = true)
            }
        }
        if (stateValue.filterSchoolName != "All") {
            result = result.filter { student ->
                student.school == stateValue.filterSchoolName
            }
        }
        if (!stateValue.showBundled) {
            result = result.filter { !it.isAsset }
        }
        if (!stateValue.showDIY) {
            result = result.filter { it.isAsset }
        }
        _uiState.update { it.copy(filteredStudents = result) }
    }

    fun updateSearchText(query: String) {
        _uiState.update {
            it.copy(
                searchText = query,
            )
        }
        filterCharacters()
    }

    fun updateFilterSchool(school: String) {
        _uiState.update {
            it.copy(filterSchoolName = school)
        }
        filterCharacters()
    }

    fun switchShowBundled() {
        _uiState.update {
            it.copy(showBundled = !stateValue.showBundled)
        }
        filterCharacters()
    }

    fun switchShowDIY() {
        _uiState.update { it.copy(showDIY = !stateValue.showDIY) }
        filterCharacters()
    }

    fun isSaveReminderEnabled() = mmkv.decodeBool(PreferenceKeys.USE_SAVE_CONFIRM, true)

    fun updateFolderName(name: String) {
        _uiState.update {
            it.copy(inputFolderName = name)
        }
    }
}

private const val TAG = "TalkViewModel"