package com.lolo.io.onelist.feature.lists.components.items_lists

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.performClick
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.testing.core.ComposeTest
import com.lolo.io.onelist.core.testing.data.testItemCommentDisplayed
import com.lolo.io.onelist.core.testing.data.testItemWithComment
import com.lolo.io.onelist.core.testing.data.testItemWithCommentDone
import com.lolo.io.onelist.core.testing.data.testItemWithoutComment
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemUITest : ComposeTest() {
    @Test
    fun testItemUI_NormalState() {
        val testItem = testItemWithComment
        setupAndRunCommonTests(testItem)
        assertTextIsNotShown(testItem.comment)
    }

    @Test
    fun testItemUI_CommentDisplayed() {
        val testItem = testItemCommentDisplayed
        setupAndRunCommonTests(testItemCommentDisplayed)
        assertTextIsShown(testItem.comment)
    }

    @Test
    fun testItemUI_Done() {
        val testItem = testItemWithCommentDone
        setupAndRunCommonTests(testItem)
        assertTextIsNotShown(testItem.comment)
    }

    @Test
    fun testItemUI_NoComment() {
        val testItem = testItemWithoutComment
        setupAndRunCommonTests(testItem)

        assertTextIsNotShown(testItem.comment)
        assertNodeTagIsNotShown(TAG_ITEM_UI_ARROW_COMMENT)
    }

    private fun setupAndRunCommonTests(
        item: Item,
    ) {
        var itemClicked = false
        var displayCommentClicked = false

        composeTestRule.setContent {
            ItemUI(
                item = item,
                onClick = { itemClicked = true },
                onClickDisplayComment = { displayCommentClicked = true }
            )
        }

        assertNodeTagIsShown(TAG_ITEM_UI_SURFACE)
            .assertHasClickAction()
            .performClick()

        assertTrue(itemClicked, "Item has not been clicked")
        assertFalse(displayCommentClicked,
            "Display comment arrow should not have been clicked")

        assertTextIsShown(item.title)

        // reset item clicked boolean for next assertion
        itemClicked = false

        if(item.comment.isNotEmpty()) {
            assertNodeTagIsShown(TAG_ITEM_UI_ARROW_COMMENT)
                .assertHasClickAction()
                .performClick()

            assertTrue(displayCommentClicked, "Display comment arrow not been clicked")
            assertFalse(itemClicked, "Item should not have been clicked")
        }
    }

    companion object {
        private const val TAG_ITEM_UI_SURFACE = "item-ui-surface"
        private const val TAG_ITEM_UI_ARROW_COMMENT = "item-ui-arrow-comment"
    }
}