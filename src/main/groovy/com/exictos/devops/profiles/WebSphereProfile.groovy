package com.exictos.devops.profiles

import com.exictos.devops.helpers.CmdRunner
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.WSAdminWrapper
import groovy.util.logging.Slf4j

/**
 * This class represents a concrete WebSphere profile, and implements methods to get information about the profile
 * and the applications installed
 *
 */
@Slf4j
class WebSphereProfile extends Profile{

    WSAdminWrapper wsadmin

    WebSphereProfile(WSAdminWrapper aWsadmin)
    {
        wsadmin = aWsadmin
    }

    /**
     * Lists all instances of all applications installed in this profile
     *
     * @return List of instances
     */
    @Override
    List<Instance> listAllInstances() {
        log.info("Getting all deployments in profile...")
        List<Instance> instances = new ArrayList<Instance>()

        try{
            List<String> deployments = wsadmin.list()
            deployments.each {deployment ->
                Instance instance = new Instance()
                instance.setName(deployment)
                instance.setTimestamp(LiberoHelper.extractTimestamp(deployment))
                instance.setEnabled(wsadmin.isApplicationRunning(deployment))
                instances.add(instance)
            }
        }catch(Exception e){
            log.error("Could not get list of all deployments. Cause: ${e.getMessage()}")
            throw e
        }

        return instances
    }

    /**
     * Lists all instances of an application installed in this profile
     *
     * @param applicationName
     * @return List of instances
     */
    @Override
    List<Instance> listInstances(String applicationName) {
        log.info("Getting instances of ${applicationName}...")
        List<Instance> instances = new ArrayList<Instance>()
        try{
            List<Instance> deployments = listAllInstances()
            deployments.each {instance ->
                if(LiberoHelper.extractName(instance.getName()) == applicationName) {
                    instances.add(instance)
                }
            }
            instances = LiberoHelper.oldnessLevel(instances)
        }catch(Exception e){
            log.error("Could not get list of instances of ${applicationName}. Cause: ${e.getCause()}")
            throw e
        }

        return instances
    }

    /**
     * Lists all installed applications in this profile
     *
     * @return List of application names
     */
    @Override
    List<String> listInstalledApplications() {
        log.info("Getting all installed applications...")
        List<String> applications = new ArrayList<String>()
        try {
            List<Instance> deployments = listAllInstances()
            deployments.each { deployment ->
                String name = LiberoHelper.extractName(deployment.getName())
                if (!applications.contains(name) && name != null) {
                    applications.add(name)
                }
            }
        }catch(Exception e){
            log.error("Could not get list of installed applications. Cause: ${e.getMessage()}")
            throw e
        }

        return applications
    }

    @Override
    String getApplicationContextRoot(String application) {
        return null
    }
}
