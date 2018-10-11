pipeline {
	agent {label 'master'}
	stages {
		stage('Build') {
			steps{
				bat 'gradlew clean build test'
				archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
			}
		}
	}
}
