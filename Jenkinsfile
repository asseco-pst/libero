pipeline {
	agent {label 'master'}
	stages {
		stage('Build') {
			steps{
				echo 'Building...'
				bat 'gradlew clean build test'
				archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
			}
		}
	}
}
