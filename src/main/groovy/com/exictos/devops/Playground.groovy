package com.exictos.devops

import com.exictos.devops.cli.Libero
import com.exictos.devops.helpers.LiberoHelper

class Playground {

    static void main(String[] args){

        String[] arg = ["-container","websphere","-host","***REMOVED***","-port","8888","-username","jenkins","-password","ieapdjnw70","-wsadmin","C:/IBM/WebSphere/AppServer/bin/wsadmin.bat","-appLocation","C:/Program Files (x86)/Jenkins/workspace/MW Modules (Sandbox)/ant/DistributionEAR/BackOfficeWS/2018-09-19_10-35-19/BackOfficeWSEAR.ear","-appName","BackofficeWS","-install","-start"]

        Main.main(arg)

    }

}
