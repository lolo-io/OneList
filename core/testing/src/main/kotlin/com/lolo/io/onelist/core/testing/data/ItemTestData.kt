package com.lolo.io.onelist.core.testing.data

import com.lolo.io.onelist.core.model.Item

val testItemWithComment = Item(
    title = "Item Title",
    comment = "Item Comment",
    commentDisplayed = false,
    done = false,
    id = 1L
)
val testItemCommentDisplayed = testItemWithComment.copy(commentDisplayed = true)
val testItemWithCommentDone = testItemWithComment.copy(done = true)
val testItemWithoutComment = testItemWithComment.copy(comment = "")