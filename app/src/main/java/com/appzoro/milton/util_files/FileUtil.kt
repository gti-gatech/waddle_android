package com.appzoro.milton.util_files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.appzoro.milton.utility.Constant
import java.io.*

class FileUtil {

    companion object {

        private const val EOF = -1
        private const val DEFAULT_BUFFER_SIZE = 1024 * 4

        @Throws(IOException::class)
        fun from(context: Context, uri: Uri): File {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri)
            val splitName = splitFileName(fileName)
            var tempFile = File.createTempFile(splitName[0], splitName[1])
            tempFile = rename(tempFile, fileName)
            tempFile.deleteOnExit()
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(tempFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            if (inputStream != null) {
                copy(inputStream, out)
                inputStream.close()
            }

            out?.close()
            return tempFile
        }

        private fun splitFileName(fileName: String): Array<String> {
            var name = fileName
            var extension = ""
            val i = fileName.lastIndexOf(".")
            if (i != -1) {
                name = fileName.substring(0, i)
                extension = fileName.substring(i)
            }

            return arrayOf(name, extension)
        }

        private fun getFileName(context: Context, uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf(File.separator)
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }

        private fun rename(file: File, newName: String): File {
            val newFile = File(file.parent, newName)
            if (newFile != file) {
                if (newFile.exists() && newFile.delete()) {
                    Log.e(Constant.TAG, "Delete old $newName file")
                }
                if (file.renameTo(newFile)) {
                    Log.e(Constant.TAG, "Rename file to $newName")
                }
            }
            return newFile
        }

        @Throws(IOException::class)
        private fun copy(input: InputStream, output: OutputStream?): Long {
            var count: Long = 0
            var n: Int
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (((input.read(buffer))).let { n = it; it != EOF }) {
//            while (EOF != (n = input.read(buffer))) {
                output!!.write(buffer, 0, n)
                count += n.toLong()
            }
            return count
        }

        fun deleteFile(filePth: String) {

            val fDelete = File(filePth)
            if (fDelete.exists()) {
                if (fDelete.delete()) {
                    //                System.out.println("file Deleted :" + filePth);
                    Log.e(Constant.TAG, "Deleted : $filePth")
                    //                return true;
                } else {
                    //                System.out.println("file not Deleted :" + filePth);
                    Log.e(Constant.TAG, " not Deleted: $filePth")
                    //                return false;
                }
                //        }else {
                //            return false;
            }

        }

        fun deleteFile(fDelete: File) {

            //        File fDelete = new File(filePth);
            if (fDelete.exists()) {
                if (fDelete.delete()) {
                    //                System.out.println("file Deleted :" + filePth);
                    Log.e(Constant.TAG, "Deleted :$fDelete")
                    //                return true;
                } else {
                    Log.e(Constant.TAG, " not Deleted :$fDelete")
                    //                return false;
                }
                //        }else {
                //            return false;
            }

        }
    }

}