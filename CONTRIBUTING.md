## Creating a tag

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
6. Build the project and upload to Nexus
```sh
gradlew clean build upload
```
