package com.exictos.devops.profiles

import java.sql.Timestamp

/**
 * This class represents an instance of an application installed on a profile
 */
class Instance implements Serializable{

    String disabledTime
    Timestamp enabledTime
    String name
    Timestamp timestamp
    boolean enabled
    int oldness

    String toString()
    {
        "Instance{\n" +
                "\tdisabledTime: ${disabledTime},\n" +
                "\tenabledTime: ${enabledTime},\n" +
                "\tname: ${name},\n" +
                "\ttimestamp: ${timestamp},\n" +
                "\tenabled: ${enabled},\n" +
                "\toldness: ${oldness}\n" +
        "}"
    }

}
