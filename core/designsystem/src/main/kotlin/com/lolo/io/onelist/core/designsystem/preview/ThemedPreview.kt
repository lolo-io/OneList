package com.lolo.io.onelist.core.designsystem.preview

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
import com.lolo.io.onelist.core.designsystem.OneListTheme

interface ThemedPreviewScope {
    fun showPreviewDialog(text: String = "Clicked !")
}

private fun themedPreviewScope(
    showPreviewDialog: (text: String) -> Unit,
) =
    object : ThemedPreviewScope {
        override fun showPreviewDialog(text: String) {
            showPreviewDialog(text)
        }
    }

@Composable
fun ThemedPreview(content: @Composable ThemedPreviewScope.() -> Unit) {

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
            content(themedPreviewScope(showPreviewDialog = showPreviewDialogFun))
        }
    }
}

@Preview
@Composable
private fun Preview_ThemedPreview() {
    ThemedPreview {
        Button(onClick = { showPreviewDialog() }) {
            Text(text = "Click Me")
        }
    }
}