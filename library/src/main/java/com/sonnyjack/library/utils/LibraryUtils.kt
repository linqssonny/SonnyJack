package com.sonnyjack.library.utils

import android.content.Context
import com.blankj.utilcode.util.FileIOUtils
import java.io.File
import java.io.InputStream


object LibraryUtils {

    /**
     * 在 Android/data/包名/文件夹名/文件名
     */
    fun createFilePathInExternalFiles(
        context: Context,
        fileDirName: String,
        fileName: String
    ): String? {
        return try {
            var filesDir = context.getExternalFilesDir(null)
            var file = File(filesDir?.absolutePath + File.separator + fileDirName, fileName)
            if (file.exists()) {
                file.delete()
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 保存文件
     */
    fun writeFileFromIS(filePath: String?, inputStream: InputStream): Boolean {
        return FileIOUtils.writeFileFromIS(filePath, inputStream)
    }
}