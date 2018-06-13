package com.exictos.devops.helpers

class CmdBuilder {

    String command = "\"[DEPLOY_SCRIPT]\" " +
    "-conntype SOAP " +
    "-host [IP] " +
    "-port [PORT] " +
    "-user [USERNAME] " +
    "-password [PASSWORD] " +
    "-lang jython -javaoption \"-Dpython.path=[WAS_ADMIN_SCRIPT_ROOT_LOCATION]\"  " +
    "-f \"[WAS_ADMIN_INSTALL_SCRIPT_LOCATION]\" " +
    "workspace=\"[WORKSPACE]\" " +
    "appName=\"[APP_NAME]\" " +
    "appTimestamp=\"[APP_TIMESTAMP]\" " +
    "appLocation=\"[APP_LOCATION]\" " +
    "earOrWar=\"[PACKAGE_TYPE]\" " +
    "contextRoot=\"\" " +
    "actions="

    CmdBuilder deployScript(String path){
        command = command.replace("[DEPLOY_SCRIPT]", path)
        return this
    }

    CmdBuilder ip(String ip){
        command = command.replace("[IP]", ip)
        return this
    }

    CmdBuilder port(int port){
        command = command.replace("[PORT]", port.toString())
        return this
    }

    CmdBuilder username(String username){
        command = command.replace("[USERNAME]", username)
        return this
    }

    CmdBuilder password(char[] password){
        command = command.replace("[PASSWORD]", password.toString())
        return this
    }

    CmdBuilder wasAdminScriptRootLocation(String path){
        command = command.replace("[WAS_ADMIN_SCRIPT_ROOT_LOCATION]", path)
        return this
    }

    CmdBuilder wasAdminInstallScriptLocation(String path){
        command = command.replace("[WAS_ADMIN_INSTALL_SCRIPT_LOCATION]", path)
        return this
    }

    CmdBuilder workspace(String path){
        command = command.replace("[WORKSPACE]", path)
        return this
    }

    CmdBuilder appName(String name){
        command = command.replace("[APP_NAME]", name)
        return this
    }

    CmdBuilder appLocation(String path){
        command = command.replace("[APP_LOCATION]", path)
        return this
    }

    CmdBuilder packageType(String type){
        command = command.replace("[PACKAGE_TYPE]", type)
        return this
    }

    CmdBuilder appTimestamp(String timestamp){
        command = command.replace("[APP_TIMESTAMP]", timestamp)
        return this
    }

    CmdBuilder addAction(String action){

        if(command.endsWith("="))
            command += action
        else
            command += ",${action}"
        return this
    }

    String build(){
        return command
    }

}
