package com.lolo.io.onelist.feature.lists

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.lolo.io.onelist.core.testing.core.ComposeTestRule
import com.lolo.io.onelist.core.testing.core.checkItemDeleteBackgroundIsDisplayed
import com.lolo.io.onelist.core.testing.core.checkItemEditBackgroundIsDisplayed
import com.lolo.io.onelist.core.testing.core.getItemForegroundUI
import com.lolo.io.onelist.core.testing.core.getSwipeableRow
import com.lolo.io.onelist.core.testing.core.testListChipIsShown
import com.lolo.io.onelist.core.testing.core.waitUntilDeleteBackgroundIsNotDisplayed
import com.lolo.io.onelist.core.testing.core.waitUntilEditBackgroundIsNotDisplayed
import com.lolo.io.onelist.feature.lists.components.list_chips.ListChipState

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestSwipeDeleteItem(itemIndex: Int) {
    waitUntilAtLeastOneExists(hasTestTag("swipeable_item"), 10000)

    val swipeableRow = getSwipeableRow(itemIndex)
    val itemForegroundUI = getItemForegroundUI(itemIndex)

    itemForegroundUI.assertIsDisplayed()
    swipeableRow.performTouchInput { swipeLeft() }
    itemForegroundUI.assertIsNotDisplayed()
    checkItemDeleteBackgroundIsDisplayed()
    // Cancel deletion by swiping back right
    swipeableRow.performTouchInput { swipeRight() }
    itemForegroundUI.assertIsDisplayed()
    // Swipe left again to delete
    swipeableRow.performTouchInput { swipeLeft() }
    waitUntilDeleteBackgroundIsNotDisplayed()
}


@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestSwipeEditItem(
    itemIndex: Int,
    itemTitle: String,
    itemComment: String
) {
    waitUntilAtLeastOneExists(hasTestTag("swipeable_item"), 10000)

    val swipeableRow = getSwipeableRow(itemIndex)
    val itemForegroundUI = getItemForegroundUI(itemIndex)

    itemForegroundUI.assertIsDisplayed()
    swipeableRow.performTouchInput { swipeRight() }
    itemForegroundUI.assertIsNotDisplayed()

    checkItemEditBackgroundIsDisplayed()

    val suffixAddedAfterEdit = " Edited"

    onNodeWithTag("edit-item-dialog").assertIsDisplayed()
    onNodeWithTag("edit-item-dialog-item-title").assertTextContains(itemTitle)
    onNodeWithTag("edit-item-dialog-item-comment").assertTextContains(itemComment)

    onNodeWithTag("edit-item-dialog-item-title").assertIsFocused()

    onNodeWithTag("edit-item-dialog-item-title").performTextInput(suffixAddedAfterEdit)

    onNodeWithTag("edit-item-dialog-item-comment").performTextInput(suffixAddedAfterEdit)

    onNodeWithTag("common-dialog-positive-button").performClick()

    waitUntilEditBackgroundIsNotDisplayed()

    itemForegroundUI.assertIsDisplayed()

    waitUntilAtLeastOneExists(
        hasText("$itemTitle$suffixAddedAfterEdit"), 3000
    )

    onAllNodesWithTag("item-ui-arrow-comment")[itemIndex].performClick()

    waitUntilAtLeastOneExists(
        hasText("$itemComment$suffixAddedAfterEdit"), 3000
    )

}


@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestCreateList(listName: String) {

    onNodeWithTag("add_list_button").assertIsDisplayed()
    onNodeWithTag("add_list_button").performClick()

    onNodeWithTag("edit_list_dialog").assertIsDisplayed()
    onNodeWithTag("edit_list_dialog_input").assertIsDisplayed()

    onNodeWithTag("edit_list_dialog_input").performTextInput(listName)

    onNodeWithTag("common-dialog-positive-button").performClick()

    testListChipIsShown(listName)

    waitUntilExactlyOneExists(
        hasTestTag("list_chip_${listName}_${ListChipState.SELECTED}")
    )

}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestEditList(listName: String, editedListSuffix: String) {

    onNodeWithTag("edit_list_button").assertDoesNotExist()

    onNodeWithText(listName).performTouchInput { longClick() }

    onNodeWithTag("edit_list_button").assertIsDisplayed()
    onNodeWithTag("edit_list_button").performClick()

    onNodeWithTag("edit_list_dialog").assertIsDisplayed()
    onNodeWithTag("edit_list_dialog_input").assertIsDisplayed()
    onNodeWithTag("edit_list_dialog_input").assertTextEquals(listName)

    onNodeWithTag("edit_list_dialog_input").performTextInput(editedListSuffix)


    onNodeWithTag("common-dialog-positive-button").performClick()

    val newListTitle = listName+editedListSuffix

    waitUntilAtLeastOneExists(
        hasText(newListTitle), 3000
    )

    onNodeWithTag("list_chip_${newListTitle}_${ListChipState.SELECTED}").assertIsDisplayed()

}