package com.carcare.utils

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadUtility(activity: Activity ) {

    var activity = activity;
    var dialog: ProgressDialog? = null
    val client = OkHttpClient()

    fun uploadFile(sourceFile: File, serverURL: String, uploadListener: UploadListener) {
        Thread {
            val mimeType = getMimeType(sourceFile) ?: return@Thread;
            val fileName: String =   sourceFile.name
            toggleProgressDialog(true)
            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("audio", fileName,sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                        .build()

                val request: Request = Request.Builder().url(serverURL).put(requestBody).build()

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    showToast("File uploaded successfully")
                    uploadListener.uploaded()
                } else {
                    showToast("File uploading failed" +response.message)
                    uploadListener.failed()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                showToast("File uploading failed")
            }
            toggleProgressDialog(false)
        }.start()
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText( activity, message, Toast.LENGTH_LONG ).show()
        }
    }

    fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
            } else {
                dialog?.dismiss();
            }
        }
    }

    interface UploadListener{
        fun  uploaded()
        fun failed()
    }
}