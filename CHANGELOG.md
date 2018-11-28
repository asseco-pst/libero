# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Added
 - Icon to launch4j executable
 - Install only option to command line

## :arrow_forward: v1.6.4 <sup>2018-11-15</sup>
### Fixed
 - Bug when parsing application names with underscores (eg. eBankaPlus_v2)

## :arrow_forward: v1.6.3 <sup>2018-11-09</sup>
### Changed
 - Stopping applications will not rethrow exceptions

## :arrow_forward: v1.6.2 <sup>2018-11-06</sup>
### Fixed
 - Make sure all instances of an application are stopped before starting another one

## :arrow_forward: v1.6.1 <sup>2018-11-06</sup>
### Fixed
 - Bug when uninstalling old instances

## :arrow_forward: v1.6.0 <sup>2018-10-22</sup>
### Changed
 - Logback for a custom logger to support thread safe logging (#16)
 - Disabled NSSM logging (#16)

### Added
 - Unit tests to LiberoHelper class (#6)
 - Enable/Disable application loading on WebSphere boot (#18) 

### Fixed
 - Bug when parsing deployed applications (#14)
 - Oldness threshold when installing app with rollback (#17)
 - Added double quotes to WildFly package argument

## :arrow_forward: v1.5.0 <sup>2018-09-20</sup>
### Added
 - Gradle plugin Launch4j to convert UberJar to EXE file
 - Command Line interface features to support Jenkins integration

### Fixed
 - Use java util SimpleDateFormat to format dates
 - Minor bugs to support Jenkins integration

## :arrow_forward: v1.4.0 <sup>2018-09-13</sup>
### Added
 - Command Line Interface functionality (#11)
 - Jenkinsfile with declarative pipeline to build and archive the generated jar
 - Snapshot or release validation when uploading to Maven repo.
 - Gradle Shadow plugin to build an uberJar.

### Fixed
 - Added literal '-' to name regex validator to support PortalPFS modules names.

## :arrow_forward: v1.3.0 <sup>2018-08-01</sup>
### Changed
- All classes use the same logger. If a log file is set the console appender is disabled.

## :arrow_forward: v1.2.3 <sup>2018-07-31</sup>
### Fixed
- Minor bug with the Pattern matching used to detect invalid deployment names

## :arrow_forward: v1.2.2 <sup>2018-07-31</sup>
### Fixed
- Minor bug when looking for installed instances of Windows Services

## :arrow_forward: v1.2.1 <sup>2018-07-30</sup>
### Fixed
- NSSM executable path is passed as an argument in the WindowsServiceManager constructor, instead of using an exe included in the jar file.

## :arrow_forward: v1.2.0 <sup>2018-07-30</sup>
### Added 
- More verbose logging
- WildFly profile can now return a running application's context root
- WebSphere profile can now return an application's context root
    
### Changed
- Applications can be installed with a version number and a specific timestamp (#7)

## :arrow_forward: v1.1.0 <sup>2018-07-24</sup>
### Changed
- Logging strategy now allows to specify a file to log to.

## :arrow_forward: v1.0.0 <sup>2018-07-17</sup>
### Added
- Gradle task to upload archive to Nexus Repository
- Use File class instead of Strings for files

## :arrow_forward: v1.0.0-beta <sup>2018-06-20</sup>

### Added
- Deployment manager support for WildFly
- Deployment manager support for Windows Services
- Deployment manager support for WebSphere
