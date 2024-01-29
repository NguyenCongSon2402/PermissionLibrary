package com.coding.permissionlibrary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.coding.permissionlibrary.databinding.ActivityMainBinding
import com.coding.permisson.utils.PermissionsUtils
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.ButtonGravity
import gun0912.tedimagepicker.builder.type.MediaType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionUtils: PermissionsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.checkPermissionButton.setOnClickListener {
            //openGallery()
            checkAndRequestPermissions()
        }
    }

    private fun init() {
        permissionUtils = PermissionsUtils(this) { granted ->
            // Xử lý kết quả quyền ở đây
            if (granted) {
                openGallery()
                //addImage()
                // Quyền đã được cấp
                // Thực hiện các hành động sau khi quyền đã được cấp
                Toast.makeText(this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Quyền bị từ chối
                // Thực hiện các hành động khi quyền bị từ chối
                Toast.makeText(this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        permissionUtils.checkAndRequestStoragePermissions()
    }
    private fun addImage() {
            TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .buttonGravity(ButtonGravity.BOTTOM)
                .title("Pick Image")
                .start { uri ->
                    binding.image.setImageURI(uri)
                }
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                // Thực hiện các hành động với ảnh đã chọn
                handleSelectedImage(selectedImageUri)
            }
        }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(galleryIntent)
    }

    private fun handleSelectedImage(imageUri: Uri?) {
        // Đặt mã xử lý ảnh ở đây
        // Ví dụ: hiển thị ảnh trên ImageView
        binding.image.setImageURI(imageUri)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }
}
