package com.exictos.devops

import com.exictos.devops.helpers.LiberoHelper

import java.sql.Timestamp

class Playground {
    
    static void main(String []args){

        Timestamp now = new Timestamp(118,9,11,10,46,0,0)
        String time = now.format(LiberoHelper.DATE_FORMAT)
        String version = "1.2.3"
        String appName = "BackOfficeWS"

        String standardizedName = "${appName}___v${version}___${time}.ear"

        String actual = LiberoHelper.extractTimestamp(standardizedName)
        println actual

    }
}
