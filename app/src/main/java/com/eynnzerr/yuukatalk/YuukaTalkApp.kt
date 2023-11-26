package com.eynnzerr.yuukatalk

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eynnzerr.yuukatalk.ui.common.Destinations
import com.eynnzerr.yuukatalk.ui.page.home.HomePage
import com.eynnzerr.yuukatalk.ui.theme.YuukaTalkTheme

@Composable
fun YuukaTalkApp() {
    YuukaTalkTheme {
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
            HomePage()
        }
        animatedComposable(Destinations.HISTORY_ROUTE) {

        }
        animatedComposable(Destinations.TALK_ROUTE) {

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