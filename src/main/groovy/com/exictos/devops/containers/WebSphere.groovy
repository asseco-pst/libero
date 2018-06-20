package com.exictos.devops.containers

import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.WSAdminWrapper
import com.exictos.devops.profiles.WebSphereProfile
import groovy.util.logging.Slf4j

@Slf4j
class WebSphere extends Container{

    WSAdminWrapper wsadmin

    WebSphere(String host, int port, String username, char[] password, String aWsadmin)
    {
        wsadmin = new WSAdminWrapper(aWsadmin,host,port,username,password)
        profile = new WebSphereProfile(wsadmin)
        profile.host = host
        profile.port = port
        profile.username = username
        profile.password = password
    }

    void test(){
        log.info("Starting test....")
        log.error("Test failed")
        log.warn("WARNING")
    }

    /**
     * Connects the CLI to the provided WebSphere server instance
     */
    @Override
    boolean connect() {}

    /**
     * Disconnects the CLI from the provided WebSphere server instance
     */
    @Override
    void disconnect() {}

    /**
     * Installs application with the package provided at pathToPackage and with the name applicationName standardized
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return
     */
    @Override
    protected String installApp(String pathToPackage, String applicationName)
    {
        log.info("Installing application ${applicationName} from package at ${pathToPackage}...")
        String name = null
        try{
            name = LiberoHelper.standardizeName(pathToPackage, applicationName)
            wsadmin.installApplication(pathToPackage,name)
            log.info("${applicationName} installed successfully as ${name}")
        }catch(Exception e){
            log.error "Could not install application ${applicationName} from package ${pathToPackage}. Cause: ${e.getCause()}"
        }
        return name
    }

    /**
     * Starts the the instance named deploymentName
     *
     * @param deploymentName
     */
    @Override
    void startApp(String deploymentName)
    {
        try{
            if(wsadmin.isAppReady(deploymentName))
                wsadmin.startApplication(deploymentName)
        }catch(Exception e){
            log.error("Could not start application ${deploymentName}. Cause: ${e.getCause()}")
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
        try{
            wsadmin.stopApplication(deploymentName)
        }catch(Exception e){
            log.error("Could not stop application ${deploymentName}. Cause: ${e.getCause()}")
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
        try{
            wsadmin.uninstallApp(deploymentName)
        }catch(Exception e){
            log.error("Could not uninstall application ${deploymentName}. Cause: ${e.getCause()}")
        }
    }

}
