package com.lolo.io.onelist.core.testing.core

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.data.utils.TestTags

typealias ComposeTestRule = AndroidComposeTestRule<out ActivityScenarioRule<out ComponentActivity>, out ComponentActivity>

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.testHasOnlyFirstLaunchSizeListsAmountOfLists(firstLaunchLists: List<ItemList>) =
    waitUntilNodeCount(
        hasTestTag(TestTags.FlowRowItem), firstLaunchLists.size,
        3000
    )

fun ComposeTestRule.testListChipIsShown(itemListTitle: String) =
    onNodeWithText(itemListTitle).assertExists()

fun ComposeTestRule.clickOnListChip(itemListTitle: String) =
    onNodeWithText(itemListTitle).performClick()

fun ComposeTestRule.testItemsListIsScrollable() =
    onNodeWithTag(TestTags.ItemsLazyColumn).assert(hasScrollAction())

fun ComposeTestRule.scrollToItemIfNecessary(index: Int) =
    onNodeWithTag(TestTags.ItemsLazyColumn).performScrollToIndex(index)

fun ComposeTestRule.testItemIsInList(itemTitle: String) =
    onNodeWithText(itemTitle).assertExists()

fun ComposeTestRule.getSwipeableRow(index: Int) =
    onAllNodesWithTag(TestTags.SwipeableItem)[index]

fun ComposeTestRule.getItemForegroundUI(index: Int) =
    onAllNodesWithTag(TestTags.ItemUiSurface)[index]

fun ComposeTestRule.checkItemDeleteBackgroundIsDisplayed() =
    onNodeWithTag(TestTags.SwipeableItemDeleteBackground)
        .assertIsDisplayed()

fun ComposeTestRule.checkItemEditBackgroundIsDisplayed() =
    onNodeWithTag(TestTags.SwipeableItemEditBackground)
        .assertIsDisplayed()

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilDeleteBackgroundIsNotDisplayed() =
    waitUntilDoesNotExist(
        hasTestTag(TestTags.SwipeableItemDeleteBackground),
        10000
    )

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilEditBackgroundIsNotDoesNotExist() =
    waitUntilDoesNotExist(
        hasTestTag(TestTags.SwipeableItemEditBackground),
        10000
    )

