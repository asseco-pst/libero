# Libero

A container agnostic deployment management tool for application servers

## Index

 * [Containers Supported](#containers-supported)
 * [Getting Started](#getting-started)
 * [Usage](#usage)
    * [As a CLI](#as-a-cli-since-v140)
    * [As a Groovy lib](#as-a-groovy-lib)
    * [In Jenkins](#in-jenkins)
 * [Uploading artifact to Nexus](#uploading-artifact-to-nexus)
 

## Containers Supported
 * WebSphere Application Server
 * WildFly
 * Windows Services

## Getting Started

### Include in your project as a dependency:

Maven:
```xml
<dependency>
    <groupId>com.exictos.devops</groupId>
    <artifactId>libero</artifactId>
    <version>1.5.0</version>
</dependency>
```

Gradle:
```json
compile group: 'com.exictos.devops', name: 'libero', version: '1.5.0'
```

### Build from source
1. Clone the project
```sh
git clone git@gitlab.dcs.exictos.com:devops/libero.git
```

2. Run the following command on the root of the project:
```sh
gradlew build
```

## Usage

### As a CLI (since v1.5.0)

**Note:** When using from CLI you can only deploy applications

```console
C:\> java -jar libero.jar -help

usage: libero -container <CONTAINER> [-host] [...]
Install an application in either WildFly, WebSphere or as a Windows
service
 -appLocation <APPLOCATION>            The path to the application package
 -appName <APPNAME>                    The application name
 -appVersion <APPVERSION>              The application version
 -argument <ARGUMENT>                  The argument to append to the
                                       install directory. (usually the
                                       entry point file)
 -bin <BIN>                            The path to the service executable
                                       (eg. node.exe)
 -container <CONTAINER>                [REQUIRED] The container you wish
                                       to deploy to.
                                       WildFly - 'wildfly'
                                       WebSphere - 'websphere'
                                       Windows Service - 'ws'
 -help                                 Show help
 -host <HOST>                          Hostname or IP
 -install                              Installs an application with
                                       rollback
 -installDir <INSTALLDIR>              The path to the directory where
                                       you'd like to install the service
 -nssm <NSSM>                          The path to the NSSM executable
 -password <PASSWORD>                  Application server admin password
 -port <PORT>                          Management or SOAP port
 -start                                Start application after installing
 -startMostRecentApps                  Start all most recent applications
 -stopAllApps                          Stops all applications installed
 -uninstallAppOldInstances <APPNAME>   Uninstalls old instances from an
                                       application
 -uninstallOldInstances                Uninstalls all old instances
 -username <USERNAME>                  Application server admin username
 -wsadmin <WSADMIN>                    The path to WebSphere wsadmin
                                       script

Please report issues at https://gitlab.dcs.exictos.com/devops/libero


C:\> java -jar libero.jar -container wildfly -host ***REMOVED*** -port 9997 -username admin -password ***REMOVED*** -appName BackOfficeWS -appLocation C:/BackOfficeWSEAR.ear -appVersion 1.2.3

```

### As a Groovy Lib

#### Installing an application in a WildFly profile

```groovy
Container wildfly = new WildFly("***REMOVED***", 49990, ***REMOVED***, ***REMOVED***.toCharArray())

wildfly.connect()
wildfly.installAppWithRollBack("C:/packages/irc/IRC_WS_BBEAR.ear","IRC_WS_BB")
```

#### Installing an application in a WAS profile

```groovy
Container was = new WebSphere("***REMOVED***", 8881, ***REMOVED***, ***REMOVED***.toCharArray(), "C:/IBM/WebSphere/AppServer/bin/wsadmin.bat")

was.installAppWithRollBack("C:/packages/irc/IRC_WS_BBEAR.ear","IRC_WS_BB")
```

#### Starting an application in a WildFly/WebSphere profile

```groovy
Container appserver = new WildFly/WebSphere(...)

// Explicitly connect if deploying to WildFly
appserver.connect()

String deployment = appserver.profile.listInstances("IRC_WS_BB").first().getName()

appserver.startApp(deployment)
```

#### Listing all installed applications in a profile

```groovy
Container appserver = new WildFly/WebSphere(...)

appserver.connect()

println "Installed applications: "
appserver.profile.listInstalledApplications().each{ instance ->
    println "\t - instance"
}

```
The output would be something like:
```console
Installed applications: 
    - IRC_WS_BB
    - Server
    - ReportingServices
```

#### Starting most recent applications in a profile

```groovy
Container appserver = new WildFly/WebSphere(...)

// Explicitly connect if deploying to WildFly
appserver.connect()

appserver.startMostRecentApps()

```

#### Installing a Windows service

```groovy
ServiceManager ws = new ServiceManager()

Service service = new Service()
    service.set_package(new File("C:\\package\\pfs_hardware.zip"))
    service.setBin(new File("C:\\Users\\jcoelho\\AppData\\Roaming\\nvm\\v6.11.2\\node.exe"))
    service.setInstallDirectory(new File("C:\\NodeJS"))
    service.setName("Exictos - PFS Hardware")
    service.setArguments(["main.js"])

ws.installServiceWithRollback(service)
```

#### Setting a log file

If you need Libero to log to a specific file do the following:

```groovy
File logFile = new File("C:/Exictos/logs/deployment.log")

Container was = new WebSphere(...)
was.setLogFile(logFile)

was.installAppWithRollBack(...)
```

### In Jenkins

Check out the [wiki](https://gitlab.dcs.exictos.com/devops/libero/wikis/using-libero-in-jenkins)

## Uploading artifact to Nexus

1. Make sure the project ìnformation specified in ``build.gradle`` is correct
```properties
project.version = "1.1.0"
project.name = "libero"
project.group = "com.exictos.devops"
```

2. Set the following variables in the gradle.properties file:
```properties
nexusUrl=http://nexus.dcs.exictos.com
nexusUsername=<ldap_username>
nexusPassword=<ldap_password>
```

3. Run the following command:
```sh
gradlew upload
```