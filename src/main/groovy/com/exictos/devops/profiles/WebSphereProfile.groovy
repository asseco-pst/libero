package com.exictos.devops.profiles

/**
 * This class represents a concrete WebSphere profile, and implements methods to get information about the profile
 * and the applications installed
 *
 */
class WebSphereProfile extends Profile{

    /**
     * Lists all instances of all applications installed in this profile
     *
     * @return List of instances
     */
    @Override
    List<Instance> listAllInstances() {
        return null
    }

    /**
     * Lists all instances of an application installed in this profile
     *
     * @param applicationName
     * @return List of instances
     */
    @Override
    List<Instance> listInstances(String applicationName) {
        return null
    }

    /**
     * Lists all installed applications in this profile
     *
     * @return List of application names
     */
    @Override
    List<String> listInstalledApplications() {
        return null
    }

}
