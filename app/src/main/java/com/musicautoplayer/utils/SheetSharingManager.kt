package com.musicautoplayer.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.musicautoplayer.model.MusicSheet
import com.musicautoplayer.model.Note
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.*
import java.util.*

class SheetSharingManager(private val context: Context) {
    companion object {
        private const val SHARE_FILE_PREFIX = "music_sheet_"
        private const val METADATA_FILE = "metadata.json"
        private const val NOTES_FILE = "notes.json"
        private const val MAX_QR_DATA_SIZE = 2953 // QR碼最大容量
    }

    private val gson = GsonBuilder().create()

    /**
     * 產生分享用的 QR 碼
     */
    fun generateQRCode(sheet: MusicSheet, notes: List<Note>): Bitmap {
        // 創建分享資料
        val shareData = ShareData(
            sheet = sheet,
            notes = notes
        )

        // 轉換為 JSON
        val jsonData = gson.toJson(shareData)
        
        // 如果資料太大，則使用檔案分享
        if (jsonData.length > MAX_QR_DATA_SIZE) {
            throw ShareException("資料太大，請使用檔案分享")
        }

        // 產生 QR 碼
        val bitMatrix = MultiFormatWriter().encode(
            jsonData,
            BarcodeFormat.QR_CODE,
            512,
            512
        )

        return createBitmap(bitMatrix)
    }

    /**
     * 產生分享用的檔案
     */
    fun createShareFile(sheet: MusicSheet, notes: List<Note>): File {
        val shareDir = File(context.cacheDir, "share")
        shareDir.mkdirs()

        // 創建臨時 ZIP 檔案
        val zipFile = File(shareDir, "${SHARE_FILE_PREFIX}${sheet.id}.zip")
        
        ZipArchiveOutputStream(zipFile).use { zipOut ->
            // 寫入中繼資料
            addToZip(zipOut, METADATA_FILE, gson.toJson(sheet))
            
            // 寫入音符資料
            addToZip(zipOut, NOTES_FILE, gson.toJson(notes))
        }

        return zipFile
    }

    /**
     * 從 QR 碼資料匯入
     */
    fun importFromQRData(qrData: String): Pair<MusicSheet, List<Note>> {
        val shareData = gson.fromJson(qrData, ShareData::class.java)
        return Pair(shareData.sheet, shareData.notes)
    }

    /**
     * 從檔案匯入
     */
    fun importFromFile(uri: Uri): Pair<MusicSheet, List<Note>> {
        // 複製檔案到臨時目錄
        val tempFile = createTempFile(uri)
        
        try {
            ZipFile(tempFile).use { zip ->
                // 讀取中繼資料
                val metadataEntry = zip.getEntry(METADATA_FILE)
                val sheet = gson.fromJson(
                    zip.getInputStream(metadataEntry).reader(),
                    MusicSheet::class.java
                )

                // 讀取音符資料
                val notesEntry = zip.getEntry(NOTES_FILE)
                val notes = gson.fromJson<List<Note>>(
                    zip.getInputStream(notesEntry).reader(),
                    object : TypeToken<List<Note>>() {}.type
                )

                return Pair(sheet, notes)
            }
        } finally {
            tempFile.delete()
        }
    }

    /**
     * 分享樂譜
     */
    fun shareSheet(sheet: MusicSheet, notes: List<Note>) {
        val shareFile = createShareFile(sheet, notes)
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/zip"
            putExtra(
                Intent.EXTRA_STREAM,
                Uri.fromFile(shareFile)
            )
            putExtra(Intent.EXTRA_SUBJECT, "分享樂譜：${sheet.name}")
        }

        val chooserIntent = Intent.createChooser(
            shareIntent,
            "選擇分享方式"
        )

        context.startActivity(chooserIntent)
    }

    private fun addToZip(zipOut: ZipArchiveOutputStream, name: String, content: String) {
        val entry = ZipArchiveEntry(name)
        zipOut.putArchiveEntry(entry)
        zipOut.write(content.toByteArray())
        zipOut.closeArchiveEntry()
    }

    private fun createTempFile(uri: Uri): File {
        val tempFile = File.createTempFile("import_", ".zip", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun createBitmap(matrix: BitMatrix): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (matrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }

        return bitmap
    }

    private data class ShareData(
        val sheet: MusicSheet,
        val notes: List<Note>
    )

    class ShareException(message: String) : Exception(message)
} 