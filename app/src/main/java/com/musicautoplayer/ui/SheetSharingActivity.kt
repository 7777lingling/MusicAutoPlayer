package com.musicautoplayer.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.musicautoplayer.R
import com.musicautoplayer.db.MusicDatabaseHelper
import com.musicautoplayer.utils.SheetSharingManager

class SheetSharingActivity : AppCompatActivity() {
    private lateinit var sharingManager: SheetSharingManager
    private lateinit var dbHelper: MusicDatabaseHelper
    private lateinit var qrCodeImageView: ImageView

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val (sheet, notes) = sharingManager.importFromFile(uri)
                    // 儲存匯入的樂譜
                    dbHelper.insertSheet(sheet)
                    Toast.makeText(this, "樂譜匯入成功", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "匯入失敗：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet_sharing)

        sharingManager = SheetSharingManager(this)
        dbHelper = MusicDatabaseHelper(this)
        qrCodeImageView = findViewById(R.id.qrCodeImageView)

        setupUI()
    }

    private fun setupUI() {
        // 分享按鈕
        findViewById<View>(R.id.shareButton).setOnClickListener {
            val sheet = dbHelper.getSheet(sheetId) // 假設有 sheetId
            if (sheet != null) {
                sharingManager.shareSheet(sheet, notes)
            }
        }

        // QR 碼掃描按鈕
        findViewById<View>(R.id.scanButton).setOnClickListener {
            IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("掃描樂譜 QR 碼")
                .initiateScan()
        }

        // 檔案匯入按鈕
        findViewById<View>(R.id.importButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/zip"
            }
            filePickerLauncher.launch(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            try {
                val (sheet, notes) = sharingManager.importFromQRData(result.contents)
                dbHelper.insertSheet(sheet)
                Toast.makeText(this, "樂譜匯入成功", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "QR 碼解析失敗：${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
} 