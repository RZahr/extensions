package com.rzahr.extensions

import android.content.Context
import android.util.Log
import java.io.File

/*
* this class should be created in the applications class and used across app
* */
@Suppress("unused")
class LogWriter(private val context: Context, private val logFolderName: String, private val supportFileLogging: Boolean) {

    private lateinit var dateLog: String
    private lateinit var mErrorFileName: String
    private lateinit var mLogFileName: String
    private lateinit var mSystemLogFileName: String
    private lateinit var mFatalErrorFileName: String
    private lateinit var mConnectionErrorFileName: String
    private var mExternalFilesDirectory: File? = null
    private var mLogWriterInternal: LogWriterInternal = LogWriterInternal(supportFileLogging)

    fun init() {

        if (supportFileLogging) {
            try {
                val folder = File(getAppStorageDirectory().toString() + "/" + logFolderName)

                if (!folder.exists()) folder.mkdir()
                else Log.e("LogWriter", "Error in Creating Logs Directory")

            } catch (exc: Exception) {

                Log.e("LogWriter", exc.toString())
            }
        }
        dateLog = getCurrentDate(true, "yy.MM.dd")
        dateLog = dateLog.replace("-", "-")
        dateLog = dateLog.replace(":", ".")
        dateLog = dateLog.replace(" ", " ")

        mSystemLogFileName = "SYS-$dateLog"
        mLogFileName = "NOR-$dateLog"
        mErrorFileName = "ERR-$dateLog"
        mConnectionErrorFileName = "CNX-$dateLog"
        mFatalErrorFileName = "FAT-$dateLog"
        mExternalFilesDirectory = getAppStorageDirectory()
    }

    fun errorLogsExists() = File("${mExternalFilesDirectory.toString()}/$logFolderName/$mErrorFileName.txt").exists()

    fun getErrorLogFilePath() = "${mExternalFilesDirectory.toString()}/$logFolderName/$mErrorFileName.txt"

    fun getLogDirectory() = "${mExternalFilesDirectory.toString()}/$logFolderName"

    private fun getAppStorageDirectory(): File? {

        return context.getExternalFilesDir(null)
    }

    fun logToFile(msg: String, fileName: String) {
        try {
            mLogWriterInternal.logToFileHelper(msg, "${mExternalFilesDirectory.toString()}/$logFolderName/$fileName.txt")
        }

        catch (ignored: Exception) { }
    }

    fun systemLogging(msg: String, logInFirebase: (message: String) -> Unit = {_->}) {
        var level = 4
        for (stack in Thread.currentThread().stackTrace) {

            if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true) && !stack.className.contains("GVBase", ignoreCase = true) && !stack.className.contains("GVFTDatabase", ignoreCase = true)) {

                level = Thread.currentThread().stackTrace.indexOf(stack)
                break
            }
        }
        val callingMethod = mLogWriterInternal.getCallerClass(level)

        try {
            mLogWriterInternal.logHelper(callingMethod, msg, "${mExternalFilesDirectory.toString()}/$logFolderName/$mSystemLogFileName.txt", false)
            logInFirebase("Activity: " + callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Msg: $msg\n")
        }

        catch (ignored: Exception) { }
    }

    fun logging(msg: String, logInFirebase: (message: String) -> Unit = {_->}) {
        var level = 4
        for (stack in Thread.currentThread().stackTrace) {

            if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true) && !stack.className.contains("GVBase", ignoreCase = true) && !stack.className.contains("GVFTDatabase", ignoreCase = true)) {

                level = Thread.currentThread().stackTrace.indexOf(stack)
                break
            }
        }
        val callingMethod = mLogWriterInternal.getCallerClass(level)

        try {
            mLogWriterInternal.logHelper(callingMethod, msg, "${mExternalFilesDirectory.toString()}/$logFolderName/$mLogFileName.txt", false)
            logInFirebase("Activity: " + callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Msg: $msg\n")
        }

        catch (ignored: Exception) { }
    }

    fun errorLogging(error: String, exception: Exception? = null, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        var level = 4
        for (stack in Thread.currentThread().stackTrace) {

            if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true) && !stack.className.contains("GVBase", ignoreCase = true) && !stack.className.contains("GVFTDatabase", ignoreCase = true)) {

                level = Thread.currentThread().stackTrace.indexOf(stack)
                break
            }
        }
        val callingMethod = mLogWriterInternal.getCallerClass(level)

        errorLogHelper(mErrorFileName, callingMethod, error, exception, logErrorInFirebase)
    }

    fun fatalErrorLogging(error: String, exception: Exception? = null, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        var level = 4
        for (stack in Thread.currentThread().stackTrace) {

            if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true) && !stack.className.contains("GVBase", ignoreCase = true) && !stack.className.contains("GVFTDatabase", ignoreCase = true)) {

                level = Thread.currentThread().stackTrace.indexOf(stack)
                break
            }
        }
        val callingMethod = mLogWriterInternal.getCallerClass(level)

        errorLogHelper(mFatalErrorFileName, callingMethod, error, exception, logErrorInFirebase)
    }

    fun connectionLogging(error: String, exception: Exception? = null, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        var level = 4
        for (stack in Thread.currentThread().stackTrace) {

            if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true) && !stack.className.contains("GVBase", ignoreCase = true) && !stack.className.contains("GVFTDatabase", ignoreCase = true)) {

                level = Thread.currentThread().stackTrace.indexOf(stack)
                break
            }
        }
        val callingMethod = mLogWriterInternal.getCallerClass(level)

        errorLogHelper(mConnectionErrorFileName, callingMethod, error, exception, logErrorInFirebase)
    }

    private fun errorLogHelper(fileName: String, callingMethod: Array<String>, error: String, exception: Exception?, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        try {
            mLogWriterInternal.logErrorHelper(callingMethod, "${mExternalFilesDirectory.toString()}/$logFolderName/$fileName.txt", error, false)
            logErrorInFirebase(exception, "Activity: " + callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Error: $error \n")
        }

        catch (ignored: Exception) { }
    }
}