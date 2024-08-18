package com.eynnzerr.yuukatalk

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.common.LocalTalkPieceProperty
import com.eynnzerr.yuukatalk.ui.common.TalkPieceProperty
import com.eynnzerr.yuukatalk.ui.component.RememberScreenInfo
import com.eynnzerr.yuukatalk.ui.component.ScreenInfo
import com.eynnzerr.yuukatalk.ui.component.VerticalDivider
import com.eynnzerr.yuukatalk.ui.ext.toHexString
import com.eynnzerr.yuukatalk.ui.page.character.CharacterPage
import com.eynnzerr.yuukatalk.ui.page.character.CharacterViewModel
import com.eynnzerr.yuukatalk.ui.page.empty.EmptyPage
import com.eynnzerr.yuukatalk.ui.page.history.HistoryPage
import com.eynnzerr.yuukatalk.ui.page.history.HistoryViewModel
import com.eynnzerr.yuukatalk.ui.page.home.HomePage
import com.eynnzerr.yuukatalk.ui.page.home.HomeViewModel
import com.eynnzerr.yuukatalk.ui.page.read.ReadPage
import com.eynnzerr.yuukatalk.ui.page.settings.SettingsPage
import com.eynnzerr.yuukatalk.ui.page.settings.about.AboutPage
import com.eynnzerr.yuukatalk.ui.page.settings.about.AboutViewModel
import com.eynnzerr.yuukatalk.ui.page.settings.appearance.AppearancePage
import com.eynnzerr.yuukatalk.ui.page.settings.appearance.AppearanceViewModel
import com.eynnzerr.yuukatalk.ui.page.settings.dimension.DimensionPage
import com.eynnzerr.yuukatalk.ui.page.settings.editor_options.EditorOptionsPage
import com.eynnzerr.yuukatalk.ui.page.settings.editor_options.EditorOptionsViewModel
import com.eynnzerr.yuukatalk.ui.page.settings.preview.FontPage
import com.eynnzerr.yuukatalk.ui.page.settings.preview.PreviewViewModel
import com.eynnzerr.yuukatalk.ui.page.split.SplitPage
import com.eynnzerr.yuukatalk.ui.page.talk.TalkPage
import com.eynnzerr.yuukatalk.ui.page.talk.TalkViewModel
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme
import com.eynnzerr.yuukatalk.ui.theme.getTypography
import com.eynnzerr.yuukatalk.utils.AppearanceUtils
import com.eynnzerr.yuukatalk.utils.DimensionUtils

@Composable
fun YuukaTalkApp() {
    val appearanceState by AppearanceUtils.appearanceState.collectAsState()
    val dimensionState by DimensionUtils.dimensionState.collectAsState() // 下一版本使用

    YuukaTalkTheme(
        paletteOption = appearanceState.paletteOption,
        seedColor = Color(appearanceState.assignedSeedColor),
        typography = getTypography(appearanceState.fontResource)
    ) {

        // observe theme change
        val backgroundColor = MaterialTheme.colorScheme.surface.toHexString()
        LaunchedEffect(backgroundColor) {
            AppearanceUtils.updateExportBackground(backgroundColor)
        }

        val screenInfo = RememberScreenInfo()
//        val talkPieceProperty = TalkPieceProperty(
//            avatarSize = 56.dp,
//            nameFontSize = TextUnit.Unspecified,
//            textStartPadding = 64.dp,
//            textFontSize = 16.sp,
//            verticalMargin = 8.dp,
//            photoWidth = 192.dp,
//            narrationPadding = 16.dp,
//            narrationFontSize = 18.sp,
//            branchStartPadding = if (screenInfo.widthType is ScreenInfo.ScreenType.Compact) 72.dp else 288.dp,
//            loveSceneStartPadding = if (screenInfo.widthType is ScreenInfo.ScreenType.Compact) 72.dp else 288.dp,
//        )
        val appNavController = rememberNavController()
        CompositionLocalProvider(LocalTalkPieceProperty provides dimensionState) {
            if (screenInfo.widthType is ScreenInfo.ScreenType.Compact) {
                AppNavGraph(appNavController)
            } else {
                ExpandedAppNavGraph(appNavController)
            }
        }
    }
}

