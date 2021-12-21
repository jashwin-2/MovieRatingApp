package com.example.tmdbRatingApp.imageLoading

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.example.imageloading.CacheRepository
import com.example.imageloading.DownloadTask
import java.net.HttpURLConnection
import java.net.URL

class DownloadImageTask(
    private val fileCache : FileCache,
    private val url: String,
    private val imageView: ImageView,
    private val cache: CacheRepository,

    ) : DownloadTask<Bitmap?>() {

    private val uiHandler = Handler(Looper.getMainLooper())

    override fun run() {
        val bitmap = download(url)
        bitmap?.let {
            if (imageView.tag == url) {
                updateImageView(imageView, it)
            }
        }
    }

    override fun download(url: String): Bitmap? {

        var bitmap: Bitmap? = null
        val _file = fileCache.getFile(url)
        bitmap = Utils.decodeFile(_file)
        if(bitmap != null)
            return bitmap

        try {
            val _url = URL(url)
            val conn: HttpURLConnection = _url.openConnection() as
                    HttpURLConnection
            val inputStream = conn.inputStream
            cache.put(url, inputStream)
            conn.disconnect()

            bitmap = cache.get(url)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun updateImageView(imageview: ImageView, bitmap: Bitmap) {
        uiHandler.post(BitmapDisplayer(bitmap , imageview , url))
    }

    inner class BitmapDisplayer(var bitmap: Bitmap?, val imageview: ImageView, val url: String) : Runnable {
        override fun run() {
            // Show bitmap on UI
            if (bitmap != null) imageView.setImageBitmap(bitmap)
        }
    }
}