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
    private String uninstallAppOldInstances



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

                wildfly(start, startMostRecentApps,stopAllApps, uninstallOldInstances, uninstallAppOldInstances)
                break
            case "was":
                host = cmd.getOptionValue("host")
                port = cmd.getOptionValue("port")
                username = cmd.getOptionValue("username")
                password = cmd.getOptionValue("password")
                String wsadmin = cmd.getOptionValue("wsadmin")

                was(wsadmin)

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
        uninstallAppOldInstances = cmd.getOptionValue("uninstallAppOldInstances")

    }

    /**
     * Installs an application in WildFly with rollback using the arguments from the CLI
     */
    private void wildfly(String start, String startMostRecentApps, String stopAllApps, String uninstallOldInstances
                         , String uninstallAppOldInstances){

        Container wildfly = new WildFly(host, port.toInteger(), username, password.toCharArray())
        wildfly.connect()
        String deploymentName = wildfly.installAppWithRollBack(new File(appLocation), appName, appVersion)

        if(start)
            wildfly.startApp(deploymentName)



    }

    /**
     * Installs an application in WebSphere with rollback using the arguments from the CLI
     */
    private void was(String wsadmin){

        Container was = new WebSphere(host, port.toInteger(), username, password.toCharArray(), wsadmin)
        was.connect()
        was.installAppWithRollBack(new File(appLocation), appName, appVersion)

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

}
