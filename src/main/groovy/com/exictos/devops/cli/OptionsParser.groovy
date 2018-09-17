package com.exictos.devops.cli

import groovy.json.JsonSlurper
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

class OptionsParser {

    static Options parse(String filename)
    {
        def file = CommandLineBuilder.class.getResource(filename)
        def data = new JsonSlurper().parseText(file.text)
        Options options = new Options()

        for(Object object in data["options"]) {

            Option option = Option.builder((String)object["option"])
                    .argName(((String)(object["argName"])).toUpperCase())
                    .hasArg((boolean)object["hasArg"])
                    .desc((String)object["desc"])
                    .required((boolean)object["required"])
                    .build()

            options.addOption(option)
        }

        return options
    }

}
