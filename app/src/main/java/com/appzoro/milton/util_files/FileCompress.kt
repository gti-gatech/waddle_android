package com.appzoro.milton.util_files

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import id.zelory.compressor.Compressor
import java.io.File

class FileCompress {

    companion object {

        @Throws(Exception::class)
        fun compressImage(file: File, mActivity: Activity): File {
            return Compressor(mActivity)
                .setMaxWidth(400)
                .setMaxHeight(400)
                .setQuality(60)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFile(file)
        }

        @Throws(Exception::class)
        fun compressImageLarge(file: File, mActivity: Activity): File {
            return Compressor(mActivity)
                .setMaxWidth(850)
                .setMaxHeight(850)
                .setQuality(70)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                //                .setCompressFormat(Bitmap.CompressFormat.PNG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                //                .build()
                .compressToFile(file)
        }

        fun getImageContentUri(imageFile: File, mActivity: Activity): Uri? {
            val filePath = imageFile.absolutePath
            val cursor = mActivity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath), null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(
                    cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID)
                )
                val baseUri = Uri.parse("content://media/external/images/media")
                return Uri.withAppendedPath(baseUri, "" + id)
            } else {
                if (imageFile.exists()) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, filePath)
                    return mActivity.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    )
                } else {
                    return null
                }
            }
        }
    }
}
