package com.exictos.devops.helpers


import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import org.slf4j.LoggerFactory

/**
 * This logger is used throughout all classes of the application.
 *
 */
class LiberoLogger{

    private static final Logger logger = (Logger)LoggerFactory.getLogger(LiberoLogger.class)

    /**
     * Gets the logger instance with the application wide configurations (ie. file to log to)
     * @return the logger
     */
    static Logger getLogger(){
        return logger
    }

    /**
     * Sets the file path where to log.
     * @param filePath to the log file
     */
    static void setLogFile(String filePath)
    {
        logger.setAdditive(false)
        logger.addAppender(getFileAppender(filePath))
    }

    /**
     * Build and starts a FileAppender to be added to logger.
     * This is where the log file is set.
     *
     * @param logFilePath
     * @return the fileAppender to be used for logging
     */
    static FileAppender<ILoggingEvent> getFileAppender(String logFilePath)
    {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory()

        PatternLayoutEncoder ple = new PatternLayoutEncoder()
        ple.setPattern("%highlight(%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] %msg%n)")
        ple.setContext(lc)
        ple.start()

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>()
        fileAppender.setName("FileAppender")
        fileAppender.setFile(logFilePath)
        fileAppender.setEncoder(ple)
        fileAppender.start()
        fileAppender.setContext(lc)
        return fileAppender
    }


}
