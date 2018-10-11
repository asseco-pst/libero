package com.exictos.devops.containers


import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.WSAdminWrapper
import com.exictos.devops.profiles.WebSphereProfile

import java.sql.Timestamp

/**
 * WebSphere container concrete class. Implements all the necessary methods to manage and deploy applications
 *
 */
class WebSphere extends Container{

    WSAdminWrapper wsadmin

    WebSphere(String host, int port, String username, char[] password, String aWsadmin)
    {
        wsadmin = new WSAdminWrapper(aWsadmin,host,port,username,password)
        profile = new WebSphereProfile(wsadmin, log)
        profile.host = host
        profile.port = port
        profile.username = username
        profile.password = password
    }

    /**
     * Connects the CLI to the provided WebSphere server instance
     *
     */
    @Override
    boolean connect() {
        true
    }

    /**
     * Disconnects the CLI from the provided WebSphere server instance
     *
     */
    @Override
    void disconnect() {
        true
    }

    /**
     * Installs application with the package provided at pathToPackage and with the name applicationName standardized
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return
     */
    @Override
    protected String installApp(File pathToPackage, String applicationName, String applicationVersion
                                , Timestamp timestamp)
    {
        log.log("Installing application ${applicationName} from package at ${pathToPackage}...")
        try{
            String name = LiberoHelper.standardizeName(pathToPackage.getAbsolutePath(), applicationName, applicationVersion
                    , timestamp)
            wsadmin.installApplication(pathToPackage.getAbsolutePath(),name)
            log.log("${applicationName} installed successfully as ${name}")
            return name
        }catch(Exception e){
            log.log "Could not install application ${applicationName} from package ${pathToPackage}. Cause: ${e.getMessage()}"
            throw e
        }
    }

    /**
     * Starts the the instance named deploymentName
     *
     * @param deploymentName
     */
    @Override
    void startApp(String deploymentName)
    {
        log.log("Starting application: ${deploymentName}...")
        try{
            if(wsadmin.isAppReady(deploymentName))
                wsadmin.startApplication(deploymentName)
            log.log("Deployment ${deploymentName} started.")
        }catch(Exception e){
            log.log("Could not start application ${deploymentName}. Cause: ${e}")
            throw e
        }
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
        log.log("Stopping deployment ${deploymentName}...")
        try{
            wsadmin.stopApplication(deploymentName)
            log.log("Deployment ${deploymentName} stopped")
        }catch(Exception e){
            log.log("Could not stop deployment: ${deploymentName}. Cause: ${e}")
            throw e
        }
    }

    /**
     * Uninstall deploymentName installed in this profile
     *
     * @param deploymentName
     */
    @Override
    void uninstallApp(String deploymentName)
    {
        log.log("Uninstalling deployment ${deploymentName}...")
        try{
            wsadmin.uninstallApp(deploymentName)
            log.log("Deployment ${deploymentName} uninstalled.")
        }catch(Exception e){
            log.log "Could not uninstall ${deploymentName}. Cause: ${e}"
            throw e
        }
    }

}
