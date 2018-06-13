package com.exictos.devops.helpers

class CmdRunner {

    static void run(String command)
    {
        ProcessBuilder builder = new ProcessBuilder(command.split(' '))
        builder.redirectErrorStream(true)

        Process process = builder.start()

        InputStream stdout = process.getInputStream()

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))

        def line

        while((line = reader.readLine()) != null){
            println line
        }
    }

}
