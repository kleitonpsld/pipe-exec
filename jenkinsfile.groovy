#!/usr/bin/env groovy

def MAVEN_VERSION="maven-3.6.3"

pipeline {
    agent any

    stages {
        stage('Build') {
	 
            steps {
		script {
		    git branch: 'vtest10',
			url: 'https://github.com/WebGoat/WebGoat.git'
		    cd WebGoat
		    withMaven(maven:MAVEN_VERSION){
                        sh "mvn clean install -DskipTests" 
                    }
		    ls
		    pwd
		    uname -a
		}
                
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
