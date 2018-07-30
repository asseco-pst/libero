package com.exictos.devops.containers

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.FileAppender
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.WSAdminWrapper
import com.exictos.devops.profiles.WebSphereProfile
import groovy.util.logging.Slf4j

import java.sql.Timestamp

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

    /**
     * Connects the CLI to the provided WebSphere server instance
     *
     */
    @Override
    boolean connect() {
    	println "teste"
        false
    }

    /**
     * Disconnects the CLI from the provided WebSphere server instance
     *
     */
    @Override
    void disconnect() {
        false
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
        log.info("Installing application ${applicationName} from package at ${pathToPackage}...")
        try{
            String name = LiberoHelper.standardizeName(pathToPackage.getAbsolutePath(), applicationName, applicationVersion
                    , timestamp)
            wsadmin.installApplication(pathToPackage.getAbsolutePath(),name)
            log.info("${applicationName} installed successfully as ${name}")
            return name
        }catch(Exception e){
            log.error "Could not install application ${applicationName} from package ${pathToPackage}. Cause: ${e.getMessage()}"
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
        log.info("Starting application: ${deploymentName}...")
        try{
            if(wsadmin.isAppReady(deploymentName))
                wsadmin.startApplication(deploymentName)
            log.info("Deployment ${deploymentName} started.")
        }catch(Exception e){
            log.error("Could not start application ${deploymentName}. Cause: ${e}")
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
        log.info("Stopping deployment ${deploymentName}...")
        try{
            wsadmin.stopApplication(deploymentName)
            log.info("Deployment ${deploymentName} stopped")
        }catch(Exception e){
            log.error("Could not stop deployment: ${deploymentName}. Cause: ${e}")
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
        log.info("Uninstalling deployment ${deploymentName}...")
        try{
            wsadmin.uninstallApp(deploymentName)
            log.info("Deployment ${deploymentName} uninstalled.")
        }catch(Exception e){
            log.error "Could not uninstall ${deploymentName}. Cause: ${e}"
            throw e
        }
    }

}
