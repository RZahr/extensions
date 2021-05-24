package com.rzahr.extensions

import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

internal class LogWriterInternal(private val supportFileLogging: Boolean) {

    private val tag = "LogWriterInternal"
    /**
     * get caller class string [ ].
     * @param level the level
     * @return the string [ ]
     */
    fun getCallerClass(level: Int): Array<String> {
        return try {
            val stElements = Thread.currentThread().stackTrace
            arrayOf(
                stElements[level + 1].lineNumber.toString() + "",
                stElements[level + 1].fileName,
                stElements[level + 1].methodName
            )
        } catch (e: Exception) {
            arrayOf("", "", "")
        }
    }

    fun logErrorHelper(callingMethod: Array<String>, filePath: String, error: String, deleteFileIfExist: Boolean) {

        Log.e(callingMethod[1] + "(" + callingMethod[0] + ")", " <=> " + callingMethod[2] + ": " + error)
        if (supportFileLogging) appendContents(filePath, callingMethod[1] + " <=> " + callingMethod[2] + "(" + callingMethod[0] + "): " + error + " \n",true, deleteFileIfExist)
    }

    fun logHelper(callingMethod: Array<String>, msg: String, filePath: String, deleteFileIfExist: Boolean) {

        Log.w(callingMethod[1] + "(" + callingMethod[0] + ")", " <=> " + callingMethod[2] + ": " + msg)
        if (supportFileLogging) appendContents(filePath, callingMethod[1] + " <=> " + callingMethod[2] + "(" + callingMethod[0] + "): " + msg + " \n",true, deleteFileIfExist)
    }

    fun logToFileHelper(msg: String, filePath: String) {

        appendContents(filePath, msg, false, deleteFileIfExist = true)
    }

    private fun appendContents(filePath: String, sContent: String, includeDate: Boolean, deleteFileIfExist: Boolean) {

        try {
            val oFile = File(filePath)

            if (deleteFileIfExist && oFile.exists()) oFile.delete()

            if (!oFile.exists()) oFile.createNewFile()

            if (oFile.canWrite()) {
                val oWriter = BufferedWriter(FileWriter(File(filePath), true))
                try {
                    oWriter.newLine()
                    if (includeDate) oWriter.write(" ###" + getCurrentDate(true, SLASHED_FORMAT) + ":" + sContent + " \n\r")
                    else oWriter.write(sContent)
                } finally {
                    safeCloseBufferedWriter(oWriter)
                }
            }
        } catch (oException: IOException) {
            Log.e(tag, "Error in appendContents oException $oException")
        }
    }

    /**
     * safe close buffered writer.
     * @param bufferedWriter the buffered writer
     */
    private fun safeCloseBufferedWriter(bufferedWriter: BufferedWriter?) {

        if (bufferedWriter != null) {

            bufferedWriter.flush()
            bufferedWriter.close()
        }
    }
}