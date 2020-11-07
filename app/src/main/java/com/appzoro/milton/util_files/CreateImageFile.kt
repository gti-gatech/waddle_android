package com.appzoro.milton.util_files

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.appzoro.milton.BuildConfig
import java.io.File
import java.io.IOException
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

class CreateImageFile {

    companion object {

        @Throws(IOException::class)
        fun createImageFile(mActivity: Activity): File {

            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
            val storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            return File.createTempFile(
                timeStamp, //prefix
                ".jpg", //suffix
                storageDir       //directory
            )
        }

        fun getRealPathFromURI(mActivity: Activity, contentURI: Uri): String? {
            //  val result: String
            val cursor = mActivity.contentResolver.query(
                contentURI, null,
                null, null, null
            )
            return if (cursor == null) { // Source is Dropbox or other similar local file path
                contentURI.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                val result = cursor.getString(idx)
                cursor.close()
                result
            }
        }

        fun takeImageFromCamera(mActivity: Activity, mCameraFile: File, requestCode: Int) {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val mCameraFileUri = FileProvider.getUriForFile(
                mActivity,
                BuildConfig.APPLICATION_ID + ".provider", mCameraFile
            )

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri)
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0)
            // start the image capture Intent
            mActivity.startActivityForResult(intent, requestCode)

        }

        fun getFileType(path: String): String {
            var mimeType = "other"
            mimeType = try {
                URLConnection.guessContentTypeFromName(path)
            } catch (e: Exception) {
                e.printStackTrace()
                return mimeType
            }
            return if (mimeType != null && mimeType.startsWith("image")) {
                "image"
            } else if (mimeType != null && mimeType.startsWith("video")) {
                "video"
            } else {
                "other"
            }
        }

    }

}