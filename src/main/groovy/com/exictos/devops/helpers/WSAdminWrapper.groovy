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

    /**
     * Determines if the specified application has been distributed and is ready to be run.
     *
     * @param deploymentName
     * @return true if app is ready. False otherwise
     */
    boolean isAppReady(String deploymentName){
        String output = run("\$AdminApp isAppReady ${deploymentName}")
        String[] lines = output.split("\n")
        lines.last() == "true"
    }

    /**
     * Lists all applications installed in the profile.
     *
     * @return a list of applications
     */
    List<String> list()
    {
        String output = run("\$AdminApp list")
        toLines(output)
    }

    /**
     * Installs an application from package at pathToPackage with the name applicationName
     *
     * @param pathToPackage - the package to install application from
     * @param applicationName - the name to install the application with
     */
    void installApplication(String pathToPackage, String applicationName)
    {
        String normalizedPath = LiberoHelper.normalizePath(pathToPackage)
        run("\$AdminApp install {${normalizedPath}} {-appname ${applicationName}}")
        saveConfig()
    }

    /**
     * Starts an application.
     *
     * @param applicationName - the name of the application to start
     */
    void startApplication(String applicationName)
    {
        run("\$AdminControl invoke [\$AdminControl queryNames type=ApplicationManager,*] startApplication ${applicationName}")
    }

    /**
     * Stops an application.
     *
     * @param applicationName - the name of the application to stop
     */
    void stopApplication(String applicationName)
    {
        run("\$AdminControl invoke [\$AdminControl queryNames type=ApplicationManager,*] stopApplication ${applicationName}")
    }

    /**
     * Uninstalls an application.
     *
     * @param applicationName - the name of the application to uninstall
     */
    void uninstallApp(String applicationName)
    {
        run("\$AdminApp uninstall ${applicationName}")
        saveConfig()
    }

    /**
     * Gets the context root of an installed application
     *
     * @param deploymentName - the name of the deployment of an application
     * @return the context root of an application
     */
    String getApplicationContextRoot(String deploymentName)
    {
        run("\$AdminApp view  ${deploymentName} {-CtxRootForWebMod}")

    }

    /**
     * Returns true if the application is running.
     *
     * @param deploymentName - the name of the deployment
     * @return true if the application is running
     */
    boolean isApplicationRunning(String deploymentName)
    {
        String output = run("\$AdminControl completeObjectName type=Application,name=${deploymentName},*")
        if(toLines(output).isEmpty())
            return false
        return true
    }

    /**
     * Saves WAS configuration after a management operation is performed.
     */
    void saveConfig()
    {
        run("\$AdminConfig save")
    }

    /**
     * Runs commands on a WebSphere profile using wsadmin script.
     *
     * @param command to run
     * @return the command output as string
     */
    private String run(String command){
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

    /**
     * Parses the output of a command run from wsadmin to a List of strings. One string per line of output.
     *
     * @param output
     * @return list of strings
     */
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
