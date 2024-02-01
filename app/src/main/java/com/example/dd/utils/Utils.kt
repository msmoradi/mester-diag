package com.example.dd.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> Flow<T>.collectAsEvent(
    key: Any = Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend (T) -> Unit,
) {
    LaunchedEffect(key) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectAsEvent.collectLatest {
                    block.invoke(it)
                }
            } else {
                withContext(context) {
                    this@collectAsEvent.collectLatest {
                        block.invoke(it)
                    }
                }
            }
        }
    }
}