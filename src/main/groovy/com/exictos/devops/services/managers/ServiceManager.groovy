package com.exictos.devops.services.managers


import com.exictos.devops.Application
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.NssmWrapper
import com.exictos.devops.helpers.XHDLogger
import com.exictos.devops.profiles.Instance
import com.exictos.devops.services.Service

/**
 * The abstract class representing a service manager. Should be extended by concrete classes for each supported OS.
 */
abstract class ServiceManager{

    protected XHDLogger logger = new Application().getLogger()
    /**
     * Sets the file path to logger events
     *
     * @param the full filePath (eg. C:/logs/output.logger)
     */
    void setLogFile(File filePath){
        logger.setLogFile(filePath.toString())
        logger.log("Logging to ${filePath}")
    }

    /**
     * Installs service
     *
     * @param service
     * @return true if service was installed successfully
     */
    abstract protected boolean install(String pathToPackage, String installDirectory, String serviceName, String binPath, String argument)

    /**
     * Starts service
     *
     * @param service
     * @return true if service was started successfully
     */
    abstract boolean start(Service service)

    /**
     * Stops service
     *
     * @param service
     * @return true if service was stopped successfully
     */
    abstract boolean stop(Service service)

    /**
     * Removes service
     *
     * @param service
     * @return true if service was removed successfully
     */
    abstract boolean remove(Service service)

    /**
     * Gets the status of service
     *
     * @param service
     * @return the status of service
     */
    abstract NssmWrapper.Status status(Service service)

    /**
     * Lists all instances of service installed in the directory
     *
     * @param service
     * @return a list of instances of service installed
     */
    List<Instance> listInstances(Service service)
    {
        logger.log("Listing instances of ${service.getName()}...")
        List<Instance> instances = new ArrayList<Instance>()
        String instancePrefix = LiberoHelper.extractFolderNameFromPackageFile(service._package)

        service.installDirectory.eachDir {directory ->
            if(directory.getName().startsWith(instancePrefix) && LiberoHelper.isValidDeploymentName(directory.getName())){
                Instance instance = new Instance()
                instance.setName(directory.getName())
                try{
                    instance.setTimestamp(LiberoHelper.extractTimestamp(directory.getName()))
                }catch(Exception e){
                    logger.log("Could not parse application timestamp. Cause: ${e}")
                }
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
        logger.log("Installing service ${service.getName()} with rollback")
        stop(service)
        uninstallOldInstances(service)
        installService(service)
    }

    /**
     * Installs service
     *
     * @param service
     */
    abstract protected void installService(Service service)

    /**
    * Uninstall all old instances of service with an oldness level > oldnessThreshold
    *
    * @param service
    * @param oldnessThreshold
    */
    void uninstallOldInstances(Service service, int oldnessThreshold = 0)
    {
        logger.log("Uninstalling old instances of ${service.getName()}...")
        listInstances(service).each {instance ->
            if(instance.getOldness() > oldnessThreshold)
                new File(service.installDirectory,instance.getName()).deleteDir()
        }
    }

}
