package com.exictos.devops

import com.exictos.devops.containers.Container
import com.exictos.devops.containers.WildFly
import com.exictos.devops.services.Service
import com.exictos.devops.services.managers.ServiceManager
import com.exictos.devops.services.managers.WindowsServiceManager
import org.jboss.as.cli.CliConfig
import org.jboss.as.cli.scriptsupport.CLI

class Playground {
    
    static void main(String []args){

        ServiceManager sm = new WindowsServiceManager(new File("C:\\Users\\jcoelho\\Documents\\nssm-2.24\\win32\\nssm.exe"))

        Service service = new Service()

        service.setName("PFS Hardware")
        service.set_package(new File("C:/package/pfs_hardware___v1.2.3.zip"))
        service.setArguments(["main.js"])
        service.setInstallDirectory(new File("C:/services"))
        service.setBin(new File("C:\\Program Files\\nodejs\\node.exe"))

        sm.installServiceWithRollback(service)
    }

}
