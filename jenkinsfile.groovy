#!/usr/bin/env groovy

def MAVEN_VERSION="maven-3.6.3"

pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                script {
                    git branch: 'master',
                        url: 'https://github.com/WebGoat/WebGoat.git'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "ls"
                    sh "pwd"
                    sh "cd WebGoat"
                    withMaven(maven:MAVEN_VERSION){
                        sh "mvn clean install -DskipTests" 
                    }
                    sh "ls"
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
            }
        }
    }
}
