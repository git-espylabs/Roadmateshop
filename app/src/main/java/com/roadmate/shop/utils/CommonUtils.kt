package com.roadmate.shop.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.common.util.IOUtils
import com.yalantis.ucrop.util.FileUtils.*
import java.io.*
import java.net.URISyntaxException
import java.text.SimpleDateFormat


class CommonUtils {
    companion object{

        const val PREFIX = "stream2file"
        const val SUFFIX = ".tmp"

        @Throws(IOException::class)
        fun stream2file(`in`: InputStream?): File? {
            val tempFile =
                File.createTempFile(CommonUtils.PREFIX, CommonUtils.SUFFIX)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile)
                .use { out -> IOUtils.copyStream(`in`, out) }
            return tempFile
        }

        fun compressImage(imageFilePath: String, context: Context?): Uri? {
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(imageFilePath, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
            val maxHeight = 816.0f
            val maxWidth = 612.0f
            var imgRatio = actualWidth / actualHeight.toFloat()
            val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize =
                CommonUtils.calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(imageFilePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap =
                    Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )

//      check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(imageFilePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0
                )
                Log.d("EXIF", "Exif: $orientation")
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            val filename: String = imageFilePath
            try {
                out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return Uri.fromFile(File(filename))
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio =
                    Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio =
                    Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = width * height.toFloat()
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }

        fun getRealPathFromURI(
            context: Context,
            contentUri: Uri?
        ): String? {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
                val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor!!.moveToFirst()
                cursor.getString(column_index)
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }

        @SuppressLint("NewApi")
        @Throws(URISyntaxException::class)
        fun getFilePath(
            context: Context,
            uri: Uri
        ): String? {
            var uri = uri
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                    context.applicationContext,
                    uri
                )
            ) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(
                        split[1]
                    )
                }
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val column_index =
                        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }


        fun formatDate_yyyyMMdd(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date = SimpleDateFormat("yyyy-MM-dd").parse(inputDate)
                outputDate = SimpleDateFormat("dd-MM-yyyy").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }

        fun formatDate_ddMMyyyy(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date = SimpleDateFormat("dd-MM-yyyy").parse(inputDate)
                outputDate = SimpleDateFormat("yyyy-MM-dd").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }

        fun formatDate_yyyyMMddHHmmss(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputDate)
                outputDate = SimpleDateFormat("dd-MM-yyyy").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }

        fun formatTime_Hmmss(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date =
                    SimpleDateFormat("HH:mm:ss").parse(inputDate)
                outputDate = SimpleDateFormat("hh:mm a").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }

        fun formatTime_HmmA(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date =
                    SimpleDateFormat("hh:mm a").parse(inputDate)
                outputDate = SimpleDateFormat("HH:mm:ss").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }


        fun formatDate_yyyyMMddInWords(inputDate: String?): String? {
            var outputDate: String? = ""
            try {
                val date = SimpleDateFormat("yyyy-MM-dd").parse(inputDate)
                outputDate = SimpleDateFormat("dd MMM yyyy").format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return outputDate
        }

        fun capitalizeSentence(sententence: String): String? {
            var upperString = ""
            upperString = try {
                sententence.substring(0, 1).toUpperCase() + sententence.substring(1)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                ""
            }
            return upperString
        }

        fun getClassSimpleName(context: Context): String? {
            return context.javaClass.simpleName
        }

        fun getFileNameFromPathString(path: String): String{
            return path.substring(path.lastIndexOf("/") + 1);
        }

    }
}