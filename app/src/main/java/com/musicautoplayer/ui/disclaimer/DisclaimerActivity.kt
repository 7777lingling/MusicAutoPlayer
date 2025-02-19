package com.musicautoplayer.ui.disclaimer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.musicautoplayer.MainActivity
import com.musicautoplayer.R
import com.musicautoplayer.databinding.ActivityDisclaimerBinding
import com.musicautoplayer.ui.base.BaseActivity
import com.google.android.material.button.MaterialButton

class DisclaimerActivity : BaseActivity() {
    private val viewModel: DisclaimerViewModel by viewModels()
    private lateinit var binding: ActivityDisclaimerBinding

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, DisclaimerActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisclaimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.acceptButton.setOnClickListener {
            viewModel.onAcceptClick()
        }

        binding.declineButton.setOnClickListener {
            viewModel.onDeclineClick()
        }
    }

    private fun observeViewModel() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is DisclaimerViewModel.NavigationEvent.NavigateToMain -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is DisclaimerViewModel.NavigationEvent.Finish -> {
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        // 禁止返回，用戶必須選擇接受或拒絕
    }
} 