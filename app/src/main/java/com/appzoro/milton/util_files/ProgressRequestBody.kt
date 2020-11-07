package com.appzoro.milton.util_files

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressRequestBody(
    private val mFile: File,
    private val mListener: UploadCallbacks) : RequestBody() {

    interface UploadCallbacks {
        fun onProgressUpdate(
            percentage: Int,
            mUploaded: Long,
            mTotal: Long
        )
    }

    override fun contentType(): MediaType? {
        return "multipart/form-data".toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer =
            ByteArray(DEFAULT_BUFFER_SIZE)
        FileInputStream(mFile).use { `in` ->
            var uploaded: Long = 0
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
        Runnable {
        override fun run() {
            mListener.onProgressUpdate((100 * mUploaded / mTotal).toInt(), mUploaded, mTotal)
        }

    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

}