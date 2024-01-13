package com.eynnzerr.yuukatalk.ui.page.split

import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.dialog.RegionPickerDialog
import com.eynnzerr.yuukatalk.ui.view.AdapterWorkingState
import com.eynnzerr.yuukatalk.ui.view.TalkAdapter
import com.eynnzerr.yuukatalk.utils.ImageUtils
import com.eynnzerr.yuukatalk.utils.SplitRelay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitPage(
    navHostController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var screenshotTalk by remember { mutableStateOf(false) }
    val talkAdapter = remember {
        TalkAdapter(SplitRelay.talkList).apply {
            workingState = AdapterWorkingState.InSplit
        }
    }
    val talkPieceEditState by talkAdapter.talkPieceState.collectAsState()
    var regionStart by remember { mutableIntStateOf(0) }
    var regionEnd by remember { mutableIntStateOf(0) }
    var isNotScrolling by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "已选中${regionEnd - regionStart + 1}条对话内容") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "export split picture"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isNotScrolling,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(text = "起始: $regionStart, 终止: $regionEnd") },
                    icon = { Icon(imageVector = Icons.Filled.Download, contentDescription = "export split pic") },
                    onClick = { screenshotTalk = true }
                )
            }
        }
    ) { scaffoldPadding ->
        AndroidView(
            factory = { context ->
                RecyclerView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    layoutManager = LinearLayoutManager(context)
                    adapter = talkAdapter
                    talkAdapter.layoutManager = layoutManager

                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)
                            isNotScrolling = newState == RecyclerView.SCROLL_STATE_IDLE
                            // TODO 在非Composable代码中修改state的值不会触发重组？
                            Log.d(TAG, "onScrollStateChanged: scroll state changed to $newState, isNotScrolling: $isNotScrolling")
                        }
                    })
                }
            },
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(16.dp),
            update = { view ->
                if (screenshotTalk) {
                    screenshotTalk = false
                    scope.launch(Dispatchers.Main) {
                        val bitmap = ImageUtils.generateBitMapInRange(view, regionStart..regionEnd)
                        val imageUri = withContext(Dispatchers.IO) {
                            ImageUtils.saveBitMapToDisk(bitmap, context)
                        }
                        Toast.makeText(
                            context,
                            context.getText(R.string.toast_export_project).toString() + " ${imageUri.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    // 设置当前点选对话为起点/终点
    if (talkPieceEditState.openEditDialog) {
        RegionPickerDialog(
            onDismissRequest = { talkAdapter.closeDialog() },
            onDismiss = { talkAdapter.closeDialog() },
            onSetStart = {
                regionStart = talkPieceEditState.position
                talkAdapter.closeDialog()
            },
            onSetEnd = {
                regionEnd = talkPieceEditState.position
                talkAdapter.closeDialog()
            }
        )
    }
}

private const val TAG = "SplitPage"