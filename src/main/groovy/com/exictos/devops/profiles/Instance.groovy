package com.exictos.devops.profiles

class Instance implements Serializable{

    String disabledTime
    String enabledTime
    String name
    String status
    String timestamp
    boolean enabled
    int oldness

    String toString()
    {
        "class Instance{" +
                "disabledTime: ${disabledTime}" +
                "enabledTime: ${enabledTime}" +
                "name: ${name}" +
                "status: ${status}" +
                "timestamp: ${timestamp}" +
                "enabled: ${enabled}" +
                "oldness: ${oldness}" +
        "}"
    }

}