@Composable
private fun ExpandedAppNavGraph(
    appNavController: NavHostController = rememberNavController()
) {
    val startDestination = Destinations.EMPTY_ROUTE
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
        ) {
            HomePage(
                viewModel = hiltViewModel<HomeViewModel>(),
                navController = appNavController
            )
        }
        VerticalDivider(
            modifier = Modifier.fillMaxHeight()
        )
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
        ) {
            NavHost(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                navController = appNavController,
                startDestination = startDestination,
            ) {
                animatedComposable(Destinations.EMPTY_ROUTE) {
                    EmptyPage()
                }
                animatedComposable(Destinations.HISTORY_ROUTE) {
                    val historyViewModel = hiltViewModel<HistoryViewModel>()
                    HistoryPage(historyViewModel, appNavController)
                }
                animatedComposable(
                    route = Destinations.TALK_ROUTE + "/{id}",
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    ),
                ) {
                    val talkViewModel = hiltViewModel<TalkViewModel>().apply {
                        val id = it.arguments?.getInt("id") ?: -1
                        setProjectId(id)
                    }
                    TalkPage(talkViewModel, appNavController)
                }
                animatedComposable(Destinations.CHARACTER_ROUTE) {
                    val characterViewModel = hiltViewModel<CharacterViewModel>()
                    CharacterPage(characterViewModel, appNavController)
                }
                animatedComposable(Destinations.SETTINGS_ROUTE) {
                    SettingsPage(appNavController)
                }
                animatedComposable(Destinations.EDITOR_OPTIONS_ROUTE) {
                    val editorOptionsViewModel = hiltViewModel<EditorOptionsViewModel>()
                    EditorOptionsPage(appNavController, editorOptionsViewModel)
                }
                animatedComposable(Destinations.APPEARANCE_ROUTE) {
                    val appearanceViewModel = hiltViewModel<AppearanceViewModel>()
                    AppearancePage(appearanceViewModel, appNavController)
                }
                animatedComposable(Destinations.LANGUAGE_ROUTE) {

                }
                animatedComposable(Destinations.ABOUT_ROUTE) {
                    val aboutViewModel = hiltViewModel<AboutViewModel>()
                    AboutPage(aboutViewModel, appNavController)
                }
                animatedComposable(Destinations.FONT_ROUTE) {
                    val previewViewModel = hiltViewModel<PreviewViewModel>()
                    FontPage(
                        viewModel = previewViewModel,
                        onBack = { appNavController.popBackStack() }
                    )
                }
                animatedComposable(
                    route = Destinations.SPLIT_ROUTE,
                ) {
                    SplitPage(navHostController = appNavController)
                }
                animatedComposable(
                    route = Destinations.READ_ROUTE,
                ) {
                    ReadPage(navHostController = appNavController)
                }
                animatedComposable(
                    route = Destinations.DIMENSION_ROUTE,
                ) {
                    DimensionPage {
                        appNavController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNavGraph(
    appNavController: NavHostController = rememberNavController()
) {
    val startDestination = Destinations.HOME_ROUTE
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        navController = appNavController,
        startDestination = startDestination,
    ) {
        animatedComposable(Destinations.EMPTY_ROUTE) {
            EmptyPage()
        }
        animatedComposable(Destinations.HOME_ROUTE) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomePage(homeViewModel, appNavController)
        }
        animatedComposable(Destinations.HISTORY_ROUTE) {
            val historyViewModel = hiltViewModel<HistoryViewModel>()
            HistoryPage(historyViewModel, appNavController)
        }
        animatedComposable(
            route = Destinations.TALK_ROUTE + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
        ) {
            val talkViewModel = hiltViewModel<TalkViewModel>().apply {
                val id = it.arguments?.getInt("id") ?: -1
                setProjectId(id)
            }
            TalkPage(talkViewModel, appNavController)
        }
        animatedComposable(Destinations.CHARACTER_ROUTE) {
            val characterViewModel = hiltViewModel<CharacterViewModel>()
            CharacterPage(characterViewModel, appNavController)
        }
        animatedComposable(Destinations.SETTINGS_ROUTE) {
            SettingsPage(appNavController)
        }
        animatedComposable(Destinations.EDITOR_OPTIONS_ROUTE) {
            val editorOptionsViewModel = hiltViewModel<EditorOptionsViewModel>()
            EditorOptionsPage(appNavController, editorOptionsViewModel)
        }
        animatedComposable(Destinations.APPEARANCE_ROUTE) {
            val appearanceViewModel = hiltViewModel<AppearanceViewModel>()
            AppearancePage(appearanceViewModel, appNavController)
        }
        animatedComposable(Destinations.LANGUAGE_ROUTE) {

        }
        animatedComposable(Destinations.ABOUT_ROUTE) {
            val aboutViewModel = hiltViewModel<AboutViewModel>()
            AboutPage(aboutViewModel, appNavController)
        }
        animatedComposable(Destinations.FONT_ROUTE) {
            val previewViewModel = hiltViewModel<PreviewViewModel>()
            FontPage(
                viewModel = previewViewModel,
                onBack = { appNavController.popBackStack() }
            )
        }
        animatedComposable(
            route = Destinations.SPLIT_ROUTE,
        ) {
            SplitPage(navHostController = appNavController)
        }
        animatedComposable(
            route = Destinations.READ_ROUTE,
        ) {
            ReadPage(navHostController = appNavController)
        }
        animatedComposable(
            route = Destinations.DIMENSION_ROUTE,
        ) {
            DimensionPage {
                appNavController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    exitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    popEnterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    popExitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    content = content
)