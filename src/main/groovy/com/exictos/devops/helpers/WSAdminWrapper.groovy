package com.exictos.devops.helpers

/**
 * This is a wrapper for WebSphere wsadmin script. It encapsulates the basic logic of calling wsadmin.
 */
class WSAdminWrapper {

    String home
    String host
    int port
    String username
    char[] password

    WSAdminWrapper(String path, String aHost, int aPort, String aUsername, char[] aPassword){
        home = path
        host = aHost
        port = aPort
        username = aUsername
        password = aPassword
    }

    boolean isAppReady(String aDeploymentName){
        String output = run("\$AdminApp isAppReady ${aDeploymentName}")
        String[] lines = output.split("\n")
        lines.last() == "true"
    }

    List<String> list()
    {
        String output = run("\$AdminApp list")
        List<String> lines = new ArrayList<String>()
        output.split("\n").each {line ->
            //noinspection SpellCheckingInspection
            if(!line.startsWith("WASX7209I"))
                lines.add(line)
        }
        lines
    }

    void installApplication(String pathToPackage, String applicationName)
    {
        run("\$AdminApp install ${pathToPackage} {-appname ${applicationName}}")
        saveConfig()
    }

    void startApplication(String applicationName)
    {
        run("\$AdminControl invoke [\$AdminControl queryNames type=ApplicationManager,*] startApplication ${applicationName}")
    }

    void stopApplication(String applicationName)
    {
        run("\$AdminControl invoke [\$AdminControl queryNames type=ApplicationManager,*] stopApplication ${applicationName}")
    }

    void uninstallApp(String applicationName)
    {
        run("\$AdminApp uninstall ${applicationName}")
        saveConfig()
    }

    void saveConfig()
    {
        run("\$AdminConfig save")
    }

    String run(String command){
        String cmd = "${home} -conntype SOAP -host ${host} -port ${port} -user ${username} -password ${password.toString()} " +
                "-c \"${command}\""
        CmdRunner.runOutput(cmd)
    }

}
