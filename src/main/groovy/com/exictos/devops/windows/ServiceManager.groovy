package com.exictos.devops.windows

import com.exictos.devops.helpers.FileUtils
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.NssmWrapper
import com.exictos.devops.profiles.Instance
import groovy.util.logging.Slf4j

/**
 * This class manages system services. At the moment only supports Windows services using NSSM tool.
 *
 */
@Slf4j
class ServiceManager {

    NssmWrapper nssm

    ServiceManager(){
        nssm = new NssmWrapper()
    }

    /**
     * Lists all instances of service
     *
     * @param service
     * @return list of instances
     */
    static List<Instance> listInstances(Service service)
    {
        List<Instance> instances = new ArrayList<Instance>()
        String instancePrefix = LiberoHelper.extractFolderNameFromPackageFile(service._package)

        service.installDirectory.eachDir {directory ->
            if(directory.getName().startsWith(instancePrefix)){
                Instance instance = new Instance()
                instance.setName(directory.getName())
                instance.setTimestamp(LiberoHelper.extractTimestamp(directory.getName()))
                instances.add(instance)
            }
        }

        LiberoHelper.oldnessLevel(instances)
    }

    /**
     * Install service with rollback procedure
     *
     * @param service
     */
    void installServiceWithRollback(Service service)
    {
        service.stop()
        uninstallOldInstances(service)
        installService(service)
        service.start()
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
    void installService(Service service)
    {
        if(service.status() != NssmWrapper.Status.SERVICE_NOT_FOUND){
            service.stop()
            service.remove()
            uninstallOldInstances(service)
            install(service._package.toString(), service.installDirectory.toString(), service.getName()
                    , service.getBin().toString(), service.arguments.first())
        }
        else{
            install(service._package.toString(), service.installDirectory.toString(), service.getName()
                    , service.getBin().toString(), service.arguments.first())
        }
    }

    /**
    * Installs a windows service. Used by installService()
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
     * Uninstall all old instances of service with an oldness level > oldnessThreshold
     *
     * @param service
     * @param oldnessThreshold
     */
    static void uninstallOldInstances(Service service, int oldnessThreshold = 0)
    {
        listInstances(service).each {instance ->
            if(instance.getOldness() > oldnessThreshold)
                new File(service.installDirectory,instance.getName()).deleteDir()
        }
    }

}
