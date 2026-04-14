package com.darkside.wallet.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    /**
     * Copies an image from a temporary URI to permanent app storage.
     * Prevents photos from being deleted by the OS cache/temporary cleanup.
     */
    fun saveImagePermanently(context: Context, uri: Uri): String? {
        return try {
            val fileName = "profile_photo_${System.currentTimeMillis()}.jpg"
            val permanentFile = File(context.filesDir, fileName)
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(permanentFile).use { output ->
                    input.copyTo(output)
                }
            }
            permanentFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Checks if a file exists at the given path.
     */
    fun fileExists(path: String?): Boolean {
        if (path == null) return false
        return File(path).exists()
    }
}
