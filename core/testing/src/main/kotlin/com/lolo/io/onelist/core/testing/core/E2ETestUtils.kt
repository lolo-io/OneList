package com.lolo.io.onelist.core.testing.core

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.model.ItemList

typealias ComposeTestRule = AndroidComposeTestRule<out ActivityScenarioRule<out ComponentActivity>, out ComponentActivity>

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.testHasOnlyFirstLaunchSizeListsAmountOfLists(firstLaunchLists: List<ItemList>) =
    waitUntilNodeCount(
        hasTestTag("flow_row_item"), firstLaunchLists.size,
        3000
    )

fun ComposeTestRule.testListChipIsShown(itemListTitle: String) =
    onNodeWithText(itemListTitle).assertExists()

fun ComposeTestRule.clickOnListChip(itemListTitle: String) =
    onNodeWithText(itemListTitle).performClick()

fun ComposeTestRule.testItemsListIsScrollable() =
    onNodeWithTag("items_lazy_column").assert(hasScrollAction())

fun ComposeTestRule.scrollToItemIfNecessary(index: Int) =
    onNodeWithTag("items_lazy_column").performScrollToIndex(index)

fun ComposeTestRule.testItemIsInList(item: Item) =
    onNodeWithText(item.title).assertExists()

fun ComposeTestRule.getSwipeableRow(index: Int) =
    onAllNodesWithTag("swipeable_item")[index]

fun ComposeTestRule.getItemForegroundUI(index: Int) =
    onAllNodesWithTag("item-ui-surface")[index]

fun ComposeTestRule.checkItemDeleteBackgroundIsDisplayed() =
    onNodeWithTag("swipeable_item_delete_background")
        .assertIsDisplayed()

fun ComposeTestRule.checkItemEditBackgroundIsDisplayed() =
    onNodeWithTag("swipeable_item_edit_background")
        .assertIsDisplayed()

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilDeleteBackgroundIsNotDisplayed() =
    waitUntilDoesNotExist(
        hasTestTag("swipeable_item_delete_background"),
        10000
    )

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitUntilEditBackgroundIsNotDisplayed() =
    waitUntilDoesNotExist(
        hasTestTag("swipeable_item_edit_background"),
        10000
    )

