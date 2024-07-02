package de.thermondo.qmobile

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AppFileUtils(private val context: Context) {

    /**
     * Creates a file from the given content URI.
     *
     * @param uri The content URI of the media.
     * @param fileName The name of the output file.
     * @return The created file or null if there was an error.
     */
    fun createFileFromUri(uri: Uri, fileName: String): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val file = File(context.cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyStream(inputStream, outputStream)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets an InputStream from the given content URI.
     *
     * @param uri The content URI of the media.
     * @return The InputStream or null if there was an error.
     */
    fun getInputStreamFromUri(uri: Uri): InputStream? {
        return try {
            context.contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int

        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        output.flush()
    }
}