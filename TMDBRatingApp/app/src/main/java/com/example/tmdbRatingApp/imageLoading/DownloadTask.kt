package com.example.imageloading

abstract class DownloadTask<T> : Runnable {
    abstract fun download(url: String): T
}

