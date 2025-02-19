package com.musicautoplayer.data

import android.content.Context
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    companion object {
        private const val PREF_NAME = "app_preferences"
        private const val KEY_DISCLAIMER_ACCEPTED = "disclaimer_accepted"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun hasAcceptedDisclaimer(): Boolean {
        return preferences.getBoolean(KEY_DISCLAIMER_ACCEPTED, false)
    }

    fun setDisclaimerAccepted(accepted: Boolean) {
        preferences.edit {
            putBoolean(KEY_DISCLAIMER_ACCEPTED, accepted)
        }
    }
} 