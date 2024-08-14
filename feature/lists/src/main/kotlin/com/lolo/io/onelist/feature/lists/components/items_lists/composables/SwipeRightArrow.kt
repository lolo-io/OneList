package com.lolo.io.onelist.feature.lists.components.items_lists.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.designsystem.preview.ThemedPreview

object CustomIcons
val Icons.Custom
    get() = CustomIcons

public val CustomIcons.SwipeRightArrow: ImageVector
    get() {
        if (_keyboardArrowRight != null) {
            return _keyboardArrowRight!!
        }
        _keyboardArrowRight = materialIcon(name = "CustomIcons.SwipeRightArrow") {
            materialPath {
                moveTo(5.59f, 16.59f)
                lineTo(10.17f, 12.0f)
                lineTo(5.59f, 7.41f)
                lineTo(7.0f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(-6.0f, 6.0f)
                lineToRelative(-1.41f, -1.41f)

                moveTo(10.59f, 16.59f)
                lineTo(15.17f, 12.0f)
                lineTo(10.59f, 7.41f)
                lineTo(12.0f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(-6.0f, 6.0f)
                lineToRelative(-1.41f, -1.41f)
                close()
            }
        }
        return _keyboardArrowRight!!
    }

private var _keyboardArrowRight: ImageVector? = null

@Preview
@Composable
private fun Preview_SwipeableItem() = ThemedPreview {
    Icon(
        imageVector = CustomIcons.SwipeRightArrow,
        contentDescription = ""
    )

}