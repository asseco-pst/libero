package com.exictos.devops.windows

import com.exictos.devops.helpers.NssmWrapper

class Service {

    NssmWrapper nssm

    File _package
    String name
    File bin
    List<String> arguments
    File installDirectory

    Service() {
        nssm = new NssmWrapper()
    }

    /**
     * Installs service using NSSM and returns exit code
     *
     * @return true if successful
     */
    boolean install()
    {
        nssm.run(NssmWrapper.Command.install, name, bin.toString(), arguments) == 0
    }

    /**
     * Starts this service with NSSM
     *
     * @return true if successful
     */
    boolean start()
    {
        nssm.run(NssmWrapper.Command.start,name) == 0
    }

    /**
     * Stops this service
     *
     * @return true if successful
     */
    boolean stop()
    {
        nssm.run(NssmWrapper.Command.stop,name) == 0
    }

    /**
     * Uninstall this service
     *
     * @return true if successful
     */
    boolean remove()
    {
        nssm.run(NssmWrapper.Command.remove, name, "confirm") == 0
    }

    /**
     * Gets the current status of this service
     *
     * @return this service status (Check NssmWrapper.Status enum)
     */
    @SuppressWarnings("SpellCheckingInspection")
    NssmWrapper.Status status(){
        nssm.status(name)
    }

}
