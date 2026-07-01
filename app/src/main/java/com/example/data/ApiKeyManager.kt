package com.example.data

import android.content.Context
import android.content.SharedPreferences

object ApiKeyManager {
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences("api_keys_prefs", Context.MODE_PRIVATE)
        }
    }

    fun getGeminiKey(): String {
        val key = prefs?.getString("api_key_gemini", "") ?: ""
        return if (key.isNotEmpty()) key else com.example.BuildConfig.GEMINI_API_KEY
    }

    fun getGitHubKey(): String {
        return prefs?.getString("api_key_github", "") ?: ""
    }

    fun getTelegramKey(): String {
        return prefs?.getString("api_key_telegram", "") ?: ""
    }

    fun getHuggingFaceKey(): String {
        return prefs?.getString("api_key_huggingface", "") ?: ""
    }

    fun getOpenAIKey(): String {
        return prefs?.getString("api_key_openai", "") ?: ""
    }

    fun getAnthropicKey(): String {
        return prefs?.getString("api_key_anthropic", "") ?: ""
    }

    fun saveKeys(
        gemini: String,
        github: String,
        telegram: String,
        huggingface: String,
        openai: String,
        anthropic: String
    ) {
        prefs?.edit()?.apply {
            putString("api_key_gemini", gemini.trim())
            putString("api_key_github", github.trim())
            putString("api_key_telegram", telegram.trim())
            putString("api_key_huggingface", huggingface.trim())
            putString("api_key_openai", openai.trim())
            putString("api_key_anthropic", anthropic.trim())
            apply()
        }
    }
}
