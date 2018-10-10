package com.exictos.devops.services.managers

import ch.qos.logback.classic.Logger
import com.exictos.devops.helpers.FileUtils
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.LiberoLogger
import com.exictos.devops.helpers.NssmWrapper
import com.exictos.devops.helpers.XHDLogger
import com.exictos.devops.services.Service

/**
 * This class manages system services. At the moment only supports Windows services using NSSM tool.
 *
 */
class WindowsServiceManager extends ServiceManager{

    NssmWrapper nssm

    WindowsServiceManager(File nssmExecutable){
        nssm = new NssmWrapper(nssmExecutable.toString())
    }

    /**
     * Starts this service with NSSM
     *
     * @return true if successful
     */
    boolean start(Service service)
    {
        log.log("Starting service ${service.getName()}...")
        nssm.run(NssmWrapper.Command.start, service.name) == 0
    }

    /**
     * Stops this service
     *
     * @return true if successful
     */
    boolean stop(Service service)
    {
        log.log("Stopping service ${service.getName()}...")
        nssm.run(NssmWrapper.Command.stop, service.name) == 0
    }

    /**
     * Uninstall this service
     *
     * @return true if successful
     */
    boolean remove(Service service)
    {
        log.log("Uninstalling service ${service.getName()}...")
        nssm.run(NssmWrapper.Command.remove, service.name, "confirm") == 0
    }

    /**
     * Gets the current status of this service
     *
     * @return this service status (Check NssmWrapper.Status enum)
     */
    NssmWrapper.Status status(Service service){
        nssm.status(service.name)
    }

    /**
    * Installs a services service. If the service already exists, it will stop the service, uninstall it and reinstall
    * it with the new parameters.
    *
    * @param pathToPackage - a .zip file
    * @param installDirectory - directory where the package will be installed
    * @param serviceName - Windows service name
    * @param binPath - Path to the executable
    * @param argument - argument to append to the executable
    */
    protected void installService(Service service)
    {
        if(status(service) != NssmWrapper.Status.SERVICE_NOT_FOUND){
            stop(service)
            remove(service)
            uninstallOldInstances(service)
            install(service._package.toString(), service.installDirectory.toString(), service.getName()
                    , service.getBin().toString(), service.arguments.first())
        }
        else{
            log.log("Service not found")
            install(service._package.toString(), service.installDirectory.toString(), service.getName()
                    , service.getBin().toString(), service.arguments.first())
        }
    }

    /**
    * Installs a services service. Used by installService()
    *
    * @param pathToPackage
    * @param installDirectory
    * @param serviceName
    * @param binPath
    * @param argument
    */
    @Override
    protected boolean install(String pathToPackage, String installDirectory, String serviceName, String binPath, String argument)
    {
        log.log("Installing service ${serviceName}...")
        def timestamp = LiberoHelper.getCurrentTimestamp()

        log.log("Copying package to install directory")
        def newFile = FileUtils.copyFile(pathToPackage, installDirectory)
        log.log("Unzipping package in install directory")
        def folder = FileUtils.unzip(newFile)
        def newName = new File(folder).getName()
        log.log("Renaming folder with current timestamp")
        newName = FileUtils.renameFile(folder,"${newName}___${timestamp}")

        if(nssm.run(NssmWrapper.Command.install, serviceName, binPath,["${newName}${File.separator}${argument}"])){
            log.log("Failed to install ${serviceName}")
            return false
        }
        log.log("Service ${serviceName} installed")
        return true
    }

}
