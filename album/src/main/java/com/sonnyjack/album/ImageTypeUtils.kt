package com.sonnyjack.album

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


object ImageTypeUtils {

    private fun getFileByPath(filePath: String?): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * Return the type of image.
     *
     * @param filePath The path of file.
     * @return the type of image
     */
    fun getImageType(filePath: String?): String {
        return getImageType(getFileByPath(filePath))
    }

    /**
     * Return the type of image.
     *
     * @param file The file.
     * @return the type of image
     */
    fun getImageType(file: File?): String {
        if (file == null) return ""
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            val type = getImageType(inputStream)
            if (type != null) {
                return type
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (inputStream != null) {
                    inputStream!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return getFileExtension(file!!.absolutePath).toUpperCase()
    }

    private fun getFileExtension(filePath: String): String {
        if (isSpace(filePath)) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    private fun getImageType(inputStream: InputStream?): String? {
        if (inputStream == null) return null
        return try {
            val bytes = ByteArray(8)
            if (inputStream!!.read(bytes, 0, 8) !== -1) getImageType(bytes) else null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    private fun getImageType(bytes: ByteArray): String? {
        if (isJPEG(bytes)) return "JPEG"
        if (isGIF(bytes)) return "GIF"
        if (isPNG(bytes)) return "PNG"
        return if (isBMP(bytes)) "BMP" else null
    }

    private fun isJPEG(b: ByteArray): Boolean {
        return (b.size >= 2
                && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte())
    }

    private fun isGIF(b: ByteArray): Boolean {
        return (b.size >= 6
                && b[0] == 'G'.toByte() && b[1] == 'I'.toByte()
                && b[2] == 'F'.toByte() && b[3] == '8'.toByte()
                && (b[4] == '7'.toByte() || b[4] == '9'.toByte()) && b[5] == 'a'.toByte())
    }

    private fun isPNG(b: ByteArray): Boolean {
        return b.size >= 8 && (b[0] == 137.toByte() && b[1] == 80.toByte()
                && b[2] == 78.toByte() && b[3] == 71.toByte()
                && b[4] == 13.toByte() && b[5] == 10.toByte()
                && b[6] == 26.toByte() && b[7] == 10.toByte())
    }

    private fun isBMP(b: ByteArray): Boolean {
        return (b.size >= 2
                && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d)
    }
}