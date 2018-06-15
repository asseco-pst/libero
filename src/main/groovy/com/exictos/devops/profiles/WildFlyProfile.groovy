package com.exictos.devops.profiles

import com.exictos.devops.helpers.LiberoHelper
import org.jboss.as.cli.scriptsupport.CLI
import java.sql.Timestamp

/**
 * This class represents a concrete WildFly profile, and implements methods to get information about the profile
 * and the applications installed
 *
 */
class WildFlyProfile extends Profile{

    CLI cli

    WildFlyProfile(CLI aCli){
        cli = aCli
    }

    /**
     * Lists all instances of all applications installed in this profile
     *
     * @return List of instances
     */
    @Override
    List<Instance> listAllInstances(){

        List<Instance> instances = new ArrayList<Instance>()
        def result  = cli.cmd("/deployment=*:read-resource()")
        def response = result.getResponse()
        def nodes = response.get("result")

        nodes.asList().each { it ->

            try {

                Instance instance = new Instance()
                instance.setName(it.get("result").get("name").asString())
                instance.setEnabled(it.get("result").get("enabled").asBoolean())

                def enabledTime = it.get("result").get("enabled-time")
                if(enabledTime.toString() != "undefined")
                    instance.setEnabledTime(new Timestamp(enabledTime.asLong()))

                Timestamp timestamp = LiberoHelper.extractTimestamp(instance.getName())
                instance.setTimestamp(timestamp)
                instances.add(instance)
            }catch(Exception e) {
                //TODO: log.warning("Instance [${instance.getName()}] does not follow naming convention: appName___yyyyMMdd_HHmmss")
            }
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
    List<Instance> listInstances(String applicationName){

        List<Instance> instances = new ArrayList<Instance>()
        List<Instance> deployments = listAllInstances()

        deployments.each {instance ->
            if(LiberoHelper.extractName(instance.getName()) == applicationName)
                instances.add(instance)
        }

        instances = oldnessLevel(instances)

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

        deployments.each {deployment ->
            String name = LiberoHelper.extractName(deployment.getName())
            if(!applications.contains(name))
                applications.add(name)
        }

        return applications
    }

    /**
     * Loops through instances and sets the oldness level for each instance according to its timestamp
     *
     * @param instances
     * @return list of instances
     */
    private List<Instance> oldnessLevel(List<Instance> instances){

        instances.sort{it.timestamp}
        instances.reverse(true)

        instances.eachWithIndex { Instance entry, int i ->
            entry.setOldness(i)
        }

        return instances
    }
}
