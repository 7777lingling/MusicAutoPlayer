package com.musicautoplayer.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.musicautoplayer.utils.LanguageManager

open class BaseActivity : AppCompatActivity() {
    private lateinit var languageManager: LanguageManager

    override fun attachBaseContext(newBase: Context) {
        languageManager = LanguageManager(newBase)
        val context = languageManager.updateResources(languageManager.getCurrentLanguage())
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageManager = LanguageManager(this)
    }

    protected fun updateLanguage(languageCode: String) {
        languageManager.setLanguage(languageCode)
        recreate()
    }
} 