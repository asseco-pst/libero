package com.exictos.devops.containers

import ch.qos.logback.classic.Logger
import com.exictos.devops.Application
import com.exictos.devops.helpers.LiberoLogger
import com.exictos.devops.helpers.XHDLogger
import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.Profile

import java.sql.Timestamp

/**
 * Container abstract class.
 * Should be extended by WildFly, WebSphere and other concrete containers.
 */
abstract class Container{

    protected XHDLogger log = new Application().getLog()
    Profile profile

    /**
     * Sets the file path to log events
     *
     * @param the full filePath (eg. C:/logs/output.log)
     */
    void setLogFile(File filePath){
        log.setLogFile(filePath.toString())
        log.log("Logging to ${filePath}")
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
    abstract protected String installApp(File pathToPackage, String applicationName,String applicationVersion = null, Timestamp timestamp = null)

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
        log.log("--------------------------------------------------------")
        log.log("           INSTALL APPLICATION WITH ROLLBACK            ")
        log.log("--------------------------------------------------------")
        installApp(pathToPackage, applicationName, applicationVersion, timestamp)
        uninstallAppOldInstances(applicationName)
    }

    /**
     * Starts the newest instance of applicationName after stopping all old instances
     *
     * @param deploymentName
     */
    abstract void startApp(String deploymentName)

    /**
     *  Starts the most recent instance of an application
     *
     * @param applicationName
     */
    void startMostRecentInstance(String applicationName){
        log.log("Starting most recent instances of ${applicationName}...")
        profile.listInstances(applicationName).each {instance ->
            if(instance.getOldness() == 0)
                startApp(instance.getName())
        }
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
        log.log("--------------------------------------------------------")
        log.log("               STARTING MOST RECENT APPS                ")
        log.log("--------------------------------------------------------")
        stopOldInstances()
        profile.listInstalledApplications().each {app ->
            startMostRecentInstance(app)
        }
    }

    /**
     *  Stops all old instances of all applications installed in the profile
     *
     */
    void stopOldInstances(){
        log.log("Stopping all old instances...")
        List<String> applications = profile.listInstalledApplications()
        applications.each {app ->
            List<Instance> instances = profile.listInstances(app)
            instances.each {instance ->
                if(instance.getOldness() > 0 && instance.isEnabled())
                    stopApp(instance.getName())
            }
        }
    }

    /**
     *  Stops all instances installed in this profile
     *
     */
    void stopAllApps()
    {
        log.log("--------------------------------------------------------")
        log.log("                     STOP ALL APPS                      ")
        log.log("--------------------------------------------------------")
        profile.listAllInstances().each {instance ->
            if(instance.isEnabled())
                stopApp(instance.getName())
        }
    }

    /**
     * Uninstalls all old instances of applicationName with an oldness level above oldnessThreshold
     *
     * @param applicationName
     * @param oldnessThreshold
     * @returns the name of the most recent application deployment
     */
    String uninstallAppOldInstances(String applicationName, int oldnessThreshold = 0)
    {
        log.log("--------------------------------------------------------")
        log.log("       UNINSTALL ${applicationName} OLD INSTANCES       ")
        log.log("--------------------------------------------------------")

        String newest = null
        profile.listInstances(applicationName).each {instance ->
            if(instance.getOldness() > oldnessThreshold)
                uninstallApp(instance.getName())
            else
                newest = instance.getName()
        }

        return newest
    }

    /**
     * Uninstalls all old instances of all installed applications
     *
     */
    void uninstallOldInstances()
    {
        log.log("--------------------------------------------------------")
        log.log("                UNINSTALL OLD INSTANCES                 ")
        log.log("--------------------------------------------------------")

        List<String> applications = profile.listInstalledApplications()

        applications.each {app ->
            uninstallAppOldInstances(app)
        }

    }

}
