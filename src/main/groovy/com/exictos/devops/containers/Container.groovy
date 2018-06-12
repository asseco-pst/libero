package com.exictos.devops.containers

import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.Profile

abstract class Container {

    Profile profile

    /**
     * Connects to the container's profile specified by host:port
     */
    abstract void connect()

    /**
     * Disconnects from the container's profile specified by host:port
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
        uninstallAppOldInstances(applicationName)

        List<Instance> instances = profile.listInstances(applicationName)

        String deploymentName = installApp(pathToPackage,applicationName)

        instances.each {instance ->
            stopApp(instance.getName())
        }

        startApp(applicationName)

        return deploymentName
    }

    /**
     * Starts the newest instance of applicationName after stopping all old instances
     *
     * @param applicationName
     */
    abstract void startApp(String applicationName)

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
     */
    void startMostRecentApps(){

        List<String> applications = profile.listInstalledApplications()

        applications.each {app ->
            List<Instance> instances = profile.listInstances(app)
            instances.each {instance ->
                if(instance.getOldness() > 0 && instance.isEnabled())
                    stopApp(instance.getName())
            }

            instances = profile.listInstances(app)
            instances.each{ instance ->
                if(instance.getOldness() == 0 && !instance.isEnabled())
                    startApp(app)
            }
        }

    }

    /**
     *  Stops all instances installed in this profile
     *
     */
    void stopAllApps(){

        List<Instance> instances = profile.listAllInstances()

        instances.each {instance ->
            if(instance.isEnabled())
                stopApp(instance.getName())
        }

    }

    /**
     * Uninstalls all old instances of applicationName with an oldness level above oldnessThreshold
     *
     * @param applicationName
     * @param oldnessThreshold
     */
    void uninstallAppOldInstances(String applicationName, int oldnessThreshold = 0){

        List<Instance> instances = profile.listInstances(applicationName)

        instances.each {instance ->
            if(instance.getOldness() > oldnessThreshold)
                uninstallApp(instance.getName())
        }

    }

    void uninstallOldInstances(){

        List<String> applications = profile.listInstalledApplications()

        applications.each {app ->
            uninstallAppOldInstances(app)
        }

    }

}
