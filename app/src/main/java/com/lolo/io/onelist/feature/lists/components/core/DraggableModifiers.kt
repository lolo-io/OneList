package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moveItemToLeftOf
import moveItemToRightOf
import kotlin.math.roundToInt

class DraggableItem<T>(
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
        value: Rect, orientation: DraggableListState.Orientation =
            DraggableListState.Orientation.VERTICAL
    ) {
        bounds = value
        if(orientation == DraggableListState.Orientation.HORIZONTAL) {
            startHitBox =
                Rect(value.left, value.top, (value.left + value.width / 4.0f), value.bottom)
            endHitBox =
                Rect((value.right - value.width / 4.0f), value.top, value.right, value.bottom)
        } else {
            startHitBox =
                Rect(value.left, value.top, value.right, (value.top + value.height / 4.0f))
            endHitBox =
                Rect(value.left, (value.bottom - value.height / 4.0f), value.right, value.bottom)
        }

    }
}

class DraggableListState<T>(
    private val data: MutableState<List<DraggableItem<T>>>,
    val orientation: Orientation = Orientation.VERTICAL
) {
    enum class Orientation { HORIZONTAL, VERTICAL }

    private val _draggedItem: MutableState<DraggableItem<T>?> = mutableStateOf(null)
    private val _dragOffset: MutableState<Offset> = mutableStateOf(Offset.Zero)
    var draggableItems
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
fun <T> rememberDraggableListState(
    items: List<T>,
    orientation: DraggableListState.Orientation = DraggableListState.Orientation.VERTICAL,
): DraggableListState<T> {
    val draggableListState = remember {
        val data =
            mutableStateOf(
                items.map {
                    DraggableItem(it)
                }
            )
        DraggableListState(data, orientation)
    }

    return draggableListState
}


@Composable
fun <T> Modifier.draggableItemList(
    draggableListState: DraggableListState<T>,
    onDragStart: (item: T) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
): Modifier {

    var debounce = false

    return this.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->

                draggableListState.draggableItems
                    .find { it.bounds.contains(offset) }
                    ?.let {
                        onDragStart(it.item)
                        draggableListState.draggedItem = it
                        draggableListState.dragOffset = it.center
                    }
            },
            onDrag = { change, dragAmmount ->
                change.consume()
                draggableListState.dragOffset += Offset(dragAmmount.x, dragAmmount.y)


                if (!debounce) {
                    draggableListState.draggedItem?.let { draggedItem ->

                        CoroutineScope(Dispatchers.IO).launch {
                            draggableListState.draggableItems
                                .forEach {
                                    val hitLeft =
                                        it.startHitBox.contains(draggableListState.dragOffset) && it.item != draggedItem.item
                                    val hitRight =
                                        it.endHitBox.contains(draggableListState.dragOffset) && it.item != draggedItem.item
                                    when {
                                        hitLeft -> draggableListState.draggableItems =
                                            draggableListState.draggableItems.moveItemToLeftOf(
                                                draggedItem,
                                                it
                                            )

                                        hitRight -> draggableListState.draggableItems =
                                            draggableListState.draggableItems.moveItemToRightOf(
                                                draggedItem,
                                                it
                                            )

                                    }
                                    if (hitLeft || hitRight) {
                                        debounce = true
                                        delay(500)
                                        debounce = false
                                    }

                                }
                        }
                    }
                }
            },

            onDragEnd = {
                onDragEnd()
                draggableListState.draggedItem = null
                draggableListState.dragOffset = Offset.Zero
            }, onDragCancel = {
                onDragCancel()
                draggableListState.draggedItem = null
                draggableListState.dragOffset = Offset.Zero
            }
        )
    }
}


@Composable
fun <T> Modifier.draggedItem(
    draggableListState: DraggableListState<T>,
    draggedItem: DraggableItem<T>
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
fun <T> Modifier.draggableItem(
    draggableListState: DraggableListState<T>,
    draggableItem: DraggableItem<T>
): Modifier {
    return this
        .animatePlacement()
        .onGloballyPositioned {
            draggableItem.setBounds(it.boundsInParent(), draggableListState.orientation)
        }
}

fun Modifier.animatePlacement(): Modifier = composed {
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
                    anim.animateTo(targetOffset, spring(stiffness = Spring.StiffnessMediumLow))
                }
            }
            // Offset the child in the opposite direction to the targetOffset, and slowly catch
            // up to zero offset via an animation to achieve an overall animated movement.
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
}