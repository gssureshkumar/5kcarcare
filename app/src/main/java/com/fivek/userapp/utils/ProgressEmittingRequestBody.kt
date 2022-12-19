package com.fivek.userapp.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressEmittingRequestBody constructor(
    private val mediaType: String,
    val file: File,
    private val progressUpdater: ProgressUpdater
) : RequestBody() {

    interface ProgressUpdater {
        fun updateProgress(progress: Int)
    }

    var TAG: String = "UploadProgress"
    override fun contentType(): MediaType? = mediaType.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {

        val inputStream = FileInputStream(file)
        val buffer = ByteArray(BUFFER_SIZE)
        var uploaded: Long = 0
        val fileSize = file.length()

        try {
            while (true) {

                val read = inputStream.read(buffer)
                if (read == -1) break

                uploaded += read
                sink.write(buffer, 0, read)

                val progress = (((uploaded / fileSize.toDouble())) * 100).toInt()
                progressUpdater.updateProgress(progress)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream.close()
        }
    }

    companion object {
        const val BUFFER_SIZE = 1024
    }
}