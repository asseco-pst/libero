# Libero

A container agnostic deployment management tool for application servers

## Index

 * [Containers Supported](#containers-supported)
 * [Getting Started](#getting-started)
 * [Usage](#usage)
    * [As a CLI](#as-a-cli)
    * [As a Groovy lib](#as-a-groovy-lib)
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
    <version>1.3.0</version>
</dependency>
```

Gradle:
```json
compile group: 'com.exictos.devops', name: 'libero', version: '1.3.0'
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

### As a CLI

**Note:** When using from CLI you can only deploy applications

```sh
> java -jar libero.jar -help

=========================================
                 HELP
=========================================
usage: libero [-appLocation <appLocation>] [-appName <appName>] [-appVersion
       <appVersion>] -container <container> [-help] [-host <host>] [-nssm
       <nssm>] [-password <password>] [-port <port>] [-username <username>]
       [-wsadmin <wsadmin>]


> java -jar libero.jar -container wildfly -host ***REMOVED*** -port 9997 -username admin -password ***REMOVED*** -appName BackOfficeWS -appLocation C:/BackOfficeWSEAR.ear -appVersion 1.2.3

```

### Aa a Groovy Lib

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