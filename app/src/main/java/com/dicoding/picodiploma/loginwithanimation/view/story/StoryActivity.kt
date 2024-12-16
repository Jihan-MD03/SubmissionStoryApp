package com.dicoding.picodiploma.loginwithanimation.view.story

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var progressBar: View

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inisialisasi ViewModel
        storyViewModel = ViewModelProvider(this, ViewModelFactory(this)).get(StoryViewModel::class.java)

        // Inisialisasi RecyclerView
        storyAdapter = StoryAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = storyAdapter

        // Ambil referensi ProgressBar
        progressBar = findViewById(R.id.progressBar)

        // Tambahkan LoadStateListener untuk memantau kondisi loading
        storyAdapter.addLoadStateListener { loadState ->
            when {
                loadState.refresh is LoadState.Loading -> {
                    // Tampilkan ProgressBar ketika memuat
                    progressBar.visibility = View.VISIBLE
                }

                loadState.refresh is LoadState.Error -> {
                    // Jika error, sembunyikan loading dan tampilkan pesan
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Error: ${(loadState.refresh as LoadState.Error).error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    // Sembunyikan loading saat data sudah dimuat
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Observasi LiveData stories dan error
        lifecycleScope.launch {
            storyViewModel.pagedStories.collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
                progressBar.visibility = View.GONE
            }
        }

        storyViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        storyViewModel.error.observe(this@StoryActivity) { error ->
            progressBar.visibility = View.GONE
            Toast.makeText(this@StoryActivity, error, Toast.LENGTH_SHORT)
                .show()  // Tampilkan error jika ada
            Log.e("StoryActivity", "Error: $error")
        }

        // Set click listener on the Floating Action Button
        val fabAddStory: FloatingActionButton = findViewById(R.id.fab_add_story)
        fabAddStory.setOnClickListener {
            // Navigate to AddStoryActivity
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser() // Panggil fungsi logout
                true
            }
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        lifecycleScope.launch {
            // Hapus sesi atau token pengguna
            val userPreference = UserPreference.getInstance(applicationContext.dataStore)
            userPreference.clearToken()

            storyViewModel.logout()

            // Arahkan ke halaman Welcome
            val intent = Intent(this@StoryActivity, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Fungsi untuk mengambil token dari preferensi atau session
    private suspend fun getTokenFromPreference(): String {
        // Ambil token dari UserPreference atau session
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        return userPreference.getToken() ?: ""
    }
}




