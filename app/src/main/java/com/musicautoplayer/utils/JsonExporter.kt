package com.musicautoplayer.utils

import android.content.Context
import com.google.gson.GsonBuilder
import com.musicautoplayer.model.MusicScript
import com.musicautoplayer.model.Note
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class JsonExporter(private val context: Context) {
    companion object {
        private const val SCRIPTS_DIR = "scripts"
        private val DATE_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    }

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    /**
     * 將音符列表匯出為 JSON 檔案
     * @param notes 音符列表
     * @param fileName 檔案名稱（可選）
     * @return 儲存的檔案
     */
    @Throws(IOException::class)
    fun exportToJson(
        notes: List<Note>,
        fileName: String? = null
    ): File {
        // 創建腳本物件
        val script = MusicScript(notes = notes)

        // 準備檔案名稱
        val actualFileName = fileName ?: generateFileName()
        
        // 確保目錄存在
        val scriptDir = getScriptsDirectory()
        if (!scriptDir.exists()) {
            scriptDir.mkdirs()
        }

        // 創建檔案
        val file = File(scriptDir, "$actualFileName.json")

        // 寫入 JSON
        file.writeText(gson.toJson(script))

        return file
    }

    /**
     * 從 JSON 檔案讀取音符列表
     * @param file JSON 檔案
     * @return 音符列表
     */
    @Throws(IOException::class)
    fun importFromJson(file: File): List<Note> {
        val script = gson.fromJson(file.readText(), MusicScript::class.java)
        return script.notes
    }

    /**
     * 取得腳本目錄
     */
    private fun getScriptsDirectory(): File {
        return File(context.getExternalFilesDir(null), SCRIPTS_DIR)
    }

    /**
     * 產生檔案名稱
     */
    private fun generateFileName(): String {
        return "script_${DATE_FORMAT.format(Date())}"
    }

    /**
     * 列出所有已儲存的腳本
     */
    fun listSavedScripts(): List<File> {
        val scriptDir = getScriptsDirectory()
        return if (scriptDir.exists()) {
            scriptDir.listFiles { file -> 
                file.isFile && file.extension == "json" 
            }?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * 刪除腳本
     */
    fun deleteScript(file: File): Boolean {
        return file.delete()
    }
} 