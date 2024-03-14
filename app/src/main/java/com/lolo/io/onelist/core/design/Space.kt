package com.lolo.io.onelist.core.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object Space {
    val Tiny = 4.dp
    val Small = 8.dp
    val SmallUpper = 12.dp
    val Normal = 16.dp
    val Big = 24.dp
    val xBig = 32.dp
    val xBigUpper = 40.dp
    val xxBig = 48.dp
    val xxxBig = 64.dp
    val xHuge = 80.dp
    val xxHuge = 104.dp
}


val MaterialTheme.space: Space
    @Composable get() = space()
@Composable
fun space() = Space