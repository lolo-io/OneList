package com.lolo.io.onelist.core.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object Dimen {
    val listItemMinHeight = 48.dp
}

val MaterialTheme.dimen: Dimen
    @Composable get() = dimen()
@Composable
fun dimen() = Dimen