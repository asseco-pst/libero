package com.exictos.devops.cli

import org.apache.commons.cli.CommandLine

class Libero {

    static void run(CommandLine cmd){
        println cmd.getOptions()
    }

}
