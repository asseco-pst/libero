# Definition of Done

In order to consider an issue as **Done** all the following should apply:

* [ ] Any config/build changes should be documented
* [ ] Code review passes
* [ ] Acceptance criteria:
    * unit tests passed
    * project buids/deploys without errors
    * new methods are documented

# General practices

1. Document **every** method in **every** class

# Release a version of this project

## Create a tag

1. Open a release branch named ``release/vX.X.X``, where X.X.X is the new version  
```sh
git flow release start vX.X.X
```

2. Update ``CHANGELOG.md`` with the newest version
3. Update ``version`` variable on ``build.gradle`` file
4. Commit changes to the release branch and create a tag
```sh
git add .
git commit -m "Update version number"
git flow release finish
```
5. Push the tag to remote repo

## Build the project

1. Run the following command on the root of the project:

```sh
gradlew clean build
```

## Upload the artifact to Nexus

1. Run the following command on the root of the project:

```sh
gradlew clean upload
```

## Update supporting files

1. Update ``README.md`` file where necessary
2. Update the project badge `latest release`

