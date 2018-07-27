package com.exictos.devops.helpers

import com.exictos.devops.profiles.Instance
import org.apache.commons.lang3.SystemUtils
import groovy.util.logging.Slf4j
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
 * Utils class containing several different helpers methods
 * Should be refactored into different classes if it gets too big
 *
 */
@Slf4j
class LiberoHelper {

    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss"
    private static final String NAME_VALIDATOR_REGEX = "^([A-Za-z0-9_]+)((_v{1})(([1-9]{1,2}.){2,4})|(___))"
    private static final String VERSION_VALIDATOR_REGEX = "^(([1-9]{1,2}.){2,4})"

    /**
     * Returns the application standard name for installation. In the format appName___2018-06-11_17-20-35.pkg
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return applicationName in the standard form for installation
     */
    static String standardizeName(String aPathToPackage, String aApplicationName, String aVersion = null, Timestamp aTimestamp = null) throws IllegalArgumentException
    {
        if(!aApplicationName.contains("___"))
            throw new IllegalArgumentException("Application name provided ${aApplicationName} is not valid. Missing the date separator '___'")

        def version = ""
        if(aVersion != null) {
            if (!Pattern.matches(VERSION_VALIDATOR_REGEX, aVersion))
                throw new IllegalArgumentException("Version provided ${aVersion} is not valid.")
            version = "_v${aVersion}"
        }

        def now = aTimestamp.format(DATE_FORMAT)
        if(now == null)
            now = getCurrentTimestamp()

        def _package = packageType(aPathToPackage)

        "${aApplicationName}${version}___${now}.${_package}"
    }

    /**
     * The reverse process of LiberoHelper::standardizeName()
     *
     * @param standardizedName
     * @return The application name without the timestamp
     */
    static String extractName(String standardizedName)
    {
        if(!standardizedName.contains("___"))
            throw new IllegalArgumentException("Application name provided ${standardizedName} is not valid. Missing the date separator '___'")

        String name = null
        try{
            name = standardizedName.split("___").first()
            if(name.contains("_v"))
                name = name.substring(0, name.indexOf("_v"))
        }catch(Exception e){
            log.error("Could not parse name ${standardizedName}. Cause: ${e}")
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

        String timestamp = applicationStandardizedName.split("___").last()
        timestamp = timestamp.substring(0, DATE_FORMAT.length())
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

    /**
     *  Extracts the package name, given a file as input (removes extension .zip)
     *
     * @param _package file
     * @return resulting folder name
     */
    static String extractFolderNameFromPackageFile(File _package)
    {
        _package.getName().replace(".zip","")
    }

    static String normalizePath(String path) {
        if(SystemUtils.IS_OS_WINDOWS)
            return path.replace("\\","/")
        else
            return path
    }
}
