package com.ifkusyoba.dicogram.utility

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import com.ifkusyoba.dicogram.R
import java.io.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

object Files {
    private const val MAXIMAL_SIZE = 1000000

    @SuppressLint("ConstantLocale")
    val currentTimestamp: String = SimpleDateFormat(
        "ddMMyySSSSS",
        Locale.getDefault()
    ).format(System.currentTimeMillis())

    private fun getRandomString(length: Int = 20): String {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(length) { alphabet.random() }.joinToString("")
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(currentTimestamp, ".jpg", storageDir)
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)
        try {
            val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
            val outputStream: OutputStream = FileOutputStream(myFile)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (error: NullPointerException) {
            print("ERROR WOIIII $error")
        }
        return myFile
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "Story-${currentTimestamp}.jpg")
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    fun reduceFileImage(file: File, isBackCamera: Boolean): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val rotatedBitmap = rotateBitmap(bitmap, isBackCamera)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}