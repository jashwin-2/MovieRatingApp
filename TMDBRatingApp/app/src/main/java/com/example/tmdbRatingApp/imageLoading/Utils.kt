package com.example.tmdbRatingApp.imageLoading

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*
import java.lang.Exception


object Utils {
    fun copyStream(inputStream: InputStream, os: OutputStream) {
        val bufferSize = 1024
        try {
            val bytes = ByteArray(bufferSize)
            while (true) {

                //Read byte from input stream
                val count = inputStream.read(bytes, 0, bufferSize)
                if (count == -1) break

                //Write byte from output stream
                os.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
        }
    }

    fun decodeFile(f: File): Bitmap? {
        try {

            //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            val stream1 = FileInputStream(f)
            BitmapFactory.decodeStream(stream1, null, o)
            stream1.close()

            //Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            val REQUIRED_SIZE = 250
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            //decode with current scale values
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val stream2 = FileInputStream(f)
            val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
            stream2.close()
            return bitmap
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}