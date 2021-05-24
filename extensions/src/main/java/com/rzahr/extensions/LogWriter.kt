package com.rzahr.extensions

import android.content.Context
import android.util.Log
import java.io.File

/*
* this class should be created in the applications class and used across app
* */
@Suppress("unused")
class LogWriter(private val context: Context, private val logFolderName: String, supportFileLogging: Boolean) {

    private var dateLog: String
    private var mErrorFileName: String
    private var mLogFileName: String
    private var mSystemLogFileName: String
    private var mFatalErrorFileName: String
    private var mConnectionErrorFileName: String
    private var mExternalFilesDirectory: File? = null
    private var mLogWriterInternal: LogWriterInternal = LogWriterInternal(supportFileLogging)

    init {

        if (supportFileLogging) {
            try {
                val folder = File(getAppStorageDirectory().toString() + "/" + logFolderName)
                if (!folder.exists()) folder.mkdir()
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

    fun systemLogging(msg: String, className: String = "", functionName: String = "", lineNumber: Int = -1, logInFirebase: (message: String) -> Unit = {_->}) {
        val callingMethod = if (functionName.isEmpty()) {
            var level = 4
            for (stack in Thread.currentThread().stackTrace) {
                if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {
                    level = Thread.currentThread().stackTrace.indexOf(stack)
                    break
                }
            }
            mLogWriterInternal.getCallerClass(level)
        } else arrayOf(lineNumber.toString(), className, functionName)

        try {
            mLogWriterInternal.logHelper(callingMethod, msg, "${mExternalFilesDirectory.toString()}/$logFolderName/$mSystemLogFileName.txt", false)
            logInFirebase("Class: " + callingMethod[1] + " <=> " + callingMethod[2] + "(" + callingMethod[0] + "): $msg\n")
        }

        catch (ignored: Exception) { }
    }

    fun logging(msg: String, className: String = "", functionName: String = "", lineNumber: Int = -1, logInFirebase: (message: String) -> Unit = {_->}) {
        val callingMethod = if (functionName.isEmpty()) {
            var level = 4
            for (stack in Thread.currentThread().stackTrace) {
                if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {
                    level = Thread.currentThread().stackTrace.indexOf(stack)
                    break
                }
            }
            mLogWriterInternal.getCallerClass(level)
        } else arrayOf(lineNumber.toString(), className, functionName)

        try {
            mLogWriterInternal.logHelper(callingMethod, msg, "${mExternalFilesDirectory.toString()}/$logFolderName/$mLogFileName.txt", false)
            logInFirebase("Class: " + callingMethod[1] + " <=> " + callingMethod[2] + "(" + callingMethod[0] + "): $msg\n")
        }

        catch (ignored: Exception) { }
    }

    fun errorLogging(error: String, exception: Exception? = null, className: String = "", functionName: String = "", lineNumber: Int = -1, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        val callingMethod = if (functionName.isEmpty()) {
            var level = 4
            for (stack in Thread.currentThread().stackTrace) {
                if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {
                    level = Thread.currentThread().stackTrace.indexOf(stack)
                    break
                }
            }
            mLogWriterInternal.getCallerClass(level)
        } else arrayOf(lineNumber.toString(), className, functionName)

        errorLogHelper(mErrorFileName, callingMethod, error, exception, logErrorInFirebase)
    }

    fun fatalErrorLogging(error: String, exception: Exception? = null, className: String = "", functionName: String = "", lineNumber: Int = -1, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        val callingMethod = if (functionName.isEmpty()) {
            var level = 4
            for (stack in Thread.currentThread().stackTrace) {
                if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {
                    level = Thread.currentThread().stackTrace.indexOf(stack)
                    break
                }
            }
            mLogWriterInternal.getCallerClass(level)
        } else arrayOf(lineNumber.toString(), className, functionName)

        errorLogHelper(mFatalErrorFileName, callingMethod, error, exception, logErrorInFirebase)
    }

    fun connectionLogging(error: String, exception: Exception? = null, className: String = "", functionName: String = "", lineNumber: Int = -1, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        val callingMethod = if (functionName.isEmpty()) {
            var level = 4
            for (stack in Thread.currentThread().stackTrace) {
                if (stack.className.contains(context.packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {
                    level = Thread.currentThread().stackTrace.indexOf(stack)
                    break
                }
            }
            mLogWriterInternal.getCallerClass(level)
        } else arrayOf(lineNumber.toString(), className, functionName)

        errorLogHelper(mConnectionErrorFileName, callingMethod, error, exception, logErrorInFirebase)
    }

    private fun errorLogHelper(fileName: String, callingMethod: Array<String>, error: String, exception: Exception?, logErrorInFirebase: (exception: Exception?, extraMessage: String?) -> Unit = {_,_->}) {
        try {
            mLogWriterInternal.logErrorHelper(callingMethod, "${mExternalFilesDirectory.toString()}/$logFolderName/$fileName.txt", error, false)
            logErrorInFirebase(exception, "Class: " + callingMethod[1] + " <=> " + callingMethod[2] + "(" + callingMethod[0] + "): $error \n")
        }

        catch (ignored: Exception) { }
    }
}