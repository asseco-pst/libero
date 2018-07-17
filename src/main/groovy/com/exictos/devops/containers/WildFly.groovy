package com.exictos.devops.containers

import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.profiles.WildFlyProfile
import groovy.util.logging.Slf4j
import org.jboss.as.cli.scriptsupport.CLI

/**
 * WildFly container concrete class. Implements all the necessary methods to manage and deploy applications
 *
 */
@Slf4j
class WildFly extends Container{

    CLI cli

    WildFly(String aHost, int aPort = 9990, String aUsername = null, char[] aPassword = null){

        cli = CLI.newInstance()
        profile = new WildFlyProfile(cli)
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
        log.info("Connecting ${profile.username}@${profile.host}:${profile.port}")
        try{
            cli.connect(profile.host, profile.port, profile.username, profile.password)
            takeSnapshot()
            return true
        }catch(Exception e){
            log.error("Unable to connect to controller ${profile.host}:${profile.port}. Cause: ${e.getCause()}")
            throw e
        }
    }

    /**
     * Disconnects the CLI from the provided WildFly server instance
     */
    @Override
    void disconnect()
    {
        log.info("Disconnecting CLI")
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
    protected String installApp(File aPathToPackage, String aApplicationName)
    {
        log.info("Installing application ${aApplicationName} from package at ${aPathToPackage}...")
        String name = null

        try{

            name = LiberoHelper.standardizeName(aPathToPackage.getAbsolutePath(), aApplicationName)
            cli.cmd("deploy --name=${name} --runtime-name=${name} ${aPathToPackage.getAbsolutePath()} --disabled")
            log.info("${aApplicationName} installed successfully as ${name}")

        }catch(IllegalArgumentException iae) {
            log.error "Could not install application ${aApplicationName} from package ${aPathToPackage}. Cause: ${iae.getCause()}"
        }catch(Exception e){
            log.error "Could not install application ${aApplicationName} from package ${aPathToPackage}. Cause: ${e.getCause()}"
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
        log.info("Starting application: ${deploymentName}...")
        try{
            cli.cmd("deploy --name=${deploymentName}")
            log.info("Deployment ${deploymentName} started.")
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
        log.info("Stopping deployment ${deploymentName}...")
        try{
            cli.cmd("undeploy ${deploymentName} --keep-content")
            log.info("Deployment ${deploymentName} stopped")
        }catch(Exception e){
            log.error("Could not stop deployment: ${deploymentName}. Cause: ${e.getCause()}")
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
            cli.cmd("/deployment=${deploymentName}:remove()")
            log.info("Deployment ${deploymentName} uninstalled.")
        }catch(Exception e){
            log.error "Could not uninstall ${deploymentName}. Cause: ${e.getCause()}"
        }
    }

    /**
     *  Takes a snapshot of the current server configurations to be restored in case of failure.
     *
     * @return path to the snapshot file
     */
    void takeSnapshot()
    {
        log.info("Taking current WildFly configuration snapshot...")
        try{
            def result = cli.cmd(":take-snapshot")
            String path = result.getResponse().get("result").asString()
            log.info("Snapshot saved at ${path}")
        }catch(Exception e){
            log.error("Could not take snapshot. Cause ${e.getCause()}")
        }
    }

}