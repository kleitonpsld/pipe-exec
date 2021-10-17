#!/usr/bin/env groovy

def MAVEN_VERSION="maven-3.6.3"

pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                script {
                    git branch: 'main',
                        url: 'https://github.com/WebGoat/WebGoat.git'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "ls"
                    sh "pwd"
                    withMaven(maven:MAVEN_VERSION){
                        sh "mvn clean install -DskipTests" 
                    }
                }
                
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                script {
                    sh "docker build -f webgoat-server/Dockerfile webgoat-server/. --tag webgoat-8.0:1.0.0"
                    sh "docker build -f webwolf/Dockerfile webwolf/. --tag webwolf-8.0:1.0.0"
                    sh "docker-compose up -d --force-recreate"
                }
            }
        }
    }
}
