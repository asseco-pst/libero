package com.exictos.devops.containers

import com.exictos.devops.profiles.Profile

abstract class Container {

    Profile profile

    abstract void listInstances(String application)
    abstract void listApps()

    abstract void installApp(String pathToPackage, String applicationName)
    abstract void startApp(String applicationName)
    abstract void stopApp()
    abstract void uninstallApp()


    void startMostRecentApps(){

    }

    void stopAllApps(){

    }

    void uninstallAppOldInstances(){

    }

    void uninstallOldInstances(){

    }

}
