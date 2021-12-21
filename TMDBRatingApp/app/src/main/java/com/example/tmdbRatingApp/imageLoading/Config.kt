package com.example.imageloading
class Config {
    companion object {
       val maxMemory = Runtime.getRuntime().maxMemory() /1024
        val defaultCacheSize = (maxMemory/4).toInt()
        const val MAX_CACHE_SIZE = 100 * 1024 * 1024
    }
}