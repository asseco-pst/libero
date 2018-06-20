package com.exictos.devops.helpers

import com.exictos.devops.profiles.Instance
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Path
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Utils class containing several different helpers methods
 * Should be refactored into different classes if it gets too big
 */
@Slf4j
class LiberoHelper {

    private static String DATE_FORMAT = "yyyyMMdd_HHmmss"

    /**
     * Returns the application standard name for installation. In the format appName___20180611_172035.pkg
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return applicationName in the standard form for installation
     */
    static String standardizeName(String aPathToPackage, String aApplicationName) throws IllegalArgumentException
    {

        def now = new Date().format(DATE_FORMAT)
        def _package = packageType(aPathToPackage)
        "${aApplicationName}___${now}.${_package}"
    }

    /**
     * The reverse process of LiberoHelper::standardizeName()
     *
     * @param standardizedName
     * @return The application name without the timestamp
     */
    static String extractName(String standardizedName)
    {
        String name = null
        try{
            name = standardizedName.substring(0, standardizedName.indexOf("___"))
        }catch(Exception e){
            log.error("Could not parse name ${standardizedName}. Cause: ${e.getCause()}")
        }
        return name
    }

    /**
     * Extracts the timestamp from an application standardized name
     *
     * @param applicationStandardizedName
     * @return the application name timestamp
     */
    static Timestamp extractTimestamp(String applicationStandardizedName)
    {

        String timestamp = applicationStandardizedName.split("___")[1]
        timestamp = timestamp.substring(0, 15)
        toTimestamp(timestamp)

    }

    /**
     * Converts a String to a java.sql.Timestamp object in the standard date format
     *
     * @param timestamp
     * @return Timestamp object
     */
    static Timestamp toTimestamp(String timestamp)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT)
        Date parsedDate = dateFormat.parse(timestamp)
        new Timestamp(parsedDate.getTime())
    }

    /**
     * Returns the file type of the file specified in filePath
     *
     * @param filePath
     * @return file package type (ie. EAR, WAR, etc.)
     */
    static String packageType(String filePath) throws IllegalArgumentException
    {
        try{
            filePath.substring(filePath.lastIndexOf(".")+1).toLowerCase().trim()
        }catch(Exception e){
            throw new IllegalArgumentException("Could not extract package type from file: ${filePath}. Cause: ${e.getCause()}")
        }
    }

    static String getCurrentTimestamp(){
        new Date().format(DATE_FORMAT).toString()
    }

    /**
     * Loops through instances and sets the oldness level for each instance according to its timestamp
     *
     * @param instances
     * @return list of instances
     */
    static List<Instance> oldnessLevel(List<Instance> instances){

        instances.sort{it.timestamp}
        instances.reverse(true)

        instances.eachWithIndex { Instance entry, int i ->
            entry.setOldness(i)
        }

        return instances
    }

    static String extractFolderNameFromPackageFile(File _package)
    {
        _package.getName().replace(".zip","")
    }
}
