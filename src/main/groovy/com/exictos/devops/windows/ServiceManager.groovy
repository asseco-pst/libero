package com.exictos.devops.windows

import com.exictos.devops.helpers.FileUtils
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.NssmWrapper
import com.exictos.devops.profiles.Instance
import groovy.util.logging.Slf4j

@Slf4j
class ServiceManager {

    NssmWrapper nssm

    ServiceManager(){
        nssm = new NssmWrapper()
    }

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

    void installServiceWithRollback(Service service)
    {
        service.stop()
        uninstallOldInstances(service)
        installService(service)
        service.start()
    }

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

    static void uninstallOldInstances(Service service, int oldnessThreshold = 0)
    {
        listInstances(service).each {instance ->
            if(instance.getOldness() > oldnessThreshold)
                new File(service.installDirectory,instance.getName()).deleteDir()
        }
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
    /*void installService(Service service)
    {
        if(status(service.name) != NssmWrapper.Status.SERVICE_NOT_FOUND){
            stopService(service.name)
            uninstallApp(service.name)
            removeOldInstances(pathToPackage, installDirectory)
            install(pathToPackage, installDirectory, name, binPath, argument)
        }
        else{
            install(pathToPackage, installDirectory, name, binPath, argument)
        }

    }

    *//**
     * Installs a windows service. Used by installService()
     *
     * @param pathToPackage
     * @param installDirectory
     * @param serviceName
     * @param binPath
     * @param argument
     *//*
    private void install(String pathToPackage, String installDirectory, String name, String binPath, String argument)
    {
        log.info("Installing service ${name}...")
        def timestamp = LiberoHelper.getCurrentTimestamp()

        def newFile = FileUtils.copyFile(pathToPackage, installDirectory)
        def folder = FileUtils.unzip(newFile)
        def newName = new File(folder).getName()
        newName = FileUtils.renameFile(folder,"${newName}___${timestamp}")

        if(nssm.run(NssmWrapper.Command.install, name, binPath,["${newName}${File.separator}${argument}"]))
            log.error("Failed to install ${name}")
        log.info("Service ${name} installed")
    }

    *//**
     * Starts service name at host specified in the profile
     *
     * @param name - Windows service name
     *//*
    void startService(Service service){
        log.info("Starting service ${service.name}...")
        if(service.start())
            log.error("Failed to start service")
        else
            log.info("Service ${service.name} started successfully")
    }

    *//**
     * Stops service name at host specified in the profile
     *
     * @param name
     *//*
    void stopService(Service service){
        log.info("Stopping service ${service.name}...")
        if (service.stop())
            log.error("Failed to stop service")
        else
            log.info("Service ${service.name} stopped successfully")
    }

    *//**
     * Uninstalls service name at host specified in the profile
     *
     * @param name
     *//*
    void uninstallApp(Service service)
    {
        log.info("Removing service ${service.name}...")
        service.stop()
        if(service.remove())
            log.error("Failed to remove service")
        else
            log.info("Removed service ${service.name} successfully")
    }

    static List<Instance> listInstances(String packageName, String installDirectory){

        List<Instance> instances = new ArrayList<Instance>()

        def folder = new File(installDirectory)
        folder.eachDir {directory ->
            if(directory.getName().startsWith(packageName)){
                Instance inst = new Instance()
                inst.setTimestamp(LiberoHelper.extractTimestamp(directory.getName()))
                inst.setName(directory.getName())
                instances.add(inst)
            }
        }

        instances = LiberoHelper.oldnessLevel(instances)

        return instances
    }

    private static void removeOldInstances(String pathToPackage, String installDirectory)
    {
        File folder = new File(installDirectory)
        File file = new File(pathToPackage)
        if(!file.isFile())
            throw new IllegalArgumentException("Invalid package ${pathToPackage}")
        if(!folder.isDirectory())
            throw new IllegalArgumentException("Invalid install directory ${installDirectory}")

        listInstances(file.getName().replace(".zip",""),installDirectory).each {instance ->
            File f = new File(folder.getAbsolutePath(),instance.getName())
            f.deleteDir()
        }

    }*/


}
