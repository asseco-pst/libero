package com.exictos.devops.profiles

import com.exictos.devops.helpers.LiberoHelper
import org.jboss.as.cli.scriptsupport.CLI

import java.sql.Timestamp
import java.text.SimpleDateFormat

class WildFlyProfile extends Profile{

    CLI cli

    WildFlyProfile(CLI aCli){
        cli = aCli
    }

    /**
     * Gets all instances of aApplicationName installed in this profile
      * @param aApplicationName
     * @return List of instances installed
     */
    List<Instance> getAppInstances(String aApplicationName){

        List<Instance> instances = new ArrayList<Instance>()
        def result  = cli.cmd("/deployment=*:read-resource()")
        def response = result.getResponse()
        def nodes = response.get("result")

        nodes.asList().each { it ->
            Instance instance = new Instance()
            instance.setName(it.get("result").get("name").toString().replace("\"",""))
            instance.setEnabled(it.get("result").get("enabled").toString() == "true")
            instance.setEnabledTime(it.get("result").get("enabledTime").toString())

            try {

                Timestamp timestamp = LiberoHelper.extractTimestamp(instance.getName())
                instance.setTimestamp(timestamp.toString())
                instances.add(instance)

            }catch(Exception e) {
                //TODO: log.warning("Instance [${instance.getName()}] does not follow naming convention: appName___yyyyMMdd_HHmmss")
            }
        }

        return instances

    }

}
