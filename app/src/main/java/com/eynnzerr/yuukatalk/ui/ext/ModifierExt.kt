package com.eynnzerr.yuukatalk.ui.ext

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.appBarScroll(scrollable: Boolean, topAppBarScrollBehavior: TopAppBarScrollBehavior)
= if (scrollable) Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection) else Modifier

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.autoImePadding(isFocused: Boolean): Modifier = if (isFocused) Modifier.imePadding() else Modifier