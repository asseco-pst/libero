pipeline {
	agent {label 'master'}
	stages {
		stage('Build') {
			steps{
				echo 'Building...'
				bat 'gradlew build'
				archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
			}
		}
	}
}
