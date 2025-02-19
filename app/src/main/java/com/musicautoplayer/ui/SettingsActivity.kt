package com.musicautoplayer.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.musicautoplayer.R
import com.musicautoplayer.ui.base.BaseActivity
import com.musicautoplayer.utils.LanguageManager

class SettingsActivity : BaseActivity() {
    private lateinit var languageManager: LanguageManager
    private lateinit var languageListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        languageManager = LanguageManager(this)
        setupLanguageList()
    }

    private fun setupLanguageList() {
        languageListView = findViewById(R.id.languageListView)
        
        val languages = languageManager.getSupportedLanguages()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            languages.map { it.displayName }
        )

        languageListView.adapter = adapter
        languageListView.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage = languages[position]
            updateLanguage(selectedLanguage.code)
        }
    }
} 