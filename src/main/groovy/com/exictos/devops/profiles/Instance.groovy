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
        "class Instance{" +
                "disabledTime: ${disabledTime}, " +
                "enabledTime: ${enabledTime}, " +
                "name: ${name}, " +
                "timestamp: ${timestamp}, " +
                "enabled: ${enabled}, " +
                "oldness: ${oldness}" +
        "}"
    }

}
