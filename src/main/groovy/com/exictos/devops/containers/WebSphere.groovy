package com.exictos.devops.containers

import com.exictos.devops.profiles.WebSphereProfile

class WebSphere extends Container{

    WebSphere(String host, int port, String username, char[] password)
    {
        profile = new WebSphereProfile()
        profile.host = host
        profile.port = port
        profile.username = username
        profile.password = password
    }

    /**
     * Connects the CLI to the provided WebSphere server instance
     */
    @Override
    void connect()
    {

    }

    /**
     * Disconnects the CLI from the provided WebSphere server instance
     */
    @Override
    void disconnect()
    {

    }

    /**
     * Installs application with the package provided at pathToPackage and with the name applicationName standardized
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return
     */
    @Override
    String installApp(String pathToPackage, String applicationName)
    {
        return null
    }

    /**
     * Starts the the instance named deploymentName
     *
     * @param deploymentName
     */
    @Override
    void startApp(String deploymentName)
    {

    }

    /**
     * Starts the most recent instance of applicationName
     * @param applicationName
     */
    @Override
    void startMostRecentInstance(String applicationName)
    {

    }

    /**
     * Stops deploymentName deployed in this profile
     * If the deployment is already stopped, nothing happens
     *
     * @param deploymentName
     */
    @Override
    void stopApp(String deploymentName)
    {

    }

    /**
     * Uninstall deploymentName installed in this profile
     *
     * @param deploymentName
     */
    @Override
    void uninstallApp(String deploymentName)
    {

    }

    /**
     * Stops all old instances of all applications installed in the profile
     *
     */
    @Override
    void stopOldInstances()
    {

    }

}
