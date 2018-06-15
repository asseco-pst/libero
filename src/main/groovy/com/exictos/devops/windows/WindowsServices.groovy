package com.exictos.devops.windows

import com.exictos.devops.helpers.CmdRunner
import com.exictos.devops.helpers.FileUtils
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.NssmWrapper
import groovy.util.logging.Slf4j

@Slf4j
class WindowsServices {

    NssmWrapper nssm

    WindowsServices(){
        nssm = new NssmWrapper()
    }

    /**
     * Installs a windows service. If the service already exists, it will stop the service, uninstall it and reinstall
     * it with the new parameters.
     *
     * @param pathToPackage - a .zip file
     * @param installDirectory - directory where the package will be installed
     * @param serviceName - Windows service name
     * @param binPath - Path to the executable
     * @param argument - argument to append to the executable
     */
    void installApp(String pathToPackage, String installDirectory, String serviceName, String binPath, String argument = null)
    {
        if(status(serviceName) != NssmWrapper.Status.SERVICE_NOT_FOUND){
            stopApp(serviceName)
            uninstallApp(serviceName)
            install(pathToPackage, installDirectory, serviceName, binPath, argument)
        }
        else{
            install(pathToPackage, installDirectory, serviceName, binPath, argument)
        }

    }

    /**
     * Installs a windows service. Used by installApp()
     *
     * @param pathToPackage
     * @param installDirectory
     * @param serviceName
     * @param binPath
     * @param argument
     */
    private void install(String pathToPackage, String installDirectory, String serviceName, String binPath, String argument)
    {
        log.info("Installing service ${serviceName}...")
        def timestamp = LiberoHelper.getCurrentTimestamp()

        def newFile = FileUtils.copyFile(pathToPackage, installDirectory)
        def folder = FileUtils.unzip(newFile)
        def newName = new File(folder).getName()
        newName = FileUtils.renameFile(folder,"${newName}___${timestamp}")

        if(nssm.run(NssmWrapper.Command.install, serviceName, binPath,["${newName}${File.separator}${argument}"]))
            log.error("Failed to install ${serviceName}")
        log.info("Service ${serviceName} installed")
    }

    /**
     * Starts service serviceName at host specified in the profile
     *
     * @param serviceName - Windows service name
     */
    void startApp(String serviceName){
        log.info("Starting service ${serviceName}...")
        if(runAction(NssmWrapper.Command.start, serviceName))
            log.error("Failed to start service")
        else
            log.info("Service ${serviceName} started successfully")
    }

    /**
     * Stops service serviceName at host specified in the profile
     *
     * @param serviceName
     */
    void stopApp(String serviceName){
        log.info("Stopping service ${serviceName}...")
        if (runAction(NssmWrapper.Command.stop, serviceName))
            log.error("Failed to stop service")
        else
            log.info("Service ${serviceName} stopped successfully")
    }

    /**
     * Uninstalls service serviceName at host specified in the profile
     *
     * @param serviceName
     */
    void uninstallApp(String serviceName)
    {
        log.info("Removing service ${serviceName}...")
        if(runAction(NssmWrapper.Command.remove, serviceName, "confirm"))
            log.error("Failed to remove service")
        else
            log.info("Removed service ${serviceName} successfully")
    }

    /**
     * Call NssmWrapper to run a command
     *
     * @param action
     * @param serviceName
     * @return exit code from running the bash command
     */
    private int runAction(NssmWrapper.Command command, String serviceName, String parameter = null, List<String> arguments = Collections.emptyList()){
        nssm.run(command, serviceName,parameter,arguments)
    }

    /**
     * Gets the status of Windows service
     *
     * @param serviceName
     * @return Status of a windows service
     */
    private NssmWrapper.Status status(String serviceName){
        nssm.status(serviceName)
    }

}
