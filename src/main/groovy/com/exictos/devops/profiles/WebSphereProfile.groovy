package com.exictos.devops.profiles

import com.exictos.devops.helpers.CmdRunner
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
        List<String> deployments = wsadmin.list()

        List<Instance> instances = new ArrayList<Instance>()

        deployments.each {deployment ->
            Instance instance = new Instance()
            instance.setName(deployment)
            instance.setTimestamp(LiberoHelper.extractTimestamp(deployment))
            instances.add(instance)
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

        List<Instance> instances = new ArrayList<Instance>()
        List<Instance> deployments = listAllInstances()

        deployments.each {instance ->
            if(LiberoHelper.extractName(instance.getName()) == applicationName) {
                instances.add(instance)
            }
        }

        instances = LiberoHelper.oldnessLevel(instances)

        return instances

    }

    /**
     * Lists all installed applications in this profile
     *
     * @return List of application names
     */
    @Override
    List<String> listInstalledApplications() {

        List<String> applications = new ArrayList<String>()
        List<Instance> deployments = listAllInstances()

        deployments.each{deployment ->
            String name = LiberoHelper.extractName(deployment.getName())
            if(!applications.contains(name) && name != null){
                applications.add(name)
            }
        }


        return applications

    }

}
