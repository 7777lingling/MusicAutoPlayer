package com.musicautoplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.musicautoplayer.R
import com.musicautoplayer.ui.base.BaseActivity

class DisclaimerActivity : BaseActivity() {
    companion object {
        private const val PREF_NAME = "disclaimer_pref"
        private const val KEY_ACCEPTED = "disclaimer_accepted"

        fun hasAccepted(context: Context): Boolean {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ACCEPTED, false)
        }

        fun start(context: Context) {
            context.startActivity(Intent(context, DisclaimerActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.acceptButton).setOnClickListener {
            saveAcceptance()
            proceedToMain()
        }

        findViewById<Button>(R.id.declineButton).setOnClickListener {
            finish()
        }
    }

    private fun saveAcceptance() {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ACCEPTED, true)
            .apply()
    }

    private fun proceedToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        // 禁止返回，用戶必須選擇接受或拒絕
        // super.onBackPressed()
    }
} 