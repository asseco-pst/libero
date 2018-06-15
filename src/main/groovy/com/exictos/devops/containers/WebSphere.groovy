package com.exictos.devops.containers

import com.exictos.devops.helpers.CmdBuilder
import com.exictos.devops.helpers.CmdRunner
import com.exictos.devops.helpers.LiberoHelper
import com.exictos.devops.profiles.Profile
import com.exictos.devops.profiles.WebSphereProfile

class WebSphere extends Container{

    String deployScript
    String wasAdminScript
    String workspace
    String appLocation
    //String appName
    String packageType

    String command

    WebSphere(String aHost, int aPort = 8880, String aUsername = null, char[] aPassword = null){
        profile = new WebSphereProfile()
        profile.host = aHost
        profile.port = aPort
        profile.username = aUsername
        profile.password = aPassword
    }

    @Override
    void connect() {}

    @Override
    void disconnect(){}

    @Override
    String installApp(String pathToPackage, String applicationName)
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appName(applicationName)
                .appTimestamp(LiberoHelper.getCurrentTimestamp())
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("INSTALL_APP")
                .build()

        CmdRunner.run(command)
    }

    @Override
    void startApp(String applicationName)
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appName(applicationName)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("START_APP")
                .build()

        CmdRunner.run(command)
    }

    @Override
    void startMostRecentInstance(String applicationName) {

    }

    @Override
    void stopApp(String deploymentName)
    {}

    @Override
    void uninstallApp(String deploymentName) {

        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appName(deploymentName)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("UNINSTALL_APP")
                .build()

        CmdRunner.run(command)
    }

    @Override
    void startMostRecentApps()
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("START_MOST_RECENT_APPS")
                .build()

        CmdRunner.run(command)
    }

    @Override
    void stopOldInstances() {

    }

    @Override
    void stopAllApps()
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("STOP_ALL_APPS")
                .build()

        CmdRunner.run(command)
    }

    @Override
    String uninstallAppOldInstances(String applicationName, int oldnessThreshold = 0)
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appName(applicationName)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("UNINSTALL_APP_OLDEST_INSTANCES")
                .build()

        CmdRunner.run(command)
    }

    void uninstallOldInstances()
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("UNINSTALL_ALL_OLD_INSTANCES")
                .build()

        CmdRunner.run(command)
    }

    void disableOldAppsAutoStart()
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("DISABLE_AUTOSTART_OLD_APPS")
                .build()

        CmdRunner.run(command)
    }

    void enableMostRecentAppsAutoStart()
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appLocation(appLocation)
                .packageType(packageType)
                .addAction("ENABLE_AUTOSTART_MOST_RECENT_APPS")
                .build()

        CmdRunner.run(command)
    }

    void toggleAppAutoStart(String applicationName)
    {
        command = new CmdBuilder().deployScript(deployScript)
                .ip(profile.host)
                .port(profile.port)
                .username(profile.username)
                .wasAdminScriptRootLocation(wasAdminScript)
                .wasAdminInstallScriptLocation("${wasAdminScript}/install.py")
                .workspace(workspace)
                .appLocation(appLocation)
                .appName(applicationName)
                .packageType(packageType)
                .addAction("TOOGLE_AUTOSTART_OF_APP")
                .build()

        CmdRunner.run(command)
    }
}
