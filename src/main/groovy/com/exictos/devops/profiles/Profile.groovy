package com.exictos.devops.profiles


import com.exictos.devops.Application
import com.exictos.devops.helpers.XHDLogger

/**
 * Profile abstract class that represents an application server profile
 * Should be extended by classes such as WildFlyProfile, WebSphereProfile, etc.
 *
 */
abstract class Profile{

    protected XHDLogger log = new Application().getLog()

    String host = "127.0.0.1"
    int port
    String username
    char[] password
    boolean connected = false

    /**
     * Gets all instances of applications installed in this profile
     *
     * @return list of instances installed
     */
    abstract List<Instance> listAllInstances()

    /**
     * Lists all instances of applicationName installed in this profile
     *
     * @param applicationName
     * @return list of all instances of applicationName
     */
    abstract List<Instance> listInstances(String applicationName)

    /**
     * Lists all installed applications in this profile
     *
     * @return list of the names of installed applications
     */
    abstract List<String> listInstalledApplications()

    /**
     * Gets the context root of an application.
     *
     * @param applicationName
     * @return the context root
     */
    abstract String getApplicationContextRoot(String applicationName)

}
