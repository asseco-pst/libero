package com.exictos.devops


import com.exictos.devops.cli.CommandLineBuilder
import com.exictos.devops.cli.Libero
import com.exictos.devops.helpers.XHDLogger
import org.apache.commons.cli.CommandLine

class Main {

    protected static XHDLogger log = new Application().getLog()

    static void main(String[] args){

        CommandLine cmd

        try{
            cmd = CommandLineBuilder.commandLine(args)
        }catch(Exception e){
            log.log("Error parsing command line.")
            log.log("Cause: ${e}")
            return
        }

        new Libero().run(cmd)

    }

}
