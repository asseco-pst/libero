package com.exictos.devops.services.managers

import com.exictos.devops.profiles.Instance
import com.exictos.devops.services.Service

abstract class ServiceManager {

    /**
     * Installs service
     *
     * @param service
     * @return true if service was installed successfully
     */
    abstract boolean install(String pathToPackage, String installDirectory, String serviceName, String binPath, String argument)

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
    abstract Service.Status status(Service service)

    /**
     * Lists all instances of service installed in the directory
     *
     * @param service
     * @return a list of instances of service installed
     */
    abstract static List<Instance> listInstances(Service service)

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
     * Installs service
     *
     * @param service
     */
    abstract void installService(Service service)


    abstract static void uninstallOldInstances(Service service, int oldnessThreshold = 0)

}
