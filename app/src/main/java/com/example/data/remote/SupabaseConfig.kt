package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences

class SupabaseConfig(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_config_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_URL = "supabase_url"
        private const val KEY_ANON_KEY = "supabase_anon_key"
        private const val KEY_AUTO_SYNC = "supabase_auto_sync"

        private const val KEY_LAB_DARK_MODE = "lab_dark_mode"

        // Default medical demonstration sandbox configuration
        const val DEFAULT_URL = "https://xmyqovfbfyopznbcjtpm.supabase.co/"
        const val DEFAULT_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhteXFvdmZiZnlvcHpuYmNqdHBtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDk4NTYwMDAsImV4cCI6MjAyNTQzMjAwMH0.sample_key_token"
    }

    var isLabDarkMode: Boolean
        get() = prefs.getBoolean(KEY_LAB_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_LAB_DARK_MODE, value).apply()

    var supabaseUrl: String
        get() = prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL
        set(value) {
            val sanitized = if (value.isNotBlank() && !value.endsWith("/")) "$value/" else value
            prefs.edit().putString(KEY_URL, sanitized).apply()
        }

    var supabaseAnonKey: String
        get() = prefs.getString(KEY_ANON_KEY, DEFAULT_ANON_KEY) ?: DEFAULT_ANON_KEY
        set(value) = prefs.edit().putString(KEY_ANON_KEY, value).apply()

    var isAutoSyncEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SYNC, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SYNC, value).apply()

    val isConfigured: Boolean
        get() = supabaseUrl.isNotBlank() && supabaseAnonKey.isNotBlank()
}
