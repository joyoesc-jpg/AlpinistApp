package com.example.alpinistapp

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // Guardar el estado de la sesión y datos del usuario
    suspend fun saveUserData(isLoggedIn: Boolean, name: String?, email: String?) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_NAME] = name ?: ""
            preferences[USER_EMAIL] = email ?: ""
        }
    }

    // Guardar solo el estado de la sesión (ej. para logout)
    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            if (!isLoggedIn) {
                preferences[USER_NAME] = ""
                preferences[USER_EMAIL] = ""
            }
        }
    }

    // Leer el estado de la sesión
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    // Leer nombre del usuario
    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "Usuario Alpino"
    }

    // Leer email del usuario
    val userEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: "usuario@ejemplo.com"
    }
}