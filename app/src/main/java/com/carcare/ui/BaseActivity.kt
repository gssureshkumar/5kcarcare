package com.carcare.ui

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.carcare.R
import com.carcare.permission.checkSelfPermissionCompat
import com.carcare.permission.requestPermissionsCompat
import com.carcare.permission.shouldShowRequestPermissionRationaleCompat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    val PERMISSION_REQUEST = 0
    lateinit var permissionListener: PermissionListener

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = AlertDialog.Builder(this)
        builder.setView(R.layout.custom_progress_view)
        dialog = builder.create()
    }

    fun showToast(message: String) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }


    fun checkLocationPermission(listener: PermissionListener) {
        permissionListener = listener
        if (permissionIsGranted(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))) {
            permissionListener.permissionGranted()
        } else {
            requestPermission(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        }

    }

    open fun permissionIsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            val permissionState = checkSelfPermissionCompat(
                permission
            )
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {

            if (grantResults.isNotEmpty()) {
                var isGranted = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    isGranted = Arrays.stream(grantResults).anyMatch { t -> t == PackageManager.PERMISSION_GRANTED }
                } else {
                    for (n in grantResults) {
                        if (n != PackageManager.PERMISSION_GRANTED) {
                            isGranted = false
                            break
                        }
                    }
                }
                if (isGranted) {
                    permissionListener.permissionGranted()
                } else {
                    permissionListener.permissionDenied()
                }
            } else {
                permissionListener.permissionDenied()
            }
        }
    }


    /**
     * Requests the [android.Manifest.permission.CAMERA] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private fun requestPermission(permissions: Array<String>) {
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.CAMERA)) {
            requestPermissionsCompat(
                permissions,
                PERMISSION_REQUEST
            )
        } else {
            requestPermissionsCompat(permissions, PERMISSION_REQUEST)
        }
    }

    interface PermissionListener {
        fun permissionGranted()
        fun permissionDenied()
    }

    fun setDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }
}