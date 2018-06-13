package com.exictos.devops.containers

import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.Profile

/**
 * Container abstract class
 * Should be extended by WildFly, WebSphere and other concrete containers
 */
abstract class Container {

    Profile profile

    /**
     * Connects to the container's profile specified by host:port
     *
     */
    abstract void connect()

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
    abstract void startMostRecentInstance(String applicationName)

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
        stopOldInstances()
        profile.listInstalledApplications().each {app ->
            startMostRecentInstance(app)
        }
    }

    /**
     *  Stops all old instances of all applications installed in the profile
     *
     */
    abstract void stopOldInstances()

    /**
     *  Stops all instances installed in this profile
     *
     */
    void stopAllApps()
    {
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

        List<String> applications = profile.listInstalledApplications()

        applications.each {app ->
            uninstallAppOldInstances(app)
        }

    }

}
