package com.example.imageloading

import android.content.Context
import android.graphics.Bitmap
import com.example.tmdbRatingApp.imageLoading.FileCache
import com.example.tmdbRatingApp.imageLoading.MemoryCache
import com.example.tmdbRatingApp.imageLoading.Utils
import java.io.*

class CacheRepository(context: Context, newSize: Int) {
    private val memoryCache = MemoryCache(newSize)
    private val fileCache = FileCache(context)

    fun put(url: String, inputStream: InputStream) {
        val _file = fileCache.getFile(url)

        putInFileCache(_file, inputStream)
        val bitmap = Utils.decodeFile(_file)
        if (bitmap != null) {
            memoryCache.put(url, bitmap)
        }
    }

    private fun putInFileCache(_file: File, inputStream: InputStream) {
        val outputStream = FileOutputStream(_file)
        Utils.copyStream(inputStream, outputStream)
        outputStream.close()
    }

    fun get(url: String): Bitmap? {
        return memoryCache.get(url)
    }

    fun clear() {
        fileCache.clear()
        memoryCache.clear()
    }


}