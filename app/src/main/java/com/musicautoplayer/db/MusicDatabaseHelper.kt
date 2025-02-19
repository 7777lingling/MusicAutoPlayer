package com.musicautoplayer.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.musicautoplayer.model.MusicSheet
import java.util.Date

class MusicDatabaseHelper(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "music_sheets.db"
        private const val DATABASE_VERSION = 1

        // 資料表名稱
        private const val TABLE_SHEETS = "music_sheets"

        // 欄位名稱
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_FILE_PATH = "file_path"
        private const val COLUMN_DATE_CREATED = "date_created"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_TOTAL_NOTES = "total_notes"
        private const val COLUMN_DURATION = "duration"

        // 建立資料表 SQL
        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_SHEETS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_FILE_PATH TEXT NOT NULL,
                $COLUMN_DATE_CREATED INTEGER NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_TOTAL_NOTES INTEGER DEFAULT 0,
                $COLUMN_DURATION INTEGER DEFAULT 0
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 簡單的升級策略：刪除舊表，創建新表
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SHEETS")
        onCreate(db)
    }

    /**
     * 插入新的音樂腳本
     */
    fun insertSheet(sheet: MusicSheet): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, sheet.name)
            put(COLUMN_FILE_PATH, sheet.filePath)
            put(COLUMN_DATE_CREATED, sheet.dateCreated.time)
            put(COLUMN_DESCRIPTION, sheet.description)
            put(COLUMN_TOTAL_NOTES, sheet.totalNotes)
            put(COLUMN_DURATION, sheet.duration)
        }

        return writableDatabase.use { db ->
            db.insert(TABLE_SHEETS, null, values)
        }
    }

    /**
     * 更新音樂腳本
     */
    fun updateSheet(sheet: MusicSheet): Int {
        val values = ContentValues().apply {
            put(COLUMN_NAME, sheet.name)
            put(COLUMN_DESCRIPTION, sheet.description)
            put(COLUMN_TOTAL_NOTES, sheet.totalNotes)
            put(COLUMN_DURATION, sheet.duration)
        }

        return writableDatabase.use { db ->
            db.update(TABLE_SHEETS, values, 
                "$COLUMN_ID = ?", 
                arrayOf(sheet.id.toString())
            )
        }
    }

    /**
     * 刪除音樂腳本
     */
    fun deleteSheet(id: Long): Int {
        return writableDatabase.use { db ->
            db.delete(TABLE_SHEETS, 
                "$COLUMN_ID = ?", 
                arrayOf(id.toString())
            )
        }
    }

    /**
     * 取得所有音樂腳本
     */
    fun getAllSheets(): List<MusicSheet> {
        val sheets = mutableListOf<MusicSheet>()
        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_FILE_PATH,
            COLUMN_DATE_CREATED,
            COLUMN_DESCRIPTION,
            COLUMN_TOTAL_NOTES,
            COLUMN_DURATION
        )

        readableDatabase.use { db ->
            db.query(
                TABLE_SHEETS,
                projection,
                null,
                null,
                null,
                null,
                "$COLUMN_DATE_CREATED DESC"
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    sheets.add(
                        MusicSheet(
                            id = cursor.getLong(0),
                            name = cursor.getString(1),
                            filePath = cursor.getString(2),
                            dateCreated = Date(cursor.getLong(3)),
                            description = cursor.getString(4),
                            totalNotes = cursor.getInt(5),
                            duration = cursor.getLong(6)
                        )
                    )
                }
            }
        }

        return sheets
    }

    /**
     * 搜尋音樂腳本
     */
    fun searchSheets(query: String): List<MusicSheet> {
        val sheets = mutableListOf<MusicSheet>()
        val selection = "$COLUMN_NAME LIKE ? OR $COLUMN_DESCRIPTION LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")

        readableDatabase.use { db ->
            db.query(
                TABLE_SHEETS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                "$COLUMN_DATE_CREATED DESC"
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    sheets.add(
                        MusicSheet(
                            id = cursor.getLong(0),
                            name = cursor.getString(1),
                            filePath = cursor.getString(2),
                            dateCreated = Date(cursor.getLong(3)),
                            description = cursor.getString(4),
                            totalNotes = cursor.getInt(5),
                            duration = cursor.getLong(6)
                        )
                    )
                }
            }
        }

        return sheets
    }

    /**
     * 根據 ID 取得音樂腳本
     */
    fun getSheet(id: Long): MusicSheet? {
        readableDatabase.use { db ->
            db.query(
                TABLE_SHEETS,
                null,
                "$COLUMN_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    return MusicSheet(
                        id = cursor.getLong(0),
                        name = cursor.getString(1),
                        filePath = cursor.getString(2),
                        dateCreated = Date(cursor.getLong(3)),
                        description = cursor.getString(4),
                        totalNotes = cursor.getInt(5),
                        duration = cursor.getLong(6)
                    )
                }
            }
        }
        return null
    }
} 