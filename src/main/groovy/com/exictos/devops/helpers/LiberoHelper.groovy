package com.exictos.devops.helpers

import com.exictos.devops.profiles.Instance
import org.apache.commons.lang3.SystemUtils
import groovy.util.logging.Slf4j
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Utils class containing several different helpers methods
 * Should be refactored into different classes if it gets too big
 *
 */
@Slf4j
class LiberoHelper {

    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss"
    private static final String NAME_VALIDATOR_REGEX = "^([aA-zZ]+)((_v)(?:[\\dx]{1,3}\\.){0,3}[\\dx]{1,3})?(___)"
    private static final String VERSION_VALIDATOR_REGEX = "^(?:[\\dx]{1,3}\\.){0,3}[\\dx]{1,3}\$"

    /**
     * Returns the application standard name for installation. In the format appName___2018-06-11_17-20-35.pkg
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return applicationName in the standard form for installation
     */
    static String standardizeName(String aPathToPackage, String aApplicationName, String aVersion = null, Timestamp aTimestamp = null) throws IllegalArgumentException
    {
        def version = ""
        if(aVersion != null) {
            if (!Pattern.matches(VERSION_VALIDATOR_REGEX, aVersion))
                throw new IllegalArgumentException("Version provided ${aVersion} is not valid.")
            version = "_v${aVersion}"
        }

        def now
        if(aTimestamp != null)
            now = aTimestamp.format(DATE_FORMAT)
        else
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
        if(!isValidDeploymentName(standardizedName))
            throw new IllegalArgumentException("Application name provided ${standardizedName} is not valid. Missing the date separator '___'")

        try{
            String name = standardizedName.split("___").first()
            if(name.contains("_v"))
                name = name.substring(0, name.indexOf("_v"))
            return name
        }catch(Exception e){
            log.error("Could not parse name ${standardizedName}. Cause: ${e}")
            throw e
        }
    }

    static String extractVersion(String standardizedName)
    {
        if(!isValidDeploymentName(standardizedName))
            throw new IllegalArgumentException("Application name provided ${standardizedName} is not valid. Missing the date separator '___'")
        if(!standardizedName.contains("_v"))
            throw new IllegalArgumentException("Application name provided ${standardizedName} does not contain a version number.")

        try{
            String version = standardizedName.substring(standardizedName.indexOf("_v"), standardizedName.indexOf("___"))
            return version
        }catch(Exception e){
            log.error("Could not parse name ${standardizedName}. Cause: ${e}")
            throw e
        }
    }

    /**
     * Extracts the timestamp from an application standardized name
     *
     * @param applicationStandardizedName
     * @return the application name timestamp
     */
    static Timestamp extractTimestamp(String applicationStandardizedName)
    {
        if(!applicationStandardizedName.contains("___"))
            throw new IllegalArgumentException("Application name provided ${applicationStandardizedName} is not valid. Missing the date separator '___'")

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
            throw new IllegalArgumentException("Could not extract package type from file: ${filePath}. Cause: ${e}")
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
        if(!_package.getName().contains(".zip"))
            throw new IllegalArgumentException("Package file name is not valid. Should be a zip file.")

        _package.getName().replace(".zip","")
    }

    static String normalizePath(String path) {
        if(SystemUtils.IS_OS_WINDOWS)
            return path.replace("\\","/")
        else
            return path
    }

    static boolean isValidDeploymentName(String deploymentName)
    {
        Pattern pattern = Pattern.compile(NAME_VALIDATOR_REGEX, Pattern.CASE_INSENSITIVE)
        Matcher matcher = pattern.matcher(deploymentName)
        matcher.find()
    }
}
