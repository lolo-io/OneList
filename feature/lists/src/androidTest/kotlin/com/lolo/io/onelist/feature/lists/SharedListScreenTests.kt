package com.lolo.io.onelist.feature.lists

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
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
import com.lolo.io.onelist.core.testing.core.waitUntilEditBackgroundIsNotDoesNotExist
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.testing.util.assertWaitingNode
import com.lolo.io.onelist.feature.lists.components.list_chips.ListChipState
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestSwipeDeleteItem(itemIndex: Int) {
    waitUntilAtLeastOneExists(hasTestTag(TestTags.SwipeableItem), 10000)

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
    waitUntilAtLeastOneExists(hasTestTag(TestTags.SwipeableItem), 10000)

    val swipeableRow = getSwipeableRow(itemIndex)
    val itemForegroundUI = getItemForegroundUI(itemIndex)

    itemForegroundUI.assertIsDisplayed()
    swipeableRow.performTouchInput { swipeRight() }
    itemForegroundUI.assertIsNotDisplayed()

    checkItemEditBackgroundIsDisplayed()

    val suffixAddedAfterEdit = " Edited"

    onNodeWithTag(TestTags.EditItemDialog).assertIsDisplayed()
    onNodeWithTag(TestTags.EditItemDialogItemTitle).assertTextContains(itemTitle)
    onNodeWithTag(TestTags.EditItemDialogItemComment).assertTextContains(itemComment)
    onNodeWithTag(TestTags.EditItemDialogItemTitle).assertIsFocused()


    onNodeWithTag(TestTags.EditItemDialogItemTitle).performTextInput(suffixAddedAfterEdit)
    onNodeWithTag(TestTags.EditItemDialogItemComment).performTextInput(suffixAddedAfterEdit)
    onNodeWithTag(TestTags.CommonDialogPositiveButton).performClick()

    waitUntilEditBackgroundIsNotDoesNotExist()

    itemForegroundUI.assertIsDisplayed()

    // waitUntilItemTitleIsDisplayed(...)
    waitUntilAtLeastOneExists(
        hasText("$itemTitle$suffixAddedAfterEdit"), 3000
    )

    onAllNodesWithTag(TestTags.ItemUiArrowComment)[itemIndex].performClick()

    // waitUntilItemCommentIsDisplayed(...)
    waitUntilAtLeastOneExists(
        hasText("$itemComment$suffixAddedAfterEdit"), 3000
    )

}


@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestCreateList(listName: String) {

    onNodeWithTag(TestTags.AddListButton).assertIsDisplayed()
    onNodeWithTag(TestTags.AddListButton).performClick()

    onNodeWithTag(TestTags.EditListDialog).assertIsDisplayed()
    onNodeWithTag(TestTags.EditListDialogInput).assertIsDisplayed()

    onNodeWithTag(TestTags.EditListDialogInput).performTextInput(listName)

    onNodeWithTag(TestTags.CommonDialogPositiveButton).performClick()

    testListChipIsShown(listName)

    waitUntilExactlyOneExists(
        hasTestTag(TestTags.listChipLabelState(listName, ListChipState.SELECTED.name))
    )

}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedTestEditList(listName: String, editedListSuffix: String) {

    onNodeWithTag(TestTags.EditListButton).assertDoesNotExist()

    onNodeWithText(listName).performTouchInput { longClick() }

    onNodeWithTag(TestTags.EditListButton).assertIsDisplayed()
    onNodeWithTag(TestTags.EditListButton).performClick()

    onNodeWithTag(TestTags.EditListDialog).assertIsDisplayed()
    onNodeWithTag(TestTags.EditListDialogInput).assertIsDisplayed()
    onNodeWithTag(TestTags.EditListDialogInput).assertTextEquals(listName)

    onNodeWithTag(TestTags.EditListDialogInput).performTextInput(editedListSuffix)


    onNodeWithTag(TestTags.CommonDialogPositiveButton).performClick()

    val newListTitle = listName + editedListSuffix

    waitUntilAtLeastOneExists(
        hasText(newListTitle), 3000
    )
    // waitUntilListChipIsSelected(listName)
    onNodeWithTag(TestTags.listChipLabelState(newListTitle, ListChipState.SELECTED.name))

}

suspend fun printStackTraceOnError(block: suspend () -> Unit) {
    try {
        block()
    } catch (e: AssertionError) {
        throw AssertionError(e.stackTraceToString())
    }
}

