package com.exictos.devops


import com.exictos.devops.cli.CommandLineBuilder
import com.exictos.devops.cli.Libero
import com.exictos.devops.helpers.XHDLogger
import org.apache.commons.cli.CommandLine

class Main {

    protected static XHDLogger logger = new Application().getLogger()

    static void main(String[] args){

        CommandLine cmd

        try{
            cmd = CommandLineBuilder.commandLine(args)
        }catch(Exception e){
            logger.log("Error parsing command line.")
            logger.log("Cause: ${e}")
            throw e
        }

        new Libero().run(cmd, logger)

    }

}
