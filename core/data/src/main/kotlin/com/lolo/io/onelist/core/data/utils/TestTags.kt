package com.lolo.io.onelist.core.data.utils

object TestTags {


    const val SwipeableItem = "swipeable_item"
    const val EditItemDialog = "edit-item-dialog"
    const val EditItemDialogItemTitle = "edit-item-dialog-item-title"
    const val EditItemDialogItemComment = "edit-item-dialog-item-comment"
    const val SwipeableItemEditBackground = "swipeable_item_edit_background"
    const val SwipeableItemDeleteBackground = "swipeable_item_delete_background"
    const val FlowRowItem = "flow_row_item"
    const val ItemsLazyColumn = "items_lazy_column"
    const val EditListDialog = "edit_list_dialog"
    const val EditListDialogInput = "edit_list_dialog_input"
    const val CommonDialogNegativeButton = "common-dialog-negative-button"
    const val CommonDialogPositiveButton = "common-dialog-positive-button"
    const val EditListButton = "edit_list_button"
    const val ShareListButton = "share_list_button"
    const val DeleteListButton = "delete_list_button"
    const val AddListButton = "add_list_button"
    const val AddListIcon = "add_list_icon"
    const val DeleteListIcon = "delete_list_icon"
    const val ItemUiSurface = "item-ui-surface"
    const val ItemUiTitle = "item-ui-title"
    const val ItemUiArrowComment = "item-ui-arrow-comment"
    const val AddItemInput = "add_item_input"
    const val AddItemCommentInput = "add_item_comment_input"
    const val AddItemCommentArrowButton = "add_item_comment_arrow_button"
    const val AddItemInputSubmitButton = "add_item_input_submit_button"
    fun listChipLabelState(label: String, state: String) = "list_chip_${label}_$state"
    fun listChipLabel(label: String) = "list_chip_${label}"
    fun itemCommentArrowItemTitle(title: String) = "item_comment_arrow_${title}"
}