package com.exictos.devops.cli

import org.apache.commons.cli.*

class CommandLineBuilder {

    static final CommandLineParser cmdParser = new DefaultParser()

    static CommandLine commandLine(String[] args)
    {
        if(args.contains("-help")){
            printUsage()
            return null
        }

        CommandLine cmd = cmdParser.parse(options, args)
        return cmd

    }

    static Options getOptions()
    {
        Options options = OptionsParser.parse("/config/options.json")
        return options
    }

    static void printUsage()
    {
        final HelpFormatter formatter = new HelpFormatter()
        final String syntax = "libero"

        println("\n=========================================")
        println("                 HELP                    ")
        println("=========================================")

        final PrintWriter pw = new PrintWriter(System.out)
        formatter.printUsage(pw, 80, syntax, options)
        pw.flush()
    }

}
