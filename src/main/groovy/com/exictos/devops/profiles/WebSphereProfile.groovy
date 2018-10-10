package com.exictos.devops.profiles


import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.WSAdminWrapper

/**
 * This class represents a concrete WebSphere profile, and implements methods to get information about the profile
 * and the applications installed
 *
 */
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
        log.log("Getting all deployments in profile...")
        List<Instance> instances = new ArrayList<Instance>()

        try{
            List<String> deployments = wsadmin.list()
            deployments.each {deployment ->
                Instance instance = new Instance()
                instance.setName(deployment)
                try{
                    instance.setTimestamp(LiberoHelper.extractTimestamp(deployment))
                }catch(Exception e){
                    log.log("Could not parse application timestamp. Cause: ${e}")
                }
                instance.setEnabled(wsadmin.isApplicationRunning(deployment))
                instances.add(instance)
            }
        }catch(Exception e){
            log.log("Could not get list of all deployments. Cause: ${e.getMessage()}")
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
        log.log("Getting instances of ${applicationName}...")
        List<Instance> instances = new ArrayList<Instance>()
        try{
            List<Instance> deployments = listAllInstances()
            deployments.each {instance ->
                try{
                    if(new LiberoHelper().extractName(instance.getName()) == applicationName)
                        instances.add(instance)
                }catch(Exception e){
                    log.log("Could not parse application name. Cause: ${e}")
                }
            }
            instances = LiberoHelper.oldnessLevel(instances)
        }catch(Exception e){
            log.log("Could not get list of instances of ${applicationName}. Cause: ${e}")
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
        log.log("Getting all installed applications...")
        List<String> applications = new ArrayList<String>()
        try {
            List<Instance> deployments = listAllInstances()
            deployments.each { deployment ->
                try{
                    String name = new LiberoHelper().extractName(deployment.getName())
                    if (!applications.contains(name) && name != null)
                        applications.add(name)
                }catch(Exception e){
                    log.log("Could not parse application name. Cause: ${e}")
                }
            }
        }catch(Exception e){
            log.log("Could not get list of installed applications. Cause: ${e.getMessage()}")
            throw e
        }

        return applications
    }

    /**
     * Gets the context root of an application.
     *
     * @param applicationName
     * @return the context root
     */
    @Override
    String getApplicationContextRoot(String applicationName) {
        log.log("Getting application context root...")
        try {
            Instance newestInstance = new Instance()
            listInstances(applicationName).each { instance ->
                if (instance.getOldness() == 0)
                    newestInstance = instance
            }

            String contextRoot = ""
            wsadmin.getApplicationContextRoot(newestInstance.getName()).readLines().each { line ->
                if(line.startsWith("Context Root"))
                    contextRoot = line
            }
            contextRoot = contextRoot.replace("Context Root:  ", "")
            return contextRoot
        }catch(Exception e){
            log.log("Could not get application ${applicationName} context root. Cause: ${e})")
            throw e
        }
    }
}
