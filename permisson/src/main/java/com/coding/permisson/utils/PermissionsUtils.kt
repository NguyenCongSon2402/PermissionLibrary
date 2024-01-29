package com.coding.permisson.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionsUtils(
    private val activity: FragmentActivity,
    private val permissionCallback: (Boolean) -> Unit
) {

    private val storageActivityResultLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android is 11 (R) or above
                if (Environment.isExternalStorageManager()) {
                    // Manage External Storage Permissions Granted
                    permissionCallback.invoke(true)
                } else {
                    Toast.makeText(activity, ":>11Storage Permissions Denied", Toast.LENGTH_SHORT)
                        .show()
                    permissionCallback.invoke(false)
                }
            }
        }

    private val STORAGE_PERMISSION_CODE = 23

    fun checkAndRequestStoragePermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android is 11 (R) or above
            if (Environment.isExternalStorageManager()) {
                // Storage permissions are already granted
                permissionCallback.invoke(true)
            } else {
                // Request Storage Permissions
                requestForStoragePermissions()
            }
        } else {
            // Below Android 11
            val readPermissionGranted =
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val writePermissionGranted =
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            if (readPermissionGranted && writePermissionGranted) {
                // Cả quyền đọc và quyền ghi đã được cấp
                permissionCallback.invoke(true)
            } else {
                // Một hoặc cả hai quyền chưa được cấp, yêu cầu quyền
                requestPermissionLauncher.launch(permissions)
            }
        }
    }

    private fun requestForStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        }
    }

    private val requestPermissionLauncher =
        (activity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                Toast.makeText(activity, "<10:Storage Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "<10:Storage Permissions Denied", Toast.LENGTH_SHORT).show()
            }
            permissionCallback.invoke(granted)
        }
}

