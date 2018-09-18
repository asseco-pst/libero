package com.exictos.devops.cli

import com.exictos.devops.containers.Container
import com.exictos.devops.containers.WebSphere
import com.exictos.devops.containers.WildFly
import com.exictos.devops.services.Service
import com.exictos.devops.services.managers.ServiceManager
import com.exictos.devops.services.managers.WindowsServiceManager
import org.apache.commons.cli.CommandLine

/**
 *
 */
class Libero {

    private String host
    private String port
    private String username
    private String password
    private String appName
    private String appLocation
    private String appVersion

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
    void run(CommandLine cmd){

        populateArguments(cmd)

        switch(cmd.getOptionValue("container")){
            case "wildfly":
                host = cmd.getOptionValue("host")
                port = cmd.getOptionValue("port")
                username = cmd.getOptionValue("username")
                password = cmd.getOptionValue("password")

                wildfly(cmd)
                break
            case "websphere":
                host = cmd.getOptionValue("host")
                port = cmd.getOptionValue("port")
                username = cmd.getOptionValue("username")
                password = cmd.getOptionValue("password")
                String wsadmin = cmd.getOptionValue("wsadmin")

                was(wsadmin, cmd)

                break
            case "ws":

                String nssm = cmd.getOptionValue("nssm")
                String bin = cmd.getOptionValue("bin")
                String argument = cmd.getOptionValue("argument")
                String installDir = cmd.getOptionValue("installDir")

                ws(nssm, bin, argument, installDir)
                break
        }

    }

    private void populateArguments(CommandLine cmd){

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

    private void runActions(Container container, CommandLine cmd){

        container.connect()

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

}
