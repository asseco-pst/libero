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

    boolean install()
    {
        nssm.run(NssmWrapper.Command.install, name, bin.toString(), arguments) == 0
    }

    boolean start()
    {
        nssm.run(NssmWrapper.Command.start,name) == 0
    }

    boolean stop()
    {
        nssm.run(NssmWrapper.Command.stop,name) == 0
    }

    boolean remove()
    {
        nssm.run(NssmWrapper.Command.remove, name, "confirm") == 0
    }

    NssmWrapper.Status status(){
        nssm.status(name)
    }

}
