package com.lolo.io.onelist.core.data.utils

import android.Manifest
import android.net.Uri
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.lolo.io.onelist.MainActivity

val String?.toUri: Uri?  // todo rm
    get() = try {
        if (this.isNullOrBlank() || !startsWith("content://")) throw Exception()
        Uri.parse(this)
    } catch (e: Exception) {
        null
    }

fun withStoragePermission(activity: MainActivity, block: () -> Unit) { //todo rm
    Dexter.withActivity(activity)
        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                activity.whenResumed = block
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                Toast.makeText(
                    activity,
                    "Permission is required to access external storage.",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest,
                token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).check()
}

