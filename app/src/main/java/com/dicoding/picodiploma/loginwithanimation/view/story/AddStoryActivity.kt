package com.dicoding.picodiploma.loginwithanimation.view.story

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryImage: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var saveStoryButton: Button
    private lateinit var storyDescription: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var storyViewModel: StoryViewModel

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            showLoading(true)
            Glide.with(this)
                .load(it)
                .into(addStoryImage)
            showLoading(false)
        }
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                showLoading(true)
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(addStoryImage)
                showLoading(false)
            }
        }

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        storyViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(applicationContext))[StoryViewModel::class.java]

        // Observer untuk upload success
        storyViewModel.uploadSuccess.observe(this) { response ->
            if (response != null && !response.error) {
                navigateToMain()  // Arahkan ke MainActivity jika upload sukses
            }
        }

        // Observer untuk menangani error
        storyViewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
                .show()  // Tampilkan error jika gagal upload
        }

        checkPermissions()  // Panggil fungsi cek izin

        // Inisialisasi UI dan tombol lainnya
        addStoryImage = findViewById(R.id.add_story_image)
        selectImageButton = findViewById(R.id.select_image_button)
        saveStoryButton = findViewById(R.id.save_story_button)
        storyDescription = findViewById(R.id.story_description)
        progressBar = findViewById(R.id.progress_bar)

        selectImageButton.setOnClickListener {
            val options = arrayOf("Pilih dari Galeri", "Ambil Foto")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Opsi")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> pickImage.launch("image/*") // Pilih dari galeri
                        1 -> openCamera() // Ambil foto
                    }
                }
            builder.show()
        }

        saveStoryButton.setOnClickListener {
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

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(packageManager)?.also {
            selectedImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
            takePicture.launch(selectedImageUri)
        }
    }

    private fun saveStory() {
        val descriptionText = storyDescription.text.toString()
        Log.d("AddStoryActivity", "Description: $descriptionText")

        if (descriptionText.isBlank() || selectedImageUri == null) {
            Log.d("AddStoryActivity", "Image URI: $selectedImageUri")
            showError("Deskripsi dan gambar tidak boleh kosong")
            return

        }

        // Pastikan file ada
        val file = getFileFromUri(selectedImageUri)
        Log.d("AddStoryActivity", "File path: ${file?.absolutePath}")
        if (file == null) {
            showError("Gambar tidak valid")
            return
        }

        val requestFile = file.asRequestBody("image/jpeg/jpg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())

        // Ambil token dari SharedPreferences
        /*val sharedPreferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: ""

        if (token.isBlank()) {
            showError("Token tidak valid")
            return
        }*/

        // Menggunakan ViewModel untuk upload story
        lifecycleScope.launch {
            try {
                showLoading(true)
                val response = storyViewModel.uploadStory(body, description)
                Log.d("AddStoryActivity", "Upload successful: $response")  // Tambahkan log untuk melihat response
                showLoading(false)

                // Setelah story berhasil di-upload, navigasi ke MainActivity
                navigateToMain()

                // Atau beri tahu pengguna jika story berhasil di-upload
                Toast.makeText(this@AddStoryActivity, "Story berhasil di-upload", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                showLoading(false)
                showError("Terjadi kesalahan: ${e.message}")
                Log.e("AddStoryActivity", "Upload failed", e)  // Tambahkan log untuk error
            }
        }
    }

    private fun getFileFromUri(uri: Uri?): File? {
        uri ?: return null
        val contentResolver = contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("_data")
                return File(it.getString(columnIndex))
            }
        }
        return null
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
