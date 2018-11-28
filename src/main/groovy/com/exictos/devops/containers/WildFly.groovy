package com.exictos.devops.containers


import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.profiles.WildFlyProfile
import org.jboss.as.cli.scriptsupport.CLI

import java.sql.Timestamp

/**
 * WildFly container concrete class. Implements all the necessary methods to manage and deploy applications
 *
 */
class WildFly extends Container{

    CLI cli

    WildFly(String aHost, int aPort = 9990, String aUsername = null, char[] aPassword = null){
        cli = CLI.newInstance()
        profile = new WildFlyProfile(cli, logger)
        profile.host = aHost
        profile.port = aPort
        profile.username = aUsername
        profile.password = aPassword

    }

    /**
     * Connects the CLI to the provided WildFly server instance
     */
    @Override
    boolean connect()
    {
        logger.log("Connecting ${profile.username}@${profile.host}:${profile.port}")
        try{
            cli.connect(profile.host, profile.port, profile.username, profile.password)
            takeSnapshot()
            return true
        }catch(Exception e){
            logger.log("Unable to connect to controller ${profile.host}:${profile.port}. Cause: ${e}")
            throw e
        }
    }

    /**
     * Disconnects the CLI from the provided WildFly server instance
     */
    @Override
    void disconnect()
    {
        logger.log("Disconnecting CLI")
        cli.disconnect()
        profile.connected = false
    }

    /**
     * Installs application with the package provided at pathToPackage and with the name applicationName standardized
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return
     */
    @Override
    String installApp(File aPathToPackage, String aApplicationName, String aApplicationVersion = null
                                 , Timestamp aTimestamp = null)
    {
        logger.log("Installing application ${aApplicationName} from package at ${aPathToPackage}...")
        try{
            String name = LiberoHelper.standardizeName(aPathToPackage.getAbsolutePath(), aApplicationName, aApplicationVersion
                    , aTimestamp)
            cli.cmd("deploy --name=${name} --runtime-name=${name} \"${aPathToPackage.getAbsolutePath()}\" --disabled")
            logger.log("${aApplicationName} installed successfully as ${name}")
            return name
        }catch(IllegalArgumentException iae) {
            logger.log "Could not install application ${aApplicationName} from package ${aPathToPackage}. Cause: ${iae}"
            throw iae
        }catch(Exception e){
            logger.log "Could not install application ${aApplicationName} from package ${aPathToPackage}. Cause: ${e}"
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
        logger.log("Starting application: ${deploymentName}...")
        try{
            cli.cmd("deploy --name=${deploymentName}")
            logger.log("Deployment ${deploymentName} started.")
        }catch(Exception e){
            logger.log("Could not start application ${deploymentName}. Cause: ${e}")
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
        logger.log("Stopping deployment ${deploymentName}...")
        try{
            cli.cmd("undeploy ${deploymentName} --keep-content")
            logger.log("Deployment ${deploymentName} stopped")
        }catch(Exception e){
            logger.log("Could not stop deployment: ${deploymentName}. Cause: ${e}")
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
        logger.log("Uninstalling deployment ${deploymentName}...")
        try{
            cli.cmd("/deployment=${deploymentName}:remove()")
            logger.log("Deployment ${deploymentName} uninstalled.")
        }catch(Exception e){
            logger.log "Could not uninstall ${deploymentName}. Cause: ${e}"
            throw e
        }
    }

    @Override
    protected void disableAutoStart(String deploymentName) {}

    @Override
    protected void enableAutoStart(String deploymentName) {}

    /**
     *  Takes a snapshot of the current server configurations to be restored in case of failure.
     *
     * @return path to the snapshot file
     */
    void takeSnapshot()
    {
        logger.log("Taking current WildFly configuration snapshot...")
        try{
            def result = cli.cmd(":take-snapshot")
            String path = result.getResponse().get("result").asString()
            logger.log("Snapshot saved at ${path}")
        }catch(Exception e){
            logger.log("Could not take snapshot. Cause ${e}")
            throw e
        }
    }

}