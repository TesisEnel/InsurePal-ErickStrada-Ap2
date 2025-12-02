package edu.ucne.InsurePal.presentation.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun Context.crearArchivoDesdeUri(uri: Uri): File? {
    return try {
        val stream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("evidencia_", ".jpg", cacheDir)
        stream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        null
    }
}