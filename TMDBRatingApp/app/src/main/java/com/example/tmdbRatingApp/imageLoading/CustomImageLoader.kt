package com.example.imageloading

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.imageloading.Config.Companion.MAX_CACHE_SIZE
import com.example.tmdbRatingApp.imageLoading.DownloadImageTask
import com.example.tmdbRatingApp.imageLoading.FileCache
import java.util.*
import java.util.concurrent.Executors

class CustomImageLoader private constructor(context: Context) {
    val imageviews = Collections.synchronizedMap(WeakHashMap<ImageView , String>())
    private val cache = CacheRepository(context , MAX_CACHE_SIZE)
    private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    private val file = FileCache(context)
    fun displayImage(url: String, imageview: ImageView, placeholder:
    Int) {
        imageviews[imageview] = url
        imageview.setImageResource(placeholder)
        val bitmap = cache.get(url)
        bitmap?.let {
            imageview.setImageBitmap(it)
            return
        }
            ?: run {
                imageview.setImageResource(placeholder)
                imageview.tag = url
                addDownloadImageTask(DownloadImageTask(file , url , imageview , cache)) }

    }

    private fun addDownloadImageTask(downloadTask: DownloadTask<Bitmap?>) {
        executorService.submit(downloadTask)
    }

    fun clearCache() {
        cache.clear()
    }


    companion object {
        private val INSTANCE: CustomImageLoader? = null
        @Synchronized
        fun getInstance(context: Context): CustomImageLoader {
            return INSTANCE?.let { return INSTANCE }
                ?: run {
                    return CustomImageLoader(context)
                }
        }
    }

}