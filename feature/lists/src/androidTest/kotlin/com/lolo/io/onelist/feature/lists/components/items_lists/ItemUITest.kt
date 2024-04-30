package com.lolo.io.onelist.feature.lists.components.items_lists

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.performClick
import com.lolo.io.onelist.core.data.utils.TestTags
import com.lolo.io.onelist.core.designsystem.OneListTheme
import com.lolo.io.onelist.core.model.Item
import com.lolo.io.onelist.core.testing.core.AbstractComposeTest
import com.lolo.io.onelist.core.testing.data.testItemCommentDisplayed
import com.lolo.io.onelist.core.testing.data.testItemWithComment
import com.lolo.io.onelist.core.testing.data.testItemWithCommentDone
import com.lolo.io.onelist.core.testing.data.testItemWithoutComment
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemUITest : AbstractComposeTest(
    ComponentActivity::class.java
) {
    @Test
    fun itemUI_NormalState() {
        val testItem = testItemWithComment
        setupAndRunCommonTests(testItem)
        assertTextIsNotShown(testItem.comment)
    }

    @Test
    fun itemUI_CommentDisplayed() {
        val testItem = testItemCommentDisplayed
        setupAndRunCommonTests(testItemCommentDisplayed)
        assertTextIsShown(testItem.comment)
    }

    @Test
    fun itemUI_Done() {
        val testItem = testItemWithCommentDone
        setupAndRunCommonTests(testItem)
        assertTextIsNotShown(testItem.comment)
    }

    @Test
    fun itemUI_NoComment() {
        val testItem = testItemWithoutComment
        setupAndRunCommonTests(testItem)

        assertTextIsNotShown(testItem.comment)
        assertNodeTagIsNotShown(TestTags.ItemUiArrowComment)
    }

    private fun setupAndRunCommonTests(
        item: Item,
    ) {
        var itemClicked = false
        var displayCommentClicked = false

        composeTestRule.setContent {
            OneListTheme {
                ItemUI(
                    item = item,
                    onClick = { itemClicked = true },
                    onClickDisplayComment = { displayCommentClicked = true }
                )
            }
        }

        assertNodeTagIsShown(TestTags.ItemUiSurface)
            .assertHasClickAction()
            .performClick()

        assertTrue(itemClicked, "Item has not been clicked")
        assertFalse(displayCommentClicked,
            "Display comment arrow should not have been clicked")

        assertTextIsShown(item.title)

        // reset item clicked boolean for next assertion
        itemClicked = false

        if(item.comment.isNotEmpty()) {
            assertNodeTagIsShown(TestTags.ItemUiArrowComment)
                .assertHasClickAction()
                .performClick()

            assertTrue(displayCommentClicked, "Display comment arrow not been clicked")
            assertFalse(itemClicked, "Item should not have been clicked")
        }
    }
}