package com.musicautoplayer.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicautoplayer.R
import com.musicautoplayer.ui.adapter.ConfigAdapter
import com.musicautoplayer.utils.ConfigManager

class ConfigListActivity : AppCompatActivity() {
    private lateinit var configManager: ConfigManager
    private lateinit var configAdapter: ConfigAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_list)

        configManager = ConfigManager(this)
        setupRecyclerView()
        loadConfigs()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.configRecyclerView)
        configAdapter = ConfigAdapter(
            configs = emptyList(),
            onConfigClick = { configFile ->
                // 處理點擊事件
                openConfigEditor(configFile)
            },
            onConfigLongClick = { configFile ->
                // 處理長按事件
                showDeleteDialog(configFile)
                true
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ConfigListActivity)
            adapter = configAdapter
        }
    }

    private fun loadConfigs() {
        val configs = configManager.getAllConfigs()
        configAdapter.updateConfigs(configs)
    }

    private fun showDeleteDialog(configFile: ConfigManager.ConfigFile) {
        AlertDialog.Builder(this)
            .setTitle("刪除配置")
            .setMessage("確定要刪除「${configFile.config.name}」嗎？")
            .setPositiveButton("刪除") { _, _ ->
                if (configManager.deleteConfig(configFile.fileName)) {
                    loadConfigs()
                    Toast.makeText(this, "配置已刪除", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun openConfigEditor(configFile: ConfigManager.ConfigFile) {
        // TODO: 實作配置編輯器
        Toast.makeText(this, "開啟配置：${configFile.config.name}", Toast.LENGTH_SHORT).show()
    }
} 