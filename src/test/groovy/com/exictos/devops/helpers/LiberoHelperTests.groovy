package com.exictos.devops.helpers

import com.exictos.devops.profiles.Instance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import java.sql.Timestamp

@RunWith(JUnit4.class)
class LiberoHelperTests extends GroovyTestCase {

    Timestamp timestamp
    String time
    String version = "1.2.3"
    String appName = "BackOfficeWS"
    String standardizedName

    @Before
    void setUp(){
        timestamp = new Timestamp(118,9,11,10,46,0,0)
        time = timestamp.format(LiberoHelper.DATE_FORMAT)
        standardizedName = "${appName}___v${version}___${time}.ear"
    }

    @Test
    void testStandardizeName(){

        String actualName = LiberoHelper.standardizeName("C:/BackofficeWS.ear", appName, version, timestamp)
        assertEquals("Deployment name standardization is not working correctly", standardizedName, actualName)

    }

    @Test
    void testExtractName(){

        String actual = new LiberoHelper().extractName(standardizedName)
        assertEquals("Could not extract name successfully", appName, actual)

    }

    @Test
    void testExtractVersion(){

        String actual = new LiberoHelper().extractVersion(standardizedName)
        assertEquals("Could not extract version successfully", version, actual)

    }

    @Test
    void testExtractTimestamp(){

        Timestamp actual = LiberoHelper.extractTimestamp(standardizedName)
        assertEquals("Could not extract timestamp successfully", timestamp, actual)

    }

    @Test
    void testToTimestamp(){

        Timestamp actual = LiberoHelper.toTimestamp(time)
        assertEquals("Could not parse string to timestamp", timestamp, actual)

    }

    @Test
    void testPackageType(){

        String packageType = LiberoHelper.packageType("C:/BackOfficeWS.ear")
        assertEquals("Could not extract package type", "ear", packageType)

        packageType = LiberoHelper.packageType("C:/BackOfficeWS.war")
        assertEquals("Could not extract package type", "war", packageType)

        packageType = LiberoHelper.packageType("C:/BackOff.iceWS.ear")
        assertEquals("Could not extract package type", "ear", packageType)

    }

    @Test
    void testOldnessLevel(){

        List<Instance> instances = []
        Instance instance1 = new Instance()
        instance1.setName("Instance1")
        instance1.setTimestamp(timestamp)

        //Instance 2 is older than instance 1
        Instance instance2 = new Instance()
        instance2.setName("Instance1")
        instance2.setTimestamp(new Timestamp(110,10,11,11,0,0,0))

        instances.add(instance1)
        instances.add(instance2)

        instances = LiberoHelper.oldnessLevel(instances)

        assertTrue("Instance 2 oldness should be greater than instance 1", instances.last().getOldness() > instances.first().getOldness())
    }


}
