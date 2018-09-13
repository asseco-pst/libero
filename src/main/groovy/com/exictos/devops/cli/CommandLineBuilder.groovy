package com.exictos.devops.cli

import org.apache.commons.cli.*

class CommandLineBuilder {

    static final CommandLineParser cmdParser = new DefaultParser()

    static CommandLine commandLine(String[] args)
    {
        if(args.contains("-help")){
            printHelp()
            System.exit(0)
        }

        CommandLine cmd = cmdParser.parse(options, args)
        return cmd

    }

    static Options getOptions()
    {
        Options options = OptionsParser.parse("/config/options.json")
        return options
    }

    static void printHelp()
    {
        final HelpFormatter formatter = new HelpFormatter()
        String syntax = "libero -container <CONTAINER> [-host] [...]"

        final String header = "Install an application in either WildFly, WebSphere or as a Windows service\n"
        final String footer = "\nPlease report issues at https://gitlab.dcs.exictos.com/devops/libero"

        formatter.printHelp(syntax, header, options, footer)
    }

}
