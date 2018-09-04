pipeline {
	agent any

	stages {
		stage('Build') {
			steps{
				echo 'Building...'
				sh 'gradlew build'
				archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
			}
		}
	}
}
