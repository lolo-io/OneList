package com.lolo.io.onelist.core.testing.data

import com.lolo.io.onelist.core.model.Item

val testItemWithComment
    get() = Item(
    title = "Test Item",
    comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut eu felis non enim ornare placerat at quis lorem. Nam vel ligula ligula. Aenean convallis magna eu lacus cursus, id tempor ex malesuada.",
    commentDisplayed = false,
    done = false,
    id = 1L
)
val testItemCommentDisplayed
    get() =testItemWithComment.copy(commentDisplayed = true)
val testItemWithCommentDone
    get() =testItemWithComment.copy(done = true)
val testItemWithoutComment
    get() =testItemWithComment.copy(comment = "")