@OptIn(ExperimentalTestApi::class)
suspend fun ComposeTestRule.sharedAddItemToList(itemTitle: String, itemComment: String) {
    printStackTraceOnError {
        onNodeWithTag(TestTags.AddItemInput).assertIsDisplayed()

        onNodeWithTag(TestTags.AddItemInput).performTextInput(itemTitle)
        if (itemComment.isNotEmpty()) {
            if (!onNodeWithTag(TestTags.AddItemCommentInput).isDisplayed()) {
                onNodeWithTag(TestTags.AddItemCommentArrowButton).assertIsDisplayed()
                onNodeWithTag(TestTags.AddItemCommentArrowButton).performClick()
            }
            waitUntilExactlyOneExists(hasTestTag(TestTags.AddItemCommentInput), 3000)
            onNodeWithTag(TestTags.AddItemCommentInput).assertIsDisplayed()
            onNodeWithTag(TestTags.AddItemCommentInput).performTextInput(itemComment)
        }

        onNodeWithTag(TestTags.AddItemInputSubmitButton).assertIsDisplayed()
        onNodeWithTag(TestTags.AddItemInputSubmitButton).performClick()

        assertWaitingNode {
            onNodeWithTag(TestTags.AddItemInput).assertTextContains(
                this.activity.getString(R.string.add_item_placeholder)
            )
        }

        waitUntilExactlyOneExists(
            hasText(itemTitle)
        )

        if (itemComment.isNotEmpty()) {
            assertWaitingNode {
                onNodeWithTag(TestTags.AddItemCommentInput).assertTextContains(
                    this.activity.getString(R.string.add_comment_placeholder)
                )
            }


            onNodeWithTag(
                TestTags.itemCommentArrowItemTitle(itemTitle),
                useUnmergedTree = true
            ).assertExists()

            waitUntilAtLeastOneExists(
                hasText(itemComment),
            )
        } else {
            onNodeWithTag(
                TestTags.itemCommentArrowItemTitle(itemTitle),
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }
}


fun ComposeTestRule.sharedCheckListItemOrders(itemTitles: List<String>) {
    onAllNodesWithTag(TestTags.SwipeableItem).assertCountEquals(itemTitles.size)
    // First get all added item nodes
    val nodes = itemTitles.map {
        onNodeWithText(it)
    }
    // Get all nodes positions in root
    val positions = nodes.map {
        it.fetchSemanticsNode().positionInRoot.y
    }
    // Items should be sorted last to first (last item has lower Y, first has highest => sorted descending)
    assertEquals(positions, positions.sortedDescending())
}


@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedDeleteList(listName: String) {
    onNodeWithText(listName).performTouchInput { longClick() }
    onNodeWithTag(TestTags.DeleteListButton).apply {
        assertIsDisplayed()
        performClick()
    }
    onNodeWithTag(TestTags.DeleteListDialog).assertIsDisplayed()
    onNodeWithTag(TestTags.CommonDialogPositiveButton).performClick()
    onNodeWithTag(TestTags.DeleteListDialog).assertIsNotDisplayed()
    waitUntilDoesNotExist(
        hasText(listName)
    )
}

fun ComposeTestRule.sharedCheckIsSelected(listName: String) {
    onNodeWithTag(TestTags.listChipLabelState(listName, ListChipState.SELECTED.name))
        .assertExists()
}

fun ComposeTestRule.sharedCheckIsNotSelected(listName: String) {
    onNodeWithTag(TestTags.listChipLabelState(listName, ListChipState.SELECTED.name))
        .assertDoesNotExist()
}

fun ComposeTestRule.sharedSelectList(listName: String) {
    onNodeWithText(listName).performClick()
    sharedCheckIsSelected(listName)
}

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.sharedJustClearList(listName: String) {
    onAllNodesWithTag(TestTags.SwipeableItem).onFirst().assertExists()
    onNodeWithText(listName).performTouchInput { longClick() }
    onNodeWithTag(TestTags.DeleteListButton).apply {
        assertIsDisplayed()
        performClick()
    }
    onNodeWithTag(TestTags.DeleteListDialog).assertIsDisplayed()
    onNodeWithTag(TestTags.JustClearListButton).performClick()
    onNodeWithTag(TestTags.DeleteListDialog).assertIsNotDisplayed()
    waitUntilDoesNotExist(
        hasTestTag(TestTags.SwipeableItem)
    )
}