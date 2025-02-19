package com.musicautoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 檢查是否已同意免責聲明
        if (!DisclaimerActivity.hasAccepted(this)) {
            DisclaimerActivity.start(this)
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
    }
} 