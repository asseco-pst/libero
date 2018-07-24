package com.exictos.devops.services

/**
 * This class represents a system service. It is used by WindowsServiceManager class to implement its methods
 *
 */
class Service {

    /**
     * The .zip package to install from
     */
    File _package
    /**
     * The service name to be installed
     */
    String name
    /**
     * The executable directory
     */
    File bin
    /**
     * Arguments to be appended to the executable
     */
    List<String> arguments
    /**
     * The install directory
     */
    File installDirectory

}
