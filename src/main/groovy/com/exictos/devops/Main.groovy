package com.exictos.devops

import ch.qos.logback.classic.Logger
import com.exictos.devops.cli.CommandLineBuilder
import com.exictos.devops.cli.Libero
import com.exictos.devops.helpers.LiberoLogger
import org.apache.commons.cli.CommandLine

class Main {

    protected static final Logger log = LiberoLogger.getLogger()

    static void main(String[] args){

        CommandLine cmd

        try{
            cmd = CommandLineBuilder.commandLine(args)
        }catch(Exception e){
            log.error("Error parsing command line.")
            log.debug("Cause: ${e}")
            return
        }

        new Libero().run(cmd)

    }

}
