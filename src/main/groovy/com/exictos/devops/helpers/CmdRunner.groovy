package com.exictos.devops.helpers

import groovy.util.logging.Slf4j

@Slf4j
class CmdRunner {

    /**
     * Runs a bash command and returns its exit code
     * @param command
     * @return script exit code
     */
    static int run(String command)
    {
        ProcessBuilder builder = new ProcessBuilder(command.split(' '))
        builder.redirectErrorStream(true)

        Process process = builder.start()

        InputStream stdout = process.getInputStream()

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))

        def line

        while((line = reader.readLine()) != null){
            log.info((String)line)
        }

        process.exitValue()
    }

    /**
     * Runs a bash command and returns its output
     *
     * @param command
     * @return script output as string
     */
    static String runOutput(String command)
    {
        ProcessBuilder builder = new ProcessBuilder(command.split(' '))
        builder.redirectErrorStream(true)

        Process process = builder.start()

        InputStream stdout = process.getInputStream()

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))

        def line
        StringBuilder sb = new StringBuilder()
        while((line = reader.readLine()) != null){
            sb.append(line)
        }

        return sb.toString()

    }

}
