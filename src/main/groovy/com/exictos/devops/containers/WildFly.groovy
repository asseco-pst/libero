package com.exictos.devops.containers

import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.profiles.WildFlyProfile
import org.jboss.as.cli.scriptsupport.CLI

class WildFly extends Container{

    CLI cli

    WildFly(String aHost, int aPort = 9990, String aUsername = null, char[] aPassword = null){

        profile = new WildFlyProfile(cli)
        profile.host = aHost
        profile.port = aPort
        profile.username = aUsername
        profile.password = aPassword

        cli = CLI.newInstance()
        cli.connect(aHost, aPort, aUsername, aPassword)
    }

    @Override
    void listInstances(String application) {

    }

    @Override
    void listApps() {

    }

    @Override
    void installApp(String aPathToPackage, String aApplicationName) {

        def name = LiberoHelper.standardizeName(aPathToPackage, aApplicationName)
        cli.cmd("deploy --name=${name} --runtime-name=${name} ${aPathToPackage} --disabled")

    }

    @Override
    void startApp(String aApplicationName) {

        cli.cmd("deploy --name=${aApplicationName}")
    }

    @Override
    void stopApp() {

    }

    @Override
    void uninstallApp() {

    }
}
