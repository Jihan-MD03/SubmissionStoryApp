package com.dicoding.picodiploma.loginwithanimation.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val tokenKey = stringPreferencesKey("token")
    private val passwordKey = stringPreferencesKey("password") // Deklarasi PASSWORD_KEY

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun savePassword(password: String) {
        dataStore.edit { preferences ->
            preferences[passwordKey] = password // Menyimpan password
        }
    }

    suspend fun getToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[tokenKey]
    }

    suspend fun getPassword(): String? {
        val preferences = dataStore.data.first()
        return preferences[passwordKey]
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[PASSWORD_KEY] = user.password // Pastikan Anda juga menyimpan password di sini
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[PASSWORD_KEY] ?: "", // Ambil password dari session
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }




    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val PASSWORD_KEY = stringPreferencesKey("password") // Deklarasi PASSWORD_KEY di sini

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}