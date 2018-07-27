# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Added
    - More verbose logging
    - WildFly profile can now return a running application's context root
    - WebSphere profile can now return an application's context root

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