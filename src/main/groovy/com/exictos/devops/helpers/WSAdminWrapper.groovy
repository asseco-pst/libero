package com.exictos.devops.helpers

import groovy.util.logging.Slf4j

/**
 * This is a wrapper for WebSphere wsadmin script. It encapsulates the basic logic of calling wsadmin.
 */
@Slf4j
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
