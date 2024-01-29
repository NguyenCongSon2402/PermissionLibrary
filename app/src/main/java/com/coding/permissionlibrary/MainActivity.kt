package com.coding.permissionlibrary

import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.coding.permissionlibrary.databinding.ActivityMainBinding
import com.coding.permisson.utils.PermissionsUtils
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.ButtonGravity
import gun0912.tedimagepicker.builder.type.MediaType
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionUtils: PermissionsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkAndRequestPermissions()
        binding.idBtnRead.setOnClickListener {
            readTextFromFile()
        }

        binding.idBtnWrite.setOnClickListener {
            writeTextToFile()
        }
    }

    private fun init() {
        permissionUtils = PermissionsUtils(this) { granted ->
            // Xử lý kết quả quyền ở đây
            if (granted) {
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

    private fun readTextFromFile() {
        val contextWrapper = ContextWrapper(this)
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val txtFile = File(directory, "file.txt")
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(txtFile))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
        } catch (e: Exception) {
            Log.d("OK","${e.message}")
            init()
            Toast.makeText(applicationContext, "Fail to read the file..", Toast.LENGTH_SHORT).show()
            return
        }
        binding.idTVReadFile.text = text
        Toast.makeText(contextWrapper, "File read successful..", Toast.LENGTH_SHORT).show()
    }

    private fun writeTextToFile() {
        val text = binding.idEdtMsg.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(
                this@MainActivity,
                "Please enter the data to be saved..",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val txtFile = File(directory, "file.txt")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(txtFile)
            val osw = OutputStreamWriter(fos)
            osw.write(text)
            osw.flush()
            osw.close()
            fos.close()
            Toast.makeText(contextWrapper, "File write successful..", Toast.LENGTH_SHORT).show()
            binding.idEdtMsg.setText("")
            binding.pathSave.text=txtFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            init()
            Log.d("OK1","${e.message}")
        }
    }
}
