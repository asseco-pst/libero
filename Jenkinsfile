pipeline {
	agent {label 'master'}
	stages {
		stage('Build') {
			steps{
				bat 'gradlew clean build test'
				archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
			}
		}
		stage('Deploy') {
		    steps{
		        bat 'gradlew createExe'
		        archiveArtifacts artifacts: '**/build/launch4j/libero.exe', fingerprint:true
		    }
		}
	}
}
