package com.dicoding.picodiploma.loginwithanimation.view.story

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.getImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryImage: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var saveStoryButton: Button
    private lateinit var storyDescription: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: StoryDetailViewModel

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            showLoading(true)
            Glide.with(this)
                .load(it)
                .into(addStoryImage)
            showLoading(false)
            selectedImageUri = uri
        }
    }

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aktivasi Toolbar di Activity
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Menampilkan judul di toolbar
        supportActionBar?.title = "Add Your Stories"

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(application)
        )[StoryDetailViewModel::class.java]

        // Observer untuk upload success
        storyViewModel.uploadSuccess.observe(this) { response ->
            if (response != null && !response.error) {
            }
        }

        // Observer untuk menangani error
        storyViewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
                .show()  // Tampilkan error jika gagal upload
        }

        // Observer untuk memantau status loading
        storyViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        checkPermissions()  // Panggil fungsi cek izin

        // Inisialisasi UI dan tombol lainnya
        addStoryImage = findViewById(R.id.add_story_image)
        selectImageButton = findViewById(R.id.select_image_button)
        saveStoryButton = findViewById(R.id.save_story_button)
        storyDescription = findViewById(R.id.story_description)
        progressBar = findViewById(R.id.progress_bar)

        binding.selectImageButton.setOnClickListener {
            val options = arrayOf("Pilih dari Galeri", "Ambil Foto")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Opsi")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> pickImage.launch("image/*") // Pilih dari galeri
                        1 -> startCamera() // Ambil foto
                    }
                }
            builder.show()
        }

        binding.saveStoryButton.setOnClickListener {
            saveStory()
        }
    }

    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Jika ada izin yang belum diberikan, minta izin
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 100)
        }
    }

    // Tangani hasil permintaan izin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, lanjutkan dengan operasi
            } else {
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(this, "Izin diperlukan untuk menggunakan kamera dan galeri", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        selectedImageUri = getImageUri(this)
        launcherIntentCamera.launch(selectedImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            selectedImageUri = null
        }
    }

    private fun showImage() {
        selectedImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.addStoryImage.setImageURI(it)
        }
    }

    private fun saveStory() {
        val descriptionText = storyDescription.text.toString()
        if (descriptionText.isBlank() || selectedImageUri == null) {
            showError("Deskripsi dan gambar tidak boleh kosong")
            return

        }

        // Pastikan file ada
        val file = getFileFromUri(selectedImageUri)
        if (file == null) {
            showError("Gambar tidak valid")
            return
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())


        // Menggunakan ViewModel untuk upload story
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                storyViewModel.uploadStory(body, description)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Story berhasil di-upload",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@AddStoryActivity, StoryActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun getFileFromUri(uri: Uri?): File? {
        uri ?: return null
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r", null) ?: return null
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(uri)) // Cache file
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    // Helper untuk mendapatkan nama file dari URI
    private fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                name = it.getString(index)
            }
        }
        return name
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
