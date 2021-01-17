package com.roadmate.shop.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import com.roadmate.shop.log.AppLogger
import java.io.*

class ImageUtils {


    companion object{
        private var ROOT_FOLDER_CACHE_IMAGE = ""
        const val _200KB = 200 * 1024
        private var compressedImageFileName = ""


        fun getCompressedPath(path : String,  context: Context, compressedFileName: String): String? {

            ROOT_FOLDER_CACHE_IMAGE = context.getExternalFilesDir(null).toString() + "/Image Cache"

            compressedImageFileName = compressedFileName

            return resizeAndCompressFile(path)
        }

    private fun resizeAndCompressFile(filePath: String): String? {
        val imageFile = File(filePath)
        if (imageFile.exists()) {
            val fileSize = imageFile.length()
            if (fileSize > 0) {
                return if (fileSize < _200KB) {
                    filePath
                } else {
                    resizeAndCompressBitmapTo200KB(filePath)
                }
            }
        }
        return null
    }

    private fun resizeAndCompressBitmapTo200KB(filePath: String): String? {
        val imageFile = File(filePath)
        val fileSize = imageFile.length()
        AppLogger.error("ImageUtils", "size of original file = $fileSize")

        if (fileSize > _200KB) {
            var qualityCompress = 80
            if (fileSize > 3145728) {// > 3MB
                qualityCompress = 55
            } else if (fileSize > 2097152) {// > 2MB
                qualityCompress = 60
            } else if (fileSize > 1560576) {// > 1.5MB
                qualityCompress = 65
            } else if (fileSize > 1048576) {// > 1MB
                qualityCompress = 70
            }

            var newFilePath: String?
            do {
                newFilePath = compressFileAndReturnNewPathOfNewFile(filePath, qualityCompress)
                qualityCompress -= 5

                //TODO test
                newFilePath?.let {
                    AppLogger.error(
                        "ImageUtils",
                        "qualityCompress = " + qualityCompress + "size of new file = " + File(newFilePath).length()
                    )
                }
            } while (newFilePath != null && File(newFilePath).length() > _200KB)
            //copy attributes from old exif to new exif
            if (newFilePath != null) {
                copyExif(filePath, newFilePath)
            }

            return newFilePath
        }
        return filePath
    }

    private fun compressFileAndReturnNewPathOfNewFile(filePath: String, qualityCompress: Int): String? {
        try {
            val inputStream = FileInputStream(filePath)
            var compressBitmap = BitmapFactory.decodeStream(inputStream)
            //original width height
            val widthOriginal = compressBitmap.width
            val heightOriginal = compressBitmap.height

            //resize image 50% (keep original scale)
            val width50Percent: Int = (widthOriginal / 1.41421356237).toInt()
            val height50Percent: Int = (heightOriginal / 1.41421356237).toInt()
            //
            val scaleWidth: Float = width50Percent.toFloat() / widthOriginal
            val scaleHeight: Float = height50Percent.toFloat() / heightOriginal
            //
            //Must Rotate bitmap before upload them
            val matrix = Matrix()
            matrix.setRotate(getOrientation(filePath).toFloat())
            matrix.postScale(scaleWidth, scaleHeight);

            compressBitmap = Bitmap.createBitmap(
                compressBitmap, 0, 0, compressBitmap.width,
                compressBitmap.height, matrix, true
            )
            //make a new file directory inside the "sdcard" folder
            val mediaStorageDir = File(ROOT_FOLDER_CACHE_IMAGE)
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            var file = File(mediaStorageDir.absolutePath, compressedImageFileName)
            if (file.exists()) {
                file.deleteOnExit()
                file = File(mediaStorageDir.absolutePath, compressedImageFileName)
            }

            val fos = FileOutputStream(file)
            compressBitmap.compress(Bitmap.CompressFormat.JPEG, qualityCompress, fos)
            fos.flush()
            fos.close()

            inputStream.close()
            compressBitmap.recycle()
            compressBitmap = null
            // Use this for reading the data.
            /*val inputStream = FileInputStream(file.absolutePath)
            val buffer = ByteArray(file.length().toInt())
            inputStream.read(buffer)
            inputStream.close()
            return buffer*/
            return file.absolutePath

        } catch (e1: FileNotFoundException) {
            AppLogger.error("ImageUtils", "compressFileToByteArray(1)$e1")
        } catch (e2: IOException) {
            AppLogger.error("ImageUtils", "compressFileToByteArray(2)$e2")
        } catch (e3: Exception) {
            AppLogger.error("ImageUtils", "compressFileToByteArray(3)$e3")
        }
        return null
    }

    private fun copyExif(oldPath: String, newPath: String) {
        val attributes = arrayOf(
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_BITS_PER_SAMPLE,
            ExifInterface.TAG_COMPRESSION,
            ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_SAMPLES_PER_PIXEL,
            ExifInterface.TAG_PLANAR_CONFIGURATION,
            ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING,
            ExifInterface.TAG_Y_CB_CR_POSITIONING,
            ExifInterface.TAG_X_RESOLUTION,
            ExifInterface.TAG_Y_RESOLUTION,
            ExifInterface.TAG_RESOLUTION_UNIT,
            ExifInterface.TAG_STRIP_OFFSETS,
            ExifInterface.TAG_ROWS_PER_STRIP,
            ExifInterface.TAG_STRIP_BYTE_COUNTS,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
            ExifInterface.TAG_TRANSFER_FUNCTION,
            ExifInterface.TAG_WHITE_POINT,
            ExifInterface.TAG_PRIMARY_CHROMATICITIES,
            ExifInterface.TAG_Y_CB_CR_COEFFICIENTS,
            ExifInterface.TAG_REFERENCE_BLACK_WHITE,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_IMAGE_DESCRIPTION,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_SOFTWARE,
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_COPYRIGHT,
            ExifInterface.TAG_EXIF_VERSION,
            ExifInterface.TAG_FLASHPIX_VERSION,
            ExifInterface.TAG_COLOR_SPACE,
            ExifInterface.TAG_GAMMA,
            ExifInterface.TAG_PIXEL_X_DIMENSION,
            ExifInterface.TAG_PIXEL_Y_DIMENSION,
            ExifInterface.TAG_COMPONENTS_CONFIGURATION,
            ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL,
            ExifInterface.TAG_MAKER_NOTE,
            ExifInterface.TAG_USER_COMMENT,
            ExifInterface.TAG_RELATED_SOUND_FILE,
            ExifInterface.TAG_DATETIME_ORIGINAL,
            ExifInterface.TAG_DATETIME_DIGITIZED,
            ExifInterface.TAG_SUBSEC_TIME,
            ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
            ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_EXPOSURE_PROGRAM,
            ExifInterface.TAG_SPECTRAL_SENSITIVITY,
            ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
            ExifInterface.TAG_OECF,
            ExifInterface.TAG_SENSITIVITY_TYPE,
            ExifInterface.TAG_STANDARD_OUTPUT_SENSITIVITY,
            ExifInterface.TAG_RECOMMENDED_EXPOSURE_INDEX,
            ExifInterface.TAG_ISO_SPEED,
            ExifInterface.TAG_ISO_SPEED_LATITUDE_YYY,
            ExifInterface.TAG_ISO_SPEED_LATITUDE_ZZZ,
            ExifInterface.TAG_SHUTTER_SPEED_VALUE,
            ExifInterface.TAG_APERTURE_VALUE,
            ExifInterface.TAG_BRIGHTNESS_VALUE,
            ExifInterface.TAG_EXPOSURE_BIAS_VALUE,
            ExifInterface.TAG_MAX_APERTURE_VALUE,
            ExifInterface.TAG_SUBJECT_DISTANCE,
            ExifInterface.TAG_METERING_MODE,
            ExifInterface.TAG_LIGHT_SOURCE,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_SUBJECT_AREA,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_FLASH_ENERGY,
            ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE,
            ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION,
            ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION,
            ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT,
            ExifInterface.TAG_SUBJECT_LOCATION,
            ExifInterface.TAG_EXPOSURE_INDEX,
            ExifInterface.TAG_SENSING_METHOD,
            ExifInterface.TAG_FILE_SOURCE,
            ExifInterface.TAG_SCENE_TYPE,
            ExifInterface.TAG_CFA_PATTERN,
            ExifInterface.TAG_CUSTOM_RENDERED,
            ExifInterface.TAG_EXPOSURE_MODE,
            ExifInterface.TAG_WHITE_BALANCE,
            ExifInterface.TAG_DIGITAL_ZOOM_RATIO,
            ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM,
            ExifInterface.TAG_SCENE_CAPTURE_TYPE,
            ExifInterface.TAG_GAIN_CONTROL,
            ExifInterface.TAG_CONTRAST,
            ExifInterface.TAG_SATURATION,
            ExifInterface.TAG_SHARPNESS,
            ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,
            ExifInterface.TAG_SUBJECT_DISTANCE_RANGE,
            ExifInterface.TAG_IMAGE_UNIQUE_ID,
            ExifInterface.TAG_CAMARA_OWNER_NAME,
            ExifInterface.TAG_BODY_SERIAL_NUMBER,
            ExifInterface.TAG_LENS_SPECIFICATION,
            ExifInterface.TAG_LENS_MAKE,
            ExifInterface.TAG_LENS_MODEL,
            ExifInterface.TAG_LENS_SERIAL_NUMBER,
            ExifInterface.TAG_GPS_VERSION_ID,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_GPS_SATELLITES,
            ExifInterface.TAG_GPS_STATUS,
            ExifInterface.TAG_GPS_MEASURE_MODE,
            ExifInterface.TAG_GPS_DOP,
            ExifInterface.TAG_GPS_SPEED_REF,
            ExifInterface.TAG_GPS_SPEED,
            ExifInterface.TAG_GPS_TRACK_REF,
            ExifInterface.TAG_GPS_TRACK,
            ExifInterface.TAG_GPS_IMG_DIRECTION_REF,
            ExifInterface.TAG_GPS_IMG_DIRECTION,
            ExifInterface.TAG_GPS_MAP_DATUM,
            ExifInterface.TAG_GPS_DEST_LATITUDE_REF,
            ExifInterface.TAG_GPS_DEST_LATITUDE,
            ExifInterface.TAG_GPS_DEST_LONGITUDE_REF,
            ExifInterface.TAG_GPS_DEST_LONGITUDE,
            ExifInterface.TAG_GPS_DEST_BEARING_REF,
            ExifInterface.TAG_GPS_DEST_BEARING,
            ExifInterface.TAG_GPS_DEST_DISTANCE_REF,
            ExifInterface.TAG_GPS_DEST_DISTANCE,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
            ExifInterface.TAG_GPS_AREA_INFORMATION,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_DIFFERENTIAL,
            ExifInterface.TAG_GPS_H_POSITIONING_ERROR,
            ExifInterface.TAG_INTEROPERABILITY_INDEX,
            ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH,
            ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH,
            ExifInterface.TAG_DNG_VERSION,
            ExifInterface.TAG_DEFAULT_CROP_SIZE,
            ExifInterface.TAG_ORF_THUMBNAIL_IMAGE,
            ExifInterface.TAG_ORF_PREVIEW_IMAGE_START,
            ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH,
            ExifInterface.TAG_ORF_ASPECT_FRAME,
            ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER,
            ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER,
            ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER,
            ExifInterface.TAG_RW2_SENSOR_TOP_BORDER,
            ExifInterface.TAG_RW2_ISO,
            ExifInterface.TAG_RW2_JPG_FROM_RAW,
            ExifInterface.TAG_NEW_SUBFILE_TYPE,
            ExifInterface.TAG_SUBFILE_TYPE
            /*
            There are private attributes
            ExifInterface.TAG_EXIF_IFD_POINTER,
            ExifInterface.TAG_GPS_INFO_IFD_POINTER,
            ExifInterface.TAG_INTEROPERABILITY_IFD_POINTER,
            ExifInterface.TAG_SUB_IFD_POINTER,
            ExifInterface.TAG_ORF_CAMERA_SETTINGS_IFD_POINTER,
            ExifInterface.TAG_ORF_IMAGE_PROCESSING_IFD_POINTER,
            ExifInterface.TAG_HAS_THUMBNAIL,
            ExifInterface.TAG_THUMBNAIL_LENGTH,
            ExifInterface.TAG_THUMBNAIL_DATA*/
        )
        val oldExif = ExifInterface(oldPath)
        val newExif = ExifInterface(newPath)
        attributes.forEach { attribute ->
            oldExif.getAttribute(attribute)?.let { value ->
                newExif.setAttribute(attribute, value)
            }
        }
        newExif.saveAttributes()
    }


    /**
     * Get orientation of bitmap.
     *
     * @param filePath : link of bitmap in sdcard
     * @return Orientation of bitmap
     */
    private fun getOrientation(filePath: String?): Int {
        var ori = 0
        if (filePath != null) {
            val exif: ExifInterface
            try {
                exif = if (filePath.contains("file://")) {
                    ExifInterface(filePath.substring(7))
                } else {
                    ExifInterface(filePath)
                }
                val exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (exifOrientation) {
                    ExifInterface.ORIENTATION_UNDEFINED -> {
                    }
                    ExifInterface.ORIENTATION_NORMAL -> {
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> ori = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> ori = 90
                    ExifInterface.ORIENTATION_ROTATE_270 -> ori = 270
                    else -> {
                    }
                }
            } catch (e: IOException) {
                AppLogger.error("ImageUtils", "getOrientation(String filePath) method: $e")
            }
        }
        return ori
    }

    }
}