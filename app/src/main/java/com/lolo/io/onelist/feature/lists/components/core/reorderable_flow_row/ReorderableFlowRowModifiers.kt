package com.lolo.io.onelist.feature.lists.components.core.reorderable_flow_row

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moveItemToLeftOf
import moveItemToRightOf
import kotlin.math.roundToInt

class ReorderableFlowRowItem<T>(
    val item: T,
) {
    var bounds: Rect = Rect.Zero
        private set
    var startHitBox: Rect = Rect.Zero
        private set
    var endHitBox: Rect = Rect.Zero
        private set
    val center
        get() = bounds.center

    fun setBounds(
        value: Rect
    ) {
        bounds = value
        startHitBox =
            Rect(value.left, value.top, (value.left + value.width / 2.0f), value.bottom)
        endHitBox =
            Rect((value.right - value.width / 2.0f), value.top, value.right, value.bottom)
    }
}

class ReorderableFlowRowState<T>(
    private val data: MutableState<List<ReorderableFlowRowItem<T>>>,
    val onListReordered: (List<T>, draggedItem: ReorderableFlowRowItem<T>) -> Unit
) {

    private val _draggedItem: MutableState<ReorderableFlowRowItem<T>?> = mutableStateOf(null)
    private val _dragOffset: MutableState<Offset> = mutableStateOf(Offset.Zero)
    var reorderableItems
        get() = data.value
        set(value) {
            data.value = value
        }

    var draggedItem
        get() = _draggedItem.value
        set(value) {
            _draggedItem.value = value
        }

    var dragOffset
        get() = _dragOffset.value
        set(value) {
            _dragOffset.value = value
        }
}

@Composable
fun <T> rememberReorderableFlowRowState(
    items: List<T>,
    onListReordered: (List<T>, draggedItem: ReorderableFlowRowItem<T>) -> Unit = { _, _ -> }
): ReorderableFlowRowState<T> {
    val draggableListState = remember(items) {
        val data =
            mutableStateOf(
                items.map {
                    ReorderableFlowRowItem(it)
                }
            )
        ReorderableFlowRowState(data, onListReordered)
    }

    return draggableListState
}


@Composable
fun <T> Modifier.reorderableFlowRow(
    reorderableFlowLayoutState: ReorderableFlowRowState<T>,
    enable: Boolean = true,
    onDragStart: (item: T) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
): Modifier {
    return this.pointerInput(enable, reorderableFlowLayoutState) {

        if (enable) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->

                    reorderableFlowLayoutState.reorderableItems
                        .find { it.bounds.contains(offset) }
                        ?.let {
                            onDragStart(it.item)
                            reorderableFlowLayoutState.draggedItem = it
                            reorderableFlowLayoutState.dragOffset = it.center
                        }
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    reorderableFlowLayoutState.dragOffset += Offset(dragAmount.x, dragAmount.y)

                    reorderableFlowLayoutState.draggedItem?.let { draggedItem ->
                        CoroutineScope(Dispatchers.IO).launch {
                            reorderableFlowLayoutState.reorderableItems
                                .forEach {
                                    when {
                                        it.startHitBox.contains(reorderableFlowLayoutState.dragOffset)
                                                && it.item != draggedItem.item
                                                && reorderableFlowLayoutState.reorderableItems.indexOf(
                                            it
                                        ) < reorderableFlowLayoutState.reorderableItems.indexOf(
                                            draggedItem
                                        )
                                        -> {
                                            reorderableFlowLayoutState.reorderableItems =
                                                reorderableFlowLayoutState.reorderableItems.moveItemToLeftOf(
                                                    draggedItem,
                                                    it
                                                )
                                            reorderableFlowLayoutState.onListReordered(
                                                reorderableFlowLayoutState.reorderableItems.map { it.item },
                                                it
                                            )
                                        }


                                        it.endHitBox.contains(reorderableFlowLayoutState.dragOffset) && it.item != draggedItem.item
                                                && reorderableFlowLayoutState.reorderableItems.indexOf(
                                            it
                                        ) > reorderableFlowLayoutState.reorderableItems.indexOf(
                                            draggedItem
                                        )
                                        -> {
                                            reorderableFlowLayoutState.reorderableItems =
                                                reorderableFlowLayoutState.reorderableItems.moveItemToRightOf(
                                                    draggedItem,
                                                    it
                                                )
                                            reorderableFlowLayoutState.onListReordered(
                                                reorderableFlowLayoutState.reorderableItems.map { it.item },
                                                it
                                            )
                                        }

                                    }
                                }
                        }
                    }
                },

                onDragEnd = {
                    onDragEnd()
                    reorderableFlowLayoutState.draggedItem = null
                    reorderableFlowLayoutState.dragOffset = Offset.Zero
                }, onDragCancel = {
                    onDragCancel()
                    reorderableFlowLayoutState.draggedItem = null
                    reorderableFlowLayoutState.dragOffset = Offset.Zero
                }
            )
        }
    }
}


@Composable
fun <T> Modifier.draggedItemInFlowRow(
    draggableListState: ReorderableFlowRowState<T>,
    draggedItem: ReorderableFlowRowItem<T>
): Modifier {
    return this
        .graphicsLayer {
            translationX = draggableListState.dragOffset.x
            translationY = draggableListState.dragOffset.y
        }
        .offset {
            IntOffset(
                -draggedItem.bounds.width.roundToInt() / 2,
                -draggedItem.bounds.height.roundToInt() / 2
            )
        }
}

@Composable
fun <T> Modifier.reorderableItemInFlowRow(
    draggableItem: ReorderableFlowRowItem<T>
): Modifier {
    return this
        .animatePlacement()
        .onGloballyPositioned {
            draggableItem.setBounds(it.boundsInParent())
        }
}

private fun Modifier.animatePlacement(): Modifier = composed {
    val scope = rememberCoroutineScope()
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }
    this
        .onPlaced {
            // Calculate the position in the parent layout
            targetOffset = it
                .positionInParent()
                .round()
        }
        .offset {
            // Animate to the new target offset when alignment changes.
            val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
                .also { animatable = it }
            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, tween(350))
                }
            }
            // Offset the child in the opposite direction to the targetOffset, and slowly catch
            // up to zero offset via an animation to achieve an overall animated movement.
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
}