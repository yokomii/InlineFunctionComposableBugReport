package com.yokomii.report.bug_20230803

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MainActivity : ComponentActivity()

@Composable
inline fun PageViewListenerInline(
    isContentReady: Boolean,
    crossinline onPageView: () -> Unit,
) {
    var isResumed by remember { mutableStateOf(false) }
    val isPageView = isContentReady && isResumed
    LaunchedEffect(isPageView) {
        if (isPageView) {
            onPageView()
        }
    }
    LifecycleEventHandler { event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> true
            Lifecycle.Event.ON_PAUSE -> false
            else -> null
        }?.let { isResumed = it }
    }
}

@Composable
fun PageViewListener(
    isContentReady: Boolean,
    onPageView: () -> Unit,
) {
    var isResumed by remember { mutableStateOf(false) }
    val isPageView = isContentReady && isResumed
    LaunchedEffect(isPageView) {
        if (isPageView) {
            onPageView()
        }
    }
    LifecycleEventHandler { event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> true
            Lifecycle.Event.ON_PAUSE -> false
            else -> null
        }?.let { isResumed = it }
    }
}

@Composable
fun ListItemVisibleListener(
    state: LazyListState,
    onVisibleItem: (visibleKeys: List<Any>) -> Unit,
) {
    var visibleKeys = remember(state) { emptyList<Any>() }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo }
            .collect { visibleItemsInfo ->
                val current = visibleItemsInfo.map { it.key }
                /* Execute onVisibleItem but omit. */
                visibleKeys = current
            }
    }
    LifecycleEventHandler { event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> onVisibleItem(visibleKeys)
            else -> Unit
        }
    }
}

@Composable
fun LifecycleEventHandler(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (Lifecycle.Event) -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            currentOnEvent(event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
