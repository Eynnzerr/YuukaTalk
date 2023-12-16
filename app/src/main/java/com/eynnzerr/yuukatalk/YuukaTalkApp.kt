package com.eynnzerr.yuukatalk

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.ext.toHexString
import com.eynnzerr.yuukatalk.ui.page.character.CharacterPage
import com.eynnzerr.yuukatalk.ui.page.character.CharacterViewModel
import com.eynnzerr.yuukatalk.ui.page.history.HistoryPage
import com.eynnzerr.yuukatalk.ui.page.history.HistoryViewModel
import com.eynnzerr.yuukatalk.ui.page.home.HomePage
import com.eynnzerr.yuukatalk.ui.page.home.HomeViewModel
import com.eynnzerr.yuukatalk.ui.page.settings.SettingsPage
import com.eynnzerr.yuukatalk.ui.page.settings.about.AboutPage
import com.eynnzerr.yuukatalk.ui.page.settings.appearance.AppearancePage
import com.eynnzerr.yuukatalk.ui.page.settings.appearance.AppearanceViewModel
import com.eynnzerr.yuukatalk.ui.page.settings.editor_options.EditorOptionsPage
import com.eynnzerr.yuukatalk.ui.page.settings.editor_options.EditorOptionsViewModel
import com.eynnzerr.yuukatalk.ui.page.settings.preview.PreviewPage
import com.eynnzerr.yuukatalk.ui.page.settings.preview.PreviewViewModel
import com.eynnzerr.yuukatalk.ui.page.talk.TalkPage
import com.eynnzerr.yuukatalk.ui.page.talk.TalkViewModel
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme
import com.eynnzerr.yuukatalk.ui.theme.getTypography
import com.eynnzerr.yuukatalk.utils.AppearanceUtils

@Composable
fun YuukaTalkApp() {
    val appearanceState by AppearanceUtils.appearanceState.collectAsState()
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

        AppNavGraph()
    }
}

@Composable
private fun AppNavGraph() {
    val appNavController = rememberNavController()
    val startDestination = Destinations.HOME_ROUTE
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        navController = appNavController,
        startDestination = startDestination,
    ) {
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
            AboutPage(appNavController)
        }
        animatedComposable(Destinations.PREVIEW_ROUTE) {
            val previewViewModel = hiltViewModel<PreviewViewModel>()
            PreviewPage(
                viewModel = previewViewModel,
                onBack = { appNavController.popBackStack() }
            )
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