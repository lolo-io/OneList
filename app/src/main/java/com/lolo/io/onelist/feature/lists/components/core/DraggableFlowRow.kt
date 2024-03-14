package com.lolo.io.onelist.feature.lists.components.core

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.unit.dp
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
    private var _leftHitbox: Rect = Rect.Zero
    private var _rightHitbox: Rect = Rect.Zero
    var bounds: Rect = Rect.Zero
        set(value) {
            field = value
            _leftHitbox =
                Rect(value.left, value.top, (value.left + value.width / 4.0f), value.bottom)
            _rightHitbox =
                Rect((value.right - value.width / 4.0f), value.top, value.right, value.bottom)
        }

    val leftHitbox
        get() = _leftHitbox
    val rightHitbox
        get() = _rightHitbox

    val center
        get() = bounds.center
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> DraggableFlowRow(
    items: List<T>,
    itemKeys: (item: T) -> Any,
    drawItem: @Composable (item: T, isDragged: Boolean) -> Unit = { _, _ -> },
    drawDragItem: @Composable (item: T) -> Unit = {},
    onDragStart: (item: T) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    var data =
        items.map {
            DraggableItem(it)
        }

    var draggedItem by remember {
        mutableStateOf<DraggableItem<T>?>(null)
    }

    var dragOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    var debounce = false
    FlowRow(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->

                        data
                            .find { it.bounds.contains(offset) }
                            ?.let {
                                onDragStart(it.item)
                                draggedItem = it
                                dragOffset = it.center
                            }
                    },
                    onDrag = { change, dragAmmount ->
                        change.consume()
                        dragOffset += Offset(dragAmmount.x, dragAmmount.y)


                        if (!debounce) {
                            draggedItem?.let { draggedItem ->

                                CoroutineScope(Dispatchers.IO).launch {
                                    data
                                        .forEach {
                                            val hitLeft =
                                                it.leftHitbox.contains(dragOffset) && it.item != draggedItem.item
                                            val hitRight =
                                                it.rightHitbox.contains(dragOffset) && it.item != draggedItem.item
                                            when {
                                                hitLeft -> data =
                                                    data.moveItemToLeftOf(draggedItem, it)

                                                hitRight -> data =
                                                    data.moveItemToRightOf(draggedItem, it)

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
                        draggedItem = null
                        dragOffset = Offset.Zero
                    }, onDragCancel = {
                        onDragCancel()
                        draggedItem = null
                        dragOffset = Offset.Zero
                    }
                )
            },
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        maxItemsInEachRow = maxItemsInEachRow
    ) {
        data.map { item ->
            key(itemKeys(item.item)) {

                Box(
                    modifier = Modifier
                        .animatePlacement()
                        .onGloballyPositioned {
                            item.bounds = it.boundsInParent()
                        }
                ) {
                    drawItem(item.item, item == draggedItem)
                }

            }
        }
    }


    draggedItem?.let { draggedItem ->
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = dragOffset.x
                    translationY = dragOffset.y
                }
                .offset {
                    IntOffset(
                        -draggedItem.bounds.width.roundToInt() / 2,
                        -draggedItem.bounds.height.roundToInt() / 2
                    )
                }
        ) {
            drawDragItem(draggedItem.item)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipItem(text: String, modifier: Modifier = Modifier, selected: Boolean = false) {
    FilterChip(
        modifier = modifier
            .padding(end = 4.dp),
        label = { Text(text) },
        onClick = {},
        selected = selected
    )
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



