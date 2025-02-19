package com.musicautoplayer.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*

class LanguageManager(private val context: Context) {
    companion object {
        private const val PREF_NAME = "language_pref"
        private const val KEY_LANGUAGE = "selected_language"
        
        // 支援的語言代碼
        const val ENGLISH = "en"
        const val CHINESE_TRADITIONAL = "zh-TW"
        const val CHINESE_SIMPLIFIED = "zh-CN"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * 取得目前的語言設定
     */
    fun getCurrentLanguage(): String {
        return preferences.getString(KEY_LANGUAGE, getSystemLanguage()) ?: ENGLISH
    }

    /**
     * 設定應用程式語言
     */
    fun setLanguage(languageCode: String) {
        if (getCurrentLanguage() == languageCode) return

        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
        updateResources(languageCode)
    }

    /**
     * 更新資源設定
     */
    fun updateResources(languageCode: String): Context {
        val locale = when (languageCode) {
            CHINESE_TRADITIONAL -> Locale("zh", "TW")
            CHINESE_SIMPLIFIED -> Locale("zh", "CN")
            else -> Locale(languageCode)
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }

    /**
     * 取得系統語言
     */
    private fun getSystemLanguage(): String {
        val locale = Resources.getSystem().configuration.locales[0]
        return when {
            locale.language == "zh" -> {
                when (locale.country.uppercase()) {
                    "TW", "HK" -> CHINESE_TRADITIONAL
                    "CN" -> CHINESE_SIMPLIFIED
                    else -> CHINESE_TRADITIONAL
                }
            }
            else -> ENGLISH
        }
    }

    /**
     * 取得支援的語言列表
     */
    fun getSupportedLanguages(): List<Language> {
        return listOf(
            Language(ENGLISH, context.getString(R.string.language_english)),
            Language(CHINESE_TRADITIONAL, context.getString(R.string.language_chinese_traditional)),
            Language(CHINESE_SIMPLIFIED, context.getString(R.string.language_chinese_simplified))
        )
    }

    data class Language(
        val code: String,
        val displayName: String
    )
} 