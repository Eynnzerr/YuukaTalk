package com.eynnzerr.yuukatalk.ui.page.settings.dimension

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eynnzerr.yuukatalk.R
import com.eynnzerr.yuukatalk.ui.component.TalkPreview
import com.eynnzerr.yuukatalk.ui.ext.appBarScroll
import com.eynnzerr.yuukatalk.ui.ext.surfaceColorAtElevation
import com.eynnzerr.yuukatalk.utils.DimensionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DimensionPage(
    onBack: () -> Unit
) {
    val dimensionState by DimensionUtils.dimensionState.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    BottomSheetScaffold(
        modifier = Modifier
            .appBarScroll(true, scrollBehavior)
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    elevation = 0.dp,
                    color = MaterialTheme.colorScheme.surface
                )
            ),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.font)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            TalkPreview()
        },
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
        ) {

        }
    }
}