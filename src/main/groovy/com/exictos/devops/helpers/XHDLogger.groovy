package com.exictos.devops.helpers

import org.codehaus.groovy.runtime.StackTraceUtils

import java.nio.file.Paths

class XHDLogger {

    enum Level {
        INFO("\u001B[37m"),
        WARNING("\u001B[33m"),
        ERROR("\u001B[31m")

        String color

        Level(String aColor) {
            color = aColor
        }
    }

    private String logsDirPath
    private String logsFileName
    private String logsFileTimestamp
    private String logsFilePath
    private File logsFile

    XHDLogger() {
    }

    void setLogFile(String logFile){
        File logF = new File(logFile)
        logsFileName = logF.getName()
        logsDirPath = logF.toString().replace(logsFileName, "")
        logsFileTimestamp = LiberoHelper.timestamp(true /* trim white spaces */)
        logsFilePath = logFile
        createLogsFile()
    }

    void log(String aMessage, Level aLevel = Level.INFO, boolean aPrintTimestamp = false) {

        Level level = aLevel
        String logPrefix = String.format("[%s][%s] - ", LiberoHelper.timestamp(), level.toString())

        String log = String.format("%s%s%s\r\n", (aPrintTimestamp) ? logPrefix : "", (level == Level.ERROR) ? "\n${getStackTrace()}\n" : "", aMessage)

        logsFile << log
    }

    private void createLogsFile() {

        if(!FileUtils.fileExists(Paths.get(logsDirPath)))
            createLogsRootFolder()

        try {
            logsFile = new File(logsFilePath)
        } catch(SecurityException e) {
            println "[ERROR] Cannot create logs file '${logsFilePath}' due to SecurityException. Cause: ${e}"
            throw new RuntimeException()
        }
    }

    void createLogsRootFolder() {
        FileUtils.createDirectory(Paths.get(logsDirPath))
    }

    private static String getStackTrace() {

        def stackTraceArr = []

        def marker = new Throwable()
        StackTraceUtils.sanitize(marker).stackTrace.eachWithIndex { e, i ->
            stackTraceArr << "> $i ${e.toString().padRight(30)} ${e.methodName}"
        }

        return stackTraceArr.iterator().join("\n")
    }
}
