package com.lolo.io.onelist.core.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.lolo.io.onelist.core.design.OneListTheme


interface ComposePreviewScope {
    fun showPreviewDialog(text: String = "Clicked !")
}

private fun composePreviewScope(
    showPreviewDialog: (text: String) -> Unit,
) =
    object : ComposePreviewScope {
        override fun showPreviewDialog(text: String) {
            showPreviewDialog(text)
        }
    }

@Composable
fun ComposePreview(content: @Composable ComposePreviewScope.() -> Unit) {

    var showPreviewDialogText by remember {
        mutableStateOf<String?>(null)
    }

    val showPreviewDialogFun: (text: String) -> Unit = {
            showPreviewDialogText = it
    }

    if (showPreviewDialogText != null) {
        AlertDialog(
            title = { Text("Preview")},
            text = { Text(showPreviewDialogText ?: "")},
            onDismissRequest = { showPreviewDialogText = null },
            confirmButton = {
                Button(
                    onClick = { showPreviewDialogText = null }) {
                    Text("Ok")
                }
            })
    }

    OneListTheme {
        Surface {
            content(composePreviewScope(showPreviewDialog = showPreviewDialogFun))
        }
    }
}

@Preview
@Composable
private fun Preview_ComposePreview() {
    ComposePreview {
        Button(onClick = { showPreviewDialog() }) {
            Text(text = "Click Me")
        }
    }
}