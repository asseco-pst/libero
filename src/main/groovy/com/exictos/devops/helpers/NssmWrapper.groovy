package com.exictos.devops.helpers

/**
 * This class serves as an NSSM wrapper.
 */
class NssmWrapper {


    private String nssmHome

    NssmWrapper(String nssmHome){
        this.nssmHome = nssmHome
    }

    /**
     * Runs an NSSM command and returns it's exit code
     *
     * The structure of an NSSM command is as follows:
     *
     * nssm.exe <command> [<name>] <parameter> [<argument>]
     *
     * Check NSSM documentation for more info
     *
     * @param command - An NSSM command
     * @param serviceName - Windows Service Name
     * @param parameter - a parameter for the NSSM command
     * @param arguments - an argument for the parameter
     * @return exit code returned from running NSSM
     */
    int run(Command command, String serviceName, String parameter = null, List<String> arguments = Collections.emptyList()){
        String cmd = "${nssmHome}"
        cmd += " ${command.toString()} \"${serviceName}\" \"${parameter}\" \"${arguments.join(" ")}\""

        PrintStream original = System.out
        System.setOut(new PrintStream(new OutputStream() {void write(int b) {}}))

        int exitCode = new CmdRunner().run(cmd)

        System.setOut(original)

        return exitCode
    }

    /**
     * Gets the status of a Windows Service
     *
     * @param serviceName - Windows service name
     * @return the service's status
     */
    Status status(String serviceName)
    {
        String command = "${nssmHome} status \"${serviceName}\""
        String status = CmdRunner.runOutput(command)
        status = status.replaceAll("\\W","")
        if(status.contains("Cantopenservice"))
            return "SERVICE_NOT_FOUND" as Status
        else
            return status as Status
    }

    /**
     * NSSM commands
     */
    enum Command{
        install,
        edit,
        get,
        set,
        reset,
        remove,
        start,
        stop,
        restart,
        status,
        rotate
    }

    /**
     * The status of a service
     */
    enum Status{
        SERVICE_STOPPED,
        SERVICE_RUNNING,
        SERVICE_NOT_FOUND
    }
}
