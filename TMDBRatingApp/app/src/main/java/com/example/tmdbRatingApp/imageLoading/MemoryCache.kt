package com.example.tmdbRatingApp.imageLoading

import android.graphics.Bitmap
import android.util.LruCache
import com.example.imageloading.Config

class MemoryCache (newMaxSize: Int)  {
    private val cache : LruCache<String, Bitmap>
    init {
        val cacheSize : Int = if (newMaxSize > Config.maxMemory) {
            Config.defaultCacheSize

        } else {
            newMaxSize
        }
        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return (value.rowBytes)*(value.height)/1024
            }
        }
    }

     fun put(url: String, bitmap: Bitmap) {
        cache.put(url,bitmap)
    }

     fun get(url: String): Bitmap? {
        return cache.get(url)
    }

     fun clear() {
        cache.evictAll()
    }
}