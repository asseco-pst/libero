package com.exictos.devops.containers


import com.exictos.devops.Application
import com.exictos.devops.helpers.XHDLogger
import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.Profile

import java.sql.Timestamp

/**
 * Container abstract class.
 * Should be extended by WildFly, WebSphere and other concrete containers.
 */
abstract class Container{

    protected XHDLogger logger = new Application().getLogger()
    Profile profile

    /**
     * Sets the file path to logger events
     *
     * @param the full filePath (eg. C:/logs/output.logger)
     */
    void setLogFile(File filePath){
        logger.setLogFile(filePath.toString())
        logger.log("Logging to ${filePath}")
    }

    /**
     * Connects to the container's profile specified by host:port
     *
     */
    abstract boolean connect()

    /**
     * Disconnects from the container's profile specified by host:port
     *
     */
    abstract void disconnect()

    /**
     *  Installs application with the package provided at pathToPackage and with the name applicationName standardized
     *
     * @param pathToPackage
     * @param applicationName
     * @return the deployment name in the standard form
     */
    abstract String installApp(File pathToPackage, String applicationName,String applicationVersion = null, Timestamp timestamp = null)

    /**
     *  Installs application with the package provided at pathToPackage and with the name applicationName standardized.
     *  This method keeps the last installation disabled in case you need to rollback.
     *
     * @param pathToPackage
     * @param applicationName
     * @return the deployment name in the standard form
     */
    String installAppWithRollBack(File pathToPackage, String applicationName, String applicationVersion = null
                                  , Timestamp timestamp = null)
    {
        logger.log("--------------------------------------------------------")
        logger.log("           INSTALL APPLICATION WITH ROLLBACK            ")
        logger.log("--------------------------------------------------------")
        String newest = installApp(pathToPackage, applicationName, applicationVersion, timestamp)
        disableAutoStart(newest)
        uninstallAppOldInstances(applicationName, 1)
    }

    /**
     * Starts the newest instance of applicationName after stopping all old instances
     *
     * @param deploymentName
     */
    abstract void startApp(String deploymentName)

    /**
     *  Starts the most recent instance of an application
     *  Attention: You need to call profile.updateInstances() to update instances before calling this method, otherwise
     *  you could get unexpected results
     *
     * @param applicationName
     */
    void startMostRecentInstance(String applicationName){
        logger.log("Starting most recent instances of ${applicationName}...")
        Instance mostRecent = new Instance()
        profile.listInstances(applicationName).each {instance ->
            if(instance.getOldness() == 0) {
                mostRecent = instance
            }
            else{
                disableAutoStart(instance.getName())
                stopApp(instance.getName())
            }
        }
        startApp(mostRecent.getName())
    }

    /**
     * Stops deploymentName deployed in this profile
     * If the deployment is already stopped, nothing happens
     *
     * @param deploymentName
     */
    abstract void stopApp(String deploymentName)

    /**
     * Uninstall deploymentName installed in this profile
     *
     * @param deploymentName
     */
    abstract void uninstallApp(String deploymentName)

    /**
     * Start the most recent instances of all applications installed in this profile
     * Most recent => oldness level = 0
     *
     */
    void startMostRecentApps()
    {
        logger.log("--------------------------------------------------------")
        logger.log("               STARTING MOST RECENT APPS                ")
        logger.log("--------------------------------------------------------")
        profile.updateInstances()
        stopOldInstances()
        profile.listInstalledApplications().each {app ->
            startMostRecentInstance(app)
        }
    }

    /**
     *  Stops all old instances of all applications installed in the profile
     *  Attention: You need to call profile.updateInstances() to update instances before calling this method, otherwise
     *  you could get unexpected results
     */
    void stopOldInstances(){
        logger.log("Stopping all old instances...")
        List<String> applications = profile.listInstalledApplications()
        applications.each {app ->
            List<Instance> instances = profile.listInstances(app)
            instances.each {instance ->
                if(instance.getOldness() > 0 && instance.isEnabled()){
                    disableAutoStart(instance.getName())
                    stopApp(instance.getName())
                }
            }
        }
    }

    /**
     *  Stops all instances installed in this profile
     *
     */
    void stopAllApps()
    {
        logger.log("--------------------------------------------------------")
        logger.log("                     STOP ALL APPS                      ")
        logger.log("--------------------------------------------------------")
        profile.updateInstances()
        profile.instances.each {instance ->
            if(instance.isEnabled())
                stopApp(instance.getName())
        }
    }

    /**
     * Uninstalls all old instances of applicationName with an oldness level above oldnessThreshold
     *  Attention: You need to call profile.updateInstances() to update instances before calling this method, otherwise
     *  you could get unexpected results
     *
     * @param applicationName
     * @param oldnessThreshold
     * @returns the name of the most recent application deployment
     */
    String uninstallAppOldInstances(String applicationName, int oldnessThreshold = 0)
    {
        logger.log("--------------------------------------------------------")
        logger.log("       UNINSTALL ${applicationName} OLD INSTANCES       ")
        logger.log("--------------------------------------------------------")

        String newest = null
        profile.listInstances(applicationName).each {instance ->
            if(instance.getOldness() == 0)
                newest = instance.getName()
            if(instance.getOldness() > oldnessThreshold)
                uninstallApp(instance.getName())
        }
        return newest
    }

    /**
     * Uninstalls all old instances of all installed applications
     *  Attention: You need to call profile.updateInstances() to update instances before calling this method, otherwise
     *  you could get unexpected results
     */
    void uninstallOldInstances()
    {
        logger.log("--------------------------------------------------------")
        logger.log("                UNINSTALL OLD INSTANCES                 ")
        logger.log("--------------------------------------------------------")

        List<String> applications = profile.listInstalledApplications()

        applications.each {app ->
            uninstallAppOldInstances(app)
        }

    }

    /**
     * Disables application loading when container boots up
     *
     * @param deploymentName
     */
    abstract protected void disableAutoStart(String deploymentName)

    /**
     * Enables application loading when container boots up
     *
     * @param deploymentName
     */
    abstract protected void enableAutoStart(String deploymentName)

}
