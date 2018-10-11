package com.exictos.devops

import com.exictos.devops.containers.Container
import com.exictos.devops.containers.WildFly
import org.jboss.as.cli.CliConfig
import org.jboss.as.cli.scriptsupport.CLI

class Playground {
    
    static void main(String []args){

        Container wf = new WildFly("***REMOVED***", 9997, ***REMOVED***, ***REMOVED***.toCharArray())
        wf.connect()
        wf.installAppWithRollBack(new File("C:/BackOfficeWSEAR.ear"),"BackOfficeWS","1.2.3")
        wf.disconnect()
    }

}
