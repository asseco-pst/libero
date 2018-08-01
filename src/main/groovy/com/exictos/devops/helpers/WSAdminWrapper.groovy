package com.exictos.devops.helpers

import ch.qos.logback.classic.Logger

/**
 * This is a wrapper for WebSphere wsadmin script. It encapsulates the basic logic of calling wsadmin.
 */
class WSAdminWrapper {

    protected static final Logger log = LiberoLogger.getLogger()

    String home
    String host
    int port
    String username
    char[] password

    WSAdminWrapper(String path, String host, int port, String username, char[] password){
        this.home = path
        this.host = host
        this.port = port
        this.username = username
        this.password = password
    }

    boolean isAppReady(String deploymentName){
        String output = run("\$AdminApp isAppReady ${deploymentName}")
        String[] lines = output.split("\n")
        lines.last() == "true"
    }

    List<String> list()
    {
        String output = run("\$AdminApp list")
        toLines(output)
    }


    void installApplication(String pathToPackage, String applicationName)
    {
        String normalizedPath = LiberoHelper.normalizePath(pathToPackage)
        run("\$AdminApp install ${normalizedPath} {-appname ${applicationName}}")
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

    String getApplicationContextRoot(String deploymentName)
    {
        run("\$AdminApp view  ${deploymentName} {-CtxRootForWebMod}")

    }

    boolean isApplicationRunning(String deploymentName)
    {
        String output = run("\$AdminControl completeObjectName type=Application,name=${deploymentName},*")
        if(toLines(output).isEmpty())
            return false
        return true
    }

    void saveConfig()
    {
        run("\$AdminConfig save")
    }

    String run(String command){
        String cmd = "${home} -conntype SOAP -host ${host} -port ${port} -user ${username} -password ${password.toString()} " +
                "-c \"${command}\""
        CmdRunner.CmdResponse response = CmdRunner.getResponse(cmd)
        if(response.exitCode != 0){
            log.error(response.getOutput().trim())
            throw new RuntimeException("WSAdmin command returned non-zero code(${response.getExitCode()})")
        }

        log.info(response.getOutput().trim())
        return response.getOutput().trim()
    }

    private static List<String> toLines(String output){
        List<String> lines = new ArrayList<String>()
        output.split("\n").each {line ->
            //noinspection SpellCheckingInspection
            if(!line.startsWith("WASX7209I"))
                lines.add(line)
        }
        lines
    }

}
