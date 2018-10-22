package com.exictos.devops.profiles

import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.helpers.XHDLogger
import org.jboss.as.cli.scriptsupport.CLI
import org.jboss.dmr.ModelNode

import java.sql.Timestamp

/**
 * This class represents a concrete WildFly profile, and implements methods to get information about the profile
 * and the applications installed
 *
 */
class WildFlyProfile extends Profile{

    CLI cli

    WildFlyProfile(CLI aCli, XHDLogger log){
        cli = aCli
        this.logger = log
    }

    /**
     * Lists all instances of all applications installed in this profile
     *
     * @return List of instances
     */
    @Override
    List<Instance> listAllInstances(){
        logger.log("Getting all deployments in profile...")
        List<Instance> instances = new ArrayList<Instance>()
        def result  = cli.cmd("/deployment=*:read-resource()")
        def response = result.getResponse()
        def nodes = response.get("result")

        nodes.asList().each { it ->

            Instance instance = new Instance()
            try {

                instance.setName(it.get("result").get("name").asString())
                instance.setEnabled(it.get("result").get("enabled").asBoolean())

                def enabledTime = it.get("result").get("enabled-time")
                if(enabledTime.toString() != "undefined")
                    instance.setEnabledTime(new Timestamp(enabledTime.asLong()))

                try{
                    Timestamp timestamp = LiberoHelper.extractTimestamp(instance.getName())
                    instance.setTimestamp(timestamp)
                }catch(Exception e){
                    logger.log("Could not parse application timestamp. Cause: ${e}")
                }

                instances.add(instance)
            }catch(Exception e) {
                logger.log("Could not get list of all deployments. Cause: ${e.getMessage()}")
                throw e
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
        logger.log("Getting instances of ${applicationName}...")
        List<Instance> instances = new ArrayList<Instance>()
        try {
            List<Instance> deployments = listAllInstances()
            deployments.each { instance ->
                try {
                    if (new LiberoHelper().extractName(instance.getName()) == applicationName) {
                        logger.log("\t${instance.getName()}")
                        instances.add(instance)
                    }
                }catch(Exception e){
                    logger.log("Could not parse application name. Cause: ${e}")
                }
            }
            instances = LiberoHelper.oldnessLevel(instances)
        }catch(Exception e){
            logger.log("Could not get list of instances of ${applicationName}. Cause: ${e.getMessage()}")
            throw e
        }

        return instances
    }

    /**
     * Lists all installed applications in this profile
     *
     * @return list of application names
     */
    @Override
    List<String> listInstalledApplications() {
        logger.log("Getting all installed applications...")
        List<String> applications = new ArrayList<String>()
        try {
            List<Instance> deployments = listAllInstances()
            deployments.each { deployment ->
                try {
                    String name = new LiberoHelper().extractName(deployment.getName())
                    if (!applications.contains(name)) {
                        logger.log("\t${name}")
                        applications.add(name)
                    }
                }catch(Exception e){
                    logger.log("Could not parse application name. Cause: ${e}")
                }
            }
        }catch(Exception e){
            logger.log("Could not get list of installed applications. Cause: ${e.getMessage()}")
            throw e
        }

        return applications
    }

    /**
     * Returns an installed application context root.
     * The application must be running otherwise this method will returned undefined
     *
     * @param applicationName
     * @return the application context root
     */
    @Override
    String getApplicationContextRoot(String applicationName)
    {
        logger.log("Getting application context root...")
        try {
            Instance newestInstance = new Instance()
            listInstances(applicationName).each { instance ->
                if (instance.getOldness() == 0)
                    newestInstance = instance
            }
            logger.log("/deployment=${newestInstance.getName()}/subdeployment=*/subsystem=undertow:read-attribute(name=context-root)")
            def result = cli.cmd("/deployment=${newestInstance.getName()}/subdeployment=*/subsystem=undertow:read-attribute(name=context-root)")
            def response = result.getResponse()
            ModelNode nodes = response.get("result").get(0)

            nodes.get("result").asString()
        }catch(Exception e){
            logger.log("Could not get application ${applicationName} context root. Cause: ${e})")
            throw e
        }
    }

}
