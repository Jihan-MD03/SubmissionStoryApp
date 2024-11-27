package com.dicoding.picodiploma.loginwithanimation.view.story

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var progressBar: View

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        // Dapatkan ViewModel
        val userRepository = Injection.provideUserRepository(applicationContext) // Pastikan ada fungsi ini di Injection
        val storyRepository = Injection.provideStoryRepository(applicationContext) // Pastikan fungsi ini ada
        val factory = ViewModelFactory(userRepository, storyRepository)
        storyViewModel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)

        // Inisialisasi RecyclerView
        storyAdapter = StoryAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = storyAdapter

        // Ambil referensi ProgressBar
        progressBar = findViewById(R.id.progressBar)

        // Tampilkan ProgressBar saat pemanggilan data
        progressBar.visibility = View.VISIBLE

        // Ambil token dengan cara yang benar menggunakan coroutine
        lifecycleScope.launch {
            val token = getTokenFromPreference()  // Ambil token dari preferensi secara asinkron
            Log.d("Token", "Retrieved token: $token")

            // Mengambil data stories dengan token
            storyViewModel.getStories()

            // Observasi LiveData stories dan error
            storyViewModel.stories.observe(this@StoryActivity) { stories ->
                Log.d("StoryActivity", "Stories loaded: ${stories.size}")
                progressBar.visibility = View.GONE
                if (stories.isNotEmpty()) {
                    storyAdapter.submitList(stories)  // Kirim data ke adapter
                } else {
                    Toast.makeText(this@StoryActivity, "No stories found", Toast.LENGTH_SHORT).show()  // Jika tidak ada cerita
                }
            }

            storyViewModel.error.observe(this@StoryActivity) { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(this@StoryActivity, error, Toast.LENGTH_SHORT).show()  // Tampilkan error jika ada
                Log.e("StoryActivity", "Error: $error")
            }
        }

        // Set click listener on the Floating Action Button
        val fabAddStory: FloatingActionButton = findViewById(R.id.fab_add_story)
        fabAddStory.setOnClickListener {
            // Navigate to AddStoryActivity
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi untuk mengambil token dari preferensi atau session
    private suspend fun getTokenFromPreference(): String {
        // Ambil token dari UserPreference atau session
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        return userPreference.getToken() ?: ""  // Mengembalikan token atau string kosong jika token tidak ada
    }
}



