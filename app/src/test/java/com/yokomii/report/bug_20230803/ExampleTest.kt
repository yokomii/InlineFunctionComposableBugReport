package com.yokomii.report.bug_20230803

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class LazyListItemInViewListenerTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun inlineTest() {
        var results = emptyList<Any>()

        composeTestRule.setContent {
            val state = rememberLazyListState()
            LazyColumn(state = state) {
                item(key = "key") {
                    Box(modifier = Modifier.fillMaxWidth())
                }
            }
            PageViewListenerInline(true) { /* no op */ } // diff
            ListItemVisibleListener(state = state) { results = it }
        }

        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeTestRule.waitForIdle()
        Assertions.assertThat(results).containsExactly("key") // fail
    }

    @Test
    fun notInlineTest() {
        var results = emptyList<Any>()

        composeTestRule.setContent {
            val state = rememberLazyListState()
            LazyColumn(state = state) {
                item(key = "key") {
                    Box(modifier = Modifier.fillMaxWidth())
                }
            }
            PageViewListener(true) { /* no op */ } // diff
            ListItemVisibleListener(state = state) { results = it }
        }

        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeTestRule.waitForIdle()
        Assertions.assertThat(results).containsExactly("key") // success
    }
}
