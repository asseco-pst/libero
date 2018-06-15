package com.exictos.devops.containers

import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.WildFlyProfile
import org.jboss.as.cli.scriptsupport.CLI

/**
 * WildFly container concrete class. Implements all the necessary methods to manage and deploy applications
 *
 */
@groovy.util.logging.Slf4j
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
    void connect()
    {
        log.info("Connecting ${profile.username}@${profile.host}:${profile.port}")
        cli.connect(profile.host, profile.port, profile.username, profile.password)
        takeSnapshot()
    }

    /**
     * Disconnects the CLI from the provided WildFly server instance
     */
    void disconnect()
    {
        log.info("Disconnecting CLI")
        cli.disconnect()
    }

    /**
     * Installs application with the package provided at pathToPackage and with the name applicationName standardized
     *
     * @param aPathToPackage
     * @param aApplicationName
     * @return
     */
    @Override
    String installApp(String aPathToPackage, String aApplicationName)
    {
        log.info("Installing application ${aApplicationName} from package at ${aPathToPackage}...")
        String name = null

        try{

            name = LiberoHelper.standardizeName(aPathToPackage, aApplicationName)
            cli.cmd("deploy --name=${name} --runtime-name=${name} ${aPathToPackage} --disabled")
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
            log.error "Unable to start deployment ${deploymentName}. Cause: ${e.getCause()}"
        }
    }

    /**
     * Starts the most recent instance of applicationName
     * @param applicationName
     */
    @Override
    void startMostRecentInstance(String applicationName) {
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
     * Stops all old instances of all applications installed in the profile
     */
    @Override
    void stopOldInstances() {

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