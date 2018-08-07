# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Fixed
 - Added literal '-' to name regex validtor to support PortalPFS modules names.

## [1.3.0] - 2018-08-01
### Changed
- All classes use the same logger. If a log file is set the console appender is disabled.

## [1.2.3] - 2018-07-31
### Fixed
- Minor bug with the Pattern matching used to detect invalid deployment names

## [1.2.2] - 2018-07-31
### Fixed
- Minor bug when looking for installed instances of Windows Services

## [1.2.1] - 2018-07-30
### Fixed
- NSSM executable path is passed as an argument in the WindowsServiceManager constructor, instead of using an exe included in the jar file.

## [1.2.0] - 2018-07-30
### Added 
- More verbose logging
- WildFly profile can now return a running application's context root
- WebSphere profile can now return an application's context root
    
### Changed
- Applications can be installed with a version number and a specific timestamp (#7)

## [1.1.0] - 2018-07-24
### Changed
- Logging strategy now allows to specify a file to log to.

## [1.0.0] - 2018-07-17
### Added
- Gradle task to upload archive to Nexus Repository
- Use File class instead of Strings for files

## [1.0.0-beta] - 2018-06-20

### Added
- Deployment manager support for WildFly
- Deployment manager support for Windows Services
- Deployment manager support for WebSphere