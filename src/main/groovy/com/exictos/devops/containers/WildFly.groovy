package com.exictos.devops.containers

import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.profiles.Instance
import com.exictos.devops.profiles.WildFlyProfile
import org.jboss.as.cli.scriptsupport.CLI

class WildFly extends Container{

    CLI cli
    String snapshot

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
    void connect(){
        cli.connect(profile.host, profile.port, profile.username, profile.password)
        snapshot = takeSnapshot()
    }

    /**
     * Disconnects the CLI from the provided WildFly server instance
     */
    void disconnect(){
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
    String installApp(String aPathToPackage, String aApplicationName) {

        def name = LiberoHelper.standardizeName(aPathToPackage, aApplicationName)
        cli.cmd("deploy --name=${name} --runtime-name=${name} ${aPathToPackage} --disabled")
        return name
    }

    /**
     * Starts the newest instance of applicationName after stopping all old instances
     *
     * @param applicationName
     */
    @Override
    void startApp(String applicationName) {

        List<Instance> instances = profile.listInstances(applicationName)

        // Stop oldest instances
        instances.each {instance ->
            if(instance.getOldness() > 0 && instance.isEnabled())
                stopApp(applicationName)
        }

        // Start newest instance
        instances.each{instance ->
            if(instance.getOldness() == 0 && !instance.isEnabled())
                cli.cmd("deploy --name=${instance.getName()}")
        }
    }

    /**
     * Stops deploymentName deployed in this profile
     * If the deployment is already stopped, nothing happens
     *
     * @param deploymentName
     */
    @Override
    void stopApp(String deploymentName) {

        List<Instance> deployments = profile.listInstances(LiberoHelper.extractName(deploymentName))

        deployments.each {deployment ->
            if(deployment.getName() == deploymentName)
                if(deployment.isEnabled())
                    cli.cmd("undeploy ${deploymentName} --keep-content")
        }
    }

    /**
     * Uninstall deploymentName installed in this profile
     *
     * @param deploymentName
     */
    @Override
    void uninstallApp(String deploymentName) {

        List<Instance> deployments = profile.listInstances(LiberoHelper.extractName(deploymentName))

        deployments.each {deployment ->
            if(deployment.getName() == deploymentName)
                cli.cmd("/deployment=${deploymentName}:remove()")
        }
    }

    /**
     *  Takes a snapshot of the current server configurations to be restored in case of failure.
     *
     * @return path to the snapshot file
     */
    String takeSnapshot()
    {
        def result = cli.cmd(":take-snapshot")
        result.getResponse().get("result").asString()
    }
}
