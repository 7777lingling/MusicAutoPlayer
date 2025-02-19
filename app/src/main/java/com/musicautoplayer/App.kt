package com.musicautoplayer

import android.app.Application
import android.content.Context
import com.musicautoplayer.utils.LanguageManager

class App : Application() {
    override fun attachBaseContext(base: Context) {
        val languageManager = LanguageManager(base)
        super.attachBaseContext(
            languageManager.updateResources(languageManager.getCurrentLanguage())
        )
    }
} 