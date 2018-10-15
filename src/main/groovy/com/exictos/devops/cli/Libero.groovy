package com.exictos.devops.cli

import com.exictos.devops.containers.Container
import com.exictos.devops.containers.WebSphere
import com.exictos.devops.containers.WildFly
import com.exictos.devops.helpers.XHDLogger
import com.exictos.devops.services.Service
import com.exictos.devops.services.managers.ServiceManager
import com.exictos.devops.services.managers.WindowsServiceManager
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.MissingArgumentException

/**
 *
 */
class Libero {

    protected static XHDLogger log

    private String container

    private String host
    private String port
    private String username
    private String password
    private String appName
    private String appLocation
    private String appVersion

    private String nssm
    private String bin
    private String argument
    private String installDir

    private boolean install
    private boolean start
    private boolean startMostRecentApps
    private boolean stopAllApps
    private boolean uninstallOldInstances
    private boolean uninstallAppOldInstances

    /**
     * This method evaluates the arguments received from the CLI and calls the appropriate functions
     * @param cmd
     */
    void run(CommandLine cmd, XHDLogger logger){

        log = logger

        populateArguments(cmd)
        validateArguments()

        switch(container){
            case "wildfly":
                wildfly(cmd)
                break
            case "websphere":
                String wsadmin = cmd.getOptionValue("wsadmin")
                was(wsadmin, cmd)
                break
            case "ws":
                ws(nssm, bin, argument, installDir)
                break
        }

    }

    /**
     * Populates variables with the arguments in the command line cmd
     *
     * @param cmd
     */
    private void populateArguments(CommandLine cmd){

        container = cmd.getOptionValue("container")

        if(container == "wildfly" || container == "websphere"){
            host = cmd.getOptionValue("host")
            port = cmd.getOptionValue("port")
            username = cmd.getOptionValue("username")
            password = cmd.getOptionValue("password")
        }else if(container == "ws"){
            nssm = cmd.getOptionValue("nssm")
            bin = cmd.getOptionValue("bin")
            argument = cmd.getOptionValue("argument")
            installDir = cmd.getOptionValue("installDir")
        }

        appName = cmd.getOptionValue("appName")
        appLocation = cmd.getOptionValue("appLocation")
        appVersion = cmd.getOptionValue("appVersion")

        install = cmd.hasOption("install")
        start = cmd.hasOption("start")
        startMostRecentApps = cmd.hasOption("startMostRecentApps")
        stopAllApps = cmd.hasOption("stopAllApps")
        uninstallOldInstances = cmd.hasOption("uninstallOldInstances")
        uninstallAppOldInstances = cmd.hasOption("uninstallAppOldInstances")

    }


    /**
     * Runs actions specified in the arguments from the CLI in WildFly
     */
    private void wildfly(CommandLine cmd){

        Container wildfly = new WildFly(host, port.toInteger(), username, password.toCharArray())
        runActions(wildfly, cmd)

    }

    /**
     * Runs actions specified in the arguments from the CLI in WebSphere
     */
    private void was(String wsadmin, CommandLine cmd){

        Container was = new WebSphere(host, port.toInteger(), username, password.toCharArray(), wsadmin)
        runActions(was, cmd)

    }

    /**
     * Installs a Windows service with rollback using the arguments from the CLI
     */
    private void ws(String nssm, String bin, String argument, String installDir){

        Service service = new Service()
        service.setName(appName)
        service.set_package(new File(appLocation))
        service.setInstallDirectory(new File(installDir))
        service.setBin(new File(bin))
        service.setArguments([argument])


        ServiceManager wsm = new WindowsServiceManager(new File(nssm))
        wsm.installServiceWithRollback(service)

    }

    /**
     * Executes the specified actions in the specified container
     *
     * @param container Container specified by host:port
     * @param cmd the command line
     */
    private void runActions(Container container, CommandLine cmd){

        container.connect()

        if(!(install || startMostRecentApps || stopAllApps || uninstallOldInstances || uninstallAppOldInstances)){
            log.log("Warning: No actions were selected to perform.")
        }

        if(install){
            String deployment = container.installAppWithRollBack(new File(appLocation), appName, appVersion)
            if(start)
                container.startApp(deployment)
        }

        if(startMostRecentApps)
            container.startMostRecentApps()

        if(stopAllApps)
            container.stopAllApps()

        if(uninstallOldInstances)
            container.uninstallOldInstances()

        if(uninstallAppOldInstances)
            container.uninstallAppOldInstances(cmd.getOptionValue("uninstallAppOldInstances"))


    }

    /**
     * Validates the command line arguments
     *
     * If any required arguments are missing a MissingArgumentException is thrown
     */
    private void validateArguments(){

        if(!container) throw new MissingArgumentException("Missing argument: container")

        if(container == "wildfly" || container == "websphere"){
            if(!host) throw new MissingArgumentException("Missing argument: host")
            if(!port) throw new MissingArgumentException("Missing argument: port")
            if(!username) throw new MissingArgumentException("Missing argument: username")
            if(!password) throw new MissingArgumentException("Missing argument: password")
        }else if(container == "ws"){
            if(!nssm) throw new MissingArgumentException("Missing argument: nssm")
            if(!bin)throw new MissingArgumentException("Missing argument: bin")
            if(!argument) throw new MissingArgumentException("Missing argument: argument")
            if(!installDir) throw new MissingArgumentException("Missing argument: installDir")
        }

        if(install){
            if(!appName) throw new MissingArgumentException("Missing argument: appName")
            if(!appLocation) throw new MissingArgumentException("Missing argument: appLocation")
        }
    }
}
