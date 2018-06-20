package com.exictos.devops.containers

import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.Profile
import groovy.util.logging.Slf4j

/**
 * Container abstract class
 * Should be extended by WildFly, WebSphere and other concrete containers
 */
@Slf4j
abstract class Container {

    Profile profile

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
    abstract String installApp(String pathToPackage, String applicationName)

    /**
     *  Installs application with the package provided at pathToPackage and with the name applicationName standardized.
     *  This method keeps the last installation disabled in case you need to rollback.
     *
     * @param pathToPackage
     * @param applicationName
     * @return the deployment name in the standard form
     */
    String installAppWithRollBack(String pathToPackage, String applicationName)
    {
        log.info("--------------------------------------------------------")
        log.info("           INSTALL APPLICATION WITH ROLLBACK            ")
        log.info("--------------------------------------------------------")
        String oldest = uninstallAppOldInstances(applicationName)
        String newest = installApp(pathToPackage, applicationName)

        stopApp(oldest)
        startApp(newest)
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
        log.info("Starting most recent instances of ${applicationName}...")
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
        log.info("--------------------------------------------------------")
        log.info("               STARTING MOST RECENT APPS                ")
        log.info("--------------------------------------------------------")
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
        log.info("Stopping all old instances...")
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
        log.info("--------------------------------------------------------")
        log.info("                     STOP ALL APPS                      ")
        log.info("--------------------------------------------------------")
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
        log.info("--------------------------------------------------------")
        log.info("       UNINSTALL ${applicationName} OLD INSTANCES       ")
        log.info("--------------------------------------------------------")

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
        log.info("--------------------------------------------------------")
        log.info("                UNINSTALL OLD INSTANCES                 ")
        log.info("--------------------------------------------------------")

        List<String> applications = profile.listInstalledApplications()

        applications.each {app ->
            uninstallAppOldInstances(app)
        }

    }

}
